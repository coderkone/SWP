/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

/**
 *
 * @author nguye
 */
import util.Constants;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/auth/github")
public class LoginGithubController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Scope "user:email" để xin quyền lấy email
        String reqUrl = "https://github.com/login/oauth/authorize?client_id=" + Constants.GITHUB_CLIENT_ID 
                      + "&redirect_uri=" + Constants.GITHUB_REDIRECT_URI
                      + "&scope=user:email";
        response.sendRedirect(reqUrl);
    }
}
