package control;

import dal.VoteDAO;
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
            if (session == null || session.getAttribute("user") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"error\": \"Not authenticated\"}");
                return;
            }

            Object userObj = session.getAttribute("user");
            long userId = 0;
            
            // Extract userId from user object
            if (userObj instanceof java.util.Map) {
                java.util.Map userMap = (java.util.Map) userObj;
                userId = ((Number) userMap.get("userId")).longValue();
            } else {
                try {
                    userId = (Long) userObj.getClass().getMethod("getUserId").invoke(userObj);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.println("{\"error\": \"Not authenticated\"}");
                    return;
                }
            }

            // Get parameters
            String questionIdParam = request.getParameter("questionId");
            String answerIdParam = request.getParameter("answerId");
            String voteType = request.getParameter("voteType"); // "upvote" or "downvote"

            // Validate
            if ((questionIdParam == null && answerIdParam == null) || voteType == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Invalid parameters\"}");
                return;
            }

            Long questionId = null;
            Long answerId = null;

            if (questionIdParam != null && !questionIdParam.isEmpty()) {
                questionId = Long.parseLong(questionIdParam);
            }

            if (answerIdParam != null && !answerIdParam.isEmpty()) {
                answerId = Long.parseLong(answerIdParam);
            }

            // Validate voteType
            if (!voteType.equals("upvote") && !voteType.equals("downvote")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Invalid vote type\"}");
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
