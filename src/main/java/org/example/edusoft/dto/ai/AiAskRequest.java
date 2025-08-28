package org.example.edusoft.dto.ai;

public class AiAskRequest {
    private String question;
    private String userId;
    private String context;

    // getters and setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
} 