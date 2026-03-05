package control;

import util.GoogleUtils;
import model.GoogleUser;
import dal.UserDAO;
import model.User; // Đảm bảo bạn import đúng model User của bạn
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/auth/google/callback")
public class GoogleCallbackController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String code = request.getParameter("code");
            if (code == null || code.isEmpty()) {
                response.sendRedirect("login.jsp");
                return;
            }

            String accessToken = GoogleUtils.getToken(code);
            GoogleUser gUser = GoogleUtils.getUserInfo(accessToken);
            
            UserDAO dao = new UserDAO();
            User user = dao.loginWithGoogle(gUser);

            if (user != null) {
                request.getSession().setAttribute("user", user);
                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                response.sendRedirect(request.getContextPath() + "/View/User/login.jsp?error=GoogleLoginFailed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/View/User/login.jsp");
        }
    }
}