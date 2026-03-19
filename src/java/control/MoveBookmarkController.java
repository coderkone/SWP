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

@WebServlet(name="MoveBookmarkController", urlPatterns={"/saves/move"})
public class MoveBookmarkController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/View/User/login.jsp");
            return;
        }

        String questionIdStr = request.getParameter("questionId");
        // collectionId có thể rỗng nếu user chọn chuyển ra "All saves"
        String newCollectionId = request.getParameter("collectionId"); 

        if (questionIdStr != null && !questionIdStr.isEmpty()) {
            int questionId = Integer.parseInt(questionIdStr);
            BookmarkDAO dao = new BookmarkDAO();
            dao.moveBookmark(user.getUserId(), questionId, newCollectionId);
        }

        // Điều hướng thông minh quay lại đúng trang cũ
        String referer = request.getHeader("Referer");
        if (referer != null) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect(request.getContextPath() + "/saves");
        }
    }
}