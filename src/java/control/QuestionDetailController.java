package control;

import dal.AnswerDAO;
import dal.CommentDAO;
import dal.QuestionDAO;
import dal.VoteDAO;
import dal.BookmarkQuesDAO;

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
import java.net.URLEncoder;
import java.util.*;

import model.User;

@WebServlet(name = "QuestionDetailController", urlPatterns = {"/question/detail"})
public class QuestionDetailController extends HttpServlet {

    private final QuestionDAO questionDao = new QuestionDAO();
    private final AnswerDAO answerDao = new AnswerDAO();
    private final VoteDAO voteDao = new VoteDAO();
    private final BookmarkQuesDAO bookmarkDao = new BookmarkQuesDAO();
    private final CommentDAO commentDao = new CommentDAO();

    private static final int ANSWERS_PER_PAGE = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String questionIdRaw = request.getParameter("id");

            if (questionIdRaw == null) {
                response.sendRedirect("home");
                return;
            }

            long questionId = Long.parseLong(questionIdRaw);

            QuestionDTO question = questionDao.getQuestionById(questionId);

            if (question != null) {

                // ===== LOAD ANSWERS =====
                List<AnswerDTO> answers = answerDao.getAnswersByQuestionId(questionId);
                request.setAttribute("question", question);
                request.setAttribute("answers", answers);

                // ===== COMMENTS =====
                try {
                    Map<Long, List<dto.CommentDTO>> answerComments = new HashMap<>();
                    Map<Long, Map<Long, List<dto.CommentDTO>>> answerCommentTrees = new HashMap<>();

                    for (AnswerDTO answer : answers) {
                        List<dto.CommentDTO> comments =
                                commentDao.getCommentsByAnswerId(answer.getAnswerId());

                        answerComments.put(answer.getAnswerId(), comments);

                        Map<Long, List<dto.CommentDTO>> tree = new HashMap<>();
                        Map<Long, dto.CommentDTO> byId = new HashMap<>();

                        for (dto.CommentDTO c : comments) {
                            byId.put(c.getCommentId(), c);
                        }

                        for (dto.CommentDTO c : comments) {
                            Long parentId = c.getParentCommentId();

                            if (parentId != null && !byId.containsKey(parentId)) {
                                parentId = null;
                            }

                            tree.computeIfAbsent(parentId, k -> new ArrayList<>()).add(c);
                        }

                        answerCommentTrees.put(answer.getAnswerId(), tree);
                    }

                    request.setAttribute("answerComments", answerComments);
                    request.setAttribute("answerCommentTrees", answerCommentTrees);

                } catch (Exception e) {
                    request.setAttribute("answerComments", new HashMap<>());
                    request.setAttribute("answerCommentTrees", new HashMap<>());
                }

                // ===== USER INFO =====
                try {
                    HttpSession session = request.getSession(false);
                    Long currentUserId = session == null ? null : extractUserId(session.getAttribute("user"));

                    request.setAttribute("isQuestionOwner",
                            currentUserId != null && currentUserId == question.getUserId());

                } catch (Exception e) {
                    request.setAttribute("isQuestionOwner", false);
                }

                // ===== VOTE + BOOKMARK =====
                try {
                    HttpSession session = request.getSession(false);
                    Long userId = session == null ? null : extractUserId(session.getAttribute("user"));

                    if (userId != null) {

                        // Question vote
                        String questionUserVote =
                                voteDao.getUserVote(userId, questionId, null);
                        request.setAttribute("questionUserVote", questionUserVote);

                        Map<Long, String> answerVotes = new HashMap<>();
                        Map<Long, Boolean> answerBookmarks = new HashMap<>();

                        for (AnswerDTO answer : answers) {
                            Long answerId = answer.getAnswerId();

                            String vote =
                                    voteDao.getUserVote(userId, null, answerId);

                            if (vote != null) {
                                answerVotes.put(answerId, vote);
                            }

                            boolean isBookmarked =
                                    bookmarkDao.checkIfAnswerBookmarked(userId, answerId);

                            answerBookmarks.put(answerId, isBookmarked);
                        }

                        request.setAttribute("answerVotes", answerVotes);
                        request.setAttribute("answerBookmarks", answerBookmarks);

                        boolean isBookmarked =
                                bookmarkDao.checkIfBookmarked(userId, questionId);

                        request.setAttribute("isBookmarked", isBookmarked);
                    }

                } catch (Exception e) {
                    // ignore
                }

                // ===== RELATED QUESTIONS =====
                try {
                    List<QuestionDTO> relatedQuestions =
                            questionDao.getRelatedQuestions(questionId, 4);

                    request.setAttribute("relatedQuestions", relatedQuestions);

                } catch (Exception e) {
                    request.setAttribute("relatedQuestions", new ArrayList<>());
                }

                request.getRequestDispatcher("/View/User/question-detail.jsp")
                        .forward(request, response);

            } else {
                request.setAttribute("error", "Không tìm thấy câu hỏi này.");
                request.getRequestDispatcher("/View/User/home.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal Server Error");
        }
    }

    // ===== FIX USER TYPE (User vs UserDTO) =====
    private Long extractUserId(Object principal) {
        if (principal instanceof UserDTO) {
            return ((UserDTO) principal).getUserId();
        }
        if (principal instanceof User) {
            return ((User) principal).getUserId();
        }
        return null;
    }

    private String buildAnswerFilterQuery(String sort) {
        StringBuilder query = new StringBuilder();
        try {
            if (sort != null && !sort.trim().isEmpty()) {
                query.append("&sort=")
                     .append(URLEncoder.encode(sort, "UTF-8"));
            }
        } catch (Exception e) {
            return "";
        }
        return query.toString();
    }
}