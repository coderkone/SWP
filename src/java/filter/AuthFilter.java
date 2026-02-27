package filter;

import dto.UserDTO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(filterName="Authfilter", urlPatterns={"/home", "/dashboard", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        UserDTO user = (session == null) ? null : (UserDTO) session.getAttribute("USER");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        // Kiểm tra user bị inactive không được login
        if ("inactive".equalsIgnoreCase(user.getStatus())) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/auth/login?error=inactive");
            return;
        }

        // Chặn non-admin vào dashboard và admin routes
        String path = request.getServletPath();
        if ((path.startsWith("/admin") || "/dashboard".equals(path))
                && !("admin".equalsIgnoreCase(user.getRole()))) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        chain.doFilter(req, res);
    }
}
