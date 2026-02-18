package com.example.mcpserver.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnalyzeRequest {

    private String resumeText;
    private String jobDescription;

}