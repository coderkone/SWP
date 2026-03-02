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

@WebServlet(name="RemoveBookmarkController", urlPatterns={"/saves/remove"})
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
        
        String qIdStr = request.getParameter("qId");
        if (qIdStr != null) {
            try {
                int questionId = Integer.parseInt(qIdStr);
                BookmarkDAO dao = new BookmarkDAO();
                // Gọi hàm xóa bookmark
                dao.removeBookmark(user.getUserId(), questionId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        // Quay lại trang trước đó (Tránh việc bị văng ra trang "All saves" nếu đang ở trong 1 list cụ thể)
        String referer = request.getHeader("Referer");
        if (referer != null) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect(request.getContextPath() + "/saves");
        }
    }
}