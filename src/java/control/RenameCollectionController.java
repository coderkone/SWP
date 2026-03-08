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

@WebServlet(name="RenameCollectionController", urlPatterns={"/saves/rename"})
public class RenameCollectionController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/View/User/login.jsp");
            return;
        }

        String collectionIdStr = request.getParameter("collectionId");
        String newName = request.getParameter("newName");

        if (collectionIdStr != null && newName != null && !newName.trim().isEmpty()) {
            try {
                int collectionId = Integer.parseInt(collectionIdStr);
                CollectionDAO dao = new CollectionDAO();
                dao.renameCollection(collectionId, user.getUserId(), newName.trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Quay lại trang hiện tại sau khi đổi tên xong
        String referer = request.getHeader("Referer");
        if (referer != null) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect(request.getContextPath() + "/saves");
        }
    }
}