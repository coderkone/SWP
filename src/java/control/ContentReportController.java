package control;

import dal.ReportDAO;
import dal.QuestionDAO;
import dal.AnswerDAO;
import model.Report;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ContentReportController", urlPatterns = {
    "/admin/reports",
    "/admin/reports/detail",
    "/admin/reports/approve",
    "/admin/reports/reject"
})
public class ContentReportController extends HttpServlet {

    private final ReportDAO reportDAO = new ReportDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final AnswerDAO answerDAO = new AnswerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        try {
            if ("/admin/reports/detail".equals(path)) {
                showDetail(request, response);
            } else {
                listReports(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        try {
            if ("/admin/reports/approve".equals(path)) {
                handleResolve(request, response, true);
            } else if ("/admin/reports/reject".equals(path)) {
                handleResolve(request, response, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500);
        }
    }

    private void listReports(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String status = request.getParameter("status");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int pageSize = 10;

        List<Report> reports = reportDAO.getFilteredReports(status, fromDate, toDate, page, pageSize);
        int totalReports = reportDAO.getFilteredReportCount(status, fromDate, toDate);
        int totalPages = (int) Math.ceil((double) totalReports / pageSize);

        request.setAttribute("reports", reports);
        request.setAttribute("totalReports", totalReports);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("filterStatus", status);
        request.setAttribute("fromDate", fromDate);
        request.setAttribute("toDate", toDate);

        request.getRequestDispatcher("/View/Admin/report-list.jsp").forward(request, response);
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/admin/reports");
            return;
        }

        try {
            long id = Long.parseLong(idParam);
            Report report = reportDAO.getReportById(id);

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

    private void handleResolve(HttpServletRequest request, HttpServletResponse response, boolean isApprove) throws Exception {
        String idParam = request.getParameter("id");
        String note = request.getParameter("note");

        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/admin/reports");
            return;
        }

        try {
            long id = Long.parseLong(idParam);
            Report report = reportDAO.getReportById(id);

            if (report == null) {
                response.sendRedirect(request.getContextPath() + "/admin/reports?error=notfound");
                return;
            }

            // 1. Update report status and note
            reportDAO.updateReportStatus(id, "resolved", note);

            // 2. If approve, take action on content
            if (isApprove) {
                if ("question".equals(report.getTargetType())) {
                    questionDAO.closeQuestion(report.getTargetId(), "Closed due to report violation: " + report.getReason());
                } else if ("answer".equals(report.getTargetType())) {
                    answerDAO.deleteAnswer(report.getTargetId());
                }
                // Comments are left as is for now, or could add deleteComment
            }

            response.sendRedirect(request.getContextPath() + "/admin/reports?success=" + (isApprove ? "approved" : "rejected"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/reports");
        }
    }
}
