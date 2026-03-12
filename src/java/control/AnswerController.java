package control;

import dal.AnswerDAO;
import dto.UserDTO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "AnswerController", urlPatterns = {"/answer/create"})
public class AnswerController extends HttpServlet {

    private final AnswerDAO answerDao = new AnswerDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setCharacterEncoding("UTF-8");

            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

            Object userObj = session.getAttribute("USER");
            if (userObj == null) {
                userObj = session.getAttribute("user");
            }

            long userId = -1;
            if (userObj instanceof UserDTO) {
                userId = ((UserDTO) userObj).getUserId();
            } else if (userObj instanceof User) {
                userId = ((User) userObj).getUserId();
            } else if (userObj instanceof java.util.Map) {
                java.util.Map<?, ?> userMap = (java.util.Map<?, ?>) userObj;
                Object idObj = userMap.get("userId");
                if (idObj instanceof Number) {
                    userId = ((Number) idObj).longValue();
                }
            } else if (userObj != null) {
                try {
                    Object idObj = userObj.getClass().getMethod("getUserId").invoke(userObj);
                    if (idObj instanceof Number) {
                        userId = ((Number) idObj).longValue();
                    }
                } catch (Exception ignored) {
                }
            }

            if (userId <= 0) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

            String questionIdParam = request.getParameter("questionId");
            String answerBody = request.getParameter("answerBody");

            if (questionIdParam == null || !questionIdParam.matches("\\d+")
                    || answerBody == null || answerBody.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/home?error=InvalidInput");
                return;
            }

            long questionId = Long.parseLong(questionIdParam);
            long answerId = answerDao.createAnswer(questionId, userId, answerBody.trim(), "");

            if (answerId > 0) {
                response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId + "&success=AnswerPosted");
            } else {
                response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId + "&error=FailedToPostAnswer");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}