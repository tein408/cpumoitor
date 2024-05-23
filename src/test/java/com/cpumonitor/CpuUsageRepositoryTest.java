package com.cpumonitor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import com.cpumonitor.cpuusage.CpuUsageEntity;
import com.cpumonitor.cpuusage.CpuUsageRepository;

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
}
