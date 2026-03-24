/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package control;

import dal.UserDAO;
import dto.QuestionDTO;
import dto.UserDTO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.User;

/**
 *
 * @author Asus
 */
@WebServlet(name="UserProfileController", urlPatterns={"/userprofile"})
public class UserProfileController extends HttpServlet {
   private static final int PAGE_SIZE = 10;

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect(request.getContextPath() + "/users");
            return;
        }
        long targetId;
        try {
            targetId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/users");
            return;
        }

        // 2. Session
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        boolean isLoggedIn = (currentUser != null);
        long myId = isLoggedIn ? currentUser.getUserId() : 0;

        // 3. Không xem profile chính mình
        if (isLoggedIn && myId == targetId) {
            response.sendRedirect(request.getContextPath() + "/users");
            return;
        }

        // 4. Filter + Page
        String filter = request.getParameter("filter");
        if (filter == null) filter = "popular"; // default

        int page = 1;
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null) page = Integer.parseInt(pageStr);
        } catch (NumberFormatException e) { page = 1; }
        if (page < 1) page = 1;

        // 5. Gọi DAO
        UserDAO dao = new UserDAO();

        // Thông tin user đang xem
        UserDTO profileUser = dao.getUserProfileById(targetId);
        if (profileUser == null) {
            response.sendRedirect(request.getContextPath() + "/users");
            return;
        }

        // Check đã follow chưa
        boolean isFollowing = isLoggedIn && dao.isFollowing(myId, targetId);

        // Danh sách TÔI đang follow (bên trái)
        List<UserDTO> followingList = isLoggedIn
                ? dao.getFollowingList(myId)
                : new java.util.ArrayList<>();

        // Câu hỏi của TARGET
        int totalQuestions = dao.countQuestionsByUser(targetId);
        int totalPages     = (int) Math.ceil((double) totalQuestions / PAGE_SIZE);
        List<QuestionDTO> questions = dao.getQuestionsByUser(targetId, filter, page);

        // 6. Truyền vào JSP
        request.setAttribute("profileUser",    profileUser);
        request.setAttribute("isFollowing",    isFollowing);
        request.setAttribute("isLoggedIn",     isLoggedIn);
        request.setAttribute("followingList",  followingList);
        request.setAttribute("questions",      questions);
        request.setAttribute("filter",         filter);
        request.setAttribute("currentPage",    page);
        request.setAttribute("totalPages",     totalPages);
        request.setAttribute("totalQuestions", totalQuestions);

        request.getRequestDispatcher("/View/User/userprofile.jsp")
               .forward(request, response);
    } 

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
    }

    

}
