package com.jobtracker.dto;

import com.jobtracker.model.*;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.List;

public class Dtos {
    public record RegisterRequest(@NotBlank String name, @Email @NotBlank String email,
                                  @NotBlank @Size(min = 6, message = "Password kam se kam 6 characters ka ho") String password) {}
    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record AuthResponse(String token, String name, String email, Role role) {}

    public record ApplicationRequest(@NotBlank String company, @NotBlank String jobTitle, String jobUrl,
                                     String location, String salary, ApplicationStatus status,
                                     LocalDate appliedDate, LocalDate followUpDate,
                                     String notes, String jobDescription) {}
    public record StatusRequest(@NotNull ApplicationStatus status) {}

    public record ApplicationResponse(Long id, String company, String jobTitle, String jobUrl, String location,
                                      String salary, ApplicationStatus status, LocalDate appliedDate,
                                      LocalDate followUpDate, String notes, String jobDescription,
                                      LocalDateTime createdAt) {
        public static ApplicationResponse from(JobApplication a) {
            return new ApplicationResponse(a.getId(), a.getCompany(), a.getJobTitle(), a.getJobUrl(), a.getLocation(),
                    a.getSalary(), a.getStatus(), a.getAppliedDate(), a.getFollowUpDate(), a.getNotes(),
                    a.getJobDescription(), a.getCreatedAt());
        }
    }
    public record HistoryResponse(ApplicationStatus oldStatus, ApplicationStatus newStatus, LocalDateTime changedAt) {}
    public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {}
    public record UserSummary(Long id, String name, String email, Role role, boolean active) {}
}
