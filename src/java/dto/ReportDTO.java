package dto;

import java.sql.Timestamp;

public class ReportDTO {
    // // Report fields
    // private long reportId;
    // private long reporterId;
    // private String targetType;
    // private long targetId;
    // private String reason;
    // private String status;
    // private Timestamp createdAt;

    // // Reporter info
    // private String reporterName;
    // private String reporterEmail;

    // // Target content info
    // private String targetTitle;
    // private String targetBody;
    // private String targetAuthorName;
    // private long targetAuthorId;
    // private long questionId;

    // public ReportDTO() {
    // }

    // Getters and Setters
    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    public long getReporterId() {
        return reporterId;
    }

    public void setReporterId(long reporterId) {
        this.reporterId = reporterId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterEmail) {
        this.reporterEmail = reporterEmail;
    }

    public String getTargetTitle() {
        return targetTitle;
    }

    public void setTargetTitle(String targetTitle) {
        this.targetTitle = targetTitle;
    }

    public String getTargetBody() {
        return targetBody;
    }

    public void setTargetBody(String targetBody) {
        this.targetBody = targetBody;
    }

    public String getTargetAuthorName() {
        return targetAuthorName;
    }

    public void setTargetAuthorName(String targetAuthorName) {
        this.targetAuthorName = targetAuthorName;
    }

    public long getTargetAuthorId() {
        return targetAuthorId;
    }

    public void setTargetAuthorId(long targetAuthorId) {
        this.targetAuthorId = targetAuthorId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    // Helper methods
    public String getTargetTypeDisplay() {
        if (targetType == null) return "";
        switch (targetType) {
            case "question": return "Cau hoi";
            case "answer": return "Tra loi";
            case "comment": return "Binh luan";
            default: return targetType;
        }
    }

    public String getStatusDisplay() {
        if (status == null) return "";
        return "open".equals(status) ? "Cho xu ly" : "Da xu ly";
    }

    public String getReasonTruncated(int maxLength) {
        if (reason == null) return "";
        if (reason.length() <= maxLength) return reason;
        return reason.substring(0, maxLength) + "...";
    }
}
