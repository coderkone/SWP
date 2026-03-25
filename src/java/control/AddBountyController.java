package control;

import dal.QuestionDAO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import model.User;

@WebServlet(name = "AddBountyController", urlPatterns = {"/question/bounty"})
public class AddBountyController extends HttpServlet {

    private static final Set<Integer> ALLOWED_BOUNTY_AMOUNTS = new HashSet<>(Arrays.asList(50, 100, 150, 200, 250, 500));
    private final QuestionDAO questionDao = new QuestionDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Object principal = session == null ? null : session.getAttribute("user");
        Long userId = extractUserId(principal);

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String questionIdParam = request.getParameter("questionId");
        String amountParam = request.getParameter("amount");
        long questionId;
        int amount;

        try {
            questionId = Long.parseLong(questionIdParam);
            amount = Integer.parseInt(amountParam);
        } catch (Exception ex) {
            redirectWithError(request, response, questionIdParam, "Invalid bounty request");
            return;
        }

        if (!ALLOWED_BOUNTY_AMOUNTS.contains(amount)) {
            redirectWithError(request, response, String.valueOf(questionId), "Invalid bounty amount");
            return;
        }

        try {
            QuestionDAO.BountyPlacementResult result = questionDao.placeBounty(questionId, userId, amount);
            refreshSessionReputation(session, result.getUpdatedReputation());
            response.sendRedirect(request.getContextPath() + "/question/detail?id=" + questionId + "&bounty=success");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectWithError(request, response, String.valueOf(questionId), ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            redirectWithError(request, response, String.valueOf(questionId), "Unable to add bounty right now");
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

    private Long extractUserId(Object principal) {
        if (principal instanceof UserDTO) {
            return ((UserDTO) principal).getUserId();
        }
        if (principal instanceof User) {
            return ((User) principal).getUserId();
        }
        return null;
    }

    private void redirectWithError(HttpServletRequest request,
            HttpServletResponse response,
            String questionId,
            String message) throws IOException {
        String safeQuestionId = questionId != null && questionId.matches("\\d+") ? questionId : "";
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/question/detail?id=" + safeQuestionId + "&bountyError=" + encodedMessage);
    }
}