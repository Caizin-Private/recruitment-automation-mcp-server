import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import { analyzeResume } from "./tools/analyzeResumeTool.js";

dotenv.config();

const app = express();

app.use(cors());
app.use(express.json());

const PORT = process.env.PORT || 3001;

app.get("/health", (req, res) => {
    console.log("Health endpoint called");
    res.json({ status: "MCP Server Running" });
});

app.post("/tools/analyze_resume", async (req, res) => {

    try {

        console.log("Received analyze_resume request");

        const { resume_text, job_description } = req.body;

        const result = await analyzeResume(
            resume_text,
            job_description
        );

        console.log("Analysis completed");

        res.json(result);

    } catch (error) {

        console.error("Error:", error);

        res.status(500).json({
            error: error.message
        });
    }

});

app.listen(PORT, () => {
    console.log(`🚀 MCP Server running on port ${PORT}`);
});