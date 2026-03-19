package control;

import dal.QuestionDAO;
import dto.QuestionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "HomeController", urlPatterns = {"/home"})
public class HomeController extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("q");
        String tab = request.getParameter("tab");
        String filter = request.getParameter("filter");
        String pageParam = request.getParameter("page");

        if ("unanswered".equals(filter)) {
            tab = "newest";
        } else if ("active".equals(tab) || "newest".equals(tab) || "voted".equals(tab)) {
            filter = "all";
        }

        if (keyword == null) {
            keyword = "";
        }
        if (tab == null || tab.isBlank()) {
            tab = "newest";
        }
        if (filter == null || filter.isBlank()) {
            filter = "all";
        }

        int page = 1;
        if (pageParam != null && !pageParam.isBlank()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException ignored) {
                page = 1;
            }
        }

        try {
            QuestionDAO questionDAO = new QuestionDAO();
            List<QuestionDTO> questions = questionDAO.getQuestions(page, PAGE_SIZE, tab, keyword, filter);
            int totalQuestions = questionDAO.getTotalQuestions(keyword, filter);
            int totalPage = (int) Math.ceil((double) totalQuestions / PAGE_SIZE);

            request.setAttribute("questions", questions);
            request.setAttribute("totalQuestions", totalQuestions);
            request.setAttribute("currentKeyword", keyword);
            request.setAttribute("currentSort", tab);
            request.setAttribute("currentFilter", filter);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPage", totalPage);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading questions: " + e.getMessage());
            request.setAttribute("questions", Collections.emptyList());
            request.setAttribute("totalQuestions", 0);
            request.setAttribute("currentKeyword", keyword);
            request.setAttribute("currentSort", tab);
            request.setAttribute("currentFilter", filter);
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPage", 0);
        }

        request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
    }
}