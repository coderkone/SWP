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

@WebServlet(name="CreateCollectionController", urlPatterns={"/saves/create"})
public class CreateCollectionController extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/View/User/login.jsp");
            return;
        }
        
        // Lấy tên list từ form modal
        String listName = request.getParameter("listName");
        
        if (listName != null && !listName.trim().isEmpty()) {
            CollectionDAO dao = new CollectionDAO();
            dao.createCollection(user.getUserId(), listName.trim());
        }
        
        // Tạo xong thì quay lại trang Saves
        response.sendRedirect(request.getContextPath() + "/saves");
    }
}