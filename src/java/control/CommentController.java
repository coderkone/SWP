package control;

import dal.CommentDAO;
import dal.AnswerDAO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "CommentController", urlPatterns = {"/comment/add"})
public class CommentController extends HttpServlet {

    private final CommentDAO commentDao = new CommentDAO();
    private final AnswerDAO answerDao = new AnswerDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Check authentication
            HttpSession session = request.getSession(false);
            UserDTO user = (session == null) ? null : (UserDTO) session.getAttribute("USER");

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

            // 2. Get parameters
            String answerIdParam = request.getParameter("answerId");
            String questionIdParam = request.getParameter("questionId");
            String commentBody = request.getParameter("commentBody");

            // 3. Validate parameters
            if (commentBody == null || commentBody.trim().isEmpty() || commentBody.length() > 1000) {
                request.setAttribute("error", "Comment must be between 1 and 1000 characters");
                response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionIdParam);
                return;
            }

            long userId = user.getUserId();

            // 4. Determine comment type and validate
            if (answerIdParam != null && !answerIdParam.isEmpty()) {
                // Comment on Answer
                if (!answerIdParam.matches("\\d+")) {
                    request.setAttribute("error", "Invalid answer ID");
                    request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
                    return;
                }

                if (questionIdParam == null || !questionIdParam.matches("\\d+")) {
                    request.setAttribute("error", "Invalid question ID");
                    request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
                    return;
                }

                long answerId = Long.parseLong(answerIdParam);
                long questionId = Long.parseLong(questionIdParam);

                // Validate answer existence
                if (!commentDao.answerExists(answerId)) {
                    request.setAttribute("error", "Answer does not exist");
                    request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
                    return;
                }

                // Insert comment on answer
                long commentId = commentDao.insertAnswerComment(userId, answerId, commentBody.trim());

                if (commentId <= 0) {
                    request.setAttribute("error", "Failed to add comment");
                    response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId);
                    return;
                }

                // Redirect back with anchor to answer
                response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId + "#answer-" + answerId);

            } else if (questionIdParam != null && !questionIdParam.isEmpty()) {
                // Comment on Question
                if (!questionIdParam.matches("\\d+")) {
                    request.setAttribute("error", "Invalid question ID");
                    request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
                    return;
                }

                long questionId = Long.parseLong(questionIdParam);

                // Validate question existence
                if (!commentDao.questionExists(questionId)) {
                    request.setAttribute("error", "Question does not exist");
                    request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
                    return;
                }

                // Insert comment on question
                long commentId = commentDao.insertQuestionComment(userId, questionId, commentBody.trim());

                if (commentId <= 0) {
                    request.setAttribute("error", "Failed to add comment");
                    response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId);
                    return;
                }

                // Redirect back to question
                response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId);

            } else {
                // Neither answer nor question specified
                request.setAttribute("error", "Invalid comment request");
                request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error adding comment: " + e.getMessage());
            try {
                request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
