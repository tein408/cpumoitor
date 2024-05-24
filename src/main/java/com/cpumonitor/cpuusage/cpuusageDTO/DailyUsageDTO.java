package com.cpumonitor.cpuusage.cpuusageDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyUsageDTO {
    private String date;
    private double minUserUsage;
    private double maxUserUsage;
    private double avgUserUsage;
    private double minSystemUsage;
    private double maxSystemUsage;
    private double avgSystemUsage;
    private double minIdleUsage;
    private double maxIdleUsage;
    private double avgIdleUsage;
}
