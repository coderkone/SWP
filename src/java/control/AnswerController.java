package control;

import dal.AnswerDAO;
import dal.QuestionDAO;
import dto.QuestionDTO;
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
            if (session == null || session.getAttribute("user") == null) {
                response.sendRedirect(request.getContextPath() + "/View/User/login.jsp");
                return;
            }

            Object userObj = session.getAttribute("user");
            long userId = 0;
            
            // Extract userId from user object (assuming it's a UserDTO or similar)
            if (userObj instanceof java.util.Map) {
                java.util.Map userMap = (java.util.Map) userObj;
                userId = ((Number) userMap.get("userId")).longValue();
            } else {
                // Try to get userId from object properties
                try {
                    userId = (Long) userObj.getClass().getMethod("getUserId").invoke(userObj);
                } catch (Exception e) {
                    response.sendRedirect(request.getContextPath() + "/View/User/login.jsp");
                    return;
                }
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
