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
import util.EmailUtils;
import util.TokenStore;
import java.util.UUID;


@WebServlet(name="ResetPassword", urlPatterns={"/ResetPassword"})
public class ResetPassword extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String token = request.getParameter("token");
        String email = TokenStore.getToken(token);
        if(email==null){
            request.setAttribute("error", "The recovery link we sended to you is incomplete.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        request.setAttribute("token", token);
        request.getRequestDispatcher("View/User/resetpassword.jsp").forward(request, response);
    } 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String token = request.getParameter("token");
        String newPassword = request.getParameter("new password");
        String confirmPassword = request.getParameter("confirm password");
        if(!newPassword.equals(confirmPassword)){
            request.setAttribute("error","The verification password is incorrect. Please re-enter your password.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("View/User/resetpassword.jsp").forward(request, response);
            return;
        }
        String email = TokenStore.getToken(token);
        if(email == null){
            request.setAttribute("error", "The recovery link we sended to you is overtime.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }try{
            UserDAO dao= new UserDAO();
            dao.changPassword(email, newPassword);
            TokenStore.removeToken(token);
            request.setAttribute("message", "Succesfully changed password. Please go to LogIn into your account");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }catch(Exception e){
            e.printStackTrace();
            request.setAttribute("error", "System error :" + e.getMessage());
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
        
    }
   
}
