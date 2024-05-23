package com.cpumonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CpumonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CpumonitorApplication.class, args);
	}

}
