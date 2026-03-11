/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package control;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dal.ActivityDAO;
import dal.UserDAO;
import dto.UserDTO;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
@WebServlet(name="ActivityController", urlPatterns={"/activity"})
public class ActivityController extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        // 1. Lấy thông tin User Profile
        String userIdStr = request.getParameter("id");
        long targetUserId = 0;
        
        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            targetUserId = Long.parseLong(userIdStr);
        } else {
            // Nếu không có ID trên URL, lấy ID của người đang đăng nhập (từ Session)
            model.User currentUser = (model.User) request.getSession().getAttribute("user");
            if (currentUser != null) {
                targetUserId = currentUser.getUserId();
            } else {
                // Nếu chưa đăng nhập mà cứ cố vào, đá về trang chủ
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
        }
        UserDAO userDAO = new UserDAO();
        dto.UserDTO uPro = userDAO.getUserProfileById(targetUserId);
        request.setAttribute("uPro", uPro);

        // 2. Lấy tham số tab để rẽ nhánh
        String tab = request.getParameter("tab");
        if (tab == null || tab.trim().isEmpty()) {
            tab = "summary";
        }
        request.setAttribute("currentActivityTab", tab); 
        
        // 3. Xử lý phân trang chung cho các tab danh sách
        int pageIndex = 1;
        int pageSize = 10;
        String pageStr = request.getParameter("page");
        if (pageStr != null) {
            try {
                pageIndex = Integer.parseInt(pageStr);
            } catch (Exception e) {
            }
        }
        
        ActivityDAO activityDAO = new ActivityDAO();
        String jspPage = "/View/User/activity_summary.jsp"; 
        int totalRecords = 0;
        
        // 4. Rẽ nhánh xử lý Data theo Tab
        switch (tab) {
            case "summary":
                Map<String, Integer> activityMap = activityDAO.getActivityByMonth(targetUserId);
                Map<String, Integer> topTagsMap = activityDAO.getTopTagsByReputation(targetUserId);
                
                request.setAttribute("chartActivityMap", activityMap);
                request.setAttribute("chartTopTagsMap", topTagsMap);
                jspPage = "/View/User/activity_summary.jsp";
                break;
                
            case "questions":
                List<dto.QuestionDTO> qList = activityDAO.getUserQuestions(targetUserId, pageIndex, pageSize);
                totalRecords = activityDAO.getTotalUserQuestions(targetUserId);
                request.setAttribute("itemsList", qList);
                jspPage = "/View/User/activity_questions.jsp";
                break;
                
            case "answers":
                List<Map<String, Object>> aList = activityDAO.getUserAnswers(targetUserId, pageIndex, pageSize);
                totalRecords = activityDAO.getTotalUserAnswers(targetUserId);
                request.setAttribute("itemsList", aList);
                jspPage = "/View/User/activity_answers.jsp";
                break;
                
            case "comments":
                List<Map<String, Object>> cList = activityDAO.getUserComments(targetUserId, pageIndex, pageSize);
                totalRecords = activityDAO.getTotalUserComments(targetUserId);
                request.setAttribute("itemsList", cList);
                jspPage = "/View/User/activity_comments.jsp";
                break;
                
            case "tags":
                List<Map<String, Object>> tList = activityDAO.getUserTags(targetUserId, pageIndex, pageSize);
                totalRecords = activityDAO.getTotalUserTags(targetUserId);
                request.setAttribute("itemsList", tList);
                jspPage = "/View/User/activity_tags.jsp";
                break;
                
            case "follows":
                List<Map<String, Object>> fList = activityDAO.getUserFollows(targetUserId, pageIndex, pageSize);
                totalRecords = activityDAO.getTotalUserFollows(targetUserId);
                request.setAttribute("itemsList", fList);
                jspPage = "/View/User/activity_follows.jsp";
                break;
                
            case "votes":
                // BẢO MẬT: Kiểm tra xem người đang xem có phải là chủ sở hữu không
                model.User currentSessionUser = (model.User) request.getSession().getAttribute("user");
                if (currentSessionUser == null || currentSessionUser.getUserId() != targetUserId) {
                    // Nếu là người lạ chọc vào link votes, ép văng về Summary
                    request.setAttribute("currentActivityTab", "summary");
                    request.setAttribute("chartActivityMap", activityDAO.getActivityByMonth(targetUserId));
                    request.setAttribute("chartTopTagsMap", activityDAO.getTopTagsByReputation(targetUserId));
                    jspPage = "/View/User/activity_summary.jsp";
                    break;
                }
                
                List<Map<String, Object>> vList = activityDAO.getUserVotes(targetUserId, pageIndex, pageSize);
                totalRecords = activityDAO.getTotalUserVotes(targetUserId);
                request.setAttribute("itemsList", vList);
                jspPage = "/View/User/activity_votes.jsp";
                break;
                
            default:
                request.setAttribute("chartActivityMap", activityDAO.getActivityByMonth(targetUserId));
                request.setAttribute("chartTopTagsMap", activityDAO.getTopTagsByReputation(targetUserId));
                jspPage = "/View/User/activity_summary.jsp";
                break;
        }
        
        if (!jspPage.contains("summary")) {
            int totalPage = (totalRecords % pageSize == 0) ? (totalRecords / pageSize) : (totalRecords / pageSize + 1);
            request.setAttribute("totalPage", totalPage);
            request.setAttribute("currentPage", pageIndex);
            request.setAttribute("totalRecords", totalRecords);
        }
        
        // 4. Forward sang trang JSP
        request.getRequestDispatcher(jspPage).forward(request, response);
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
