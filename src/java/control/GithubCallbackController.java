package control;

import util.GithubUtils;
import model.GithubUser;
import dal.UserDAO;
import dto.UserDTO;
import model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/auth/github/callback")
public class GithubCallbackController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String code = request.getParameter("code");
            if (code != null && !code.isEmpty()) {
                
                String accessToken = GithubUtils.getToken(code);
                GithubUser gitUser = GithubUtils.getUserInfo(accessToken);
                
                if (gitUser.email == null) {
                    response.sendRedirect(request.getContextPath() + "/auth/login?error=GithubEmailPrivate");
                    return;
                }

                UserDAO dao = new UserDAO();
                User user = dao.loginWithGithub(gitUser);

                if (user != null) {
                    UserDTO userDTO = new UserDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getRole());
                    request.getSession().setAttribute("USER", userDTO);
                    request.getSession().setAttribute("user", user);
                    response.sendRedirect(request.getContextPath() + "/home");
                } else {
                    response.sendRedirect(request.getContextPath() + "/auth/login?error=GithubLoginFailed");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/auth/login");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/auth/login");
        }
    }
}