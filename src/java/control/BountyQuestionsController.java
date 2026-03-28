package control;

import dal.QuestionDAO;
import dto.QuestionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "BountyQuestionsController", urlPatterns = {"/bounty/questions"})
public class BountyQuestionsController extends HttpServlet {

    private final QuestionDAO questionDao = new QuestionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sort = request.getParameter("sort");
        if (!"expiring".equalsIgnoreCase(sort)
                && !"newest".equalsIgnoreCase(sort)
                && !"amount".equalsIgnoreCase(sort)) {
            sort = "amount";
        }

        List<QuestionDTO> bountyQuestions = questionDao.getActiveBountyQuestions(sort);
        request.setAttribute("bountyQuestions", bountyQuestions);
        request.setAttribute("currentSort", sort);
        request.setAttribute("popularTags", questionDao.getPopularTags(10));
        request.getRequestDispatcher("/View/User/bounty-questions.jsp").forward(request, response);
    }
}