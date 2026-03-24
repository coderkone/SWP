package control;

import dal.ReportDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Report;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ContentReportController", urlPatterns = {
    "/admin/reports",
    "/admin/reports/resolve"
})
public class ContentReportController extends HttpServlet {

    private final ReportDAO dao = new ReportDAO();
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/admin/reports".equals(path)) {
            handleList(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/reports");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/admin/reports/resolve".equals(path)) {
            handleResolve(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/reports");
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

        try {
            List<Report> reports = dao.getAllReports(page, PAGE_SIZE);
            int totalReports = dao.getReportCount();
            int totalPages = (int) Math.ceil((double) totalReports / PAGE_SIZE);

            request.setAttribute("reports", reports);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalReports", totalReports);

            request.getRequestDispatcher("/View/Admin/report-list.jsp").forward(request, response);
        } catch (Exception e) {
            response.setContentType("text/plain;charset=UTF-8");
            java.io.PrintWriter out = response.getWriter();
            e.printStackTrace(out);
            out.close();
        }
    }

    private void handleResolve(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/reports?error=invalid");
            return;
        }

        try {
            long reportId = Long.parseLong(idParam);
            boolean success = dao.updateReportStatus(reportId, "resolved");

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/reports?success=resolved");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/reports?error=failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/reports?error=system");
        }
    }
}
