package control;

import dal.ReportDAO;
import dal.TagDAO;
import dal.UserDAO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import model.Report;

@WebServlet(name="DashboardController", urlPatterns={"/dashboard"})
public class DashboardController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final TagDAO tagDAO = new TagDAO();
    // Thêm khởi tạo đối tượng ReportDAO để sửa lỗi "non-static method"
    private final ReportDAO reportDAO = new ReportDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy thống kê cho dashboard
            int totalUsers = userDAO.getUserCount();
            int totalQuestions = userDAO.getQuestionCount();
            int totalAnswers = userDAO.getAnswerCount();
            List<Map<String, Object>> questionByTagCurrentMonth = userDAO.getCurrentMonthQuestionCountByTag(8);
            List<Map<String, Object>> userTrend = userDAO.getUserRegistrationTrend(7);
            List<Map<String, Object>> questionTrend = userDAO.getQuestionTrend(7);

            // Lấy 5 users mới nhất
            List<UserDTO> newestUsers = userDAO.getNewestUsers(5);
            
            // Lấy báo cáo cho dashboard
            // Gọi qua đối tượng reportDAO
            int totalReports = reportDAO.getFilteredReportCount("open", null, null);
            List<Report> recentReports = reportDAO.getRecentReports(5);

            // Set attributes cho JSP
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("totalQuestions", totalQuestions);
            request.setAttribute("totalAnswers", totalAnswers);
            request.setAttribute("newestUsers", newestUsers);
            request.setAttribute("questionByTagCurrentMonth", questionByTagCurrentMonth);
            request.setAttribute("totalReports", totalReports);
            request.setAttribute("recentReports", recentReports);
            request.setAttribute("userTrend", userTrend);
            request.setAttribute("questionTrend", questionTrend);

            request.getRequestDispatcher("/View/Admin/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            // Xử lý lỗi (Exception) bắt buộc phải catch
            e.printStackTrace();
            
            // Nếu có lỗi, bạn có thể set một attribute báo lỗi và forward về trang dashboard
            // hoặc chuyển hướng sang một trang báo lỗi chung của dự án
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi tải dữ liệu dashboard.");
            request.getRequestDispatcher("/View/Admin/dashboard.jsp").forward(request, response);
        }
    }
}