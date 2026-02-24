package control;

import dal.AnswerDAO;
import dal.QuestionDAO;
import dto.QuestionDTO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "AnswerController", urlPatterns = {"/answer/create"})
public class AnswerController extends HttpServlet {

    private final AnswerDAO answerDao = new AnswerDAO();
    private final QuestionDAO questionDao = new QuestionDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Get user from session
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("USER") == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

            Object userObj = session.getAttribute("USER");
            long userId = 0;
            
            // Extract userId from UserDTO
            if (userObj instanceof UserDTO) {
                UserDTO user = (UserDTO) userObj;
                userId = user.getUserId();
            } else {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

            // Get parameters
            String questionIdParam = request.getParameter("questionId");
            String answerBody = request.getParameter("answerBody");

            // Validate
            if (questionIdParam == null || !questionIdParam.matches("\\d+") || answerBody == null || answerBody.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionIdParam + "&error=Invalid input");
                return;
            }

            long questionId = Long.parseLong(questionIdParam);

            // Create answer
            long answerId = answerDao.createAnswer(questionId, userId, answerBody.trim(), "");

            if (answerId > 0) {
                response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId + "&success=Answer posted");
            } else {
                response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId + "&error=Failed to post answer");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
