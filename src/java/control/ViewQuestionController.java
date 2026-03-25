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
import java.util.ArrayList;
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
        String query = request.getQueryString();
        String target = request.getContextPath() + "/question/detail"
                + (query != null && !query.isEmpty() ? "?" + query : "");
        response.sendRedirect(target);
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
