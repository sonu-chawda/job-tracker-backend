package com.jobtracker.model;

import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity @Table(name = "job_applications")
@Getter @Setter @NoArgsConstructor
public class JobApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false) private String company;
    @Column(nullable = false) private String jobTitle;
    private String jobUrl;
    private String location;
    private String salary;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.WISHLIST;
    private LocalDate appliedDate;
    private LocalDate followUpDate;
    @Column(columnDefinition = "TEXT") private String notes;
    @Column(columnDefinition = "TEXT") private String jobDescription; // AI isi se kaam karega
    private LocalDateTime createdAt = LocalDateTime.now();
}
