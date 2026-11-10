package com.jobtracker.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobtracker.service.AiService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/ai/{id}") @RequiredArgsConstructor
public class AiController {
    private final AiService ai;

    @PostMapping("/analyze")
    public JsonNode analyze(@PathVariable Long id, Authentication a) { return ai.analyze(id, a.getName()); }

    @PostMapping("/interview-questions")
    public JsonNode questions(@PathVariable Long id, Authentication a) { return ai.interviewQuestions(id, a.getName()); }

    @PostMapping("/cover-letter")
    public Map<String, String> cover(@PathVariable Long id, Authentication a) { return ai.coverLetter(id, a.getName()); }

    @PostMapping("/follow-up")
    public Map<String, String> followUp(@PathVariable Long id, Authentication a) { return ai.followUp(id, a.getName()); }

    @PostMapping("/tailor-resume")
    public Map<String, String> tailor(@PathVariable Long id, Authentication a) { return ai.tailorResume(id, a.getName()); }
}
