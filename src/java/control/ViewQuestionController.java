package control;

import dal.QuestionDAO;
import dto.QuestionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "ViewQuestionController", urlPatterns = {"/question"})
public class ViewQuestionController extends HttpServlet {

    private final QuestionDAO questionDao = new QuestionDAO();
    private static final long VIEW_COOLDOWN_MS = 30 * 60 * 1000; // 30 minutes

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            long questionId = Long.parseLong(idParam);
            QuestionDTO question = questionDao.getQuestionById(questionId);

            if (question == null) {
                request.setAttribute("error", "Câu hỏi không tồn tại.");
                request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
                return;
            }

            // Check if view should be counted
            HttpSession session = request.getSession(true);
            String viewKey = "view_" + questionId;
            Long lastViewTime = (Long) session.getAttribute(viewKey);
            long currentTime = System.currentTimeMillis();

            // Count view only if last view was > 30 minutes ago
            if (lastViewTime == null || (currentTime - lastViewTime) > VIEW_COOLDOWN_MS) {
                questionDao.incrementViewCount(questionId);
                session.setAttribute(viewKey, currentTime);
            }

            request.setAttribute("question", question);
            request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID câu hỏi không hợp lệ.");
            request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi server: " + e.getMessage());
            request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
        }
    }
}
