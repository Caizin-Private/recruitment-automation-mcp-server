# Recruitment Automation MCP Server (Spring Boot)

This repo is a Spring Boot port of the original Node MCP server. It exposes two endpoints:

- `GET /health` — health check
- `POST /tools/analyze_resume` — analyzes a resume against a job description using OpenAI Responses API

Quick start


1. Set your OpenAI API key in environment variable `OPENAI_API_KEY`.

On Windows (PowerShell):

```powershell
$env:OPENAI_API_KEY = "sk-..."
./gradlew bootRun
```

Or build and run the JAR:

```powershell
./gradlew build
java -jar build/libs/recruitment-automation-mcp-server-1.0.0.jar
```

The server will listen on port `3001` by default (same as the Node server).

Request format for `/tools/analyze_resume`:

POST JSON body:

```
{
  "resume_text": "...",
  "job_description": "..."
}
```

The endpoint returns the OpenAI-parsed JSON matching the original Node output.
