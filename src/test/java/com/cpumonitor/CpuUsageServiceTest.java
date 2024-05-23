package com.cpumonitor;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import com.cpumonitor.cpuusage.CpuUsageEntity;
import com.cpumonitor.cpuusage.CpuUsageRepository;
import com.cpumonitor.cpuusage.CpuUsageService;
import com.cpumonitor.cpuusage.cpuusageDTO.CpuUsageDTO;
import com.cpumonitor.exception.DatabaseOperationException;
import com.cpumonitor.exception.InvalidDataException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CpuUsageServiceTest {

    @Mock
    private CpuUsageRepository cpuUsageRepository;

    @InjectMocks
    private CpuUsageService cpuUsageService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveCpuUsage() {
        CpuUsageDTO cpuUsageDTO = new CpuUsageDTO(50.0, 30.0, 20.0, LocalDateTime.now());

        cpuUsageService.saveCpuUsage(cpuUsageDTO);

        verify(cpuUsageRepository, times(1)).save(any(CpuUsageEntity.class));
    }

    @Test
    public void testSaveCpuUsage_InvalidData() {
        CpuUsageDTO cpuUsageDTO = null;

        Assertions.assertThrows(InvalidDataException.class, () -> {
            cpuUsageService.saveCpuUsage(cpuUsageDTO);
        });

        verify(cpuUsageRepository, never()).save(any(CpuUsageEntity.class));
    }

    @Test
    public void testSaveCpuUsage_DatabaseOperationException() {
        CpuUsageDTO cpuUsageDTO = new CpuUsageDTO(50.0, 30.0, 20.0, LocalDateTime.now());

        doThrow(RuntimeException.class).when(cpuUsageRepository).save(any(CpuUsageEntity.class));

        DatabaseOperationException exception = Assertions.assertThrows(DatabaseOperationException.class, () -> {
            cpuUsageService.saveCpuUsage(cpuUsageDTO);
        });
        Assertions.assertEquals("Failed to save CPU usage data", exception.getMessage());
        verify(cpuUsageRepository, times(1)).save(any(CpuUsageEntity.class));
    }
}
