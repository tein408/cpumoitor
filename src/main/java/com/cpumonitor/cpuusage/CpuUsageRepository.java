package com.cpumonitor.cpuusage;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cpumonitor.cpuusage.cpuusageDTO.CpuUsageDTO;

@Repository
public interface CpuUsageRepository extends JpaRepository<CpuUsageEntity, Long> {

    @Query("SELECT NEW com.cpumonitor.cpuusage.cpuusageDTO.CpuUsageDTO(c.userUsage, c.systemUsage, c.idleUsage, c.recordedAt) FROM CpuUsageEntity c WHERE c.recordedAt BETWEEN :startTime AND :endTime ORDER BY c.recordedAt")
    List<CpuUsageDTO> getCpuUsageBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

}
