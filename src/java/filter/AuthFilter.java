package filter;

import dto.UserDTO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.User;
@WebFilter(filterName="Authfilter", urlPatterns={"/home", "/dashboard"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        // Nếu muốn chặn user vào dashboard:
        String path = request.getServletPath(); // /home or /dashboard
        if ("/dashboard".equals(path) && !("admin".equalsIgnoreCase(user.getRole()))) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        chain.doFilter(req, res);
    }
}
