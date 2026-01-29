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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
        String linkUser = Constants.GITHUB_LINK_GET_USER_INFO;
        String responseUser = Request.Get(linkUser)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Accept", "application/json")
                .execute().returnContent().asString();
        GithubUser user = new Gson().fromJson(responseUser, GithubUser.class);
        
        if (user.email == null) {
            System.out.println("Email is null");
            
            String linkEmail = "https://api.github.com/user/emails";
            String responseEmail = Request.Get(linkEmail)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Accept", "application/json")
                    .execute().returnContent().asString();
            
            JsonArray emails = new Gson().fromJson(responseEmail, JsonArray.class);
            
            for (JsonElement e : emails) {
                JsonObject obj = e.getAsJsonObject();
                if (obj.get("primary").getAsBoolean()) {
                    user.email = obj.get("email").getAsString();
                    break;
                }
            }
        }
        return user;
    }
}
