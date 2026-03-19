//package control;
//
//import dal.CommentDAO;
//import dal.QuestionDAO;
//import dto.UserDTO;
//import model.User;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.*;
//import java.io.IOException;
//
//@WebServlet(name = "CommentController", urlPatterns = {"/comment/add"})
//public class CommentController extends HttpServlet {
//
//    private final CommentDAO commentDao = new CommentDAO();
//    private final QuestionDAO questionDao = new QuestionDAO();
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        try {
//            // 1. Check authentication
//            HttpSession session = request.getSession(false);
//            Object principal = (session == null) ? null : session.getAttribute("user");
//
//            long userId;
//            if (principal instanceof UserDTO) {
//                userId = ((UserDTO) principal).getUserId();
//            } else if (principal instanceof User) {
//                userId = ((User) principal).getUserId();
//            } else {
//                response.sendRedirect(request.getContextPath() + "/auth/login");
//                return;
//            }
//
//            // 2. Get parameters
//            String answerIdParam = request.getParameter("answerId");
//            String questionIdParam = request.getParameter("questionId");
//            String parentCommentIdParam = request.getParameter("parentCommentId");
//            String commentBody = request.getParameter("commentBody");
//
//            // 3. Validate parameters exist
//            if (questionIdParam == null || questionIdParam.trim().isEmpty()) {
//                request.setAttribute("error", "Question ID is required");
//                response.sendRedirect(request.getContextPath() + "/question/detail");
//                return;
//            }
//
//            // 4. Validate comment body
//            if (commentBody == null || commentBody.trim().isEmpty()) {
//                request.setAttribute("error", "Comment cannot be empty");
//                response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionIdParam);
//                return;
//            }
//
//            if (commentBody.length() > 1000) {
//                request.setAttribute("error", "Comment cannot exceed 1000 characters");
//                response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionIdParam);
//                return;
//            }
//
//            // 5. Determine comment type and validate
//            if (answerIdParam != null && !answerIdParam.trim().isEmpty()) {
//                // Comment on Answer
//                if (!answerIdParam.matches("\\d+")) {
//                    request.setAttribute("error", "Invalid answer ID");
//                    request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
//                    return;
//                }
//
//                if (questionIdParam == null || !questionIdParam.matches("\\d+")) {
//                    request.setAttribute("error", "Invalid question ID");
//                    request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
//                    return;
//                }
//
//                long answerId = Long.parseLong(answerIdParam);
//                long questionId = Long.parseLong(questionIdParam);
//
//                if (questionDao.isQuestionClosed(questionId)) {
//                    response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId + "&error=Question is closed");
//                    return;
//                }
//
//                Long parentCommentId = null;
//
//                // Optional reply target for nested comments
//                if (parentCommentIdParam != null && !parentCommentIdParam.trim().isEmpty()) {
//                    if (!parentCommentIdParam.matches("\\d+")) {
//                        request.setAttribute("error", "Invalid parent comment ID");
//                        response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId + "#answer-" + answerId);
//                        return;
//                    }
//
//                    parentCommentId = Long.parseLong(parentCommentIdParam);
//                    dto.CommentDTO parentComment = commentDao.getCommentById(parentCommentId);
//
//                    if (parentComment == null || parentComment.getAnswerId() <= 0) {
//                        request.setAttribute("error", "Parent comment does not exist");
//                        response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId + "#answer-" + answerId);
//                        return;
//                    }
//
//                    // Force child comment to remain in the same answer thread as its parent
//                    if (parentComment.getAnswerId() != answerId) {
//                        request.setAttribute("error", "Parent comment does not belong to this answer");
//                        response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId + "#answer-" + answerId);
//                        return;
//                    }
//                }
//
//                // Insert comment on answer
//                long commentId = commentDao.insertAnswerComment(userId, answerId, commentBody.trim(), parentCommentId);
//
//                if (commentId <= 0) {
//                    request.setAttribute("error", "Failed to add comment");
//                    response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId);
//                    return;
//                }
//
//                // Redirect back with anchor to answer
//                response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId + "#answer-" + answerId);
//
//            } else if (questionIdParam != null && !questionIdParam.isEmpty()) {
//                // Comment on Question
//                if (!questionIdParam.matches("\\d+")) {
//                    request.setAttribute("error", "Invalid question ID");
//                    request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
//                    return;
//                }
//
//                long questionId = Long.parseLong(questionIdParam);
//
//                if (questionDao.isQuestionClosed(questionId)) {
//                    response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId + "&error=Question is closed");
//                    return;
//                }
//
//                // Insert comment on question
//                long commentId = commentDao.insertQuestionComment(userId, questionId, commentBody.trim());
//
//                if (commentId <= 0) {
//                    request.setAttribute("error", "Failed to add comment");
//                    response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId);
//                    return;
//                }
//
//                // Redirect back to question
//                response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId);
//
//            } else {
//                // Neither answer nor question specified
//                request.setAttribute("error", "Invalid comment request");
//                request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
//                return;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            request.setAttribute("error", "Error adding comment: " + e.getMessage());
//            try {
//                request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//}