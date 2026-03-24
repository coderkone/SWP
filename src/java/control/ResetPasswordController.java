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


@WebServlet(name="ResetPasswordController", urlPatterns={"/resetPassword"})
public class ResetPasswordController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String token = request.getParameter("token");
        String email = TokenStore.getToken(token);
        if(email==null){
            request.setAttribute("status", "expired");
            request.getRequestDispatcher("/View/User/resetpassword.jsp").forward(request, response);
            return;
        }
        request.setAttribute("email", email);
        request.setAttribute("token", token);
        request.getRequestDispatcher("/View/User/resetpassword.jsp").forward(request, response);
    } 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String token = request.getParameter("token");
        String newPassword = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");
        
        if(newPassword.length() < 8){
            request.setAttribute("error", "Password must contain at least eight characters");
            request.setAttribute("token", token);
            String email = TokenStore.getToken(token);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/View/User/resetpassword.jsp").forward(request, response);
            return;
        }
        
        if(newPassword ==null ||!newPassword.equals(confirmPassword)){
            request.setAttribute("error", "The passwords don't match. Please re-enter your password.");
            request.setAttribute("token", token);
            String email = TokenStore.getToken(token);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/View/User/resetpassword.jsp").forward(request, response);
            return;
        }
        
        String email = TokenStore.getToken(token);
        if(email == null){
            request.setAttribute("status", "expired");
            request.getRequestDispatcher("/View/User/resetpassword.jsp").forward(request, response);
            return;
        }

        try{
            UserDAO dao= new UserDAO();
            dao.changPassword(email, newPassword);
            TokenStore.removeToken(token);
            request.setAttribute("status", "success");
            request.getRequestDispatcher("/View/User/resetpassword.jsp").forward(request, response);
        }catch(Exception e){
            e.printStackTrace();
            request.setAttribute("error", "System error :" + e.getMessage());
            request.getRequestDispatcher("/View/User/resetpassword.jsp").forward(request, response);
        }
        
    }
   
}
