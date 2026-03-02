package control;

import dal.UserDAO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "UserManagementController", urlPatterns = {
    "/admin/users",
    "/admin/users/create",
    "/admin/users/edit",
    "/admin/users/toggle-status",
    "/admin/users/search"
})
public class UserManagementController extends HttpServlet {

    private final UserDAO dao = new UserDAO();
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {
            case "/admin/users":
                handleList(request, response);
                break;
            case "/admin/users/create":
                handleCreateForm(request, response);
                break;
            case "/admin/users/edit":
                handleEditForm(request, response);
                break;
            case "/admin/users/search":
                handleSearch(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {
            case "/admin/users/create":
                handleCreateSubmit(request, response);
                break;
            case "/admin/users/edit":
                handleEditSubmit(request, response);
                break;
            case "/admin/users/toggle-status":
                handleToggleStatus(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    // Hiển thị danh sách users với pagination và filter
    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // Filter parameters
        String filterRole = request.getParameter("role");
        String filterStatus = request.getParameter("status");

        // Use filter methods
        List<UserDTO> users = dao.getUsersByFilter(filterRole, filterStatus, page, PAGE_SIZE);
        int totalUsers = dao.getUserCountByFilter(filterRole, filterStatus);
        int totalPages = (int) Math.ceil((double) totalUsers / PAGE_SIZE);

        request.setAttribute("users", users);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("filterRole", filterRole);
        request.setAttribute("filterStatus", filterStatus);

        request.getRequestDispatcher("/View/Admin/user-list.jsp").forward(request, response);
    }

    // Form tạo user mới
    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/View/Admin/user-create.jsp").forward(request, response);
    }

    // Xử lý tạo user
    private void handleCreateSubmit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String role = request.getParameter("role");

        // Validation
        if (username == null || username.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.length() < 8
                || !password.equals(confirmPassword)) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin. Password >= 8 ký tự và phải khớp.");
            request.getRequestDispatcher("/View/Admin/user-create.jsp").forward(request, response);
            return;
        }

        // Validate role
        if (!isValidRole(role)) {
            role = "member";
        }

        try {
            if (dao.usernameExists(username)) {
                request.setAttribute("error", "Username đã tồn tại.");
                request.getRequestDispatcher("/View/Admin/user-create.jsp").forward(request, response);
                return;
            }

            if (dao.emailExists(email)) {
                request.setAttribute("error", "Email đã tồn tại.");
                request.getRequestDispatcher("/View/Admin/user-create.jsp").forward(request, response);
                return;
            }

            boolean success = dao.createUser(username.trim(), email.trim(), password, role);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/users?success=created");
            } else {
                request.setAttribute("error", "Không thể tạo user. Vui lòng thử lại.");
                request.getRequestDispatcher("/View/Admin/user-create.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/View/Admin/user-create.jsp").forward(request, response);
        }
    }

    // Form edit user
    private void handleEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        try {
            long userId = Long.parseLong(idParam);
            UserDTO user = dao.getUserById(userId);

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=notfound");
                return;
            }

            request.setAttribute("editUser", user);
            request.getRequestDispatcher("/View/Admin/user-edit.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    // Xử lý edit user
    private void handleEditSubmit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        String role = request.getParameter("role");
        String status = request.getParameter("status");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        try {
            long userId = Long.parseLong(idParam);

            // Prevent self-deactivation
            HttpSession session = request.getSession();
            UserDTO currentUser = (UserDTO) session.getAttribute("USER");
            if (currentUser != null && currentUser.getUserId() == userId && "inactive".equals(status)) {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=self-deactivate");
                return;
            }

            // Validate role and status
            if (!isValidRole(role)) {
                role = "member";
            }
            if (!isValidStatus(status)) {
                status = "active";
            }

            boolean success = dao.updateUser(userId, role, status);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/users?success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=update-failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    // Toggle status (active <-> inactive)
    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        try {
            long userId = Long.parseLong(idParam);

            // Prevent self-deactivation
            HttpSession session = request.getSession();
            UserDTO currentUser = (UserDTO) session.getAttribute("USER");
            if (currentUser != null && currentUser.getUserId() == userId) {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=self-toggle");
                return;
            }

            boolean success = dao.toggleUserStatus(userId);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/users?success=toggled");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=toggle-failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    // Search users
    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("q");
        if (keyword == null || keyword.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        List<UserDTO> users = dao.searchUsers(keyword.trim(), 50);
        int totalUsers = users.size();

        request.setAttribute("users", users);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("currentPage", 1);
        request.setAttribute("totalPages", 1);
        request.setAttribute("totalUsers", totalUsers);

        request.getRequestDispatcher("/View/Admin/user-list.jsp").forward(request, response);
    }

    private boolean isValidRole(String role) {
        return "member".equals(role) || "moderator".equals(role) || "admin".equals(role);
    }

    private boolean isValidStatus(String status) {
        return "active".equals(status) || "inactive".equals(status);
    }
}
