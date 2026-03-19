package control;

import dal.AnswerDAO;
import dal.QuestionDAO;
import dal.VoteDAO;
import dto.QuestionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.User;

@WebServlet(name = "ViewQuestionController", urlPatterns = {"/question"})
public class ViewQuestionController extends HttpServlet {

    private final QuestionDAO questionDao = new QuestionDAO();
    private final AnswerDAO answerDao = new AnswerDAO();
    private final VoteDAO voteDao = new VoteDAO();
    private static final long VIEW_COOLDOWN_MS = 30 * 60 * 1000; // 30 minutes
    private static final int ANSWERS_PER_PAGE = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            long questionId = Long.parseLong(idParam);
            QuestionDTO question = questionDao.getQuestionById(questionId);

            if (question == null) {
                request.setAttribute("error", "Câu hỏi không tồn tại.");
                request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
                return;
            }

            // Check if view should be counted
            HttpSession session = request.getSession(true);
            String viewKey = "view_" + questionId;
            Long lastViewTime = (Long) session.getAttribute(viewKey);
            long currentTime = System.currentTimeMillis();

            // Count view only if last view was > 30 minutes ago
            if (lastViewTime == null || (currentTime - lastViewTime) > VIEW_COOLDOWN_MS) {
                questionDao.incrementViewCount(questionId);
                session.setAttribute(viewKey, currentTime);
            }

            // Load vote score for question
            try {
                int questionScore = voteDao.getVoteScore(questionId, null);
                question.setScore(questionScore);
            } catch (Exception e) { /* use default score */ }

            int answerCurrentPage = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    answerCurrentPage = Integer.parseInt(pageParam);
                } catch (NumberFormatException e) {
                    answerCurrentPage = 1;
                }
            }

            // Load answers with vote scores
            List answers = new ArrayList();
            int totalAnswers = 0;
            int answerTotalPages = 1;
            try {
                totalAnswers = answerDao.getTotalAnswersByQuestionId(questionId);
                answerTotalPages = Math.max(1, (int) Math.ceil(totalAnswers / (double) ANSWERS_PER_PAGE));
                if (answerCurrentPage < 1) {
                    answerCurrentPage = 1;
                } else if (answerCurrentPage > answerTotalPages) {
                    answerCurrentPage = answerTotalPages;
                }

                answers = answerDao.getAnswersByQuestionId(questionId, answerCurrentPage, ANSWERS_PER_PAGE);
                Long acceptedId = question.getAcceptedAnswerId();
                for (Object answerObj : answers) {
                    dto.AnswerDTO answer = (dto.AnswerDTO) answerObj;
                    answer.setScore(voteDao.getVoteScore(null, answer.getAnswerId()));
                    answer.setIsAccepted(acceptedId != null && acceptedId.equals(answer.getAnswerId()));
                }
            } catch (Exception e) { /* answers stay empty */ }

            // Load user's votes (if logged in)
            try {
                if (session != null && session.getAttribute("user") != null) {
                    User user = (User) session.getAttribute("user");
                    long userId = user.getUserId();
                    request.setAttribute("questionUserVote", voteDao.getUserVote(userId, questionId, null));
                    java.util.Map<Long, String> answerVotes = new java.util.HashMap<>();
                    for (Object answerObj : answers) {
                        dto.AnswerDTO answer = (dto.AnswerDTO) answerObj;
                        String v = voteDao.getUserVote(userId, null, answer.getAnswerId());
                        if (v != null) answerVotes.put(answer.getAnswerId(), v);
                    }
                    request.setAttribute("answerVotes", answerVotes);
                }
            } catch (Exception e) { /* no user votes */ }

            // Load related questions
            try {
                request.setAttribute("relatedQuestions", questionDao.getRelatedQuestions(questionId, 4));
            } catch (Exception e) {
                request.setAttribute("relatedQuestions", new ArrayList<>());
            }

            try {
                if (session != null && session.getAttribute("user") != null) {
                    User u = (User) session.getAttribute("user");
                    request.setAttribute("isQuestionOwner", u.getUserId() == question.getUserId());
                } else {
                    request.setAttribute("isQuestionOwner", false);
                }
            } catch (Exception e) {
                request.setAttribute("isQuestionOwner", false);
            }
            request.setAttribute("question", question);
            request.setAttribute("answers", answers);
            request.setAttribute("answerCurrentPage", answerCurrentPage);
            request.setAttribute("answerTotalPages", answerTotalPages);
            request.setAttribute("answerTotalCount", totalAnswers);
            request.setAttribute("answerPageSize", ANSWERS_PER_PAGE);
            request.setAttribute("answerPaginationPath", request.getContextPath() + request.getServletPath());
            request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID câu hỏi không hợp lệ.");
            request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi server: " + e.getMessage());
            request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
        }
    }
}
