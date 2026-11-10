package com.jobtracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.model.*;
import com.jobtracker.repository.*;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AiService {
    private final AiClient ai;
    private final JobApplicationRepository appRepo;
    private final UserRepository userRepo;
    private final ResumeRepository resumeRepo;

    private static final String RULES = "Use ONLY facts present in the resume. Never invent skills, companies, degrees, numbers or experience. ";
    private static final String JSON_ONLY = "Respond ONLY with valid JSON. ";

    private User user(String email) { return userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found")); }

    private JobApplication app(Long id, String email) {
        return appRepo.findByIdAndUserId(id, user(email).getId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id " + id));
    }

    private String resume(String email) {
        return resumeRepo.findByUserId(user(email).getId()).map(Resume::getContent).filter(c -> !c.isBlank())
                .map(c -> cut(c)).orElseThrow(() -> new IllegalArgumentException("Please save your resume first (Resume page)."));
    }

    private String jd(JobApplication a) {
        if (a.getJobDescription() == null || a.getJobDescription().isBlank())
            throw new IllegalArgumentException("Add the job description to this application first.");
        return cut(a.getJobDescription());
    }

    private String cut(String s) { return s.length() > 12000 ? s.substring(0, 12000) : s; }

    private String context(JobApplication a, String email) {
        return "RESUME:\n" + resume(email) + "\n\nJOB: " + a.getJobTitle() + " at " + a.getCompany() + "\nJOB DESCRIPTION:\n" + jd(a);
    }

    public JsonNode analyze(Long id, String email) {
        JobApplication a = app(id, email);
        return ai.chatJson("You are a strict technical recruiter. " + JSON_ONLY,
                context(a, email) + "\n\nCompare the resume with the job. Return JSON: "
                        + "{\"matchScore\": number 0-100, \"requiredSkills\": [string], \"matchedSkills\": [string], "
                        + "\"missingSkills\": [string], \"strengths\": [string], \"suggestions\": [string], \"summary\": string}");
    }

    public JsonNode interviewQuestions(Long id, String email) {
        JobApplication a = app(id, email);
        return ai.chatJson("You are a senior interviewer. " + JSON_ONLY,
                context(a, email) + "\n\nGenerate 8 interview questions for this candidate and job. Return JSON: "
                        + "{\"questions\": [{\"question\": string, \"category\": \"Technical|Project|Behavioral\", "
                        + "\"difficulty\": \"Easy|Medium|Hard\", \"tip\": string}]}");
    }

    public Map<String, String> coverLetter(Long id, String email) {
        JobApplication a = app(id, email);
        return Map.of("content", ai.chat("You write concise, genuine cover letters for freshers in India. " + RULES + "No placeholders, plain text.",
                context(a, email) + "\n\nWrite a cover letter of about 200 words.", false));
    }

    public Map<String, String> followUp(Long id, String email) {
        JobApplication a = app(id, email);
        return Map.of("content", ai.chat("You write polite, short follow-up emails to recruiters. Plain text with a subject line.",
                "Write a follow-up email for my application as " + a.getJobTitle() + " at " + a.getCompany()
                        + (a.getAppliedDate() != null ? ", applied on " + a.getAppliedDate() : "") + ". Keep it under 90 words.", false));
    }

    public Map<String, String> tailorResume(Long id, String email) {
        JobApplication a = app(id, email);
        return Map.of("content", ai.chat("You are a professional resume writer. " + RULES
                        + "You may reword, reorder and emphasise existing content and use keywords from the job only where the resume already supports them. Plain text.",
                context(a, email) + "\n\nRewrite the resume tailored to this job. Do not add anything that is not in the resume.", false));
    }
}
