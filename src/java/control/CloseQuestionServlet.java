package control;

import dal.QuestionDetailDAO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import model.User;

@WebServlet(name = "CloseQuestionServlet", urlPatterns = {"/question/close"})
public class CloseQuestionServlet extends HttpServlet {

    private static final Set<String> ALLOWED_REASONS = new HashSet<>(Arrays.asList(
            "Duplicate question",
            "Needs more details",
            "Off-topic",
            "Opinion-based"
    ));

    private final QuestionDetailDAO questionDetailDAO = new QuestionDetailDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Object principal = (session == null) ? null : session.getAttribute("user");

        long userId;
        int reputation;
        if (principal instanceof UserDTO) {
            userId = ((UserDTO) principal).getUserId();
            reputation = ((UserDTO) principal).getReputation();
        } else if (principal instanceof User) {
            userId = ((User) principal).getUserId();
            reputation = ((User) principal).getReputation();
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String questionIdParam = safeTrim(request.getParameter("questionId"));
        String closeReason = safeTrim(request.getParameter("closeReason"));

        long questionId;
        try {
            questionId = Long.parseLong(questionIdParam);
            if (questionId <= 0) {
                throw new NumberFormatException("questionId must be positive");
            }
        } catch (Exception ex) {
            redirectWithError(request, response, questionIdParam, "Invalid question ID");
            return;
        }

        if (closeReason == null || !ALLOWED_REASONS.contains(closeReason)) {
            redirectWithError(request, response, String.valueOf(questionId), "Please select a valid close reason");
            return;
        }

        if (reputation < 3000) {
            redirectWithError(request, response, String.valueOf(questionId), "Only users with reputation >= 3000 can close questions");
            return;
        }

        try {
            boolean closed = questionDetailDAO.closeQuestion(questionId, userId, closeReason);
            if (!closed) {
                redirectWithError(request, response, String.valueOf(questionId), "Question is already closed or cannot be closed");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId + "&close=success");
        } catch (Exception e) {
            e.printStackTrace();
            redirectWithError(request, response, String.valueOf(questionId), "Unable to close question");
        }
    }

    private void redirectWithError(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String questionId,
                                   String errorMessage) throws IOException {
        String safeQuestionId = (questionId != null && questionId.matches("\\d+")) ? questionId : null;
        String encodedError = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        if (safeQuestionId != null) {
            response.sendRedirect(request.getContextPath() + "/question/detail?id=" + safeQuestionId + "&closeError=" + encodedError);
        } else {
            response.sendRedirect(request.getContextPath() + "/home?closeError=" + encodedError);
        }
    }

    private String safeTrim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}