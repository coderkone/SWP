package control;

import dal.UserDAO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "AuthController", urlPatterns = {"/auth/login", "/auth/register"})
public class AuthController extends HttpServlet {

    private final UserDAO dao = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath(); // /auth/login hoặc /auth/register

        if ("/auth/register".equals(path)) {
            request.getRequestDispatcher("/View/User/register.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/View/User/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        try {
            if ("/auth/register".equals(path)) {
                handleRegister(request, response);
            } else {
                handleLogin(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Server error: " + e.getMessage());
            // trả về đúng trang đang submit
            if ("/auth/register".equals(path)) {
                request.getRequestDispatcher("/View/User/register.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/View/User/login.jsp").forward(request, response);
            }
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String pass = request.getParameter("password");
        String confirm = request.getParameter("confirm");

        if (username == null || username.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || pass == null || pass.length() < 8
                || !pass.equals(confirm)) {
            request.setAttribute("error", "Register failed: password >= 8 và confirm phải khớp.");
            request.getRequestDispatcher("/View/User/register.jsp").forward(request, response);
            return;
        }

        if (dao.usernameExists(username)) {
            request.setAttribute("error", "Username đã tồn tại.");
            request.getRequestDispatcher("/View/User/register.jsp").forward(request, response);
            return;
        }

        if (dao.emailExists(email)) {
            request.setAttribute("error", "Email đã tồn tại.");
            request.getRequestDispatcher("/View/User/register.jsp").forward(request, response);
            return;
        }

        // role mặc định trong DB của bạn là 'member' (default), ok
        dao.register(username, email, pass);

        // Sau đăng ký: chuyển về login và báo thành công
        response.sendRedirect(request.getContextPath() + "/auth/login?registered=1");
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        String email = request.getParameter("email");
        String pass = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || pass == null || pass.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email và password.");
            request.getRequestDispatcher("/View/User/login.jsp").forward(request, response);
            return;
        }

        UserDTO user = dao.login(email, pass);
        if (user == null) {
            request.setAttribute("error", "Sai email hoặc password.");
            request.getRequestDispatcher("/View/User/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("USER", user);

        String role = user.getRole(); // admin / moderator / member
        if (role != null && role.equalsIgnoreCase("admin")) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
