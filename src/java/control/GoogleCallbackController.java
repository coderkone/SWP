package control;

import model.GoogleUser;
import util.GoogleUtils;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/auth/google/callback")
public class GoogleCallbackController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String code = request.getParameter("code");
        
        if (code == null || code.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/auth/login?error=GoogleLoginFailed");
            return;
        }

        try {
            String accessToken = GoogleUtils.getToken(code);
            GoogleUser googleUser = GoogleUtils.getUserInfo(accessToken);
            
            System.out.println("LOGGED IN USER: " + googleUser);

            HttpSession session = request.getSession();
            session.setAttribute("user", googleUser);

            response.sendRedirect(request.getContextPath() + "/home"); // Đảm bảo bạn có servlet /home hoặc trang index.jsp

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Login with Google failed!");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}