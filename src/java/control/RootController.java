package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "RootController", urlPatterns = {"/"})
public class RootController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Object user = (session == null) ? null : session.getAttribute("USER");
        
        if (user != null) {
            // User logged in, redirect to home
            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            // Not logged in, redirect to login
            response.sendRedirect(request.getContextPath() + "/auth/login");
        }
    }
}
