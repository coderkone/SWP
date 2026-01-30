package control;

import dal.QuestionDAO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "CreateQuestionController", urlPatterns = {"/ask"})
public class CreateQuestionController extends HttpServlet {

    private final QuestionDAO questionDao = new QuestionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/View/User/ask-question.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        UserDTO user = (session == null) ? null : (UserDTO) session.getAttribute("USER");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        try {
            String title = request.getParameter("title");
            String body = request.getParameter("body");
            String codeSnippet = request.getParameter("codeSnippet");
            String tags = request.getParameter("tags");

            // Validation
            if (title == null || title.trim().isEmpty() || title.length() < 10) {
                request.setAttribute("error", "Tiêu đề phải có ít nhất 10 ký tự.");
                request.getRequestDispatcher("/View/User/ask-question.jsp").forward(request, response);
                return;
            }

            if (body == null || body.trim().isEmpty() || body.length() < 20) {
                request.setAttribute("error", "Mô tả câu hỏi phải có ít nhất 20 ký tự.");
                request.getRequestDispatcher("/View/User/ask-question.jsp").forward(request, response);
                return;
            }

            // Create question with tags
            long questionId = questionDao.createQuestion(user.getUserId(), title, body, codeSnippet, tags);

            if (questionId > 0) {
                response.sendRedirect(request.getContextPath() + "/question?id=" + questionId);
            } else {
                request.setAttribute("error", "Lỗi tạo câu hỏi. Vui lòng thử lại.");
                request.getRequestDispatcher("/View/User/ask-question.jsp").forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Server error: " + e.getMessage());
            request.getRequestDispatcher("/View/User/ask-question.jsp").forward(request, response);
        }
    }
}
