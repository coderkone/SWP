package control;

import dal.AnswerDAO;
import dal.QuestionDAO;
import dal.VoteDAO;
import dto.AnswerDTO;
import dto.QuestionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "QuestionDetailController", urlPatterns = {"/question/detail"})
public class QuestionDetailController extends HttpServlet {

    private final QuestionDAO questionDao = new QuestionDAO();
    private final AnswerDAO answerDao = new AnswerDAO();
    private final VoteDAO voteDao = new VoteDAO();

    private void logError(String msg) {
        try {
            PrintWriter fw = new PrintWriter(new java.io.FileWriter("C:/debug_question_detail.txt", true));
            fw.println("[" + new java.util.Date() + "] " + msg);
            fw.close();
        } catch (Exception e) {
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logError("=== Request received for /question/detail ===");

        String idParam = request.getParameter("id");
        logError("ID parameter: " + idParam);

        if (idParam == null || !idParam.matches("\\d+")) {
            logError("Invalid ID format");
            request.setAttribute("error", "Đường dẫn không hợp lệ.");
            request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
            return;
        }

        try {
            long questionId = Long.parseLong(idParam);
            logError("Parsed questionId: " + questionId);

            QuestionDTO question = questionDao.getQuestionById(questionId);
            logError("Got result from getQuestionById: " + (question != null ? "Found" : "Null"));

            if (question != null) {
                logError("Question found: " + question.getTitle());

                try {
                    questionDao.incrementViewCount(questionId);
                } catch (Exception e) {
                    logError("Error incrementing view count: " + e.getMessage());
                }

                try {
                    int questionScore = voteDao.getVoteScore(questionId, null);
                    question.setScore(questionScore);
                    logError("Question score: " + questionScore);
                } catch (Exception e) {
                    logError("Error loading question score: " + e.getMessage());
                    e.printStackTrace();
                }

                List<AnswerDTO> answers = new ArrayList<>();
                try {
                    answers = answerDao.getAnswersByQuestionId(questionId);
                    logError("Loaded " + answers.size() + " answers");

                    for (AnswerDTO answer : answers) {
                        try {
                            int answerScore = voteDao.getVoteScore(null, answer.getAnswerId());
                            answer.setScore(answerScore);
                        } catch (Exception e) {
                            logError("Error loading answer score: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    logError("Error loading answers: " + e.getMessage());
                    e.printStackTrace();
                }

                request.setAttribute("question", question);
                request.setAttribute("answers", answers);
                request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);

            } else {
                logError("Question not found, redirecting to home");
                request.setAttribute("error", "Không tìm thấy câu hỏi này.");
                request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
            }

        } catch (Exception e) {
            logError("EXCEPTION in doGet: " + e.getMessage() + " / " + e.getClass().getName());
            try {
                java.io.StringWriter sw = new java.io.StringWriter();
                e.printStackTrace(new java.io.PrintWriter(sw));
                logError(sw.toString());
            } catch (Exception ignored) {
            }
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}