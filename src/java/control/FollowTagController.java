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
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import model.User;

/**
 *
 * @author Asus
 */
@WebServlet(name="FollowTagController", urlPatterns={"/follow-tags"})
public class FollowTagController extends HttpServlet {
   
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.sendRedirect("tags");
    } 

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if(currentUser == null){
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        TagDAO dao= new TagDAO();
        long userId = currentUser.getUserId();
        String action = request.getParameter("action");
        String tagIdStr = request.getParameter("tagId");
        String sort     = request.getParameter("sort");   
        String search   = request.getParameter("search"); 
        try{
            long tagId = Long.parseLong(tagIdStr);
            if("follow".equals(action)){
                dao.followTag(userId, tagId);
            }else if("unfollow".equals(action)){
                dao.unfollowTag(userId, tagId);
            }
            
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error" + e.getMessage());
        }
        StringBuilder redirectUrl = new StringBuilder(request.getContextPath() + "/tags");
        boolean hasParam = false;

        if (sort != null && !sort.trim().isEmpty()) {
            redirectUrl.append(hasParam ? "&" : "?").append("sort=").append(sort);
            hasParam = true;
        }
        
        if (search != null && !search.trim().isEmpty()) {
            
            String encodedSearch = URLEncoder.encode(search.trim(), StandardCharsets.UTF_8.toString());
            redirectUrl.append(hasParam ? "&" : "?").append("search=").append(encodedSearch);
        }
        
        response.sendRedirect(redirectUrl.toString());
        
    }

   

}
