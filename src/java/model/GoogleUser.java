/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author nguye
 */
public class GoogleUser {
    public String id;
    public String email;
    public String name;
    
    public GoogleUser() {}
    
    public GoogleUser(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}
