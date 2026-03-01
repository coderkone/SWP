/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Filter.java to edit this template
 */
package filter;

import dal.NotificationDAO;
import model.User;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 *
 * @author Asus
 */
@WebFilter(filterName = "NotificationFilter", urlPatterns = {"/*"})
public class NotificationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) sr;
        String uri = req.getRequestURI();
        if (uri.contains("/assets/") || uri.contains("/css/") || uri.contains("/js/")) {
            fc.doFilter(sr, sr1);
            return;
        }
        

        HttpSession session = req.getSession(false);
        if(session != null && session.getAttribute("user")!=null){
            User user = (User) session.getAttribute("user");
            NotificationDAO dao = new NotificationDAO();
            
            try{
                req.setAttribute("unreadNotification", dao.getUnreadCount(user.getUserId()));
                req.setAttribute("Notification", dao.getNotification(user.getUserId(), 10));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        fc.doFilter(sr, sr1);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

}
