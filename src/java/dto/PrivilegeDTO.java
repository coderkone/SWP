/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author nguye
 */
public class PrivilegeDTO {

    private int id;
    private String name;
    private String description;
    private int requiredReputation;

    public PrivilegeDTO() {
    }

    public PrivilegeDTO(int id, String name, String description, int requiredReputation) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredReputation = requiredReputation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRequiredReputation() {
        return requiredReputation;
    }

    public void setRequiredReputation(int requiredReputation) {
        this.requiredReputation = requiredReputation;
    }
}
