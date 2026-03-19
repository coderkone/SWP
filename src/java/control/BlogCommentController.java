package control;

import dal.BlogCommentDAO;
import model.BlogComment;
import model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "BlogCommentController", urlPatterns = {"/blog/comment"})
public class BlogCommentController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "auth/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) action = "add";
        
        String blogIdStr = request.getParameter("blogId");

        try {
            int blogId = Integer.parseInt(blogIdStr);
            BlogCommentDAO commentDao = new BlogCommentDAO();

            if ("add".equals(action)) {
                String content = request.getParameter("content");
                String parentIdStr = request.getParameter("parentId");
                
                BlogComment comment = new BlogComment();
                comment.setBlogId(blogId);
                comment.setUserId(user.getUserId());
                comment.setContent(content);
                
                if (parentIdStr != null && !parentIdStr.isEmpty()) {
                    comment.setParentId(Integer.parseInt(parentIdStr));
                }
                if (commentDao.insertComment(comment)) {
                    commentDao.increaseCommentCount(blogId);
                }

            } else if ("edit".equals(action)) {
                String commentIdStr = request.getParameter("commentId");
                if (commentIdStr != null && !commentIdStr.isEmpty()) {
                    int commentId = Integer.parseInt(commentIdStr);
                    String content = request.getParameter("content");
                    commentDao.updateComment(commentId, user.getUserId(), content);
                }

            } else if ("delete".equals(action)) {
                String commentIdStr = request.getParameter("commentId");
                if (commentIdStr != null && !commentIdStr.isEmpty()) {
                    int commentId = Integer.parseInt(commentIdStr);
                    commentDao.deleteComment(commentId, user.getUserId());
                    commentDao.syncCommentCount(blogId);
                }
            }

            // Xử lý thành công, luôn quay lại trang bài viết hiện tại
            response.sendRedirect(request.getContextPath() + "/blog/detail?id=" + blogId);

        } catch (Exception e) {
            System.out.println("=== LỖI TẠI BLOG COMMENT CONTROLLER ===");
            e.printStackTrace();
            
            // Dù có lỗi ngầm, vẫn cố gắng giữ người dùng lại trang detail chứ không văng ra blogHome
            if (blogIdStr != null && !blogIdStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/blog/detail?id=" + blogIdStr);
            } else {
                response.sendRedirect(request.getContextPath() + "/blog");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}