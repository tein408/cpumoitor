package com.cpumonitor.cpuusage.cpuusageDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CpuUsageDTO {
    private double userUsage;
    private double systemUsage;
    private double idleUsage;
    private LocalDateTime recordedAt;
}
