/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package control;

import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

/**
 *
 * @author Asus
 */
@WebServlet(name="UserFollowController", urlPatterns={"/user-follow"})
public class UserFollowController extends HttpServlet {
   
   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/users");
    } 

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        long myId = currentUser.getUserId();
        String action    = request.getParameter("action");
        String targetStr = request.getParameter("targetId");
        String filter    = request.getParameter("filter");
        String pageStr   = request.getParameter("page");

        try {
            long targetId = Long.parseLong(targetStr);

            // Không follow chính mình
            if (myId == targetId) {
                response.sendRedirect(request.getContextPath() + "/users");
                return;
            }

            UserDAO dao = new UserDAO();
            if ("follow".equals(action)) {
                dao.followUser(myId, targetId);
            } else if ("unfollow".equals(action)) {
                dao.unfollowUser(myId, targetId);
            }

            // Redirect về userprofile — giữ nguyên trang
            StringBuilder url = new StringBuilder(
                request.getContextPath() + "/userprofile?id=" + targetId
            );
            if (filter != null && !filter.isEmpty()) {
                url.append("&filter=").append(filter);
            }
            if (pageStr != null && !pageStr.isEmpty()) {
                url.append("&page=").append(pageStr);
            }
            response.sendRedirect(url.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/users");
        }
    }

    

}
