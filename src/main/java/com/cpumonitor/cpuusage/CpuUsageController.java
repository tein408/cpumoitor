package com.cpumonitor.cpuusage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpumonitor.cpuusage.cpuusageDTO.CpuUsageDTO;
import com.cpumonitor.cpuusage.cpuusageDTO.DailyUsageDTO;
import com.cpumonitor.cpuusage.cpuusageDTO.HourlyUsageDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * CPU 사용률 데이터를 처리하는 컨트롤러 클래스입니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Api(value = "CpuUsageController", tags = "CPU 사용 데이터 API")
public class CpuUsageController {

    private final CpuUsageService cpuUsageService;

    /**
     * 분 단위 CPU 사용률 데이터를 조회합니다.
     *
     * @param startDateTime 시작 날짜 및 시간입니다.
     * @param endDateTime   종료 날짜 및 시간입니다.
     * @return CPU 사용률 데이터 목록입니다.
     */
    @ApiOperation(value = "분 단위 CPU 사용률 데이터 조회", notes = "시작 시간과 종료 시간 사이의 분 단위 CPU 사용률 데이터를 조회합니다. 입력하지 않은 경우 최근 1주의 데이터를 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 조회됨"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/cpu-usage/minute")
    public ResponseEntity<List<CpuUsageDTO>> getMinuteCpuUsage(
            @ApiParam(value = "조회할 시작 날짜 및 시간 (예: 2024-05-01T00:00:00)", required = false, example = "2024-05-01T00:00:00") @RequestParam(value = "startDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @ApiParam(value = "조회할 종료 날짜 및 시간 (예: 2024-05-01T23:59:59)", required = false, example = "2024-05-01T23:59:59") @RequestParam(value = "endDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            endDateTime = LocalDateTime.now();
            startDateTime = endDateTime.minusWeeks(1);
        }
        try {
            log.info("Controller - Fetching minute-level CPU usage data from {} to {}", startDateTime, endDateTime);
            List<CpuUsageDTO> cpuUsageDTOs = cpuUsageService.getMinuteCpuUsage(startDateTime, endDateTime);
            return new ResponseEntity<>(cpuUsageDTOs, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Controller - Failed to fetch minute-level CPU usage data from {} to {}: {}", startDateTime,
                    endDateTime, e.getMessage());
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
