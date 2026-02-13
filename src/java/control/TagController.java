package control;

import dal.TagDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "TagController", urlPatterns = {"/tags"})
public class TagController extends HttpServlet {
    
    private final TagDAO tagDAO = new TagDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String search = request.getParameter("search");
            List<Map<String, Object>> tags;
            
            if (search != null && !search.trim().isEmpty()) {
                tags = tagDAO.getTagsByName(search.trim());
            } else {
                tags = tagDAO.getAllTags();
            }
            
            request.setAttribute("tags", tags);
            request.setAttribute("search", search);
            request.getRequestDispatcher("/View/User/tags-list.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            try {
                request.getRequestDispatcher("/View/User/tags-list.jsp").forward(request, response);
            } catch (ServletException ex) {
                ex.printStackTrace();
            }
        }
    }
}
