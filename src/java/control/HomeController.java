package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import dal.QuestionDAO;
import dto.QuestionDTO;

@WebServlet(name="HomeController", urlPatterns={"/home"})
public class HomeController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            QuestionDAO questionDAO = new QuestionDAO();
            
            // Get pagination parameters
            String pageParam = request.getParameter("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            
            String tab = request.getParameter("tab");
            if (tab == null) tab = "newest";
            
            String keyword = request.getParameter("q");
            if (keyword == null) keyword = "";
            
            // Load questions (10 per page)
            List<QuestionDTO> questions = questionDAO.getQuestions(page, 10, tab, keyword, "all");
            
            // Get total count for pagination
            int totalQuestions = questionDAO.getTotalQuestions(keyword, "all");
            int totalPages = (totalQuestions + 9) / 10; // Ceiling division
            
            // Set attributes for JSP
            request.setAttribute("questions", questions);
            request.setAttribute("currentSort", tab);
            request.setAttribute("currentKeyword", keyword);
            request.setAttribute("currentFilter", "all");
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPage", totalPages);
            
        } catch (Exception e) {
            System.err.println("ERROR in HomeController: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("questions", new java.util.ArrayList<>());
            request.setAttribute("totalPage", 1);
            request.setAttribute("currentPage", 1);
        }
        
        request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
    }
}
