package com.jobtracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity @Table(name = "status_history")
@Getter @Setter @NoArgsConstructor
public class StatusHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "application_id")
    private JobApplication application;
    @Enumerated(EnumType.STRING) private ApplicationStatus oldStatus;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ApplicationStatus newStatus;
    private LocalDateTime changedAt = LocalDateTime.now();
}
