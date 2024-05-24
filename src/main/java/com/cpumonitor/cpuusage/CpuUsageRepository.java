package com.cpumonitor.cpuusage;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cpumonitor.cpuusage.cpuusageDTO.CpuUsageDTO;
import com.cpumonitor.cpuusage.cpuusageDTO.HourlyUsageDTO;

@Repository
public interface CpuUsageRepository extends JpaRepository<CpuUsageEntity, Long> {

    @Query("SELECT NEW com.cpumonitor.cpuusage.cpuusageDTO.CpuUsageDTO(c.userUsage, c.systemUsage, c.idleUsage, c.recordedAt) FROM CpuUsageEntity c WHERE c.recordedAt BETWEEN :startTime AND :endTime ORDER BY c.recordedAt")
    List<CpuUsageDTO> getCpuUsageBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT NEW com.cpumonitor.cpuusage.cpuusageDTO.HourlyUsageDTO(CONCAT(YEAR(c.recordedAt), '-', LPAD(MONTH(c.recordedAt), 2, '0'), '-', LPAD(DAY(c.recordedAt), 2, '0'), 'T', LPAD(HOUR(c.recordedAt), 2, '0'), ':00'), MIN(c.userUsage), MAX(c.userUsage), ROUND(AVG(c.userUsage), 2), MIN(c.systemUsage), MAX(c.systemUsage), ROUND(AVG(c.systemUsage), 2), MIN(c.idleUsage), MAX(c.idleUsage), ROUND(AVG(c.idleUsage), 2)) FROM CpuUsageEntity c WHERE c.recordedAt BETWEEN :startDate AND :endDate GROUP BY CONCAT(YEAR(c.recordedAt), '-', LPAD(MONTH(c.recordedAt), 2, '0'), '-', LPAD(DAY(c.recordedAt), 2, '0'), 'T', LPAD(HOUR(c.recordedAt), 2, '0'), ':00') ORDER BY 1")
    List<HourlyUsageDTO> findHourlyUsage(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
