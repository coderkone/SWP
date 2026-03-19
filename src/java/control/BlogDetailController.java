package control;

import dal.BlogDAO;
import dal.BlogCommentDAO;
import model.Blog;
import model.BlogComment;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "BlogDetailController", urlPatterns = {"/blog/detail"})
public class BlogDetailController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        try {
            // 1. Get the blog ID from the URL (e.g., /blog/detail?id=5)
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.isEmpty()) {
                // Redirect to home if ID is missing
                response.sendRedirect(request.getContextPath() + "/blog");
                return;
            }
            
            int blogId = Integer.parseInt(idStr);
            BlogDAO blogDao = new BlogDAO();
            
            // 2. Increase the View Count by 1
            blogDao.increaseViewCount(blogId);

            // 3. Fetch the Blog details
            Blog blog = blogDao.getBlogById(blogId);

            // If the blog doesn't exist or is not PUBLISHED (status != 1), redirect back
            if (blog == null) {
                response.sendRedirect(request.getContextPath() + "/blog");
                return;
            }

            // 4. Fetch the Comment Tree
            BlogCommentDAO commentDao = new BlogCommentDAO();
            List<BlogComment> rootComments = commentDao.getCommentTreeByBlogId(blogId);

            // 5. Send data to JSP
            request.setAttribute("blog", blog);
            request.setAttribute("rootComments", rootComments);

            // 6. Forward to the view
            request.getRequestDispatcher("/View/User/blogDetail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            // Handle cases where someone types text instead of a number in the URL
            response.sendRedirect(request.getContextPath() + "/blog");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("System Error while loading Blog details: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}