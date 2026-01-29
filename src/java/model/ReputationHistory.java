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
public class ReputationHistory {
    private long repId;
    private long userId;
    private String actionType; 
    private int value;         
    private Timestamp createdAt;

    public ReputationHistory() {
    }

    public ReputationHistory(long repId, long userId, String actionType, int value, Timestamp createdAt) {
        this.repId = repId;
        this.userId = userId;
        this.actionType = actionType;
        this.value = value;
        this.createdAt = createdAt;
    }

    public long getRepId() {
        return repId;
    }

    public void setRepId(long repId) {
        this.repId = repId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
}
