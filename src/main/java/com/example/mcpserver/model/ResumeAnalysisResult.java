package com.example.mcpserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeAnalysisResult {


    private String jobId;
    private String candidateId;

    private Integer technical_score;
    private Integer experience_score;
    private Integer communication_score;
    private Integer leadership_score;

    private List<String> skills;
    private List<String> missing_skills;
    private List<String> risk_flags;

}