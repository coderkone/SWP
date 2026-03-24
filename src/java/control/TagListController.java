/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package control;

import dal.TagDAO;
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
import model.Tag;
import model.User;

/**
 *
 * @author Asus
 */
@WebServlet(name="TagListController", urlPatterns={"/tags"})
public class TagListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        TagDAO dao = new TagDAO();
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        long userId;
        if(currentUser != null){
           userId = currentUser.getUserId();
        }else{
           userId = 0;
        }
        
        String keyword = request.getParameter("search");
        String sort = request.getParameter("sort");
        
        List<TagDTO> list = dao.getAllTagsForUser(userId, keyword, sort);
        
        request.setAttribute("tagList", list);
        request.setAttribute("keyword", keyword);
        request.setAttribute("sort", sort);
        request.setAttribute("isLoggedIn", currentUser != null);
        request.getRequestDispatcher("/View/User/tag.jsp").forward(request, response);
    } 

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
    }

    

}
