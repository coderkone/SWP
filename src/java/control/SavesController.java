/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import dal.BookmarkDAO;
import dto.BookmarkDTO; // Import package dto
import model.User;
import dal.CollectionDAO;
import model.Collection;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author nguye
 */
@WebServlet(name = "SavesServlet", urlPatterns = {"/saves"})
public class SavesController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // Check login
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        BookmarkDAO dao = new BookmarkDAO();
        // Lấy danh sách bookmark
        List<BookmarkDTO> savedList = dao.getBookmarksByUserId(user.getUserId());

        request.setAttribute("savedList", savedList);
        request.setAttribute("savedCount", savedList.size());
        
        //Lấy danh sách list đã tạo
        CollectionDAO colDao = new CollectionDAO();
        List<Collection> myCollections = colDao.getCollectionsByUserId(user.getUserId());
        request.setAttribute("myCollections", myCollections);

        request.getRequestDispatcher("/View/User/saves.jsp").forward(request, response);
    }
}
