package control;

import dal.BadgeDAO;
import dto.BadgeDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name="BadgeManagerController", urlPatterns={"/admin/badges", "/admin/badges/create", "/admin/badges/edit", "/admin/badges/delete"})
public class BadgeManagerController extends HttpServlet {

    private final BadgeDAO dao = new BadgeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if (path.equals("/admin/badges/create")) {
            // Mở form tạo
            request.getRequestDispatcher("/View/Admin/badge-form.jsp").forward(request, response);
        } else if (path.equals("/admin/badges/edit")) {
            // Lấy data cũ mở form sửa
            int id = Integer.parseInt(request.getParameter("id"));
            request.setAttribute("badge", dao.getBadgeById(id));
            request.getRequestDispatcher("/View/Admin/badge-form.jsp").forward(request, response);
        } else {
            // Hiển thị danh sách
            String search = request.getParameter("q");
            String type = request.getParameter("type");
            List<BadgeDTO> list = dao.getAllBadgesForAdmin(search, type);
            request.setAttribute("badgeList", list);
            request.getRequestDispatcher("/View/Admin/badge-manager.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        if (path.equals("/admin/badges/delete")) {
            // Xóa
            int id = Integer.parseInt(request.getParameter("id"));
            dao.deleteBadge(id);
            response.sendRedirect(request.getContextPath() + "/admin/badges?success=deleted");
        } else if (path.equals("/admin/badges/create") || path.equals("/admin/badges/edit")) {
            // Thêm hoặc Sửa
            BadgeDTO b = new BadgeDTO();
            b.setName(request.getParameter("name"));
            b.setType(request.getParameter("type"));
            b.setDescription(request.getParameter("description"));
            b.setRequiredReputation(Integer.parseInt(request.getParameter("requiredReputation")));

            if (path.equals("/admin/badges/create")) {
                dao.insertBadge(b);
                response.sendRedirect(request.getContextPath() + "/admin/badges?success=created");
            } else {
                b.setBadgeId(Integer.parseInt(request.getParameter("id")));
                dao.updateBadge(b);
                response.sendRedirect(request.getContextPath() + "/admin/badges?success=updated");
            }
        }
    }
}