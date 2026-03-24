/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Asus
 */
public class TokenStore {
    private static final Map<String, String> tokenStorage = new HashMap<>();
    public static void saveToken(String token, String email){ //Lưu Token , email user
        tokenStorage.put(token, email);
    }
    public static String getToken(String token){ // kiểm tra token hợp lệ 
        return tokenStorage.get(token);
    }
    public static void removeToken(String token){ //xóa token 
        tokenStorage.remove(token);
    }
}
