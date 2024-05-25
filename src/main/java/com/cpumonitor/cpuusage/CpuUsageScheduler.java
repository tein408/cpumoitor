package com.cpumonitor.cpuusage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.cpumonitor.cpuusage.cpuusageDTO.CpuUsageDTO;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CpuUsageScheduler {

    private final CpuUsageService cpuUsageService;

    /**
     * CPU 사용량을 주기적으로 수집하는 메서드입니다.
     */
    @Scheduled(cron = "0 * * * * *")
    public void collectCpuUsage() {
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", "top -l 1 | grep -E \"^CPU\" | awk '{print $3, $5, $7}'");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();

            if (line != null) {
                String[] usageValues = line.split(" ");
                double userUsage = Double.parseDouble(usageValues[0].replace("%", ""));
                double systemUsage = Double.parseDouble(usageValues[1].replace("%", ""));
                double idleUsage = Double.parseDouble(usageValues[2].replace("%", ""));

                CpuUsageDTO cpuUsageDTO = new CpuUsageDTO(userUsage, systemUsage, idleUsage, LocalDateTime.now());
                cpuUsageService.saveCpuUsage(cpuUsageDTO);
            }
        } catch (Exception e) {
            log.error("Error while collecting CPU usage data", e);
        }
    }
}
