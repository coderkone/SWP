/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package control;
import util.GithubUtils;
import model.GithubUser;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author nguye
 */

@WebServlet("/auth/github/callback")
public class GithubCallbackController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String code = request.getParameter("code");
            if (code != null && !code.isEmpty()) {
                
                // 1. Lấy Token
                String accessToken = GithubUtils.getToken(code);
                
                // 2. Lấy User Info
                GithubUser githubUser = GithubUtils.getUserInfo(accessToken);
                
                // In ra kiểm tra
                System.out.println("GITHUB USER: " + githubUser);

                // 3. Lưu vào Session
                // Lưu ý: Để tiện hiển thị trên trang Home chung với Google, 
                // ta có thể dùng chung tên session là "user" hoặc tạo object User chung.
                HttpSession session = request.getSession();
                session.setAttribute("user", githubUser); // Lưu object GithubUser

                // 4. Về trang chủ
                response.sendRedirect(request.getContextPath() + "/View/User/home.jsp");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Nếu lỗi thì về login
        response.sendRedirect(request.getContextPath() + "/View/User/login.jsp");
    }
}