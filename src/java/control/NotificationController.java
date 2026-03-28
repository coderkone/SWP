/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package control;

import dal.NotificationDAO;
import model.Notification;
import model.User;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Asus
 */
@WebServlet(name="NotificationController", urlPatterns={"/notification"})
public class NotificationController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user==null){
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        String idParam = request.getParameter("id");
        String action = request.getParameter("action");
        String type = request.getParameter("type");
        NotificationDAO dao = new NotificationDAO();
        try{
            if("allRead".equals(action)){
                dao.markAllRead(user.getUserId());
            }else if (idParam != null){
                long notifiId = Long.parseLong(idParam);
                dao.markAsRead(notifiId, user.getUserId());
            }
            int freshCount = dao.getUnreadCount(user.getUserId());
            request.setAttribute("unreadNotification", freshCount);
            List<Notification> list;
            if (type != null && !type.equals("All")&& !type.trim().isEmpty()) {
                list = dao.getNotificationByType(user.getUserId(), type, 20);
            } else {
                list = dao.getNotification(user.getUserId(), 20);
            }
                request.setAttribute("Notification", list);
                request.setAttribute("currentType", type != null&& !type.equals("All")&& !type.trim().isEmpty() ? type : "All"); // ← label đúng
                request.setAttribute("openNoti", true);           // ← signal để JS tự mở dropdown
                request.getRequestDispatcher("/home").forward(request, response);
                
                
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
    } 

    
   

    
}
