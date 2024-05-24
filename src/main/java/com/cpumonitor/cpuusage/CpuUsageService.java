package com.cpumonitor.cpuusage;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import com.cpumonitor.cpuusage.cpuusageDTO.CpuUsageDTO;
import com.cpumonitor.cpuusage.cpuusageDTO.DailyUsageDTO;
import com.cpumonitor.cpuusage.cpuusageDTO.HourlyUsageDTO;
import com.cpumonitor.exception.CpuUsageServiceException;
import com.cpumonitor.exception.DatabaseOperationException;
import com.cpumonitor.exception.InvalidDataException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CPU 사용률 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CpuUsageService {

    private final CpuUsageRepository cpuUsageRepository;

    /**
     * CPU 사용률 데이터를 데이터베이스에 저장합니다.
     * 
     * @param cpuUsageDTO 저장할 CPU 사용률 데이터입니다.
     * @throws DatabaseOperationException 데이터베이스에 CPU 사용률 데이터를 저장하는 동안 오류가 발생한 경우
     * @throws InvalidDataException 제공된 CPU 사용률 데이터가 유효하지 않은 경우 (예: null)
     */
    @Transactional
    public void saveCpuUsage(CpuUsageDTO cpuUsageDTO) {
        validateCpuUsageData(cpuUsageDTO);

        CpuUsageEntity cpuUsageEntity = CpuUsageEntity.builder()
            .userUsage(cpuUsageDTO.getUserUsage())
            .systemUsage(cpuUsageDTO.getSystemUsage())
            .idleUsage(cpuUsageDTO.getIdleUsage())
            .recordedAt(cpuUsageDTO.getRecordedAt())
            .build();

        try {
            cpuUsageRepository.save(cpuUsageEntity);
        } catch (Exception e) {
            log.error("Failed to save CPU usage data: {}", e.getMessage());
            throw new DatabaseOperationException("Failed to save CPU usage data", e);
        }
    }

    /**
     * CPU 사용률 데이터 유효성을 검사합니다.
     * 
     * @param cpuUsageDTO 유효성을 검사할 CPU 사용률 데이터입니다.
     * @throws InvalidDataException 제공된 CPU 사용률 데이터가 유효하지 않은 경우 (예: null)
     */
    private void validateCpuUsageData(CpuUsageDTO cpuUsageDTO) {
        if (cpuUsageDTO == null) {
            throw new InvalidDataException("Invalid CPU usage data: CpuUsageDTO is null");    
        }
    }

    /**
     * 분 단위 CPU 사용률 데이터를 가져옵니다.
     * 
     * @param startDateTime 시작 날짜 및 시간입니다.
     * @param endDateTime 종료 날짜 및 시간입니다.
     * @return CPU 사용률 데이터 목록입니다.
     * @throws CpuUsageServiceException 데이터를 가져오는 동안 오류가 발생한 경우
     */
    public List<CpuUsageDTO> getMinuteCpuUsage(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try {
            log.info("Service - Fetching minute-level CPU usage data from {} to {}", startDateTime, endDateTime);
            return cpuUsageRepository.getCpuUsageBetween(startDateTime, endDateTime);
        } catch (Exception e) {
            log.error("Service - Failed to fetch minute-level CPU usage data from {} to {}: {}", startDateTime, endDateTime, e.getMessage());
            throw new CpuUsageServiceException("Error occurred while fetching minute CPU usage data", e);
        }
    }

    /**
     * 특정 날짜의 시간 단위 CPU 사용률 데이터를 가져옵니다.
     * 
     * @param date 데이터를 가져올 날짜입니다.
     * @return 시간 단위 CPU 사용률 데이터 목록입니다.
     * @throws CpuUsageServiceException 데이터를 가져오는 동안 오류가 발생한 경우
     */
    public List<HourlyUsageDTO> getHourlyCpuUsage(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
            log.info("Service - Fetching hourly CPU usage data for date {}", endDate);
            return cpuUsageRepository.findHourlyUsage(startDateTime, endDateTime);
        } catch (Exception e) {
            log.error("Service - Failed to fetch hourly CPU usage data for date {}: {}", endDate, e.getMessage());
            throw new CpuUsageServiceException("Error occurred while fetching hourly CPU usage data", e);
        }
    }

}
