package com.cpumonitor.cpuusage;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import com.cpumonitor.cpuusage.cpuusageDTO.CpuUsageDTO;
import com.cpumonitor.exception.DatabaseOperationException;
import com.cpumonitor.exception.InvalidDataException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CpuUsageService {

    private final CpuUsageRepository cpuUsageRepository;

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

    private void validateCpuUsageData(CpuUsageDTO cpuUsageDTO) {
        if (cpuUsageDTO == null) {
            throw new InvalidDataException("Invalid CPU usage data: CpuUsageDTO is null");    
        }
    }

}
