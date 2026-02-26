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

@WebServlet(name = "ViewQuestionController", urlPatterns = {"/question"})
public class ViewQuestionController extends HttpServlet {

    private final QuestionDAO questionDao = new QuestionDAO();
    private final AnswerDAO answerDao = new AnswerDAO();
    private final VoteDAO voteDao = new VoteDAO();
    private static final long VIEW_COOLDOWN_MS = 30 * 60 * 1000; // 30 minutes

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

            // Load answers with vote scores
            List answers = new ArrayList();
            try {
                answers = answerDao.getAnswersByQuestionId(questionId);
                Long acceptedId = question.getAcceptedAnswerId();
                for (Object answerObj : answers) {
                    dto.AnswerDTO answer = (dto.AnswerDTO) answerObj;
                    answer.setScore(voteDao.getVoteScore(null, answer.getAnswerId()));
                    answer.setIsAccepted(acceptedId != null && acceptedId.equals(answer.getAnswerId()));
                }
            } catch (Exception e) { /* answers stay empty */ }

            // Load user's votes (if logged in)
            try {
                if (session != null && session.getAttribute("USER") != null) {
                    dto.UserDTO user = (dto.UserDTO) session.getAttribute("USER");
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
                if (session != null && session.getAttribute("USER") != null) {
                    dto.UserDTO u = (dto.UserDTO) session.getAttribute("USER");
                    request.setAttribute("isQuestionOwner", u.getUserId() == question.getUserId());
                } else {
                    request.setAttribute("isQuestionOwner", false);
                }
            } catch (Exception e) {
                request.setAttribute("isQuestionOwner", false);
            }
            request.setAttribute("question", question);
            request.setAttribute("answers", answers);
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
