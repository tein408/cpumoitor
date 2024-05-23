package com.cpumonitor.cpuusage;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import com.cpumonitor.cpuusage.cpuusageDTO.CpuUsageDTO;
import com.cpumonitor.exception.DatabaseOperationException;
import com.cpumonitor.exception.InvalidDataException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CpuUsageService {

    private final CpuUsageRepository cpuUsageRepository;

    /**
     * CPU 사용 데이터를 데이터베이스에 저장합니다.
     * 
     * @param cpuUsageDTO 저장할 CPU 사용 데이터입니다.
     * @throws DatabaseOperationException 데이터베이스에 CPU 사용 데이터를 저장하는 동안 오류가 발생한 경우
     * @throws InvalidDataException 제공된 CPU 사용 데이터가 유효하지 않은 경우 (예: null)
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
     * CPU 사용 데이터를 유효성 검사합니다.
     * 
     * @param cpuUsageDTO 유효성을 검사할 CPU 사용 데이터입니다.
     * @throws InvalidDataException 제공된 CPU 사용 데이터가 유효하지 않은 경우 (예: null)
     */
    private void validateCpuUsageData(CpuUsageDTO cpuUsageDTO) {
        if (cpuUsageDTO == null) {
            throw new InvalidDataException("Invalid CPU usage data: CpuUsageDTO is null");    
        }
    }

}
