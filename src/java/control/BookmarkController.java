package control;

import dal.BookmarkDAO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.JsonObject;

@WebServlet(name = "BookmarkController", urlPatterns = {"/bookmark/toggle"})
public class BookmarkController extends HttpServlet {

    private final BookmarkDAO bookmarkDao = new BookmarkDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        try {
            // 1. Check if user is logged in
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("USER") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "User not logged in");
                jsonResponse.addProperty("redirect", request.getContextPath() + "/auth/login");
                out.print(jsonResponse);
                return;
            }

            UserDTO user = (UserDTO) session.getAttribute("USER");
            long userId = user.getUserId();

            // 2. Get question ID or answer ID from request
            String questionIdParam = request.getParameter("questionId");
            String answerIdParam = request.getParameter("answerId");

            if ((questionIdParam == null || !questionIdParam.matches("\\d+")) &&
                (answerIdParam == null || !answerIdParam.matches("\\d+"))) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid question or answer ID");
                out.print(jsonResponse);
                return;
            }

            boolean success = false;
            String action = "";
            boolean newBookmarkState = false;

            // 3. Toggle question bookmark
            if (questionIdParam != null && questionIdParam.matches("\\d+")) {
                long questionId = Long.parseLong(questionIdParam);
                boolean isBookmarked = bookmarkDao.checkIfBookmarked(userId, questionId);
                
                if (isBookmarked) {
                    success = bookmarkDao.removeBookmark(userId, questionId);
                    action = "removed";
                    newBookmarkState = false;
                } else {
                    success = bookmarkDao.addBookmark(userId, questionId);
                    action = "added";
                    newBookmarkState = true;
                }
            }
            // 4. Toggle answer bookmark
            else if (answerIdParam != null && answerIdParam.matches("\\d+")) {
                long answerId = Long.parseLong(answerIdParam);
                boolean isBookmarked = bookmarkDao.checkIfAnswerBookmarked(userId, answerId);
                
                if (isBookmarked) {
                    success = bookmarkDao.removeAnswerBookmark(userId, answerId);
                    action = "removed";
                    newBookmarkState = false;
                } else {
                    success = bookmarkDao.addAnswerBookmark(userId, answerId);
                    action = "added";
                    newBookmarkState = true;
                }
            }

            if (success) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("action", action);
                jsonResponse.addProperty("isBookmarked", newBookmarkState);
                jsonResponse.addProperty("message", "Bookmark " + action + " successfully");
                out.print(jsonResponse);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to update bookmark");
                out.print(jsonResponse);
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
            e.printStackTrace();
            out.print(jsonResponse);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        try {
            // 1. Check if user is logged in
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("USER") == null) {
                jsonResponse.addProperty("isBookmarked", false);
                out.print(jsonResponse);
                return;
            }

            UserDTO user = (UserDTO) session.getAttribute("USER");
            long userId = user.getUserId();

            // 2. Get question ID or answer ID from request
            String questionIdParam = request.getParameter("questionId");
            String answerIdParam = request.getParameter("answerId");

            boolean isBookmarked = false;

            if (questionIdParam != null && questionIdParam.matches("\\d+")) {
                long questionId = Long.parseLong(questionIdParam);
                isBookmarked = bookmarkDao.checkIfBookmarked(userId, questionId);
            } else if (answerIdParam != null && answerIdParam.matches("\\d+")) {
                long answerId = Long.parseLong(answerIdParam);
                isBookmarked = bookmarkDao.checkIfAnswerBookmarked(userId, answerId);
            }

            jsonResponse.addProperty("isBookmarked", isBookmarked);
            out.print(jsonResponse);

        } catch (Exception e) {
            jsonResponse.addProperty("isBookmarked", false);
            e.printStackTrace();
            out.print(jsonResponse);
        }
    }
}
