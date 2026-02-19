package com.example.mcpserver.service;

import com.example.mcpserver.model.ResumeAnalysisResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResumeAnalyzerService {

    private final OpenAIClientService openAIClientService;
    private final ObjectMapper mapper;

    @Autowired
    public ResumeAnalyzerService(OpenAIClientService openAIClientService,
                                 ObjectMapper mapper) {
        this.openAIClientService = openAIClientService;
        this.mapper = mapper;
    }

    public ResumeAnalysisResult analyze(
            String resumeText,
            String jobDescription,
            String jobId,
            String candidateId
    ) throws Exception {

        if (resumeText == null || jobDescription == null) {
            throw new IllegalArgumentException("ResumeText or JobDescription is null");
        }

        String prompt = """
You are an expert technical recruiter.

Analyze the resume against the job description.

Return ONLY valid JSON. No explanation. No markdown.

{
  "technical_score": number (0-10),
  "experience_score": number (0-10),
  "communication_score": number (0-10),
  "leadership_score": number (0-10),
  "skills": [],
  "missing_skills": [],
  "risk_flags": []
}

Resume:
""" + resumeText + """

Job Description:
""" + jobDescription;

        String responseText =
                openAIClientService.getResponseText(prompt);

        if (responseText == null || responseText.isEmpty()) {
            throw new RuntimeException("Empty response from OpenAI");
        }

        responseText = cleanJson(responseText);

        ResumeAnalysisResult result =
                mapper.readValue(responseText, ResumeAnalysisResult.class);

        // IMPORTANT: set identifiers here
        result.setJobId(jobId);
        result.setCandidateId(candidateId);

        return result;
    }

    private String cleanJson(String text) {

        text = text.trim();

        if (text.startsWith("```")) {
            text = text.replace("```json", "")
                    .replace("```", "")
                    .trim();
        }

        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");

        if (start != -1 && end != -1) {
            text = text.substring(start, end + 1);
        }

        return text;
    }
}