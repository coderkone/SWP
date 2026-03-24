/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package control;

import dal.TagDAO;
import dto.QuestionDTO;
import dto.TagDTO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.User;

/**
 *
 * @author Asus
 */
@WebServlet(name="TagDetailController", urlPatterns={"/tagsdetail"})
public class TagDetailController extends HttpServlet {
    private static final int PAGE_SIZE = 10;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String tagIdStr = request.getParameter("id");
        if (tagIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/tags");
            return;
        }
        long tagId;
        try {
            tagId = Long.parseLong(tagIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/tags");
            return;
        }

        // 2. Session
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        long userId      = (currentUser != null) ? currentUser.getUserId() : 0;
        boolean isLoggedIn = (currentUser != null);

        // 3. Filter + Page
        String filter = request.getParameter("filter");
        if (filter == null) filter = "newest";

        int page = 1;
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null) page = Integer.parseInt(pageStr);
        } catch (NumberFormatException e) { page = 1; }
        if (page < 1) page = 1;

        // 4. Gọi DAO
        TagDAO dao = new TagDAO();

        TagDTO tag = dao.getTagById(tagId, userId);
        if (tag == null) {
            response.sendRedirect(request.getContextPath() + "/tags");
            return;
        }

        int totalQuestions = dao.countQuestionsByTag(tagId, filter);
        int totalPages     = (int) Math.ceil((double) totalQuestions / PAGE_SIZE);
        List<QuestionDTO> questions = dao.getQuestionsByTag(tagId, filter, page);

        // 5. Truyền vào JSP
        request.setAttribute("tag",            tag);
        request.setAttribute("questions",      questions);
        request.setAttribute("filter",         filter);
        request.setAttribute("currentPage",    page);
        request.setAttribute("totalPages",     totalPages);
        request.setAttribute("totalQuestions", totalQuestions);
        request.setAttribute("isLoggedIn",     isLoggedIn);

        request.getRequestDispatcher("/View/User/tagDetail.jsp")
               .forward(request, response);
    }
    

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
    }

} 


