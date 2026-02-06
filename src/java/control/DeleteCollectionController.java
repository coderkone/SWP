package control;

import dal.CollectionDAO;
import model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name="DeleteCollectionController", urlPatterns={"/saves/delete"})
public class DeleteCollectionController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/View/User/login.jsp");
            return;
        }
        
        // Lấy ID của list cần xóa
        String idStr = request.getParameter("id");
        
        if (idStr != null) {
            try {
                int collectionId = Integer.parseInt(idStr);
                CollectionDAO dao = new CollectionDAO();
                // Gọi hàm xóa
                dao.deleteCollection(collectionId, user.getUserId());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        // Xóa xong reload lại trang
        response.sendRedirect(request.getContextPath() + "/saves");
    }
}