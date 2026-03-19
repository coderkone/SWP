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

        try {
            BookmarkDAO bmDao = new BookmarkDAO();
            CollectionDAO colDao = new CollectionDAO();
            UserDAO userDao = new UserDAO();

            // 1. Lấy thông tin Profile người dùng
            UserDTO userProfile = userDao.getUserProfileById(user.getUserId());
            request.setAttribute("userProfile", userProfile);

            // 2. Nhận tham số phân trang từ URL
            int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
            int colPage = request.getParameter("colPage") != null ? Integer.parseInt(request.getParameter("colPage")) : 1;
            String listIdStr = request.getParameter("listId");

            // 3. Xử lý phân trang cho Sidebar Collections (10 cái/trang)
            int totalCols = colDao.countTotalCollections(user.getUserId());
            int totalColPages = (int) Math.ceil((double) totalCols / 10);
            List<model.Collection> myCollections = colDao.getCollectionsByPage(user.getUserId(), colPage);

            // 4. Xử lý phân trang cho Bookmarks (10 bài/trang)
            int totalItems = bmDao.countTotalBookmarks(user.getUserId(), listIdStr);
            int totalItemPages = (int) Math.ceil((double) totalItems / 10);
            List<BookmarkDTO> savedList = bmDao.getBookmarksByPage(user.getUserId(), listIdStr, page);

            // 5. Xác định tiêu đề hiện tại (All saves hoặc tên Collection)
            String currentListName = "All saves";
            if (listIdStr != null && !listIdStr.isEmpty() && !listIdStr.equals("null")) {
                int listId = Integer.parseInt(listIdStr);
                request.setAttribute("activeListId", listId);
                // Lấy tên của collection đang chọn từ danh sách đã load
                for (model.Collection c : myCollections) {
                    if (c.getCollectionId() == listId) {
                        currentListName = c.getName();
                        break;
                    }
                }
            } else {
                request.setAttribute("activeListId", "");
            }

            // 6. Gửi TẤT CẢ các biến cần thiết ra JSP
            request.setAttribute("myCollections", myCollections);
            request.setAttribute("savedList", savedList);
            request.setAttribute("savedCount", totalItems);
            request.setAttribute("currentListName", currentListName);

            request.setAttribute("currentPage", page);
            request.setAttribute("totalItemPages", totalItemPages == 0 ? 1 : totalItemPages);
            request.setAttribute("currentColPage", colPage);
            request.setAttribute("totalColPages", totalColPages == 0 ? 1 : totalColPages);

            request.getRequestDispatcher("/View/User/saves.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Ném lỗi ra trình duyệt để không bị trắng trang
            throw new ServletException("Lỗi hệ thống khi tải trang Saves: " + e.getMessage(), e);
        }
    }
}
