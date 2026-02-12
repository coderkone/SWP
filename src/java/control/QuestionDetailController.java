package control;


import dal.QuestionDAO;
import dto.QuestionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

// Controller này sẽ hứng URL dạng: /question?id=123
@WebServlet(name = "QuestionDetailController", urlPatterns = {"/question/detail"})
public class QuestionDetailController extends HttpServlet {

    private final QuestionDAO questionDao = new QuestionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Lấy ID từ URL
        String idParam = request.getParameter("id");
        
        // Validate ID
        if (idParam == null || !idParam.matches("\\d+")) {
            // Nếu không có ID hoặc ID không phải số -> Về trang chủ hoặc báo lỗi
            request.setAttribute("error", "Đường dẫn không hợp lệ.");
            request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
            return;
        }

        try {
            long questionId = Long.parseLong(idParam);

            // 2. Gọi DAO lấy thông tin chi tiết câu hỏi
            // (Bạn cần đảm bảo class QuestionDAO đã có hàm getQuestionById trả về QuestionDTO)
            QuestionDTO question = questionDao.getQuestionById(questionId);

            if (question != null) {
                // 3. Đẩy dữ liệu sang JSP
                request.setAttribute("question", question);
                
                // Chuyển hướng đến file JSP giao diện chi tiết (File JSP bạn đã gửi trước đó)
                // Hãy sửa đường dẫn này đúng với vị trí file thực tế trong project của bạn
                request.getRequestDispatcher("/View/User/question-detail.jsp").forward(request, response);
            } else {
                // Trường hợp ID hợp lệ nhưng không tìm thấy câu hỏi trong DB
                request.setAttribute("error", "Không tìm thấy câu hỏi này.");
                request.getRequestDispatcher("/View/User/home.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}