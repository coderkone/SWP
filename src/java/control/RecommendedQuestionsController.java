package control;

import dal.QuestionDAO;
import dto.QuestionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "RecommendedQuestionsController", urlPatterns = {"/recommended-questions"})
public class RecommendedQuestionsController extends HttpServlet {

    private static final int RECOMMENDATION_LIMIT = 12;
    private final QuestionDAO questionDao = new QuestionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        List<Long> viewedIds = getViewedQuestionIds(session);

        List<QuestionDTO> recommendedQuestions = questionDao.getRecommendedQuestions(viewedIds, RECOMMENDATION_LIMIT);
        List<String> profileTags = questionDao.extractTagsFromViewed(viewedIds);
        List<String> profileKeywords = questionDao.extractKeywordsFromViewed(viewedIds, 8);

        request.setAttribute("recommendedQuestions", recommendedQuestions);
        request.setAttribute("profileTags", profileTags);
        request.setAttribute("profileKeywords", profileKeywords);
        request.setAttribute("viewedHistoryCount", viewedIds.size());
        request.setAttribute("hasHistory", !viewedIds.isEmpty());
        request.setAttribute("popularTags", questionDao.getPopularTags(10));

        request.getRequestDispatcher("/View/User/recommended-questions.jsp").forward(request, response);
    }

    @SuppressWarnings("unchecked")
    private List<Long> getViewedQuestionIds(HttpSession session) {
        if (session == null) {
            return Collections.emptyList();
        }

        Object history = session.getAttribute("viewedQuestionIds");
        if (!(history instanceof List<?>)) {
            return Collections.emptyList();
        }

        List<Long> result = new ArrayList<>();
        for (Object item : (List<?>) history) {
            if (item instanceof Long) {
                result.add((Long) item);
            } else if (item instanceof Integer) {
                result.add(((Integer) item).longValue());
            }
        }
        return result;
    }
}
