package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import dal.QuestionDAO;
import dto.QuestionDTO;

@WebServlet(name = "HomeController", urlPatterns = {"/home"})
public class HomeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            QuestionDAO questionDAO = new QuestionDAO();

            List<QuestionDTO> newestQuestions = questionDAO.getQuestions(1, 10, "newest", null, null);
            int totalQuestions = questionDAO.getTotalQuestions(null, null);

            request.setAttribute("newestQuestions", newestQuestions);
            request.setAttribute("totalQuestions", totalQuestions);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading questions: " + e.getMessage());
        }

        request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
    }
}