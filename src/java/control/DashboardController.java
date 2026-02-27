package control;

import dal.UserDAO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name="DashboardController", urlPatterns={"/dashboard"})
public class DashboardController extends HttpServlet {

    private final UserDAO dao = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy thống kê cho dashboard
        int totalUsers = dao.getUserCount();
        int totalQuestions = dao.getQuestionCount();
        int totalAnswers = dao.getAnswerCount();

        // Lấy 5 users mới nhất
        List<UserDTO> newestUsers = dao.getNewestUsers(5);

        // Set attributes cho JSP
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalQuestions", totalQuestions);
        request.setAttribute("totalAnswers", totalAnswers);
        request.setAttribute("newestUsers", newestUsers);

        request.getRequestDispatcher("/View/Admin/dashboard.jsp").forward(request, response);
    }
}
