package com.jobtracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity @Table(name = "resumes")
@Getter @Setter @NoArgsConstructor
public class Resume {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private Long userId;
    @Column(columnDefinition = "LONGTEXT") private String content;
    private LocalDateTime updatedAt = LocalDateTime.now();
}
