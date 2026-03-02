package control;

import dal.SystemRuleDAO;
import dto.SystemRuleDTO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "SystemRuleController", urlPatterns = {
    "/admin/rules",
    "/admin/rules/create",
    "/admin/rules/edit",
    "/admin/rules/delete",
    "/admin/rules/search"
})
public class SystemRuleController extends HttpServlet {

    private static final int PAGE_SIZE = 10;
    private final SystemRuleDAO dao = new SystemRuleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/admin/rules":
                handleList(request, response);
                break;
            case "/admin/rules/create":
                handleCreateForm(request, response);
                break;
            case "/admin/rules/edit":
                handleEditForm(request, response);
                break;
            case "/admin/rules/search":
                handleSearch(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/rules");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        switch (path) {
            case "/admin/rules/create":
                handleCreateSubmit(request, response);
                break;
            case "/admin/rules/edit":
                handleEditSubmit(request, response);
                break;
            case "/admin/rules/delete":
                handleDelete(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/rules");
        }
    }

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

        List<SystemRuleDTO> rules = dao.getAllRules(page, PAGE_SIZE);
        int totalRules = dao.getRuleCount();
        int totalPages = (int) Math.ceil((double) totalRules / PAGE_SIZE);

        request.setAttribute("rules", rules);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRules", totalRules);

        request.getRequestDispatcher("/View/Admin/rule-list.jsp").forward(request, response);
    }

    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("editMode", false);
        request.getRequestDispatcher("/View/Admin/rule-form.jsp").forward(request, response);
    }

    private void handleEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/rules?error=invalid");
            return;
        }

        try {
            long ruleId = Long.parseLong(idParam);
            SystemRuleDTO rule = dao.getRuleById(ruleId);

            if (rule == null) {
                response.sendRedirect(request.getContextPath() + "/admin/rules?error=notfound");
                return;
            }

            request.setAttribute("rule", rule);
            request.setAttribute("editMode", true);
            request.getRequestDispatcher("/View/Admin/rule-form.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/rules?error=invalid");
        }
    }

    private void handleCreateSubmit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        // Validation
        if (title == null || title.trim().isEmpty()) {
            request.setAttribute("error", "Tiêu đề không được để trống");
            request.setAttribute("editMode", false);
            request.getRequestDispatcher("/View/Admin/rule-form.jsp").forward(request, response);
            return;
        }

        if (content == null || content.trim().isEmpty()) {
            request.setAttribute("error", "Nội dung không được để trống");
            request.setAttribute("title", title);
            request.setAttribute("editMode", false);
            request.getRequestDispatcher("/View/Admin/rule-form.jsp").forward(request, response);
            return;
        }

        title = title.trim();
        content = content.trim();

        if (title.length() > 255) {
            request.setAttribute("error", "Tiêu đề không được vượt quá 255 ký tự");
            request.setAttribute("content", content);
            request.setAttribute("editMode", false);
            request.getRequestDispatcher("/View/Admin/rule-form.jsp").forward(request, response);
            return;
        }

        // Get current user
        HttpSession session = request.getSession();
        UserDTO currentUser = (UserDTO) session.getAttribute("USER");
        long createdBy = currentUser.getUserId();

        boolean success = dao.createRule(title, content, createdBy);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/rules?success=created");
        } else {
            request.setAttribute("error", "Không thể tạo nội quy. Vui lòng thử lại.");
            request.setAttribute("title", title);
            request.setAttribute("content", content);
            request.setAttribute("editMode", false);
            request.getRequestDispatcher("/View/Admin/rule-form.jsp").forward(request, response);
        }
    }

    private void handleEditSubmit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/rules?error=invalid");
            return;
        }

        long ruleId;
        try {
            ruleId = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/rules?error=invalid");
            return;
        }

        // Validation
        if (title == null || title.trim().isEmpty()) {
            SystemRuleDTO rule = dao.getRuleById(ruleId);
            request.setAttribute("rule", rule);
            request.setAttribute("error", "Tiêu đề không được để trống");
            request.setAttribute("editMode", true);
            request.getRequestDispatcher("/View/Admin/rule-form.jsp").forward(request, response);
            return;
        }

        if (content == null || content.trim().isEmpty()) {
            SystemRuleDTO rule = dao.getRuleById(ruleId);
            request.setAttribute("rule", rule);
            request.setAttribute("error", "Nội dung không được để trống");
            request.setAttribute("editMode", true);
            request.getRequestDispatcher("/View/Admin/rule-form.jsp").forward(request, response);
            return;
        }

        title = title.trim();
        content = content.trim();

        if (title.length() > 255) {
            SystemRuleDTO rule = dao.getRuleById(ruleId);
            request.setAttribute("rule", rule);
            request.setAttribute("error", "Tiêu đề không được vượt quá 255 ký tự");
            request.setAttribute("editMode", true);
            request.getRequestDispatcher("/View/Admin/rule-form.jsp").forward(request, response);
            return;
        }

        // Get current user
        HttpSession session = request.getSession();
        UserDTO currentUser = (UserDTO) session.getAttribute("USER");
        long updatedBy = currentUser.getUserId();

        boolean success = dao.updateRule(ruleId, title, content, updatedBy);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/rules?success=updated");
        } else {
            request.setAttribute("error", "Không thể cập nhật nội quy. Vui lòng thử lại.");
            SystemRuleDTO rule = dao.getRuleById(ruleId);
            request.setAttribute("rule", rule);
            request.setAttribute("editMode", true);
            request.getRequestDispatcher("/View/Admin/rule-form.jsp").forward(request, response);
        }
    }

    // private void handleDelete(HttpServletRequest request, HttpServletResponse response)
    //         throws ServletException, IOException {
    //     String idParam = request.getParameter("id");

    //     if (idParam == null || idParam.isEmpty()) {
    //         response.sendRedirect(request.getContextPath() + "/admin/rules?error=invalid");
    //         return;
    //     }

    //     try {
    //         long ruleId = Long.parseLong(idParam);
    //         boolean success = dao.deleteRule(ruleId);

    //         if (success) {
    //             response.sendRedirect(request.getContextPath() + "/admin/rules?success=deleted");
    //         } else {
    //             response.sendRedirect(request.getContextPath() + "/admin/rules?error=deletefailed");
    //         }
    //     } catch (NumberFormatException e) {
    //         response.sendRedirect(request.getContextPath() + "/admin/rules?error=invalid");
    //     }
    // }

    // private void handleSearch(HttpServletRequest request, HttpServletResponse response)
    //         throws ServletException, IOException {
    //     String keyword = request.getParameter("q");

    //     if (keyword == null || keyword.trim().isEmpty()) {
    //         response.sendRedirect(request.getContextPath() + "/admin/rules");
    //         return;
    //     }

    //     keyword = keyword.trim();
    //     List<SystemRuleDTO> rules = dao.searchRules(keyword, 50);
    //     int totalRules = rules.size();

    //     request.setAttribute("rules", rules);
    //     request.setAttribute("searchKeyword", keyword);
    //     request.setAttribute("currentPage", 1);
    //     request.setAttribute("totalPages", 1);
    //     request.setAttribute("totalRules", totalRules);

    //     request.getRequestDispatcher("/View/Admin/rule-list.jsp").forward(request, response);
    // }
}
