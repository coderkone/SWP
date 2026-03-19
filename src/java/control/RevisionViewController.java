package control;

import dal.EditHistoryDAO;
import dto.RevisionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RevisionViewController", urlPatterns = {"/revision/view"})
public class RevisionViewController extends HttpServlet {

    private final EditHistoryDAO editHistoryDAO = new EditHistoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || !idParam.matches("\\d+")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid revision id.");
                return;
            }

            long historyId = Long.parseLong(idParam);
            RevisionDTO revision = editHistoryDAO.getRevisionById(historyId);
            if (revision == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Revision not found.");
                return;
            }

            request.setAttribute("revision", revision);
            request.getRequestDispatcher("/View/User/revision-view.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
