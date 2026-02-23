/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package control;

import dal.QuestionDAO;
import dto.QuestionDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author ADMIN
 */
@WebServlet(name="SearchController", urlPatterns={"/SearchController"})
public class SearchController extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String keyword = request.getParameter("q");
        String tab = request.getParameter("tab");       
        String filter = request.getParameter("filter"); 
                
        // Nếu người dùng bấm "Unanswered" -> Reset tab về newest
        if ("unanswered".equals(filter)) {
            tab = "newest"; 
        } 
        // Nếu người dùng bấm 1 trong 3 tab sắp xếp -> Reset filter về all
        else if ("active".equals(tab) || "newest".equals(tab) || "voted".equals(tab)) {
            filter = "all";
        }
        
        if (keyword == null) keyword = "";
        if (tab == null) tab = "newest";
        if (filter == null) filter = "all";

        QuestionDAO dao = new QuestionDAO();
        int page = 1;
        List<QuestionDTO> list;
        
        try {
            list = dao.getQuestions(page, 10, tab, keyword, filter);
        } catch (Exception e) {
            System.err.println("Error searching questions: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/View/User/home.jsp?error=SearchFailed");
            return;
        }

        request.setAttribute("questions", list);
        request.setAttribute("currentKeyword", keyword);
        request.setAttribute("currentSort", tab);
        request.setAttribute("currentFilter", filter);

        request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
