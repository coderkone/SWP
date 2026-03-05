/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author nguye
 */
public class SystemRuleDTO {

    private int requiredReputation; // Sẽ lấy từ cột type (ép kiểu sang số)
    private String privilegeDescription; // Sẽ lấy từ cột content

    public SystemRuleDTO() {
    }

    public SystemRuleDTO(int requiredReputation, String privilegeDescription) {
        this.requiredReputation = requiredReputation;
        this.privilegeDescription = privilegeDescription;
    }

    public int getRequiredReputation() {
        return requiredReputation;
    }

    public void setRequiredReputation(int requiredReputation) {
        this.requiredReputation = requiredReputation;
    }

    public String getPrivilegeDescription() {
        return privilegeDescription;
    }

    public void setPrivilegeDescription(String privilegeDescription) {
        this.privilegeDescription = privilegeDescription;
    }
}
