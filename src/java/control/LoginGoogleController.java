package control;

import util.Constants;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/auth/google")
public class LoginGoogleController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reqUrl = "https://accounts.google.com/o/oauth2/auth?scope=email profile&redirect_uri=" 
                + Constants.GOOGLE_REDIRECT_URI 
                + "&response_type=code&client_id=" + Constants.GOOGLE_CLIENT_ID 
                + "&approval_prompt=force";
        response.sendRedirect(reqUrl);
    }
}