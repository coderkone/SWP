package control;

import dal.UserDAO;
import dal.ReportDAO;
import dto.UserDTO;
import dto.ReportDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name="DashboardController", urlPatterns={"/dashboard"})
public class DashboardController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final ReportDAO reportDAO = new ReportDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy thống kê cho dashboard
        int totalUsers = userDAO.getUserCount();
        int totalQuestions = userDAO.getQuestionCount();
        int totalAnswers = userDAO.getAnswerCount();

        // Lấy số report pending (thay cho hardcoded 0)
        int pendingReports = reportDAO.getReportCountByStatus("open");

        // Lấy 5 users mới nhất
        List<UserDTO> newestUsers = userDAO.getNewestUsers(5);

        // Lấy 5 reports mới nhất (thay cho fake data)
        List<ReportDTO> recentReports = reportDAO.getAllReports(1, 5);

        // Lấy data cho chart (7 ngày)
        List<Map<String, Object>> userTrend = userDAO.getUserRegistrationTrend(7);
        List<Map<String, Object>> questionTrend = userDAO.getQuestionTrend(7);

    //     // Set attributes cho JSP
    //     request.setAttribute("totalUsers", totalUsers);
    //     request.setAttribute("totalQuestions", totalQuestions);
    //     request.setAttribute("totalAnswers", totalAnswers);
    //     request.setAttribute("pendingReports", pendingReports);
    //     request.setAttribute("newestUsers", newestUsers);
    //     request.setAttribute("recentReports", recentReports);
    //     request.setAttribute("userTrend", userTrend);
    //     request.setAttribute("questionTrend", questionTrend);

    //     request.getRequestDispatcher("/View/Admin/dashboard.jsp").forward(request, response);
    // }
}


