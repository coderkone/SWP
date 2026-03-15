package control;

import dal.VoteDAO;
import dal.QuestionDAO;
import dal.AnswerDAO;
import dto.UserDTO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "VoteController", urlPatterns = {"/vote/submit"})
public class VoteController extends HttpServlet {

    private final VoteDAO voteDao = new VoteDAO();
    private final QuestionDAO questionDao = new QuestionDAO();
    private final AnswerDAO answerDao = new AnswerDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Get user from session
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"error\": \"Not authenticated\"}");
                return;
            }

            Object principal = session.getAttribute("user");
            long userId;
            if (principal instanceof UserDTO) {
                userId = ((UserDTO) principal).getUserId();
            } else if (principal instanceof User) {
                userId = ((User) principal).getUserId();
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"error\": \"Invalid user session\"}");
                return;
            }

            // Get parameters
            String questionIdParam = request.getParameter("questionId");
            String answerIdParam = request.getParameter("answerId");
            String voteType = request.getParameter("voteType"); // "upvote" or "downvote"

            // Debug log all parameters
            System.out.println("Vote request - questionIdParam: " + questionIdParam + 
                             ", answerIdParam: " + answerIdParam + 
                             ", voteType: " + voteType);

            // Normalize null strings
            if ("null".equals(questionIdParam)) questionIdParam = null;
            if ("null".equals(answerIdParam)) answerIdParam = null;

            // Validate - must have exactly one (question or answer) ID, not both
            boolean hasQuestionId = questionIdParam != null && !questionIdParam.trim().isEmpty();
            boolean hasAnswerId = answerIdParam != null && !answerIdParam.trim().isEmpty();
            if (!hasQuestionId && !hasAnswerId) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Must provide either questionId or answerId\"}");
                return;
            }
            if (hasQuestionId && hasAnswerId) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Provide either questionId or answerId, not both\"}");
                return;
            }

            if (voteType == null || voteType.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"voteType is required\"}");
                System.out.println("Vote validation failed: voteType is null/empty");
                return;
            }

            Long questionId = null;
            Long answerId = null;

            try {
                if (questionIdParam != null && !questionIdParam.trim().isEmpty()) {
                    questionId = Long.parseLong(questionIdParam);
                }

                if (answerIdParam != null && !answerIdParam.trim().isEmpty()) {
                    answerId = Long.parseLong(answerIdParam);
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Invalid ID format: " + e.getMessage() + "\"}");
                return;
            }

            // Validate voteType
            if (!voteType.equals("upvote") && !voteType.equals("downvote")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Invalid vote type. Must be 'upvote' or 'downvote'\"}");
                return;
            }

            // Add or update vote
            Long relatedQuestionId = null;
            if (questionId != null) {
                relatedQuestionId = questionId;
            } else if (answerId != null) {
                dto.AnswerDTO answer = answerDao.getAnswerById(answerId);
                if (answer != null) {
                    relatedQuestionId = answer.getQuestionId();
                }
            }

            if (relatedQuestionId != null && questionDao.isQuestionClosed(relatedQuestionId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.println("{\"error\": \"Question is closed\"}");
                return;
            }

            VoteDAO.VoteActionResult voteResult = voteDao.submitVote(userId, questionId, answerId, voteType);

            if (voteResult.isSuccess()) {
                refreshSessionReputation(session, voteResult.getVoterReputation());
                response.setStatus(HttpServletResponse.SC_OK);
                out.println("{\"success\": true, \"score\": " + voteResult.getScore()
                        + ", \"currentVote\": " + jsonString(voteResult.getCurrentVoteType())
                        + ", \"voterReputation\": " + voteResult.getVoterReputation() + "}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("{\"error\": \"Failed to save vote\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            msg = msg.replace("\\", "\\\\").replace("\"", "\\\"");
            out.println("{\"error\": \"" + msg + "\"}");
        }
    }

    private void refreshSessionReputation(HttpSession session, int newReputation) {
        if (session == null) {
            return;
        }

        Object principal = session.getAttribute("user");
        if (principal instanceof UserDTO) {
            ((UserDTO) principal).setReputation(newReputation);
        } else if (principal instanceof User) {
            ((User) principal).setReputation(newReputation);
        }
    }

    private String jsonString(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
