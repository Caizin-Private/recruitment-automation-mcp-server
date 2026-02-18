import openai from "../openaiClient.js";

export async function analyzeResume(resumeText, jobDescription) {

    const prompt = `
You are an expert technical recruiter.

Analyze the resume against job description.

Return ONLY valid JSON in this format:

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
${resumeText}

Job Description:
${jobDescription}
`;

    const response = await openai.responses.create({
        model: "gpt-4.1",
        input: prompt,
        temperature: 0
    });

    const text = response.output[0].content[0].text;

    return JSON.parse(text);
}