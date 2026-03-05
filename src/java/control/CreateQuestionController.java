/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package control;

import dal.QuestionDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.User;

/**
 *
 * @author ADMIN
 */
@WebServlet(name="CreateQuestionController", urlPatterns={"/create"})
public class CreateQuestionController extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        // kiểm tra đăng nhập
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user"); 
        
        if (currentUser == null) {
            response.sendRedirect("login");
            return;
        }

        String method = request.getMethod();
        if (method.equalsIgnoreCase("GET")) {
            // LUỒNG GET: Mở giao diện đặt câu hỏi
            request.getRequestDispatcher("View/User/createQuestion.jsp").forward(request, response);
        } else if (method.equalsIgnoreCase("POST")) {
            // LUỒNG POST: Nhận dữ liệu khi Đăng câu hỏi
            String title = request.getParameter("title");
            String body = request.getParameter("body");
            String tags = request.getParameter("tags");
            
            // validate độ dài tối thiểu
            if (title == null || title.trim().length() < 15) {
                request.setAttribute("errorMessage", "Title must be at least 15 characters.");
                request.setAttribute("oldTitle", title);
                request.setAttribute("oldBody", body);
                request.setAttribute("oldTags", tags);
                request.getRequestDispatcher("View/User/createQuestion.jsp").forward(request, response);
                return;
            }

            if (body == null || body.trim().length() < 50) {
                request.setAttribute("errorMessage", "Body must be at least 50 characters.");
                request.setAttribute("oldTitle", title);
                request.setAttribute("oldBody", body);
                request.setAttribute("oldTags", tags);
                request.getRequestDispatcher("View/User/createQuestion.jsp").forward(request, response);
                return;
            }
            
            // Lấy điểm uy tín của người dùng
            int userRep = currentUser.getReputation(); 
            QuestionDAO qDao = new QuestionDAO();
            if (userRep < 50) {
                List<String> newTags = qDao.findNewTags(tags);
                if (!newTags.isEmpty()) {
                    // Cảnh báo: Có tag mới nhưng không đủ điểm
                    String errorMsg = "You need at least 50 reputation to create new tags. Invalid tags: " + String.join(", ", newTags);
                    request.setAttribute("errorMessage", errorMsg);
                    // Giữ lại nội dung cũ để người dùng không phải gõ lại từ đầu
                    request.setAttribute("oldTitle", title);
                    request.setAttribute("oldBody", body);
                    request.setAttribute("oldTags", tags);
                    
                    request.getRequestDispatcher("View/User/createQuestion.jsp").forward(request, response);
                    return; 
                }
            }

            // Lưu DB nếu hợp lệ
            try {
                boolean success = qDao.insertQuestionWithTags(currentUser.getUserId(), title, body, tags, userRep);
                if (success) {
                    response.sendRedirect("home"); // Chuyển về trang chủ nếu thành công
                } else {
                    response.getWriter().print("Có lỗi xảy ra khi lưu Database!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().print("Lỗi hệ thống: " + e.getMessage());
            }
        }
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
