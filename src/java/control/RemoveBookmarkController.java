package control;

import dal.BookmarkDAO;
import model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "RemoveBookmarkController", urlPatterns = {"/saves/remove"})
public class RemoveBookmarkController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/View/User/login.jsp");
            return;
        }

        String qIdStr = request.getParameter("questionId");
        if (qIdStr != null) {
            try {
                int questionId = Integer.parseInt(qIdStr);
                // 1. Lấy thêm tham số thư mục đang đứng từ JSP gửi lên
                String fromCollectionId = request.getParameter("fromCollectionId");
                BookmarkDAO dao = new BookmarkDAO();
                // 2. Rẽ nhánh logic xóa
                if (fromCollectionId != null && !fromCollectionId.trim().isEmpty() && !fromCollectionId.equals("all")) {
                    // Nếu đang ở trong một thư mục cụ thể -> Đẩy bài viết ra ngoài (về All Saves)
                    dao.removeFromCollection(user.getUserId(), questionId);
                } else {
                    // Nếu đang ở ngoài All Saves -> Xóa vĩnh viễn khỏi DB
                    dao.removeBookmark(user.getUserId(), questionId);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        String referer = request.getHeader("Referer");
        if (referer != null) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect(request.getContextPath() + "/saves");
        }
    }
}
