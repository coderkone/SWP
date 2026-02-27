package control;


import dal.AnswerDAO;
import dal.BookmarkDAO;
import dal.QuestionDAO;
import dal.VoteDAO;
import dal.CommentDAO;
import dto.QuestionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

// Controller này sẽ hứng URL dạng: /question?id=123
@WebServlet(name = "QuestionDetailController", urlPatterns = {"/question/detail"})
public class QuestionDetailController extends HttpServlet {

    private final QuestionDAO questionDao = new QuestionDAO();
    private final AnswerDAO answerDao = new AnswerDAO();
    private final VoteDAO voteDao = new VoteDAO();
    private final BookmarkDAO bookmarkDao = new BookmarkDAO();
    private final CommentDAO commentDao = new CommentDAO();
    
    private void logError(String msg) {
        try {
            PrintWriter fw = new PrintWriter(new java.io.FileWriter("C:/debug_question_detail.txt", true));
            fw.println("[" + new java.util.Date() + "] " + msg);
            fw.close();
        } catch (Exception e) {}
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        logError("=== Request received for /question/detail ===");
        
        // 1. Lấy ID từ URL
        String idParam = request.getParameter("id");
        logError("ID parameter: " + idParam);
        
        // Validate ID
        if (idParam == null || !idParam.matches("\\d+")) {
            logError("Invalid ID format");
            // Nếu không có ID hoặc ID không phải số -> Về trang chủ hoặc báo lỗi
            request.setAttribute("error", "Đường dẫn không hợp lệ.");
            request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
            return;
        }

        try {
            long questionId = Long.parseLong(idParam);
            logError("Parsed questionId: " + questionId);

            // 2. Gọi DAO lấy thông tin chi tiết câu hỏi
            logError("Calling questionDao.getQuestionById(" + questionId + ")");
            QuestionDTO question = questionDao.getQuestionById(questionId);
            logError("Got result from getQuestionById: " + (question != null ? "Found" : "Null"));

            if (question != null) {
                logError("Question found: " + question.getTitle());
                
                // Try to load vote score and answers
                try {
                    logError("Loading vote score for question");
                    int questionScore = voteDao.getVoteScore(questionId, null);
                    question.setScore(questionScore);
                    logError("Question score: " + questionScore);
                } catch (Exception e) {
                    logError("Error loading question score: " + e.getMessage() + " / " + e.getClass().getName());
                    e.printStackTrace();
                }
                
                // Try to load answers
                List answers = new ArrayList();
                try {
                    logError("Loading answers");
                    answers = answerDao.getAnswersByQuestionId(questionId);
                    logError("Loaded " + answers.size() + " answers");
                    
                    // Set vote scores and accepted status for answers
                    Long acceptedId = question.getAcceptedAnswerId();
                    for (Object answerObj : answers) {
                        try {
                            dto.AnswerDTO answer = (dto.AnswerDTO) answerObj;
                            int answerScore = voteDao.getVoteScore(null, answer.getAnswerId());
                            answer.setScore(answerScore);
                            answer.setIsAccepted(acceptedId != null && acceptedId.equals(answer.getAnswerId()));
                        } catch (Exception e) {
                            logError("Error loading answer score: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    logError("Error loading answers: " + e.getMessage() + " / " + e.getClass().getName());
                    e.printStackTrace();
                }
                
                logError("Setting request attributes");
                // 3. Đẩy dữ liệu sang JSP
                request.setAttribute("question", question);
                request.setAttribute("answers", answers);
                
                // Load comments for question
                try {
                    logError("Loading comments for question");
                    java.util.List<dto.CommentDTO> questionComments = commentDao.getCommentsByQuestionId(question.getQuestionId());
                    request.setAttribute("questionComments", questionComments);
                    logError("Loaded " + questionComments.size() + " comments for question");
                } catch (Exception e) {
                    logError("Error loading question comments: " + e.getMessage());
                    request.setAttribute("questionComments", new java.util.ArrayList<>());
                }
                
                // Load comments for each answer
                try {
                    logError("Loading comments for answers");
                    java.util.Map<Long, java.util.List<dto.CommentDTO>> answerComments = new java.util.HashMap<>();
                    for (Object answerObj : answers) {
                        dto.AnswerDTO answer = (dto.AnswerDTO) answerObj;
                        java.util.List<dto.CommentDTO> comments = commentDao.getCommentsByAnswerId(answer.getAnswerId());
                        answerComments.put(answer.getAnswerId(), comments);
                        logError("Loaded " + comments.size() + " comments for answer " + answer.getAnswerId());
                    }
                    request.setAttribute("answerComments", answerComments);
                } catch (Exception e) {
                    logError("Error loading comments: " + e.getMessage());
                    request.setAttribute("answerComments", new java.util.HashMap<>());
                }
                
                try {
                    HttpSession s = request.getSession(false);
                    if (s != null && s.getAttribute("USER") != null) {
                        dto.UserDTO u = (dto.UserDTO) s.getAttribute("USER");
                        request.setAttribute("isQuestionOwner", u.getUserId() == question.getUserId());
                    } else {
                        request.setAttribute("isQuestionOwner", false);
                    }
                } catch (Exception e) {
                    request.setAttribute("isQuestionOwner", false);
                }
                // Load user's vote (if logged in)
                try {
                    HttpSession session = request.getSession(false);
                    if (session != null && session.getAttribute("USER") != null) {
                        dto.UserDTO user = (dto.UserDTO) session.getAttribute("USER");
                        long userId = user.getUserId();
                        
                        // Get user's vote for question
                        String questionUserVote = voteDao.getUserVote(userId, questionId, null);
                        request.setAttribute("questionUserVote", questionUserVote);
                        logError("Question user vote: " + questionUserVote);
                        
                        // Get user's votes for answers
                        java.util.Map<Long, String> answerVotes = new java.util.HashMap<>();
                        for (Object answerObj : answers) {
                            dto.AnswerDTO answer = (dto.AnswerDTO) answerObj;
                            String answerUserVote = voteDao.getUserVote(userId, null, answer.getAnswerId());
                            if (answerUserVote != null) {
                                answerVotes.put(answer.getAnswerId(), answerUserVote);
                            }
                        }
                        request.setAttribute("answerVotes", answerVotes);
                        logError("Loaded answer votes: " + answerVotes.size());
                        
                        // Check if question is bookmarked
                        try {
                            boolean isBookmarked = bookmarkDao.checkIfBookmarked(userId, questionId);
                            request.setAttribute("isBookmarked", isBookmarked);
                            logError("Question is bookmarked: " + isBookmarked);
                        } catch (Exception e) {
                            logError("Error loading bookmark status: " + e.getMessage());
                            request.setAttribute("isBookmarked", false);
                        }
                        
                        // Check if answers are bookmarked
                        try {
                            java.util.Map<Long, Boolean> answerBookmarks = new java.util.HashMap<>();
                            for (Object answerObj : answers) {
                                dto.AnswerDTO answer = (dto.AnswerDTO) answerObj;
                                boolean isAnswerBookmarked = bookmarkDao.checkIfAnswerBookmarked(userId, answer.getAnswerId());
                                answerBookmarks.put(answer.getAnswerId(), isAnswerBookmarked);
                            }
                            request.setAttribute("answerBookmarks", answerBookmarks);
                            logError("Loaded answer bookmarks: " + answerBookmarks.size());
                        } catch (Exception e) {
                            logError("Error loading answer bookmarks: " + e.getMessage());
                            request.setAttribute("answerBookmarks", new java.util.HashMap<>());
                        }
                    }
                } catch (Exception e) {
                    logError("Error loading user votes: " + e.getMessage());
                }
                
                // Load related questions (4 questions max)
                try {
                    logError("Loading related questions");
                    List<QuestionDTO> relatedQuestions = questionDao.getRelatedQuestions(questionId, 4);
                    request.setAttribute("relatedQuestions", relatedQuestions);
                    logError("Loaded " + relatedQuestions.size() + " related questions");
                } catch (Exception e) {
                    logError("Error loading related questions: " + e.getMessage());
                    request.setAttribute("relatedQuestions", new ArrayList<>());
                }
                
                logError("Forwarding to JSP");
                // Chuyển hướng đến file JSP giao diện chi tiết
                request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
                logError("Forward complete");
            } else {
                logError("Question not found, redirecting to home");
                // Trường hợp ID hợp lệ nhưng không tìm thấy câu hỏi trong DB
                request.setAttribute("error", "Không tìm thấy câu hỏi này.");
                request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
            }

        } catch (Exception e) {
            logError("EXCEPTION in doGet: " + e.getMessage() + " / " + e.getClass().getName());
            try {
                java.io.StringWriter sw = new java.io.StringWriter();
                e.printStackTrace(new java.io.PrintWriter(sw));
                logError(sw.toString());
            } catch (Exception e2) {}
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}