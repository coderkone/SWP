package control;

import dal.TagDAO;
import dal.UserDAO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name="DashboardController", urlPatterns={"/dashboard"})
public class DashboardController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final TagDAO tagDAO = new TagDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy thống kê cho dashboard
        int totalUsers = userDAO.getUserCount();
        int totalQuestions = userDAO.getQuestionCount();
        int totalAnswers = userDAO.getAnswerCount();
        List<Map<String, Object>> questionByTagCurrentMonth = userDAO.getCurrentMonthQuestionCountByTag(8);

        // Lấy 5 users mới nhất
        List<UserDTO> newestUsers = userDAO.getNewestUsers(5);

        // Set attributes cho JSP
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalQuestions", totalQuestions);
        request.setAttribute("totalAnswers", totalAnswers);
        request.setAttribute("newestUsers", newestUsers);
        request.setAttribute("questionByTagCurrentMonth", questionByTagCurrentMonth);

        request.getRequestDispatcher("/View/Admin/dashboard.jsp").forward(request, response);
    }
}
