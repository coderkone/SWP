/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;
import model.GithubUser;
import java.io.IOException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
/**
 *
 * @author nguye
 */
public class GithubUtils {
    
    // 1. Lấy Access Token
    public static String getToken(String code) throws IOException {
        String response = Request.Post(Constants.GITHUB_LINK_GET_TOKEN)
                .addHeader("Accept", "application/json") // Bắt buộc để nhận JSON
                .bodyForm(Form.form()
                        .add("client_id", Constants.GITHUB_CLIENT_ID)
                        .add("client_secret", Constants.GITHUB_CLIENT_SECRET)
                        .add("redirect_uri", Constants.GITHUB_REDIRECT_URI)
                        .add("code", code)
                        .build())
                .execute().returnContent().asString();

        JsonObject jobj = new Gson().fromJson(response, JsonObject.class);
        return jobj.get("access_token").getAsString();
    }
    
    // 2. Lấy thông tin User
    public static GithubUser getUserInfo(String accessToken) throws IOException {
        String link = Constants.GITHUB_LINK_GET_USER_INFO;
        String response = Request.Get(link)
                .addHeader("Authorization", "Bearer " + accessToken) // Gửi token qua Header
                .addHeader("Accept", "application/json")
                .execute().returnContent().asString();
        
        return new Gson().fromJson(response, GithubUser.class);
    }
}
