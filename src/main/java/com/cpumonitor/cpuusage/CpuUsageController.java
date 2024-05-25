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
    @ApiOperation(
        value = "분 단위 CPU 사용률 데이터 조회", 
        notes = "시작 시간과 종료 시간 사이의 분 단위 CPU 사용률 데이터를 조회합니다. <br> <b>파라미터를 둘 중 하나라도 입력하지 않은 경우 최근 1주의 데이터를 제공합니다.</b>"
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "성공적으로 조회됨", response = CpuUsageDTO.class, responseContainer = "List"),
        @ApiResponse(code = 500, message = "서버 오류", response = String.class)
    })
    @GetMapping("/cpu-usage/minute")
    public ResponseEntity<?> getMinuteCpuUsage(
            @ApiParam(value = "조회할 시작 날짜 및 시간 (예: 2024-05-01T00:00:00)", required = false, example = "2024-05-01T00:00:00") 
            @RequestParam(value = "startDateTime", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @ApiParam(value = "조회할 종료 날짜 및 시간 (예: 2024-05-01T23:59:59)", required = false, example = "2024-05-01T23:59:59") 
            @RequestParam(value = "endDateTime", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            endDateTime = LocalDateTime.now();
            startDateTime = endDateTime.minusWeeks(1);
        }
        try {
            log.info("Controller - Fetching minute-level CPU usage data from {} to {}", startDateTime, endDateTime);
            List<CpuUsageDTO> cpuUsageDTOs = cpuUsageService.getMinuteCpuUsage(startDateTime, endDateTime);
            return new ResponseEntity<>(cpuUsageDTOs, HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = String.format("Controller - Failed to fetch minute-level CPU usage data from %s to %s: %s",
                    startDateTime, endDateTime, e.getMessage());
            log.error(errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 시간 단위 CPU 사용률 데이터를 조회합니다.
     *
     * @param date 데이터를 조회할 날짜입니다.
     * @return 시간 단위 CPU 사용률 데이터 목록입니다.
     */
    @ApiOperation(
        value = "시간 단위 CPU 사용률 데이터 조회", 
        notes = "지정된 날짜의 시간 단위 CPU 사용률 데이터를 조회합니다. <br><b>파라미터를 입력하지 않은 경우 최근 3달의 데이터를 제공합니다.</b>"
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "성공적으로 조회됨", response = HourlyUsageDTO.class, responseContainer = "List"),
        @ApiResponse(code = 500, message = "서버 오류", response = String.class)
    })
    @GetMapping("/cpu-usage/hour")
    public ResponseEntity<?> getHourlyCpuUsage(
            @ApiParam(value = "조회할 날짜 (예: 2024-05-01)", required = false, example = "2024-05-01") 
            @RequestParam(value = "date", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate startDate = date;
        LocalDate endDate = date;
        if (date == null) {
            startDate = LocalDate.now().minusMonths(3);
            endDate = LocalDate.now();
        }
        try {
            log.info("Controller - Fetching hourly CPU usage data for date {}", date);
            List<HourlyUsageDTO> hourlyUsageDTOs = cpuUsageService.getHourlyCpuUsage(startDate, endDate);
            return new ResponseEntity<>(hourlyUsageDTOs, HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = String.format("Controller - Failed to fetch hourly CPU usage data for date %s: %s",
                    date, e.getMessage());
            log.error(errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 일 단위 CPU 사용률 데이터를 조회합니다.
     *
     * @param startDate 조회할 시작 날짜입니다.
     * @param endDate   조회할 종료 날짜입니다.
     * @return 일 단위 CPU 사용률 데이터 목록입니다.
     */
    @ApiOperation(
        value = "일 단위 CPU 사용률 데이터 조회", 
        notes = "지정된 시작 날짜와 종료 날짜 사이의 일 단위 CPU 사용률 데이터를 조회합니다. <br><b>파라미터를 둘 중 하나라도 입력하지 않은 경우 최근 1년의 데이터를 제공합니다.</b>"
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "성공적으로 조회됨", response = DailyUsageDTO.class, responseContainer = "List"),
        @ApiResponse(code = 500, message = "서버 오류", response = String.class)
    })
    @GetMapping("/cpu-usage/day")
    public ResponseEntity<?> getDailyCpuUsage(
            @ApiParam(value = "조회할 시작 날짜 (예: 2024-05-01)", required = false, example = "2024-05-01") 
            @RequestParam(value = "startDate", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @ApiParam(value = "조회할 종료 날짜 (예: 2024-05-31)", required = false, example = "2024-05-31") 
            @RequestParam(value = "endDate", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate == null || endDate == null) {
            endDate = LocalDate.now();
            startDate = endDate.minusYears(1);
        }
        try {
            log.info("Controller - Fetching daily CPU usage data from {} to {}", startDate, endDate);
            List<DailyUsageDTO> dailyUsageDTOs = cpuUsageService.getDailyCpuUsage(startDate, endDate);
            return new ResponseEntity<>(dailyUsageDTOs, HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = String.format("Controller - Failed to fetch daily CPU usage data from %s to %s: %s",
                    startDate, endDate, e.getMessage());
            log.error(errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
