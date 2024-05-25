package com.cpumonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.cpumonitor.cpuusage.CpuUsageEntity;
import com.cpumonitor.cpuusage.CpuUsageRepository;
import com.cpumonitor.cpuusage.CpuUsageService;
import com.cpumonitor.cpuusage.cpuusageDTO.CpuUsageDTO;
import com.cpumonitor.cpuusage.cpuusageDTO.DailyUsageDTO;
import com.cpumonitor.cpuusage.cpuusageDTO.HourlyUsageDTO;
import com.cpumonitor.exception.CpuUsageServiceException;
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

    @Test
    void getMinuteCpuUsage_ShouldReturnListOfCpuUsageDTO() {
        LocalDateTime startDateTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endDateTime = LocalDateTime.now();
        List<CpuUsageDTO> cpuUsageDTOs = new ArrayList<>();
        cpuUsageDTOs.add(new CpuUsageDTO(50.0, 30.0, 20.0, startDateTime));

        when(cpuUsageRepository.getCpuUsageBetween(startDateTime, endDateTime)).thenReturn(cpuUsageDTOs);

        List<CpuUsageDTO> result = cpuUsageService.getMinuteCpuUsage(startDateTime, endDateTime);
        assertEquals(cpuUsageDTOs.size(), result.size());
        assertEquals(cpuUsageDTOs.get(0).getUserUsage(), result.get(0).getUserUsage());
        assertEquals(cpuUsageDTOs.get(0).getSystemUsage(), result.get(0).getSystemUsage());
        assertEquals(cpuUsageDTOs.get(0).getIdleUsage(), result.get(0).getIdleUsage());
    }

    @Test
    void getMinuteCpuUsage_ShouldThrowCpuUsageServiceException() {
        LocalDateTime startDateTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endDateTime = LocalDateTime.now();

        when(cpuUsageRepository.getCpuUsageBetween(startDateTime, endDateTime)).thenThrow(RuntimeException.class);

        Assertions.assertThrows(CpuUsageServiceException.class, () -> {
            cpuUsageService.getMinuteCpuUsage(startDateTime, endDateTime);
        });
    }

    @Test
    void getHourlyCpuUsage_ShouldReturnListOfHourlyUsageDTO() {
        LocalDate date = LocalDate.now();
        LocalDateTime dateTime = LocalDateTime.now();
        List<HourlyUsageDTO> dummyHourlyUsageDTOList = new ArrayList<>();
        dummyHourlyUsageDTOList.add(new HourlyUsageDTO(
                dateTime.toString(), 10.0, 20.0, 15.0,
                5.0, 15.0, 10.0,
                2.0, 8.0, 5.0));

        LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);
        when(cpuUsageRepository.findHourlyUsage(startDateTime, endDateTime)).thenReturn(dummyHourlyUsageDTOList);

        List<HourlyUsageDTO> result = cpuUsageService.getHourlyCpuUsage(date, date);

        assertEquals(dummyHourlyUsageDTOList.size(), result.size());
        assertEquals(dummyHourlyUsageDTOList.get(0).getRecordedAt(), result.get(0).getRecordedAt());
        assertEquals(dummyHourlyUsageDTOList.get(0).getMinUserUsage(), result.get(0).getMinUserUsage());
        assertEquals(dummyHourlyUsageDTOList.get(0).getMaxUserUsage(), result.get(0).getMaxUserUsage());
        assertEquals(dummyHourlyUsageDTOList.get(0).getAvgUserUsage(), result.get(0).getAvgUserUsage());
        assertEquals(dummyHourlyUsageDTOList.get(0).getMinSystemUsage(), result.get(0).getMinSystemUsage());
        assertEquals(dummyHourlyUsageDTOList.get(0).getMaxSystemUsage(), result.get(0).getMaxSystemUsage());
        assertEquals(dummyHourlyUsageDTOList.get(0).getAvgSystemUsage(), result.get(0).getAvgSystemUsage());
        assertEquals(dummyHourlyUsageDTOList.get(0).getMinIdleUsage(), result.get(0).getMinIdleUsage());
        assertEquals(dummyHourlyUsageDTOList.get(0).getMaxIdleUsage(), result.get(0).getMaxIdleUsage());
        assertEquals(dummyHourlyUsageDTOList.get(0).getAvgIdleUsage(), result.get(0).getAvgIdleUsage());
    }

    @Test
    void getHourlyCpuUsage_ShouldThrowCpuUsageServiceException() {
        LocalDate date = LocalDate.now();
        LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);
        when(cpuUsageRepository.findHourlyUsage(startDateTime, endDateTime)).thenThrow(RuntimeException.class);

        Assertions.assertThrows(CpuUsageServiceException.class, () -> {
            cpuUsageService.getHourlyCpuUsage(date, date);
        });
    }

    @Test
    void getDailyCpuUsage_ShouldReturnListOfDailyUsageDTO() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalDateTime.MIN.toLocalTime());
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalDateTime.MAX.toLocalTime());
        List<DailyUsageDTO> dummyDailyUsageDTOList = new ArrayList<>();
        dummyDailyUsageDTOList.add(new DailyUsageDTO(
                "2023-05-17",
                10.0, 20.0, 15.0,
                5.0, 15.0, 10.0,
                2.0, 8.0, 5.0));

        when(cpuUsageRepository.findDailyUsage(startDateTime, endDateTime)).thenReturn(dummyDailyUsageDTOList);

        List<DailyUsageDTO> result = cpuUsageService.getDailyCpuUsage(startDate, endDate);

        assertEquals(dummyDailyUsageDTOList.size(), result.size());
        assertEquals(dummyDailyUsageDTOList.get(0).getMinUserUsage(), result.get(0).getMinUserUsage());
        assertEquals(dummyDailyUsageDTOList.get(0).getMaxUserUsage(), result.get(0).getMaxUserUsage());
        assertEquals(dummyDailyUsageDTOList.get(0).getAvgUserUsage(), result.get(0).getAvgUserUsage());
        assertEquals(dummyDailyUsageDTOList.get(0).getMinSystemUsage(), result.get(0).getMinSystemUsage());
        assertEquals(dummyDailyUsageDTOList.get(0).getMaxSystemUsage(), result.get(0).getMaxSystemUsage());
        assertEquals(dummyDailyUsageDTOList.get(0).getAvgSystemUsage(), result.get(0).getAvgSystemUsage());
        assertEquals(dummyDailyUsageDTOList.get(0).getMinIdleUsage(), result.get(0).getMinIdleUsage());
        assertEquals(dummyDailyUsageDTOList.get(0).getMaxIdleUsage(), result.get(0).getMaxIdleUsage());
        assertEquals(dummyDailyUsageDTOList.get(0).getAvgIdleUsage(), result.get(0).getAvgIdleUsage());
    }

    @Test
    void getDailyCpuUsage_ShouldThrowCpuUsageServiceException() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalDateTime.MIN.toLocalTime());
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalDateTime.MAX.toLocalTime());

        when(cpuUsageRepository.findDailyUsage(startDateTime, endDateTime)).thenThrow(RuntimeException.class);

        Assertions.assertThrows(CpuUsageServiceException.class, () -> {
            cpuUsageService.getDailyCpuUsage(startDate, endDate);
        });
    }

}
