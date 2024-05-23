package com.cpumonitor.cpuusage;

import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.sun.istack.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Table(name = "cpu_usage_log")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CpuUsageEntity {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;
    
    @Column(name = "user_usage")
    private Double userUsage;

    @Column(name = "system_usage")
    private Double systemUsage;

    @Column(name = "idle_usage")
    private Double idleUsage;

    @NotNull
    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Builder
    public CpuUsageEntity(double userUsage, double systemUsage, double idleUsage, LocalDateTime recordedAt) {
        this.userUsage = userUsage;
        this.systemUsage = systemUsage;
        this.idleUsage = idleUsage;
        this.recordedAt = recordedAt;
    }

}
