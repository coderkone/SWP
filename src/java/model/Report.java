/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.sql.Timestamp;
/**
 *
 * @author nguye
 */
public class Report {
    private long reportId;
    private long reporterId;
    private String targetType; // "question", "answer", "comment"
    private long targetId;
    private String reason;
    private String note;
    private String status; // "open", "resolved"
    private Timestamp createdAt;
    private String reporterUsername; 
    private String reporterEmail;
    private String reporterName;
    private long questionId;
    private String targetAuthorName;
    private String targetTitle;
    private String targetBody;

    public Report() {
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterEmail) {
        this.reporterEmail = reporterEmail;
    }

    public String getReporterName() {
        return reporterName != null ? reporterName : reporterUsername;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public String getTargetAuthorName() {
        return targetAuthorName;
    }

    public void setTargetAuthorName(String targetAuthorName) {
        this.targetAuthorName = targetAuthorName;
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

    public String getReasonTruncated(int limit) {
        if (reason == null) return "";
        if (reason.length() <= limit) return reason;
        return reason.substring(0, limit) + "...";
    }

    public Report(long reportId, long reporterId, String targetType, long targetId, String reason, String status, Timestamp createdAt) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Report(long reporterId, String targetType, long targetId, String reason, String note) {
        this.reporterId = reporterId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.note = note;
    }

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getReporterUsername() {
        return reporterUsername;
    }

    public void setReporterUsername(String reporterUsername) {
        this.reporterUsername = reporterUsername;
    }  
}