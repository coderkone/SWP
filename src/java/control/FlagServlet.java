    package control;

import dal.ReportDAO;
import dal.QuestionDAO;
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
import model.Report;
import model.User;

@WebServlet(name = "FlagServlet", urlPatterns = {"/flag/submit", "/flag/report"})
public class FlagServlet extends HttpServlet {

    private static final Set<String> ALLOWED_POST_TYPES =
            new HashSet<>(Arrays.asList("question", "answer"));

    private static final Set<String> ALLOWED_REASONS =
            new HashSet<>(Arrays.asList(
                    "Spam",
                    "Harassment or abusive language",
                    "Misleading content",
                    "Other"
            ));

    private final ReportDAO reportDao = new ReportDAO();
    private final QuestionDAO questionDao = new QuestionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Object principal = (session == null) ? null : session.getAttribute("user");

        long userId;
        if (principal instanceof UserDTO) {
            userId = ((UserDTO) principal).getUserId();
        } else if (principal instanceof User) {
            userId = ((User) principal).getUserId();
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String postType = safeTrim(request.getParameter("postType"));
        String postIdParam = safeTrim(request.getParameter("postId"));
        String questionIdParam = safeTrim(request.getParameter("questionId"));
        String answerIdParam = safeTrim(request.getParameter("answerId"));

        if (postType == null || !ALLOWED_POST_TYPES.contains(postType)) {
            redirectWithError(request, response, questionIdParam, "Invalid post type");
            return;
        }

        long postId;
        long questionId;
        try {
            postId = Long.parseLong(postIdParam);
            questionId = Long.parseLong(questionIdParam);
            if (postId <= 0 || questionId <= 0) {
                throw new NumberFormatException("IDs must be positive");
            }
        } catch (Exception ex) {
            redirectWithError(request, response, questionIdParam, "Invalid report target");
            return;
        }

        try {
            if (questionDao.isQuestionClosed(questionId)) {
                redirectWithError(request, response, questionIdParam, "Question is closed");
                return;
            }
        } catch (Exception ex) {
            redirectWithError(request, response, questionIdParam, "Unable to validate question status");
            return;
        }

        try {
            if (reportDao.isOwnerOfPost(userId, postType, postId)) {
                redirectWithError(request, response, questionIdParam, "You cannot report your own content");
                return;
            }
        } catch (Exception ex) {
            redirectWithError(request, response, questionIdParam, "Unable to validate report target");
            return;
        }

        request.setAttribute("postType", postType);
        request.setAttribute("postId", postId);
        request.setAttribute("questionId", questionId);
        request.setAttribute("answerId", answerIdParam);
        request.getRequestDispatcher("/View/User/flag-report.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Object principal = (session == null) ? null : session.getAttribute("user");

        long userId;
        if (principal instanceof UserDTO) {
            userId = ((UserDTO) principal).getUserId();
        } else if (principal instanceof User) {
            userId = ((User) principal).getUserId();
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String postType = safeTrim(request.getParameter("postType"));
        String postIdParam = safeTrim(request.getParameter("postId"));
        String reason = safeTrim(request.getParameter("reason"));
        String note = safeTrim(request.getParameter("note"));
        String questionIdParam = safeTrim(request.getParameter("questionId"));
        String answerIdParam = safeTrim(request.getParameter("answerId"));

        if (note != null && note.length() > 500) {
            note = note.substring(0, 500);
        }

        if (postType == null || !ALLOWED_POST_TYPES.contains(postType)) {
            redirectWithError(request, response, questionIdParam, "Invalid post type");
            return;
        }

        if (reason == null || !ALLOWED_REASONS.contains(reason)) {
            redirectWithError(request, response, questionIdParam, "Please select a valid reason");
            return;
        }

        long postId;
        try {
            postId = Long.parseLong(postIdParam);
            if (postId <= 0) {
                throw new NumberFormatException("Post ID must be positive");
            }
        } catch (Exception ex) {
            redirectWithError(request, response, questionIdParam, "Invalid post ID");
            return;
        }

        long questionId;
        try {
            questionId = Long.parseLong(questionIdParam);
        } catch (Exception ex) {
            redirectWithError(request, response, null, "Invalid question ID");
            return;
        }

        try {
            if (questionDao.isQuestionClosed(questionId)) {
                redirectWithError(request, response, questionIdParam, "Question is closed");
                return;
            }
        } catch (Exception ex) {
            redirectWithError(request, response, questionIdParam, "Unable to validate question status");
            return;
        }

        try {
            if (reportDao.isOwnerOfPost(userId, postType, postId)) {
                redirectWithError(request, response, questionIdParam, "You cannot report your own content");
                return;
            }

            Report report = new Report(userId, postType, postId, reason, note);
            long reportId = reportDao.insertReport(report);
            if (reportId <= 0) {
                redirectWithError(request, response, questionIdParam, "Unable to submit your report");
                return;
            }

            String redirectUrl = request.getContextPath() + "/question/detail?id=" + questionId + "&flag=success";
            if ("answer".equals(postType) && answerIdParam != null && answerIdParam.matches("\\d+")) {
                redirectUrl += "#answer-" + answerIdParam;
            }
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            e.printStackTrace();
            redirectWithError(request, response, questionIdParam, "Unable to submit your report");
        }
    }

    private void redirectWithError(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String questionId,
                                   String errorMessage) throws IOException {
        String safeQuestionId = (questionId != null && questionId.matches("\\d+")) ? questionId : null;
        String encodedError = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        if (safeQuestionId != null) {
            response.sendRedirect(request.getContextPath() + "/question/detail?id=" + safeQuestionId + "&flagError=" + encodedError);
        } else {
            response.sendRedirect(request.getContextPath() + "/home?flagError=" + encodedError);
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
