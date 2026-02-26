package control;

import dal.AnswerDAO;
import dal.QuestionDAO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "AcceptAnswerController", urlPatterns = {"/answer/accept"})
public class AcceptAnswerController extends HttpServlet {

    private final QuestionDAO questionDao = new QuestionDAO();
    private final AnswerDAO answerDao = new AnswerDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("USER") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\": false, \"error\": \"Not authenticated\"}");
                return;
            }

            UserDTO user = (UserDTO) session.getAttribute("USER");
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\": false, \"error\": \"Invalid user\"}");
                return;
            }

            String questionIdParam = request.getParameter("questionId");
            String answerIdParam = request.getParameter("answerId");

            if (questionIdParam == null || questionIdParam.trim().isEmpty() ||
                answerIdParam == null || answerIdParam.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"error\": \"questionId and answerId required\"}");
                return;
            }

            long questionId = Long.parseLong(questionIdParam);
            long answerId = Long.parseLong(answerIdParam);

            dto.QuestionDTO question = questionDao.getQuestionById(questionId);
            if (question == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"error\": \"Question not found\"}");
                return;
            }

            if (question.getUserId() != user.getUserId()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"error\": \"Only the question owner can accept answers\"}");
                return;
            }

            dto.AnswerDTO answer = answerDao.getAnswerById(answerId);
            if (answer == null || answer.getQuestionId() != questionId) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"error\": \"Invalid answer for this question\"}");
                return;
            }

            boolean ok = questionDao.toggleAcceptAnswer(questionId, answerId, user.getUserId());
            if (!ok) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"error\": \"Failed to update\"}");
                return;
            }

            Long newAccepted = questionDao.getQuestionById(questionId).getAcceptedAnswerId();
            boolean isNowAccepted = newAccepted != null && newAccepted == answerId;
            out.print("{\"success\": true, \"accepted\": " + isNowAccepted + "}");

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"error\": \"Invalid ID format\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            msg = msg.replace("\\", "\\\\").replace("\"", "\\\"");
            out.print("{\"success\": false, \"error\": \"" + msg + "\"}");
        }
    }
}
