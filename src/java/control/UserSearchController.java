package control;

import dal.BlogCommentDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "UserSearchController", urlPatterns = {"/api/users"})
public class UserSearchController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Lấy blogId từ request
            String blogIdStr = request.getParameter("blogId");
            List<String> users = new ArrayList<>();

            if (blogIdStr != null && !blogIdStr.isEmpty()) {
                int blogId = Integer.parseInt(blogIdStr);
                // Gọi DAO lấy danh sách người đã comment trong blog này
                BlogCommentDAO dao = new BlogCommentDAO();
                users = dao.getCommentersByBlogId(blogId);
            }

            // Chuyển đổi sang JSON
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < users.size(); i++) {
                String username = users.get(i);
                json.append("{\"key\": \"").append(username).append("\", \"value\": \"").append(username).append("\"}");
                if (i < users.size() - 1) {
                    json.append(", ");
                }
            }
            json.append("]");

            response.getWriter().write(json.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("[]");
        }
    }
}