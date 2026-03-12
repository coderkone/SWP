/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package control;

import dal.TagDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Tag;

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
        List<Tag> list = dao.getAllTags();
        String keyword = request.getParameter("search");
        String sort = request.getParameter("sort");
        if(keyword != null && !keyword.trim().isEmpty()){
            list = dao.searchTags(keyword.trim());
        }else if ("popular".equals(sort)){
            list = dao.sortByPopular();
        }else if ("newest".equals(sort)){
            list = dao.sortByNewest();
        }else {
            list = dao.sortByName();
        }
        request.setAttribute("tagList", list);
        request.setAttribute("keyword", keyword);
        request.setAttribute("sort", sort);
        request.getRequestDispatcher("/View/User/tag.jsp").forward(request, response);
    } 

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
    }

    

}
