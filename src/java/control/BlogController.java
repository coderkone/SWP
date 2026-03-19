package control;

import dal.BlogDAO;
import model.Blog;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "BlogController", urlPatterns = {"/blog"})
public class BlogController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        try {
            // 1. Get Search Parameter
            String search = request.getParameter("search");
            if (search != null) {
                search = search.trim();
            }

            // 2. Get Sort Parameter
            String sort = request.getParameter("sort");
            if (sort == null || sort.isEmpty()) {
                sort = "newest"; // Default sort
            }

            // 3. Get Pagination Parameter
            int page = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.isEmpty()) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }

            // 4. Initialize DAO and calculate pagination
            BlogDAO blogDao = new BlogDAO();
            
            int totalBlogs = blogDao.countTotalBlogs(search);
            int totalPages = (int) Math.ceil((double) totalBlogs / 9.0); // 9 items per page

            // Fetch the data
            List<Blog> blogList = blogDao.getBlogsByPage(page, search, sort);

            // 5. Send data to JSP
            request.setAttribute("blogList", blogList);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages == 0 ? 1 : totalPages);
            
            // Keep the search and sort values in the UI inputs
            request.setAttribute("searchParam", search);
            request.setAttribute("currentSort", sort);

            // 6. Forward to the View
            request.getRequestDispatcher("/View/User/blogHome.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("System error while loading the Blog page: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}