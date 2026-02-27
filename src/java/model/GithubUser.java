/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author nguye
 */
public class GithubUser {
    public Long id;
    public String email;
    public String name;
    public String login;

    public String getDisplayName() {
        return (name != null && !name.isEmpty()) ? name : login;
    }
}