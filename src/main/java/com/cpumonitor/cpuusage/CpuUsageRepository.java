package com.cpumonitor.cpuusage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CpuUsageRepository extends JpaRepository<CpuUsageEntity, Long> {
    
}
