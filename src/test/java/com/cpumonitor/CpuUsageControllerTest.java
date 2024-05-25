package com.cpumonitor;

import com.cpumonitor.cpuusage.CpuUsageController;
import com.cpumonitor.cpuusage.CpuUsageService;
import com.cpumonitor.cpuusage.cpuusageDTO.CpuUsageDTO;
import com.cpumonitor.cpuusage.cpuusageDTO.DailyUsageDTO;
import com.cpumonitor.cpuusage.cpuusageDTO.HourlyUsageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@WebMvcTest(CpuUsageController.class)
public class CpuUsageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CpuUsageService cpuUsageService;

    @BeforeEach
    void setup() {
        Mockito.reset(cpuUsageService);
    }

    @Test
    void getMinuteCpuUsage_ShouldReturnListOfCpuUsageDTO() throws Exception {
        LocalDateTime startDateTime = LocalDateTime.of(2024, 5, 1, 0, 0, 1);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 5, 1, 23, 59, 59);
        CpuUsageDTO cpuUsageDTO = new CpuUsageDTO(50.0, 30.0, 20.0, startDateTime);
        List<CpuUsageDTO> cpuUsageDTOList = Collections.singletonList(cpuUsageDTO);

        when(cpuUsageService.getMinuteCpuUsage(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(cpuUsageDTOList);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedDateTime = startDateTime.format(formatter);

        mockMvc.perform(get("/api/cpu-usage/minute")
                .param("startDateTime", startDateTime.toString())
                .param("endDateTime", endDateTime.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userUsage", is(cpuUsageDTO.getUserUsage())))
                .andExpect(jsonPath("$[0].systemUsage", is(cpuUsageDTO.getSystemUsage())))
                .andExpect(jsonPath("$[0].idleUsage", is(cpuUsageDTO.getIdleUsage())))
                .andExpect(jsonPath("$[0].recordedAt", is(formattedDateTime)));
    }

    @Test
    void getMinuteCpuUsage_NullStartDateTime_ReturnsDataForLastWeek() throws Exception {
        LocalDateTime currDateTime = LocalDateTime.now();
        List<CpuUsageDTO> mockData = Collections.singletonList(new CpuUsageDTO(10.0, 20.0, 70.0, currDateTime));

        when(cpuUsageService.getMinuteCpuUsage(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockData);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedDateTime = currDateTime.format(formatter);

        mockMvc.perform(get("/api/cpu-usage/minute")
                .param("endDateTime", "2024-05-01T23:59:59")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].recordedAt").value(formattedDateTime));
        verify(cpuUsageService, times(1)).getMinuteCpuUsage(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getMinuteCpuUsage_NullEndDateTime_ReturnsDataForLastWeek() throws Exception {
        LocalDateTime currDateTime = LocalDateTime.now();
        List<CpuUsageDTO> mockData = Collections.singletonList(new CpuUsageDTO(10.0, 20.0, 70.0, currDateTime));

        when(cpuUsageService.getMinuteCpuUsage(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockData);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedDateTime = currDateTime.format(formatter);

        mockMvc.perform(get("/api/cpu-usage/minute")
                .param("startDateTime", "2024-05-01T23:59:59")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].recordedAt").value(formattedDateTime));
        verify(cpuUsageService, times(1)).getMinuteCpuUsage(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getMinuteCpuUsage_BothDateTimesNull_ReturnsDataForLastWeek() throws Exception {
        LocalDateTime currDateTime = LocalDateTime.now();
        List<CpuUsageDTO> mockData = Collections.singletonList(new CpuUsageDTO(10.0, 20.0, 70.0, currDateTime));

        when(cpuUsageService.getMinuteCpuUsage(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockData);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedDateTime = currDateTime.format(formatter);

        mockMvc.perform(get("/api/cpu-usage/minute")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].recordedAt").value(formattedDateTime));
        verify(cpuUsageService, times(1)).getMinuteCpuUsage(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getMinuteCpuUsage_ShouldReturnInternalServerErrorOnException() throws Exception {
        when(cpuUsageService.getMinuteCpuUsage(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/cpu-usage/minute")
                .param("startDateTime", "2024-05-01T00:00:00")
                .param("endDateTime", "2024-05-01T23:59:59")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getHourlyCpuUsage_ShouldReturnListOfHourlyUsageDTO() throws Exception {
        LocalDate date = LocalDate.of(2024, 5, 1);
        LocalDateTime dateTime = LocalDateTime.of(2024, 5, 1, 0, 0, 1);
        HourlyUsageDTO hourlyUsageDTO = new HourlyUsageDTO(dateTime.toString(), 10.0, 20.0, 15.0, 5.0, 15.0, 10.0, 2.0,
                8.0, 5.0);
        List<HourlyUsageDTO> hourlyUsageDTOList = Collections.singletonList(hourlyUsageDTO);

        when(cpuUsageService.getHourlyCpuUsage(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(hourlyUsageDTOList);

        mockMvc.perform(get("/api/cpu-usage/hour")
                .param("date", date.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].recordedAt", is(hourlyUsageDTO.getRecordedAt())))
                .andExpect(jsonPath("$[0].minUserUsage", is(hourlyUsageDTO.getMinUserUsage())))
                .andExpect(jsonPath("$[0].maxUserUsage", is(hourlyUsageDTO.getMaxUserUsage())))
                .andExpect(jsonPath("$[0].avgUserUsage", is(hourlyUsageDTO.getAvgUserUsage())))
                .andExpect(jsonPath("$[0].minSystemUsage", is(hourlyUsageDTO.getMinSystemUsage())))
                .andExpect(jsonPath("$[0].maxSystemUsage", is(hourlyUsageDTO.getMaxSystemUsage())))
                .andExpect(jsonPath("$[0].avgSystemUsage", is(hourlyUsageDTO.getAvgSystemUsage())))
                .andExpect(jsonPath("$[0].minIdleUsage", is(hourlyUsageDTO.getMinIdleUsage())))
                .andExpect(jsonPath("$[0].maxIdleUsage", is(hourlyUsageDTO.getMaxIdleUsage())))
                .andExpect(jsonPath("$[0].avgIdleUsage", is(hourlyUsageDTO.getAvgIdleUsage())));
    }

    @Test
    void getHourlyCpuUsage_NoParams_ReturnsDataForLast3Months() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2024, 5, 1, 0, 0, 1);
        List<HourlyUsageDTO> mockData = Collections.singletonList(
                new HourlyUsageDTO(dateTime.toString(), 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0));

        when(cpuUsageService.getHourlyCpuUsage(any(LocalDate.class), any(LocalDate.class))).thenReturn(mockData);

        mockMvc.perform(get("/api/cpu-usage/hour")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recordedAt").value(dateTime.toString()));
        verify(cpuUsageService, times(1)).getHourlyCpuUsage(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getHourlyCpuUsage_ShouldReturnInternalServerErrorOnException() throws Exception {
        when(cpuUsageService.getHourlyCpuUsage(any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/cpu-usage/hour")
                .param("date", "2024-05-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getDailyCpuUsage_ShouldReturnListOfDailyUsageDTO() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 5, 1);
        LocalDate endDate = LocalDate.of(2024, 5, 31);
        DailyUsageDTO dailyUsageDTO = new DailyUsageDTO("2024-05-01", 10.0, 20.0, 15.0, 5.0, 15.0, 10.0, 2.0, 8.0, 5.0);
        List<DailyUsageDTO> dailyUsageDTOList = Collections.singletonList(dailyUsageDTO);

        when(cpuUsageService.getDailyCpuUsage(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(dailyUsageDTOList);

        mockMvc.perform(get("/api/cpu-usage/day")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].date", is(dailyUsageDTO.getDate())))
                .andExpect(jsonPath("$[0].minUserUsage", is(dailyUsageDTO.getMinUserUsage())))
                .andExpect(jsonPath("$[0].maxUserUsage", is(dailyUsageDTO.getMaxUserUsage())))
                .andExpect(jsonPath("$[0].avgUserUsage", is(dailyUsageDTO.getAvgUserUsage())))
                .andExpect(jsonPath("$[0].minSystemUsage", is(dailyUsageDTO.getMinSystemUsage())))
                .andExpect(jsonPath("$[0].maxSystemUsage", is(dailyUsageDTO.getMaxSystemUsage())))
                .andExpect(jsonPath("$[0].avgSystemUsage", is(dailyUsageDTO.getAvgSystemUsage())))
                .andExpect(jsonPath("$[0].minIdleUsage", is(dailyUsageDTO.getMinIdleUsage())))
                .andExpect(jsonPath("$[0].maxIdleUsage", is(dailyUsageDTO.getMaxIdleUsage())))
                .andExpect(jsonPath("$[0].avgIdleUsage", is(dailyUsageDTO.getAvgIdleUsage())));
    }

    @Test
    void getDailyCpuUsage_NullStartDate_ReturnsDataForLastYear() throws Exception {
        LocalDate currDate = LocalDate.now();
        List<DailyUsageDTO> mockData = Collections.singletonList(
                new DailyUsageDTO(currDate.toString(), 10.0, 20.0, 15.0, 5.0, 15.0, 10.0, 2.0, 8.0, 5.0));

        when(cpuUsageService.getDailyCpuUsage(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(mockData);

        mockMvc.perform(get("/api/cpu-usage/day")
                .param("endDate", "2024-05-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].date").value(currDate.toString()));
        verify(cpuUsageService, times(1)).getDailyCpuUsage(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getDailyCpuUsage_NullEndDate_ReturnsDataForLastYear() throws Exception {
        LocalDate currDate = LocalDate.now();
        List<DailyUsageDTO> mockData = Collections.singletonList(
                new DailyUsageDTO(currDate.toString(), 10.0, 20.0, 15.0, 5.0, 15.0, 10.0, 2.0, 8.0, 5.0));

        when(cpuUsageService.getDailyCpuUsage(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(mockData);

        mockMvc.perform(get("/api/cpu-usage/day")
                .param("startDate", "2024-05-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].date").value(currDate.toString()));
        verify(cpuUsageService, times(1)).getDailyCpuUsage(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getDailyCpuUsage_NoParams_ReturnsDataForLastYear() throws Exception {
        LocalDate currDate = LocalDate.now();
        List<DailyUsageDTO> mockData = Collections.singletonList(
                new DailyUsageDTO(currDate.toString(), 10.0, 20.0, 15.0, 5.0, 15.0, 10.0, 2.0, 8.0, 5.0));

        when(cpuUsageService.getDailyCpuUsage(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(mockData);

        mockMvc.perform(get("/api/cpu-usage/day")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].date").value(currDate.toString()));
        verify(cpuUsageService, times(1)).getDailyCpuUsage(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getDailyCpuUsage_ShouldReturnInternalServerErrorOnException() throws Exception {
        when(cpuUsageService.getDailyCpuUsage(any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/cpu-usage/day")
                .param("startDate", "2024-05-01")
                .param("endDate", "2024-05-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}
