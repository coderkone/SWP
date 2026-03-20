package control;

import dal.QuestionDetailDAO;
import dto.QuestionDTO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

@WebServlet(name = "DeleteQuestionController", urlPatterns = {"/question/delete"})
public class DeleteQuestionController extends HttpServlet {

    private final QuestionDetailDAO questionDetailDao = new QuestionDetailDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || !idParam.matches("\\d+")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid question id.");
                return;
            }

            SessionUser sessionUser = getSessionUser(request);
            if (sessionUser == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to delete this question.");
                return;
            }

            long questionId = Long.parseLong(idParam);
            QuestionDTO question = questionDetailDao.getQuestionById(questionId);
            if (question == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Question not found.");
                return;
            }

            if (question.isIsClosed()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Question is closed. Delete is disabled.");
                return;
            }

            boolean isOwner = sessionUser.userId == question.getUserId();
            boolean isAdmin = sessionUser.role != null && sessionUser.role.equalsIgnoreCase("admin");
            if (!isOwner && !isAdmin) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to delete this question.");
                return;
            }

            boolean deleted = questionDetailDao.softDeleteQuestion(questionId, sessionUser.userId);
            if (!deleted) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot delete question.");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/home");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private SessionUser getSessionUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object userObj = session.getAttribute("user");
        if (userObj instanceof UserDTO) {
            UserDTO user = (UserDTO) userObj;
            return new SessionUser(user.getUserId(), user.getRole());
        }
        if (userObj instanceof User) {
            User user = (User) userObj;
            return new SessionUser(user.getUserId(), user.getRole());
        }
        return null;
    }

    private static class SessionUser {
        private final long userId;
        private final String role;

        private SessionUser(long userId, String role) {
            this.userId = userId;
            this.role = role;
        }
    }
}