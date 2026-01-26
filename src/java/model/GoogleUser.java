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
    public String verified_email;
    public String name;
    public String given_name;
    public String family_name;
    public String picture;

    @Override
    public String toString() {
        return "GoogleUser{" + "email='" + email + '\'' + ", name='" + name + '\'' + '}';
    }
}
