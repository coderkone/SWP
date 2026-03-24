package control;

import dal.AnswerDAO;
import dal.BookmarkQuesDAO;
import dal.QuestionDetailDAO;
import dal.VoteDAO;

import dto.AnswerDTO;
import dto.QuestionDTO;
import dto.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

import model.User;

@WebServlet(name = "ViewQuestionController", urlPatterns = {"/question"})
public class ViewQuestionController extends HttpServlet {

    private final QuestionDetailDAO questionDetailDAO = new QuestionDetailDAO();
    private final AnswerDAO answerDao = new AnswerDAO();
    private final VoteDAO voteDao = new VoteDAO();

    private static final long VIEW_COOLDOWN_MS = 30 * 60 * 1000;
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

            QuestionDTO question = questionDetailDAO.getQuestionById(questionId);

            if (question == null) {
                request.setAttribute("error", "Không tìm thấy câu hỏi.");
                request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession(false);

            // ===== VIEW COUNT (ANTI SPAM) =====
            try {
                String key = "viewed_q_" + questionId;
                Long lastViewTime = (session == null) ? null : (Long) session.getAttribute(key);

                long now = System.currentTimeMillis();

                if (session != null && (lastViewTime == null || now - lastViewTime > VIEW_COOLDOWN_MS)) {
                    questionDetailDAO.incrementViewCount(questionId); 
                    session.setAttribute(key, now);
                }
            } catch (Exception e) {
            }

            // ===== PAGINATION =====
            int answerCurrentPage = 1;
            try {
                String pageParam = request.getParameter("page");
                if (pageParam != null) {
                    answerCurrentPage = Integer.parseInt(pageParam);
                }
            } catch (Exception e) {
                answerCurrentPage = 1;
            }

            // ===== LOAD ANSWERS =====
            List<AnswerDTO> answers = new ArrayList<>();
            int totalAnswers = 0;
            int answerTotalPages = 1;

            try {
                totalAnswers = answerDao.getTotalAnswersByQuestionId(questionId);

                answerTotalPages = Math.max(1,
                        (int) Math.ceil(totalAnswers / (double) ANSWERS_PER_PAGE));

                if (answerCurrentPage < 1) {
                    answerCurrentPage = 1;
                } else if (answerCurrentPage > answerTotalPages) {
                    answerCurrentPage = answerTotalPages;
                }

                answers = answerDao.getAnswersByQuestionId(
                        questionId, answerCurrentPage, ANSWERS_PER_PAGE
                );

                Long acceptedId = question.getAcceptedAnswerId();

                for (AnswerDTO answer : answers) {
                    answer.setScore(voteDao.getVoteScore(null, answer.getAnswerId()));
                    answer.setIsAccepted(
                            acceptedId != null && acceptedId.equals(answer.getAnswerId())
                    );
                }

            } catch (Exception e) {
                answers = new ArrayList<>();
            }

            // ===== USER VOTE + BOOKMARK =====
            try {
                Long userId = session == null ? null : extractUserId(session.getAttribute("user"));

                if (userId != null) {

                    BookmarkQuesDAO bookmarkDao = new BookmarkQuesDAO();

                    // Question
                    request.setAttribute("questionUserVote",
                            voteDao.getUserVote(userId, questionId, null));

                    request.setAttribute("isBookmarked",
                            bookmarkDao.checkIfBookmarked(userId, questionId));

                    // Answers
                    Map<Long, String> answerVotes = new HashMap<>();
                    Map<Long, Boolean> answerBookmarks = new HashMap<>();

                    for (AnswerDTO answer : answers) {
                        Long answerId = answer.getAnswerId();

                        String vote = voteDao.getUserVote(userId, null, answerId);
                        if (vote != null) {
                            answerVotes.put(answerId, vote);
                        }

                        boolean isBookmarked =
                                bookmarkDao.checkIfAnswerBookmarked(userId, answerId);

                        answerBookmarks.put(answerId, isBookmarked);
                    }

                    request.setAttribute("answerVotes", answerVotes);
                    request.setAttribute("answerBookmarks", answerBookmarks);
                }

            } catch (Exception e) {
                // ignore
            }

            // ===== RELATED QUESTIONS =====
            try {
                request.setAttribute("relatedQuestions",
                        questionDetailDAO.getRelatedQuestions(questionId, 4));
            } catch (Exception e) {
                request.setAttribute("relatedQuestions", new ArrayList<>());
            }

            // ===== OWNER CHECK =====
            try {
                Long currentUserId = session == null ? null : extractUserId(session.getAttribute("user"));

                request.setAttribute("isQuestionOwner",
                        currentUserId != null && currentUserId == question.getUserId());

            } catch (Exception e) {
                request.setAttribute("isQuestionOwner", false);
            }

            // ===== RESPONSE =====
            request.setAttribute("question", question);
            request.setAttribute("answers", answers);
            request.setAttribute("answerCurrentPage", answerCurrentPage);
            request.setAttribute("answerTotalPages", answerTotalPages);
            request.setAttribute("answerTotalCount", totalAnswers);
            request.setAttribute("answerPageSize", ANSWERS_PER_PAGE);
            request.setAttribute("answerPaginationPath",
                    request.getContextPath() + request.getServletPath());

            request.getRequestDispatcher("/View/User/question-detail.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID câu hỏi không hợp lệ.");
            request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Lỗi server: " + e.getMessage());
            request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
        }
    }

    // ===== HANDLE User vs UserDTO =====
    private Long extractUserId(Object principal) {
        if (principal instanceof UserDTO) {
            return ((UserDTO) principal).getUserId();
        }
        if (principal instanceof User) {
            return ((User) principal).getUserId();
        }
        return null;
    }
}