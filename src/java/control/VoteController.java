package control;

import dal.VoteDAO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "VoteController", urlPatterns = {"/vote/submit"})
public class VoteController extends HttpServlet {

    private final VoteDAO voteDao = new VoteDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Get user from session
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("USER") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"error\": \"Not authenticated\"}");
                return;
            }

            Object userObj = session.getAttribute("USER");
            long userId = 0;
            
            // Extract userId from user object
            if (userObj instanceof dto.UserDTO) {
                dto.UserDTO user = (dto.UserDTO) userObj;
                userId = user.getUserId();
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"error\": \"Invalid user object\"}");
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
            boolean success = voteDao.addVote(userId, questionId, answerId, voteType);

            if (success) {
                // Get updated score
                int score;
                if (questionId != null) {
                    score = voteDao.getVoteScore(questionId, null);
                } else {
                    score = voteDao.getVoteScore(null, answerId);
                }

                response.setStatus(HttpServletResponse.SC_OK);
                out.println("{\"success\": true, \"score\": " + score + "}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("{\"error\": \"Failed to save vote\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
