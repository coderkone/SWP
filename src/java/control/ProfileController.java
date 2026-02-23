/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package control;

import model.User;
import dal.UserDAO;
import dto.UserDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "ProfileController", urlPatterns = {"/profile"})
public class ProfileController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
    
        // Lấy ID từ session
        if (idParam == null || idParam.trim().isEmpty()) {
            HttpSession session = request.getSession();
            
            // ép kiểu về model.User
            User currentUser = (User) session.getAttribute("user"); 
            
            if (currentUser != null) {
                idParam = String.valueOf(currentUser.getUserId());
            }
        }
        
        if (idParam != null && !idParam.isEmpty()) {
            try {
                long userId = Long.parseLong(idParam);
                UserDAO userDAO = new UserDAO();

                // Gọi hàm lấy thông tin User                
                UserDTO userProfile = userDAO.getUserProfileById(userId);
                if (userProfile != null) {
                    request.setAttribute("uPro", userProfile);
                    request.getRequestDispatcher("View/User/profile.jsp").forward(request, response);
                } else {
                    response.sendRedirect("home");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect("home");
            }
        } else {
            response.sendRedirect("View/User/login.jsp");  
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
