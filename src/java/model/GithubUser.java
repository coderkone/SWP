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
    public long id;
    public String login;      // Tên đăng nhập (username)
    public String name;       // Tên hiển thị đầy đủ
    public String email;      // Email (có thể null nếu user ẩn)
    public String avatar_url; // Link ảnh đại diện

    @Override
    public String toString() {
        return "GithubUser{" + "login=" + login + ", email=" + email + '}';
    }
}
