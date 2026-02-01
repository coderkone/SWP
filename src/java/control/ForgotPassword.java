/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package control;

import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import util.EmailUtils;
import util.TokenStore;

/**
 *
 * @author Asus
 */
@WebServlet(name="ForgotPassword", urlPatterns={"/ForgotPassword"})
public class ForgotPassword extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.getRequestDispatcher("/View/User/forgotpassword.jsp").forward(request, response);
    } 

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String email = request.getParameter("email");
        UserDAO dao = new UserDAO();
        try{
            if(dao.emailExists(email)){
                String token = UUID.randomUUID().toString();
                TokenStore.saveToken(token, email);
                String resetLink = request.getScheme() + ":"
                                  + request.getServerName() 
                                  + request.getServerPort() 
                                  + request.getContextPath() 
                                  + "resetPassword"+token;
                EmailUtils.sendEmail(email, resetLink, email);
                request.setAttribute("message", "Successfully send reset link password to your email. Pleas check it!");
            }else{
                    request.setAttribute("error", "Something's was wrong. Please check your email correctly");
                }
                
        }catch(Exception e){
              request.setAttribute("error", "System error" + e.getMessage());
        }
        request.getRequestDispatcher("/View/User/forgotpassword.jsp").forward(request, response);
    }
     
}
