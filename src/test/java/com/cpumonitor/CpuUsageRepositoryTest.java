package com.cpumonitor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.cpumonitor.cpuusage.CpuUsageEntity;
import com.cpumonitor.cpuusage.CpuUsageRepository;
import com.cpumonitor.cpuusage.cpuusageDTO.CpuUsageDTO;
import com.cpumonitor.cpuusage.cpuusageDTO.DailyUsageDTO;
import com.cpumonitor.cpuusage.cpuusageDTO.HourlyUsageDTO;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CpuUsageRepositoryTest {

    @Autowired
    private CpuUsageRepository cpuUsageRepository;

    @Test
    public void testSaveCpuUsage() {
        CpuUsageEntity cpuUsageEntity = new CpuUsageEntity(50.0, 30.0, 20.0, LocalDateTime.now());

        cpuUsageRepository.save(cpuUsageEntity);

        assertThat(cpuUsageRepository.count()).isEqualTo(1);
    }

    @Test
    public void testGetCpuUsageBetween() {
        LocalDateTime startTime = LocalDateTime.of(2024, 5, 1, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 5, 3, 23, 59, 59);

        // Insert data within the range
        cpuUsageRepository.save(new CpuUsageEntity(60.0, 30.0, 10.0, LocalDateTime.of(2024, 5, 1, 12, 0, 0)));
        cpuUsageRepository.save(new CpuUsageEntity(70.0, 40.0, 20.0, LocalDateTime.of(2024, 5, 2, 13, 0, 0)));
        cpuUsageRepository.save(new CpuUsageEntity(80.0, 50.0, 30.0, LocalDateTime.of(2024, 5, 3, 14, 0, 0)));

        // Insert data outside the range
        cpuUsageRepository.save(new CpuUsageEntity(90.0, 60.0, 40.0, LocalDateTime.of(2024, 4, 30, 12, 0, 0)));
        cpuUsageRepository.save(new CpuUsageEntity(100.0, 70.0, 50.0, LocalDateTime.of(2024, 5, 4, 13, 0, 0)));

        List<CpuUsageDTO> cpuUsageDTOs = cpuUsageRepository.getCpuUsageBetween(startTime, endTime);

        assertThat(cpuUsageDTOs).isNotNull();
        assertThat(cpuUsageDTOs).hasSize(3);

        // Check if data is within the expected range and in ascending order of recordedAt
        for (CpuUsageDTO cpuUsageDTO : cpuUsageDTOs) {
            LocalDateTime recordedAt = cpuUsageDTO.getRecordedAt();
            assertThat(recordedAt).isBetween(startTime, endTime);
        }
    }

    @Test
    public void testFindHourlyUsage() {
        LocalDate date = LocalDate.of(2024, 5, 1);

        // Insert data within the range
        cpuUsageRepository.save(new CpuUsageEntity(60.0, 30.0, 10.0, LocalDateTime.of(2024, 5, 1, 12, 0, 0)));
        cpuUsageRepository.save(new CpuUsageEntity(70.0, 40.0, 20.0, LocalDateTime.of(2024, 5, 1, 13, 0, 0)));
        cpuUsageRepository.save(new CpuUsageEntity(80.0, 50.0, 30.0, LocalDateTime.of(2024, 5, 1, 14, 0, 0)));

        // Insert data outside the range
        cpuUsageRepository.save(new CpuUsageEntity(90.0, 60.0, 40.0, LocalDateTime.of(2024, 4, 30, 12, 0, 0)));
        cpuUsageRepository.save(new CpuUsageEntity(100.0, 70.0, 50.0, LocalDateTime.of(2024, 5, 4, 13, 0, 0)));

        LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);
        List<HourlyUsageDTO> hourlyUsageDTOs = cpuUsageRepository.findHourlyUsage(startDateTime, endDateTime);

        assertThat(hourlyUsageDTOs).isNotNull();
        assertThat(hourlyUsageDTOs).hasSize(3);

        // Check if data is within the expected range and in ascending order of recordedAt
        for (HourlyUsageDTO hourlyUsageDTO : hourlyUsageDTOs) {
            String recordedAt = hourlyUsageDTO.getRecordedAt() + ":00";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime formatDate = LocalDateTime.parse(recordedAt, formatter);
            assertThat(formatDate.toLocalDate()).isEqualTo(date);
        }
    }

    @Test
    public void testFindDailyUsage() {
        LocalDateTime startTime = LocalDateTime.of(2024, 5, 1, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 5, 3, 23, 59, 59);

        // Insert data within the range
        cpuUsageRepository.save(new CpuUsageEntity(60.0, 30.0, 10.0, LocalDateTime.of(2024, 5, 1, 12, 0, 0)));
        cpuUsageRepository.save(new CpuUsageEntity(70.0, 40.0, 20.0, LocalDateTime.of(2024, 5, 2, 13, 0, 0)));
        cpuUsageRepository.save(new CpuUsageEntity(80.0, 50.0, 30.0, LocalDateTime.of(2024, 5, 3, 14, 0, 0)));

        // Insert data outside the range
        cpuUsageRepository.save(new CpuUsageEntity(90.0, 60.0, 40.0, LocalDateTime.of(2024, 4, 30, 12, 0, 0)));
        cpuUsageRepository.save(new CpuUsageEntity(100.0, 70.0, 50.0, LocalDateTime.of(2024, 5, 4, 13, 0, 0)));

        List<DailyUsageDTO> dailyUsageDTOs = cpuUsageRepository.findDailyUsage(startTime, endTime);

        assertThat(dailyUsageDTOs).isNotNull();
        assertThat(dailyUsageDTOs).hasSize(3);

        // Check if data is within the expected range and in ascending order of recordedAt
        for (DailyUsageDTO dailyUsageDTO : dailyUsageDTOs) {
            String recordedAt = dailyUsageDTO.getDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(recordedAt, formatter);
            assertThat(date).isBetween(startTime.toLocalDate(), endTime.toLocalDate());
        }
    }

    @Test
    public void testFindHourlyUsageWithAggregation() {
        LocalDate date = LocalDate.of(2024, 5, 1);
        LocalDateTime dateTime = LocalDateTime.of(2024, 5, 1, 12, 0, 0);
        cpuUsageRepository.save(new CpuUsageEntity(60.0, 30.0, 10.0, LocalDateTime.of(2024, 5, 1, 12, 2, 0)));
        cpuUsageRepository.save(new CpuUsageEntity(70.0, 40.0, 20.0, LocalDateTime.of(2024, 5, 1, 12, 3, 0)));
        cpuUsageRepository.save(new CpuUsageEntity(80.0, 50.0, 30.0, LocalDateTime.of(2024, 5, 1, 12, 4, 0)));

        LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);
        List<HourlyUsageDTO> hourlyUsageDTOs = cpuUsageRepository.findHourlyUsage(startDateTime, endDateTime);

        assertThat(hourlyUsageDTOs).isNotNull();
        assertThat(hourlyUsageDTOs).hasSize(1);
        HourlyUsageDTO hourlyUsageDTO = hourlyUsageDTOs.get(0);
        assertThat(hourlyUsageDTO.getRecordedAt()).isEqualTo(dateTime.toString());
        assertThat(hourlyUsageDTO.getMinUserUsage()).isEqualTo(60.0);
        assertThat(hourlyUsageDTO.getMaxUserUsage()).isEqualTo(80.0);
        assertThat(hourlyUsageDTO.getAvgUserUsage()).isEqualTo(70.0);
        assertThat(hourlyUsageDTO.getMinSystemUsage()).isEqualTo(30.0);
        assertThat(hourlyUsageDTO.getMaxSystemUsage()).isEqualTo(50.0);
        assertThat(hourlyUsageDTO.getAvgSystemUsage()).isEqualTo(40.0);
        assertThat(hourlyUsageDTO.getMinIdleUsage()).isEqualTo(10.0);
        assertThat(hourlyUsageDTO.getMaxIdleUsage()).isEqualTo(30.0);
        assertThat(hourlyUsageDTO.getAvgIdleUsage()).isEqualTo(20.0);
    }

    @Test
    public void testFindDailyUsageWithAggregation() {
        LocalDateTime startDate = LocalDateTime.of(2024, 5, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 5, 1, 23, 59, 59);
        CpuUsageEntity cpuUsageEntity = new CpuUsageEntity(50.0, 30.0, 20.0, startDate);
        cpuUsageRepository.save(cpuUsageEntity);

        List<DailyUsageDTO> dailyUsageDTOs = cpuUsageRepository.findDailyUsage(startDate, endDate);

        assertThat(dailyUsageDTOs).isNotNull();
        assertThat(dailyUsageDTOs).hasSize(1);
        DailyUsageDTO dailyUsageDTO = dailyUsageDTOs.get(0);
        assertThat(dailyUsageDTO.getDate()).isEqualTo("2024-05-01");
        assertThat(dailyUsageDTO.getMinUserUsage()).isEqualTo(50.0);
        assertThat(dailyUsageDTO.getMaxUserUsage()).isEqualTo(50.0);
        assertThat(dailyUsageDTO.getAvgUserUsage()).isEqualTo(50.0);
        assertThat(dailyUsageDTO.getMinSystemUsage()).isEqualTo(30.0);
        assertThat(dailyUsageDTO.getMaxSystemUsage()).isEqualTo(30.0);
        assertThat(dailyUsageDTO.getAvgSystemUsage()).isEqualTo(30.0);
        assertThat(dailyUsageDTO.getMinIdleUsage()).isEqualTo(20.0);
        assertThat(dailyUsageDTO.getMaxIdleUsage()).isEqualTo(20.0);
        assertThat(dailyUsageDTO.getAvgIdleUsage()).isEqualTo(20.0);
    }
}
