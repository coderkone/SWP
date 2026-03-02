/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import dal.BookmarkDAO;
import dto.BookmarkDTO; // Import package dto
import model.User;
import dal.CollectionDAO;
import dal.UserDAO;
import dto.UserDTO;
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

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/View/User/login.jsp");
            return;
        }
        UserDAO userDao = new UserDAO();
        UserDTO userProfile = userDao.getUserProfileById(user.getUserId());
        request.setAttribute("userProfile", userProfile);
        try {
            CollectionDAO colDao = new CollectionDAO();
            BookmarkDAO bmDao = new BookmarkDAO();

            // 1. Lấy toàn bộ danh sách Collection đưa ra Sidebar trước
            List<Collection> myCollections = colDao.getCollectionsByUserId(user.getUserId());
            if (myCollections == null) {
                myCollections = java.util.Collections.emptyList();
            }
            request.setAttribute("myCollections", myCollections);

            // 2. Kiểm tra xem người dùng đang click vào List nào (Lấy listId từ URL)
            String listIdStr = request.getParameter("listId");
            List<BookmarkDTO> savedList;
            String currentListName = "All saves"; // Mặc định tiêu đề là All saves

            if (listIdStr != null && !listIdStr.isEmpty()) {
                int listId = Integer.parseInt(listIdStr);
                // Lọc bài viết theo Collection
                savedList = bmDao.getBookmarksByCollection(user.getUserId(), listId);
                request.setAttribute("activeListId", listId); // Đánh dấu ID đang chọn để tô đậm ở Sidebar

                // Tìm tên của List đang chọn để in ra làm Tiêu đề
                for (Collection c : myCollections) {
                    if (c.getCollectionId() == listId) {
                        currentListName = c.getName();
                        break;
                    }
                }
            } else {
                // Nếu không có listId, lấy TẤT CẢ
                savedList = bmDao.getBookmarksByUserId(user.getUserId());
                request.setAttribute("activeListId", "");
            }

            // 3. Gửi dữ liệu ra giao diện
            request.setAttribute("savedList", savedList);
            request.setAttribute("savedCount", savedList.size());
            request.setAttribute("currentListName", currentListName); // Truyền tiêu đề ra JSP

            request.getRequestDispatcher("/View/User/saves.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
