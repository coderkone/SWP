package control;

import dal.AnswerDAO;
import dal.QuestionDAO;
import dto.AnswerDTO;
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
import util.HtmlSanitizer;

@WebServlet(name = "EditPostController", urlPatterns = {"/post/edit"})
public class EditPostController extends HttpServlet {

    private final QuestionDAO questionDao = new QuestionDAO();
    private final AnswerDAO answerDao = new AnswerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            SessionUser sessionUser = getSessionUser(request);
            if (sessionUser == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only owner can edit this post.");
                return;
            }

            String type = request.getParameter("type");
            String idParam = request.getParameter("id");
            if (!isValidType(type) || idParam == null || !idParam.matches("\\d+")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid edit request.");
                return;
            }

            long postId = Long.parseLong(idParam);
            if ("question".equals(type)) {
                QuestionDTO question = questionDao.getQuestionById(postId);
                if (question == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Question not found.");
                    return;
                }
                if (question.isIsClosed()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Question is closed. Editing is disabled.");
                    return;
                }
                if (question.getUserId() != sessionUser.userId) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to edit this question.");
                    return;
                }

                request.setAttribute("postType", "question");
                request.setAttribute("question", question);
                request.setAttribute("tagsInput", String.join(",", question.getTags()));
            } else {
                AnswerDTO answer = answerDao.getAnswerById(postId);
                if (answer == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Answer not found.");
                    return;
                }
                if (questionDao.isQuestionClosed(answer.getQuestionId())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Question is closed. Editing is disabled.");
                    return;
                }
                if (answer.getUserId() != sessionUser.userId) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to edit this answer.");
                    return;
                }

                request.setAttribute("postType", "answer");
                request.setAttribute("answer", answer);
            }

            request.getRequestDispatcher("/View/User/edit-post.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            SessionUser sessionUser = getSessionUser(request);
            if (sessionUser == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only owner can edit this post.");
                return;
            }

            String type = request.getParameter("type");
            String idParam = request.getParameter("id");
            if (!isValidType(type) || idParam == null || !idParam.matches("\\d+")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid edit request.");
                return;
            }

            long postId = Long.parseLong(idParam);
            if ("question".equals(type)) {
                updateQuestion(request, response, postId, sessionUser);
            } else {
                updateAnswer(request, response, postId, sessionUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void updateQuestion(HttpServletRequest request, HttpServletResponse response, long questionId, SessionUser sessionUser)
            throws Exception {
        QuestionDTO question = questionDao.getQuestionById(questionId);
        if (question == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Question not found.");
            return;
        }
        if (question.isIsClosed()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Question is closed. Editing is disabled.");
            return;
        }
        if (question.getUserId() != sessionUser.userId) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to edit this question.");
            return;
        }

        String title = request.getParameter("title");
        String body = request.getParameter("body");
        String codeSnippet = request.getParameter("codeSnippet");
        String tags = request.getParameter("tags");

        if (title == null || title.trim().isEmpty() || body == null || body.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/post/edit?type=question&id=" + questionId + "&error=Missing required fields");
            return;
        }

        String sanitizedTitle = HtmlSanitizer.sanitize(title.trim());
        String sanitizedBody = HtmlSanitizer.sanitize(body);
        String sanitizedCode = codeSnippet == null ? "" : HtmlSanitizer.sanitize(codeSnippet);

        try {
            boolean updated = questionDao.updateQuestionWithHistory(
                    questionId,
                    sessionUser.userId,
                    sanitizedTitle,
                    sanitizedBody,
                    sanitizedCode,
                    tags,
                    sessionUser.reputation
            );

            if (!updated) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot update question.");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId + "&success=Question updated");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().startsWith("NOT_ENOUGH_REP:")) {
                String tagName = e.getMessage().substring("NOT_ENOUGH_REP:".length());
                response.sendRedirect(request.getContextPath() + "/post/edit?type=question&id=" + questionId + "&error=Not enough reputation for new tag: " + tagName);
                return;
            }
            throw e;
        }
    }

    private void updateAnswer(HttpServletRequest request, HttpServletResponse response, long answerId, SessionUser sessionUser)
            throws Exception {
        AnswerDTO answer = answerDao.getAnswerById(answerId);
        if (answer == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Answer not found.");
            return;
        }
        if (questionDao.isQuestionClosed(answer.getQuestionId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Question is closed. Editing is disabled.");
            return;
        }
        if (answer.getUserId() != sessionUser.userId) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to edit this answer.");
            return;
        }

        String body = request.getParameter("body");
        String codeSnippet = request.getParameter("codeSnippet");

        if (body == null || body.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/post/edit?type=answer&id=" + answerId + "&error=Answer body is required");
            return;
        }

        String sanitizedBody = HtmlSanitizer.sanitize(body);
        String sanitizedCode = codeSnippet == null ? "" : HtmlSanitizer.sanitize(codeSnippet);

        boolean updated = answerDao.updateAnswerWithHistory(answerId, sessionUser.userId, sanitizedBody, sanitizedCode);
        if (!updated) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot update answer.");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/question/detail?id=" + answer.getQuestionId() + "&success=Answer updated#answer-" + answerId);
    }

    private boolean isValidType(String type) {
        return "question".equals(type) || "answer".equals(type);
    }

    private SessionUser getSessionUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object userObj = session.getAttribute("user");
        if (userObj instanceof UserDTO) {
            UserDTO user = (UserDTO) userObj;
            return new SessionUser(user.getUserId(), user.getReputation());
        }
        if (userObj instanceof User) {
            User user = (User) userObj;
            return new SessionUser(user.getUserId(), user.getReputation());
        }
        return null;
    }

    private static class SessionUser {
        private final long userId;
        private final int reputation;

        private SessionUser(long userId, int reputation) {
            this.userId = userId;
            this.reputation = reputation;
        }
    }
}
