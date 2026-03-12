/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.sql.Timestamp;

/**
 *
 * @author nguye
 */
public class BadgeDTO {

    private String name;
    private String type; // Lưu loại: Gold, Silver, Bronze
    private String description;
    private Timestamp earnedAt;

    public BadgeDTO() {
    }

    public BadgeDTO(String name, String type, String description, Timestamp earnedAt) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.earnedAt = earnedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(Timestamp earnedAt) {
        this.earnedAt = earnedAt;
    }
}
