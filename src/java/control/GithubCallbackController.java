package control;

import util.GithubUtils;
import model.GithubUser;
import dal.UserDAO;
import model.User;
import dto.UserDTO;
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
                    response.sendRedirect(request.getContextPath() + "/View/User/login.jsp?error=GithubEmailPrivate");
                    return;
                }

                UserDAO dao = new UserDAO();
                User user = dao.loginWithGithub(gitUser);

                if (user != null) {
                    // Convert User to UserDTO and store with key "USER" (consistent with AuthController)
                    UserDTO userDTO = new UserDTO(
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole()
                    );
                    request.getSession().setAttribute("USER", userDTO);
                    response.sendRedirect(request.getContextPath() + "/View/User/home.jsp");
                } else {
                    response.sendRedirect(request.getContextPath() + "/View/User/login.jsp?error=GithubLoginFailed");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/View/User/login.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/View/User/login.jsp");
        }
    }
}