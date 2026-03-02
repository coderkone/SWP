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

    //     // Filter parameters
    //     String filterStatus = request.getParameter("status");
    //     String fromDateStr = request.getParameter("fromDate");
    //     String toDateStr = request.getParameter("toDate");

    //     // Parse dates
    //     Date fromDate = null;
    //     Date toDate = null;
    //     try {
    //         if (fromDateStr != null && !fromDateStr.isEmpty()) {
    //             fromDate = Date.valueOf(fromDateStr);
    //         }
    //         if (toDateStr != null && !toDateStr.isEmpty()) {
    //             toDate = Date.valueOf(toDateStr);
    //         }
    //     } catch (IllegalArgumentException e) {
    //         // Invalid date format, ignore
    //     }

    //     // Get reports with filters
    //     List<ReportDTO> reports;
    //     int totalReports;

    //     boolean hasDateFilter = fromDate != null || toDate != null;

    //     if (hasDateFilter || (filterStatus != null && !filterStatus.isEmpty())) {
    //         reports = reportDAO.getReportsFiltered(filterStatus, fromDate, toDate, page, PAGE_SIZE);
    //         totalReports = reportDAO.getReportCountFiltered(filterStatus, fromDate, toDate);
    //     } else {
    //         reports = reportDAO.getAllReports(page, PAGE_SIZE);
    //         totalReports = reportDAO.getReportCount();
    //     }
    //     int totalPages = (int) Math.ceil((double) totalReports / PAGE_SIZE);

    //     request.setAttribute("reports", reports);
    //     request.setAttribute("currentPage", page);
    //     request.setAttribute("totalPages", totalPages);
    //     request.setAttribute("totalReports", totalReports);
    //     request.setAttribute("filterStatus", filterStatus);
    //     request.setAttribute("fromDate", fromDateStr);
    //     request.setAttribute("toDate", toDateStr);

    //     request.getRequestDispatcher("/View/Admin/report-list.jsp").forward(request, response);
    // }

    // // Hien thi chi tiet report
    // private void handleDetail(HttpServletRequest request, HttpServletResponse response)
    //         throws ServletException, IOException {

    //     String idParam = request.getParameter("id");
    //     if (idParam == null || idParam.isEmpty()) {
    //         response.sendRedirect(request.getContextPath() + "/admin/reports");
    //         return;
    //     }

    //     try {
    //         long reportId = Long.parseLong(idParam);
    //         ReportDTO report = reportDAO.getReportById(reportId);

    //         if (report == null) {
    //             response.sendRedirect(request.getContextPath() + "/admin/reports?error=notfound");
    //             return;
    //         }

    //         request.setAttribute("report", report);
    //         request.getRequestDispatcher("/View/Admin/report-detail.jsp").forward(request, response);

    //     } catch (NumberFormatException e) {
    //         response.sendRedirect(request.getContextPath() + "/admin/reports");
    //     }
    // }

    // // Xac nhan vi pham -> soft delete content + resolve report
    // private void handleApprove(HttpServletRequest request, HttpServletResponse response)
    //         throws ServletException, IOException {

    //     HttpSession session = request.getSession();
    //     UserDTO currentUser = (UserDTO) session.getAttribute("USER");

    //     String idParam = request.getParameter("id");
    //     String note = request.getParameter("note");

    //     if (idParam == null || idParam.isEmpty()) {
    //         response.sendRedirect(request.getContextPath() + "/admin/reports");
    //         return;
    //     }

    //     try {
    //         long reportId = Long.parseLong(idParam);
    //         ReportDTO report = reportDAO.getReportById(reportId);

    //         if (report == null) {
    //             response.sendRedirect(request.getContextPath() + "/admin/reports?error=notfound");
    //             return;
    //         }

    //         // Soft delete content based on type
    //         boolean contentDeleted = false;
    //         String targetType = report.getTargetType();
    //         long targetId = report.getTargetId();

    //         if ("question".equals(targetType)) {
    //             contentDeleted = reportDAO.closeQuestion(targetId, "Vi pham quy dinh cong dong");
    //         } else if ("answer".equals(targetType)) {
    //             contentDeleted = reportDAO.hideAnswer(targetId);
    //         } else if ("comment".equals(targetType)) {
    //             contentDeleted = reportDAO.hideComment(targetId);
    //         }

    //         // Update report status
    //         boolean statusUpdated = reportDAO.updateReportStatus(reportId, "resolved");

    //         // Log moderator action
    //         String description = "Xac nhan vi pham" + (note != null && !note.trim().isEmpty() ? ": " + note.trim() : "");
    //         actionDAO.createAction(currentUser.getUserId(), "approve_violation", targetType, targetId, description);

    //         if (contentDeleted && statusUpdated) {
    //             response.sendRedirect(request.getContextPath() + "/admin/reports?success=approved");
    //         } else {
    //             response.sendRedirect(request.getContextPath() + "/admin/reports?error=approve-failed");
    //         }

    //     } catch (NumberFormatException e) {
    //         response.sendRedirect(request.getContextPath() + "/admin/reports");
    //     }
    // }

    // // Khong vi pham -> chi resolve report
    // private void handleReject(HttpServletRequest request, HttpServletResponse response)
    //         throws ServletException, IOException {

    //     HttpSession session = request.getSession();
    //     UserDTO currentUser = (UserDTO) session.getAttribute("USER");

    //     String idParam = request.getParameter("id");
    //     String note = request.getParameter("note");

    //     if (idParam == null || idParam.isEmpty()) {
    //         response.sendRedirect(request.getContextPath() + "/admin/reports");
    //         return;
    //     }

    //     try {
    //         long reportId = Long.parseLong(idParam);
    //         ReportDTO report = reportDAO.getReportById(reportId);

    //         if (report == null) {
    //             response.sendRedirect(request.getContextPath() + "/admin/reports?error=notfound");
    //             return;
    //         }

    //         // Update report status
    //         boolean statusUpdated = reportDAO.updateReportStatus(reportId, "resolved");

    //         // Log moderator action
    //         String description = "Khong vi pham" + (note != null && !note.trim().isEmpty() ? ": " + note.trim() : "");
    //         actionDAO.createAction(currentUser.getUserId(), "reject_violation", report.getTargetType(), report.getTargetId(), description);

    //         if (statusUpdated) {
    //             response.sendRedirect(request.getContextPath() + "/admin/reports?success=rejected");
    //         } else {
    //             response.sendRedirect(request.getContextPath() + "/admin/reports?error=reject-failed");
    //         }

    //     } catch (NumberFormatException e) {
    //         response.sendRedirect(request.getContextPath() + "/admin/reports");
    //     }
    }
}
