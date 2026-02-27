package control;

import dal.TagDAO;
import dto.TagDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "TagManagementController", urlPatterns = {
    "/admin/tags",
    "/admin/tags/create",
    "/admin/tags/edit",
    "/admin/tags/toggle-status",
    "/admin/tags/merge",
    "/admin/tags/search"
})
public class TagManagementController extends HttpServlet {

    private final TagDAO dao = new TagDAO();
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {
            case "/admin/tags":
                handleList(request, response);
                break;
            case "/admin/tags/create":
                handleCreateForm(request, response);
                break;
            case "/admin/tags/edit":
                handleEditForm(request, response);
                break;
            case "/admin/tags/search":
                handleSearch(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/tags");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        switch (path) {
            case "/admin/tags/create":
                handleCreateSubmit(request, response);
                break;
            case "/admin/tags/edit":
                handleEditSubmit(request, response);
                break;
            case "/admin/tags/toggle-status":
                handleToggleStatus(request, response);
                break;
            case "/admin/tags/merge":
                handleMerge(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/tags");
        }
    }

    // Hiển thị danh sách tags với pagination và filter
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

        // Filter parameter
        String filterStatus = request.getParameter("status");

        // Use filter methods
        List<TagDTO> tags;
        int totalTags;
        if (filterStatus != null && !filterStatus.isEmpty()) {
            tags = dao.getTagsByStatus(filterStatus, page, PAGE_SIZE);
            totalTags = dao.getTagCountByStatus(filterStatus);
        } else {
            tags = dao.getAllTags(page, PAGE_SIZE);
            totalTags = dao.getTagCount();
        }
        int totalPages = (int) Math.ceil((double) totalTags / PAGE_SIZE);

        // Lấy tất cả tags active cho dropdown merge
        List<TagDTO> allActiveTags = dao.getAllActiveTags();

        request.setAttribute("tags", tags);
        request.setAttribute("allActiveTags", allActiveTags);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalTags", totalTags);
        request.setAttribute("filterStatus", filterStatus);

        request.getRequestDispatcher("/View/Admin/tag-list.jsp").forward(request, response);
    }

    // Form tạo tag mới
    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("editMode", false);
        request.getRequestDispatcher("/View/Admin/tag-form.jsp").forward(request, response);
    }

    // Xử lý tạo tag
    private void handleCreateSubmit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tagName = request.getParameter("tagName");
        String description = request.getParameter("description");

        // Validation
        if (tagName == null || tagName.trim().isEmpty()) {
            request.setAttribute("error", "Tag name không được để trống.");
            request.setAttribute("editMode", false);
            request.getRequestDispatcher("/View/Admin/tag-form.jsp").forward(request, response);
            return;
        }

        if (tagName.trim().length() > 50) {
            request.setAttribute("error", "Tag name không được quá 50 ký tự.");
            request.setAttribute("editMode", false);
            request.getRequestDispatcher("/View/Admin/tag-form.jsp").forward(request, response);
            return;
        }

        if (dao.tagNameExists(tagName.trim())) {
            request.setAttribute("error", "Tag name đã tồn tại.");
            request.setAttribute("tagName", tagName);
            request.setAttribute("description", description);
            request.setAttribute("editMode", false);
            request.getRequestDispatcher("/View/Admin/tag-form.jsp").forward(request, response);
            return;
        }

        boolean success = dao.createTag(tagName.trim(), description);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/tags?success=created");
        } else {
            request.setAttribute("error", "Không thể tạo tag. Vui lòng thử lại.");
            request.setAttribute("editMode", false);
            request.getRequestDispatcher("/View/Admin/tag-form.jsp").forward(request, response);
        }
    }

    // Form edit tag
    private void handleEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/tags");
            return;
        }

        try {
            long tagId = Long.parseLong(idParam);
            TagDTO tag = dao.getTagById(tagId);

            if (tag == null) {
                response.sendRedirect(request.getContextPath() + "/admin/tags?error=notfound");
                return;
            }

            request.setAttribute("editTag", tag);
            request.setAttribute("editMode", true);
            request.getRequestDispatcher("/View/Admin/tag-form.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/tags");
        }
    }

    // Xử lý edit tag
    private void handleEditSubmit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        String tagName = request.getParameter("tagName");
        String description = request.getParameter("description");
        String isActiveParam = request.getParameter("isActive");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/tags");
            return;
        }

        try {
            long tagId = Long.parseLong(idParam);

            // Validation
            if (tagName == null || tagName.trim().isEmpty()) {
                TagDTO tag = dao.getTagById(tagId);
                request.setAttribute("editTag", tag);
                request.setAttribute("error", "Tag name không được để trống.");
                request.setAttribute("editMode", true);
                request.getRequestDispatcher("/View/Admin/tag-form.jsp").forward(request, response);
                return;
            }

            if (tagName.trim().length() > 50) {
                TagDTO tag = dao.getTagById(tagId);
                request.setAttribute("editTag", tag);
                request.setAttribute("error", "Tag name không được quá 50 ký tự.");
                request.setAttribute("editMode", true);
                request.getRequestDispatcher("/View/Admin/tag-form.jsp").forward(request, response);
                return;
            }

            if (dao.tagNameExistsExcluding(tagName.trim(), tagId)) {
                TagDTO tag = dao.getTagById(tagId);
                request.setAttribute("editTag", tag);
                request.setAttribute("error", "Tag name đã tồn tại.");
                request.setAttribute("editMode", true);
                request.getRequestDispatcher("/View/Admin/tag-form.jsp").forward(request, response);
                return;
            }

            boolean isActive = "1".equals(isActiveParam) || "true".equals(isActiveParam);
            boolean success = dao.updateTag(tagId, tagName.trim(), description, isActive);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/tags?success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/tags?error=update-failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/tags");
        }
    }

    // Toggle status (Active <-> Inactive)
    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/tags");
            return;
        }

        try {
            long tagId = Long.parseLong(idParam);
            boolean success = dao.toggleTagStatus(tagId);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/tags?success=toggled");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/tags?error=toggle-failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/tags");
        }
    }

    // Merge tags
    private void handleMerge(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sourceIdParam = request.getParameter("sourceId");
        String targetIdParam = request.getParameter("targetId");

        if (sourceIdParam == null || sourceIdParam.isEmpty() ||
            targetIdParam == null || targetIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/tags?error=merge-invalid");
            return;
        }

        try {
            long sourceId = Long.parseLong(sourceIdParam);
            long targetId = Long.parseLong(targetIdParam);

            if (sourceId == targetId) {
                response.sendRedirect(request.getContextPath() + "/admin/tags?error=merge-same");
                return;
            }

            boolean success = dao.mergeTags(sourceId, targetId);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/tags?success=merged");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/tags?error=merge-failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/tags?error=merge-invalid");
        }
    }

    // Search tags
    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("q");
        if (keyword == null || keyword.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/tags");
            return;
        }

        List<TagDTO> tags = dao.searchTags(keyword.trim(), 50);
        List<TagDTO> allActiveTags = dao.getAllActiveTags();
        int totalTags = tags.size();

        request.setAttribute("tags", tags);
        request.setAttribute("allActiveTags", allActiveTags);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("currentPage", 1);
        request.setAttribute("totalPages", 1);
        request.setAttribute("totalTags", totalTags);

        request.getRequestDispatcher("/View/Admin/tag-list.jsp").forward(request, response);
    }
}
