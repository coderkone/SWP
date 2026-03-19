package control;

import dal.EditHistoryDAO;
import dto.RevisionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "RevisionListController", urlPatterns = {"/question/*", "/answer/*"})
public class RevisionListController extends HttpServlet {

    private final EditHistoryDAO editHistoryDAO = new EditHistoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            long postId = parsePostIdFromPath(pathInfo);
            if (postId <= 0) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String servletPath = request.getServletPath();
            String postType = "/question".equals(servletPath) ? "question" : "answer";

            List<RevisionDTO> revisions = editHistoryDAO.getRevisionsByPost(postType, postId);
            request.setAttribute("postType", postType);
            request.setAttribute("postId", postId);
            request.setAttribute("revisions", revisions);

            request.getRequestDispatcher("/View/User/revision-list.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private long parsePostIdFromPath(String pathInfo) {
        if (pathInfo == null || pathInfo.trim().isEmpty()) {
            return -1;
        }

        String path = pathInfo.trim();
        if (!path.startsWith("/") || !path.endsWith("/revisions")) {
            return -1;
        }

        String idPart = path.substring(1, path.length() - "/revisions".length());
        if (!idPart.matches("\\d+")) {
            return -1;
        }

        return Long.parseLong(idPart);
    }
}
