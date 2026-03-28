package control;

import dal.AnswerDAO;
import dal.CommentDAO;
import dal.QuestionDAO;
import dal.VoteDAO;
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
import java.util.ArrayList;
import java.util.List;
import model.User;
import dal.BookmarkQuesDAO;

@WebServlet(name = "QuestionDetailController", urlPatterns = {"/question/detail"})
public class QuestionDetailController extends HttpServlet {

    private final QuestionDAO questionDao = new QuestionDAO();
    private final AnswerDAO answerDao = new AnswerDAO();
    private final VoteDAO voteDao = new VoteDAO();
    private final BookmarkQuesDAO BookmqDAO = new  BookmarkQuesDAO();
    private final CommentDAO commentDao = new CommentDAO();
    private static final int ANSWERS_PER_PAGE = 5;
    private static final long VIEW_COOLDOWN_MS = 30L * 60L * 1000L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam == null || !idParam.matches("\\d+")) {
            //Nếu không có ID hoặc ID không phải số -> Về trang chủ hoặc báo lỗi
            request.setAttribute("error", "Đường dẫn không hợp lệ.");
            request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
            return;
        }

        try {
            long questionId = Long.parseLong(idParam);

            //2. Gọi DAO lấy thông tin chi tiết câu hỏi
            QuestionDTO question = questionDao.getQuestionById(questionId);

            if (question != null) {
                trackQuestionView(request.getSession(), question);

                //Try to load vote score and answers
                try {
                    int questionScore = voteDao.getVoteScore(questionId, null);
                    question.setScore(questionScore);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int answerCurrentPage = 1;
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.trim().isEmpty()) {
                    try {
                        answerCurrentPage = Integer.parseInt(pageParam);
                    } catch (NumberFormatException ex) {
                        answerCurrentPage = 1;
                    }
                }

                String sort = request.getParameter("sort");
                if (!"score_desc".equalsIgnoreCase(sort)
                        && !"score_asc".equalsIgnoreCase(sort)
                        && !"newest".equalsIgnoreCase(sort)
                        && !"oldest".equalsIgnoreCase(sort)) {
                    sort = "score_desc";
                }
                //Try to load answers
                List<AnswerDTO> answers = new ArrayList<>();
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

                    answers = answerDao.getAnswersByQuestionId(questionId, answerCurrentPage, ANSWERS_PER_PAGE, sort);

                    //Set vote scores and accepted status for answers
                    Long acceptedId = question.getAcceptedAnswerId();
                    for (Object answerObj : answers) {
                        try {
                            dto.AnswerDTO answer = (dto.AnswerDTO) answerObj;
                            int answerScore = voteDao.getVoteScore(null, answer.getAnswerId());
                            answer.setScore(answerScore);
                            answer.setIsAccepted(acceptedId != null && acceptedId.equals(answer.getAnswerId()));
                        } catch (Exception e) {
                            // continue on error
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //3. Đẩy dữ liệu sang JSP
                request.setAttribute("question", question);
                request.setAttribute("answers", answers);
                request.setAttribute("answerCurrentPage", answerCurrentPage);
                request.setAttribute("answerTotalPages", answerTotalPages);
                request.setAttribute("answerTotalCount", totalAnswers);
                request.setAttribute("answerPageSize", ANSWERS_PER_PAGE);
                request.setAttribute("answerPaginationPath", request.getContextPath() + request.getServletPath());
                request.setAttribute("sort", sort);
                request.setAttribute("answerFilterQuery", buildAnswerFilterQuery(sort));
                request.setAttribute("minBountyReputation", QuestionDAO.MIN_BOUNTY_REPUTATION);
                request.setAttribute("bountyDurationDays", QuestionDAO.BOUNTY_DURATION_DAYS);
                recordViewedQuestion(request.getSession(), questionId);

                //Load comments for question
                try {
                    java.util.List<dto.CommentDTO> questionComments = commentDao.getCommentsByQuestionId(question.getQuestionId());
                    request.setAttribute("questionComments", questionComments);
                } catch (Exception e) {
                    request.setAttribute("questionComments", new java.util.ArrayList<>());
                }

                //Load comments for each answer
                try {
                    java.util.Map<Long, java.util.List<dto.CommentDTO>> answerComments = new java.util.HashMap<>();
                    java.util.Map<Long, java.util.Map<Long, java.util.List<dto.CommentDTO>>> answerCommentTrees = new java.util.HashMap<>();
                    for (Object answerObj : answers) {
                        dto.AnswerDTO answer = (dto.AnswerDTO) answerObj;
                        java.util.List<dto.CommentDTO> comments = commentDao.getCommentsByAnswerId(answer.getAnswerId());
                        answerComments.put(answer.getAnswerId(), comments);

                        java.util.Map<Long, dto.CommentDTO> byId = new java.util.HashMap<>();
                        for (dto.CommentDTO c : comments) {
                            byId.put(c.getCommentId(), c);
                        }

                        java.util.Map<Long, java.util.List<dto.CommentDTO>> tree = new java.util.HashMap<>();
                        for (dto.CommentDTO c : comments) {
                            Long parentId = c.getParentCommentId();

                            // Render orphaned replies as top-level comments to avoid losing data in UI.
                            if (parentId != null && !byId.containsKey(parentId)) {
                                parentId = null;
                            }

                            java.util.List<dto.CommentDTO> siblings = tree.get(parentId);
                            if (siblings == null) {
                                siblings = new java.util.ArrayList<>();
                                tree.put(parentId, siblings);
                            }
                            siblings.add(c);
                        }
                        answerCommentTrees.put(answer.getAnswerId(), tree);
                    }
                    request.setAttribute("answerComments", answerComments);
                    request.setAttribute("answerCommentTrees", answerCommentTrees);
                } catch (Exception e) {
                    request.setAttribute("answerComments", new java.util.HashMap<>());
                    request.setAttribute("answerCommentTrees", new java.util.HashMap<>());
                }

                try {
                    HttpSession s = request.getSession(false);
                    Long currentUserId = extractUserId(s == null ? null : s.getAttribute("user"));
                    if (currentUserId != null) {
                        request.setAttribute("isQuestionOwner", currentUserId == question.getUserId());
                    } else {
                        request.setAttribute("isQuestionOwner", false);
                    }
                } catch (Exception e) {
                    request.setAttribute("isQuestionOwner", false);
                }

                //Load user's vote (if logged in)
                try {
                    HttpSession session = request.getSession(false);
                    Long userId = extractUserId(session == null ? null : session.getAttribute("user"));
                    if (userId != null) {

                        //Get user's vote for question
                        String questionUserVote = voteDao.getUserVote(userId, questionId, null);
                        request.setAttribute("questionUserVote", questionUserVote);

                        //Get user's votes for answers
                        java.util.Map<Long, String> answerVotes = new java.util.HashMap<>();
                        for (Object answerObj : answers) {
                            dto.AnswerDTO answer = (dto.AnswerDTO) answerObj;
                            String answerUserVote = voteDao.getUserVote(userId, null, answer.getAnswerId());
                            if (answerUserVote != null) {
                                answerVotes.put(answer.getAnswerId(), answerUserVote);
                            }
                        }
                        request.setAttribute("answerVotes", answerVotes);
                        // Check if question is bookmarked
                        try {
                            boolean isBookmarked = BookmqDAO.checkIfBookmarked(userId, questionId);
                            request.setAttribute("isBookmarked", isBookmarked);
                        } catch (Exception e) {
                            request.setAttribute("isBookmarked", false);
                        }

                        //Check if answers are bookmarked
                        /*try {
                            java.util.Map<Long, Boolean> answerBookmarks = new java.util.HashMap<>();
                            for (Object answerObj : answers) {
                                dto.AnswerDTO answer = (dto.AnswerDTO) answerObj;
                                boolean isAnswerBookmarked = bookmarkDao.checkIfAnswerBookmarked(userId, answer.getAnswerId());
                                answerBookmarks.put(answer.getAnswerId(), isAnswerBookmarked);
                            }
                            request.setAttribute("answerBookmarks", answerBookmarks);
                        } catch (Exception e) {
                            request.setAttribute("answerBookmarks", new java.util.HashMap<>());
                        }*/
                    }
                } catch (Exception e) {
                    // continue on error
                }

                // Load related questions (4 questions max)
                try {
                    List<QuestionDTO> relatedQuestions = questionDao.getRelatedQuestions(questionId, 4);
                    request.setAttribute("relatedQuestions", relatedQuestions);
                } catch (Exception e) {
                    request.setAttribute("relatedQuestions", new ArrayList<>());
                }

                try {
                    List<QuestionDTO> popularQuestions = questionDao.getPopularQuestions(questionId, 5);
                    request.setAttribute("popularQuestions", popularQuestions);
                } catch (Exception e) {
                    request.setAttribute("popularQuestions", new ArrayList<>());
                }

                request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
            } else {
                // Trường hợp ID hợp lệ nhưng không tìm thấy câu hỏi trong DB
                request.setAttribute("error", "Không tìm thấy câu hỏi này.");
                request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }
        

    private String buildAnswerFilterQuery(String sort) {
        StringBuilder query = new StringBuilder();
        try {
            if (sort != null && !sort.trim().isEmpty()) {
                query.append("&sort=").append(URLEncoder.encode(sort, "UTF-8"));
            }
        } catch (Exception ex) {
            return "";
        }
        return query.toString();
    }

    private void trackQuestionView(HttpSession session, QuestionDTO question) {
        if (session == null || question == null) {
            return;
        }

        String key = "viewed_q_" + question.getQuestionId();
        Long lastViewTime = (Long) session.getAttribute(key);
        long now = System.currentTimeMillis();

        if (lastViewTime == null || now - lastViewTime > VIEW_COOLDOWN_MS) {
            questionDao.incrementViewCount(question.getQuestionId());
            question.setViewCount(question.getViewCount() + 1);
            session.setAttribute(key, now);
        }
    }

    private Long extractUserId(Object principal) {
        if (principal instanceof UserDTO) {
            return ((UserDTO) principal).getUserId();
        }
        if (principal instanceof User) {
            return ((User) principal).getUserId();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void recordViewedQuestion(HttpSession session, long questionId) {
        if (session == null) {
            return;
        }

        Object existing = session.getAttribute("viewedQuestionIds");
        List<Long> viewedIds = existing instanceof List<?>
                ? new ArrayList<>((List<Long>) existing)
                : new ArrayList<>();

        viewedIds.remove(questionId);
        viewedIds.add(0, questionId);

        if (viewedIds.size() > 15) {
            viewedIds = new ArrayList<>(viewedIds.subList(0, 15));
        }

        session.setAttribute("viewedQuestionIds", viewedIds);
    }
}
