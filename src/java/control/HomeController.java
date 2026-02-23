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
            
            System.out.println("DEBUG: HomeController - Loading questions...");
            
            // Lấy danh sách câu hỏi mới nhất (page 1, 10 items, sort by newest)
            List<QuestionDTO> newestQuestions = questionDAO.getQuestions(1, 10, "newest", null, null);
            
            System.out.println("DEBUG: Loaded questions count: " + (newestQuestions != null ? newestQuestions.size() : "null"));
            if (newestQuestions != null && !newestQuestions.isEmpty()) {
                for (QuestionDTO q : newestQuestions) {
                    System.out.println("  - Q" + q.getQuestionId() + ": " + q.getTitle());
                }
            }
            
            // Lấy tổng số câu hỏi
            int totalQuestions = questionDAO.getTotalQuestions(null, null);
            System.out.println("DEBUG: Total questions in DB: " + totalQuestions);
            
            // Đưa dữ liệu vào request attribute
            request.setAttribute("newestQuestions", newestQuestions);
            request.setAttribute("totalQuestions", totalQuestions);
            
        } catch (Exception e) {
            System.err.println("ERROR in HomeController: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading questions: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
    }
}
