package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import dal.QuestionDAO;
import dto.QuestionDTO;

@WebServlet(name="HomeController", urlPatterns={"/home"})
public class HomeController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Lấy các tham số từ URL (ví dụ: home?page=2&q=java&tab=views)
        String keyword = request.getParameter("q");
        String sort = request.getParameter("tab"); // new, active, hot...
        String filter = request.getParameter("filter"); // unanswered...
        String pageStr = request.getParameter("page");
        String tag = request.getParameter("tag");

        // 2. Xử lý phân trang
        int pageIndex = 1;
        int pageSize = 10; // Hiển thị 10 bài/trang
        try {
            if (pageStr != null) pageIndex = Integer.parseInt(pageStr);
        } catch (NumberFormatException e) {
            pageIndex = 1;
        }

        // 3. Gọi DAO lấy dữ liệu
        QuestionDAO dao = new QuestionDAO();
        List<QuestionDTO> list = dao.getQuestions(pageIndex, pageSize, sort, keyword, filter, tag);
        int totalRecords = dao.getTotalQuestions(keyword, filter, tag);
        int totalPage = (totalRecords % pageSize == 0) ? (totalRecords / pageSize) : (totalRecords / pageSize + 1);

        List<String> popularTags = dao.getPopularTags(10);
        
        // 4. Gửi dữ liệu sang trang JSP
        request.setAttribute("questions", list);       // Danh sách câu hỏi
        request.setAttribute("totalPage", totalPage);  // Tổng số trang
        request.setAttribute("currentPage", pageIndex);// Trang hiện tại
        request.setAttribute("totalQuestions", totalRecords); // Tổng số câu hỏi 
        request.setAttribute("popularTags", popularTags); // Gửi top tags xuống view
        
        // Gửi lại các tham số lọc để giữ trạng thái active cho nút bấm
        request.setAttribute("currentSort", sort);
        request.setAttribute("currentKeyword", keyword);
        request.setAttribute("currentFilter", filter);
        request.setAttribute("currentTag", tag); 

        request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
    }
}
