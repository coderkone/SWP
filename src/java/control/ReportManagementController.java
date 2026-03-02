package control;

import dal.ReportDAO;
import dal.ModeratorActionDAO;
import dto.ReportDTO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "ReportManagementController", urlPatterns = {
    "/admin/reports",
    "/admin/reports/detail",
    "/admin/reports/approve",
    "/admin/reports/reject"
})
public class ReportManagementController extends HttpServlet {

    private final ReportDAO reportDAO = new ReportDAO();
    private final ModeratorActionDAO actionDAO = new ModeratorActionDAO();
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check admin role
        HttpSession session = request.getSession();
        UserDTO currentUser = (UserDTO) session.getAttribute("USER");
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();

        switch (path) {
            case "/admin/reports":
                handleList(request, response);
                break;
            case "/admin/reports/detail":
                handleDetail(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/reports");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check admin role
        HttpSession session = request.getSession();
        UserDTO currentUser = (UserDTO) session.getAttribute("USER");
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        switch (path) {
            case "/admin/reports/approve":
                handleApprove(request, response);
                break;
            case "/admin/reports/reject":
                handleReject(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/reports");
        }
    }

    // Hien thi danh sach reports voi pagination va filter
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
        String filterStatus = request.getParameter("status");
        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");

        // Parse dates
        Date fromDate = null;
        Date toDate = null;
        try {
            if (fromDateStr != null && !fromDateStr.isEmpty()) {
                fromDate = Date.valueOf(fromDateStr);
            }
            if (toDateStr != null && !toDateStr.isEmpty()) {
                toDate = Date.valueOf(toDateStr);
            }
        } catch (IllegalArgumentException e) {
            // Invalid date format, ignore
        }

        // Get reports with filters
        List<ReportDTO> reports;
        int totalReports;

        boolean hasDateFilter = fromDate != null || toDate != null;

        if (hasDateFilter || (filterStatus != null && !filterStatus.isEmpty())) {
            reports = reportDAO.getReportsFiltered(filterStatus, fromDate, toDate, page, PAGE_SIZE);
            totalReports = reportDAO.getReportCountFiltered(filterStatus, fromDate, toDate);
        } else {
            reports = reportDAO.getAllReports(page, PAGE_SIZE);
            totalReports = reportDAO.getReportCount();
        }
        int totalPages = (int) Math.ceil((double) totalReports / PAGE_SIZE);

        request.setAttribute("reports", reports);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalReports", totalReports);
        request.setAttribute("filterStatus", filterStatus);
        request.setAttribute("fromDate", fromDateStr);
        request.setAttribute("toDate", toDateStr);

        request.getRequestDispatcher("/View/Admin/report-list.jsp").forward(request, response);
    }

    // Hien thi chi tiet report
    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/reports");
            return;
        }

        try {
            long reportId = Long.parseLong(idParam);
            ReportDTO report = reportDAO.getReportById(reportId);

            if (report == null) {
                response.sendRedirect(request.getContextPath() + "/admin/reports?error=notfound");
                return;
            }

            request.setAttribute("report", report);
            request.getRequestDispatcher("/View/Admin/report-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/reports");
        }
    }

   // Constants để tránh hardcode rải rác
private static final String REPORT_STATUS_RESOLVED = "resolved";
private static final String ACTION_APPROVE = "approve_violation";
private static final String ACTION_REJECT  = "reject_violation";

private static final String TYPE_QUESTION = "question";
private static final String TYPE_ANSWER   = "answer";
private static final String TYPE_COMMENT  = "comment";

private static final int NOTE_MAX_LEN = 500;

private long parseIdOrRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String idParam = request.getParameter("id");
    if (idParam == null || idParam.trim().isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/admin/reports");
        return -1;
    }
    try {
        long id = Long.parseLong(idParam.trim());
        if (id <= 0) throw new NumberFormatException();
        return id;
    } catch (NumberFormatException e) {
        response.sendRedirect(request.getContextPath() + "/admin/reports?error=invalid-id");
        return -1;
    }
}

private String normalizeNote(HttpServletRequest request) {
    String note = request.getParameter("note");
    if (note == null) return null;
    note = note.trim();
    if (note.isEmpty()) return null;
    if (note.length() > NOTE_MAX_LEN) note = note.substring(0, NOTE_MAX_LEN);
    return note;
}

private UserDTO requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession(false);
    UserDTO currentUser = (session == null) ? null : (UserDTO) session.getAttribute("USER");

    // Tuỳ dự án bạn: đổi điều kiện role cho đúng (isAdmin(), getRole(), getRoleId()...)
    if (currentUser == null /* || !currentUser.isAdmin() */) {
        response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
        return null;
    }
    return currentUser;
}

// ============ APPROVE: xác nhận vi phạm ============
private void handleApprove(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    UserDTO currentUser = requireAdmin(request, response);
    if (currentUser == null) return;

    long reportId = parseIdOrRedirect(request, response);
    if (reportId == -1) return;

    String note = normalizeNote(request);

    ReportDTO report = reportDAO.getReportById(reportId);
    if (report == null) {
        response.sendRedirect(request.getContextPath() + "/admin/reports?error=notfound");
        return;
    }

    String targetType = report.getTargetType();
    long targetId = report.getTargetId();

    // Validate targetType
    boolean supportedType = TYPE_QUESTION.equals(targetType) || TYPE_ANSWER.equals(targetType) || TYPE_COMMENT.equals(targetType);
    if (!supportedType) {
        response.sendRedirect(request.getContextPath() + "/admin/reports?error=unsupported-target");
        return;
    }

    // ---- Transaction: soft delete + resolve + log ----
    try {
        // Nếu bạn dùng JDBC Connection trong DAO: nên truyền connection vào DAO để chung 1 transaction.
        // Ở đây mình minh hoạ bằng pattern begin/commit/rollback.
        reportDAO.beginTransaction();

        boolean contentDeleted = softDeleteTarget(targetType, targetId);
        if (!contentDeleted) {
            reportDAO.rollbackTransaction();
            response.sendRedirect(request.getContextPath() + "/admin/reports?error=content-delete-failed");
            return;
        }

        // Nên lưu thêm moderatorId, resolvedAt, decisionNote (tuỳ schema)
        boolean statusUpdated = reportDAO.updateReportStatus(reportId, REPORT_STATUS_RESOLVED);
        if (!statusUpdated) {
            reportDAO.rollbackTransaction();
            response.sendRedirect(request.getContextPath() + "/admin/reports?error=resolve-failed");
            return;
        }

        String description = buildDescription("Xac nhan vi pham", note);
        boolean actionLogged = actionDAO.createAction(
                currentUser.getUserId(),
                ACTION_APPROVE,
                targetType,
                targetId,
                description
        );

        if (!actionLogged) {
            reportDAO.rollbackTransaction();
            response.sendRedirect(request.getContextPath() + "/admin/reports?error=log-failed");
            return;
        }

        reportDAO.commitTransaction();
        response.sendRedirect(request.getContextPath() + "/admin/reports?success=approved");

    } catch (Exception ex) {
        try { reportDAO.rollbackTransaction(); } catch (Exception ignore) {}
        response.sendRedirect(request.getContextPath() + "/admin/reports?error=server-error");
    }
}

private boolean softDeleteTarget(String targetType, long targetId) {
    if (TYPE_QUESTION.equals(targetType)) {
        return reportDAO.closeQuestion(targetId, "Vi pham quy dinh cong dong"); // soft close
    }
    if (TYPE_ANSWER.equals(targetType)) {
        return reportDAO.hideAnswer(targetId); // soft hide
    }
    if (TYPE_COMMENT.equals(targetType)) {
        return reportDAO.hideComment(targetId); // soft hide
    }
    return false;
}

private String buildDescription(String prefix, String note) {
    return (note == null) ? prefix : (prefix + ": " + note);
}

// ============ REJECT: không vi phạm ============
private void handleReject(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    UserDTO currentUser = requireAdmin(request, response);
    if (currentUser == null) return;

    long reportId = parseIdOrRedirect(request, response);
    if (reportId == -1) return;

    String note = normalizeNote(request);

    ReportDTO report = reportDAO.getReportById(reportId);
    if (report == null) {
        response.sendRedirect(request.getContextPath() + "/admin/reports?error=notfound");
        return;
    }

    String targetType = report.getTargetType();
    long targetId = report.getTargetId();

    try {
        reportDAO.beginTransaction();

        boolean statusUpdated = reportDAO.updateReportStatus(reportId, REPORT_STATUS_RESOLVED);
        if (!statusUpdated) {
            reportDAO.rollbackTransaction();
            response.sendRedirect(request.getContextPath() + "/admin/reports?error=resolve-failed");
            return;
        }

        String description = buildDescription("Khong vi pham", note);
        boolean actionLogged = actionDAO.createAction(
                currentUser.getUserId(),
                ACTION_REJECT,
                targetType,
                targetId,
                description
        );

        if (!actionLogged) {
            reportDAO.rollbackTransaction();
            response.sendRedirect(request.getContextPath() + "/admin/reports?error=log-failed");
            return;
        }

        reportDAO.commitTransaction();
        response.sendRedirect(request.getContextPath() + "/admin/reports?success=rejected");

    } catch (Exception ex) {
        try { reportDAO.rollbackTransaction(); } catch (Exception ignore) {}
        response.sendRedirect(request.getContextPath() + "/admin/reports?error=server-error");
    }
}
    }
