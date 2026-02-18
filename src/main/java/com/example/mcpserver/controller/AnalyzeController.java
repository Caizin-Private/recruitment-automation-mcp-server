package com.example.mcpserver.controller;

import com.example.mcpserver.model.AnalyzeRequest;
import com.example.mcpserver.model.ResumeAnalysisResult;
import com.example.mcpserver.service.ResumeAnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AnalyzeController {

    private final ResumeAnalyzerService analyzerService;

    @Autowired
    public AnalyzeController(ResumeAnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "MCP Server Running");
    }

    @PostMapping("/tools/analyze_resume")
    public ResponseEntity<?> analyze(@RequestBody AnalyzeRequest request) {

        try {

            System.out.println("ResumeText received: " + (request.getResumeText() != null));
            System.out.println("JobDescription received: " + (request.getJobDescription() != null));

            ResumeAnalysisResult result =
                    analyzerService.analyze(
                            request.getResumeText(),
                            request.getJobDescription()
                    );

            return ResponseEntity.ok(result);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(500)
                    .body(Map.of(
                            "error", e.getMessage(),
                            "type", e.getClass().getName()
                    ));
        }
    }
}
