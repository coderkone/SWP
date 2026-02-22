/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package control;

import model.User;
import dto.UserDTO;
import dal.UserDAO;
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
@WebServlet(name="ChangePassword", urlPatterns={"/ChangePassword"})
public class ChangePassword extends HttpServlet {
   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user==null){
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        request.setAttribute("email", user.getEmail());
        request.getRequestDispatcher("/View/User/changepassword.jsp").forward(request, response);
         
    } 

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user==null){
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        request.setAttribute("email", user.getEmail());
        String oldPass = request.getParameter("old_password");
        String newPass = request.getParameter("new_password");
        String confirmPass = request.getParameter("confirm_password");
        if(oldPass != null) oldPass= oldPass.trim();
        if(newPass != null) newPass= newPass.trim();
        if(confirmPass != null) confirmPass= confirmPass.trim();
         
        if(oldPass == null || oldPass.isEmpty() || newPass == null || newPass.isEmpty() || confirmPass == null || confirmPass.isEmpty()){
            request.setAttribute("error", "Please fill full information in all form");
            request.getRequestDispatcher("/View/User/changepassword.jsp").forward(request, response);
            return;
        }
        
        if(newPass.length() < 8){
            request.setAttribute("error", "Please make sure new password have at least 8 digit/character");
            request.getRequestDispatcher("/View/User/changepassword.jsp").forward(request, response);
            return;
        }
        if(!newPass.equals(confirmPass)){
            request.setAttribute("error", "Confirm password & new password doesn't match");
            request.getRequestDispatcher("/View/User/changepassword.jsp").forward(request, response);
            return; 
        }
        try{
            UserDAO dao= new UserDAO();
            UserDTO checkOldPass = dao.login(user.getEmail(), oldPass);
            if(checkOldPass == null){
                request.setAttribute("error", "Current password is incorrect.");
                request.getRequestDispatcher("/View/User/changepassword.jsp").forward(request, response);
                return;
            }
            dao.changPassword(user.getEmail(), newPass);
            request.setAttribute("status", "successful");
            request.getRequestDispatcher("/View/User/changepassword.jsp").forward(request, response);
        }catch(Exception e){
            e.printStackTrace();
            request.setAttribute("error", "System error :" + e.getMessage());
            request.getRequestDispatcher("/View/User/changepassword.jsp").forward(request, response);
        }
    }

    
}
