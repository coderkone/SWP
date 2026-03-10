package control;

import dal.BadgeDAO;
import dal.UserDAO;
import dto.BadgeDTO;
import dto.ReputationDTO;
import dto.PrivilegeDTO;
import dto.UserDTO;
import model.User;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "BadgeController", urlPatterns = {"/badge"})
public class BadgeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // Yêu cầu đăng nhập
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/View/User/login.jsp");
            return;
        }

        // Lấy thông tin Profile
        UserDAO userDao = new UserDAO();
        UserDTO userProfile = userDao.getUserProfileById(user.getUserId());
        request.setAttribute("userProfile", userProfile);

        // Điều hướng Tab
        String tab = request.getParameter("tab");
        if (tab == null || tab.isEmpty()) {
            tab = "summary";
        }
        request.setAttribute("currentTab", tab);

        BadgeDAO dao = new BadgeDAO();

        // RẼ NHÁNH XỬ LÝ THEO TAB
        if (tab.equals("summary")) {
            Map<String, Integer> badgeCounts = dao.getBadgeCounts(user.getUserId());
            request.setAttribute("badgeCounts", badgeCounts);

        } else if (tab.equals("reputation")) {
            List<ReputationDTO> repList = dao.getReputationHistory(user.getUserId());
            request.setAttribute("repList", repList);

        } else if (tab.equals("badges")) {
            // Xử lý bộ lọc sắp xếp (Mặc định là newest)
            String sort = request.getParameter("sort");
            if (sort == null) {
                sort = "newest";
            }

            List<BadgeDTO> myBadges = dao.getUserBadges(user.getUserId(), sort);
            request.setAttribute("myBadges", myBadges);
            request.setAttribute("currentSort", sort); // Gửi lại để giữ trạng thái select box

        } else if (tab.equals("privileges")) {
            // Lấy danh sách quyền lợi
            List<PrivilegeDTO> privilegesList = dao.getAllPrivileges();
            request.setAttribute("privilegesList", privilegesList);

            // Xử lý logic Thanh tiến độ (Progress Bar)
            int currentRep = userProfile.getReputation();
            PrivilegeDTO nextPrivilege = null;

            // Tìm mốc quyền lợi tiếp theo chưa đạt được
            for (PrivilegeDTO priv : privilegesList) {
                if (priv.getRequiredReputation() > currentRep) {
                    nextPrivilege = priv;
                    break;
                }
            }

            if (nextPrivilege != null) {
                int targetRep = nextPrivilege.getRequiredReputation();
                int pointsNeeded = targetRep - currentRep;
                int progressPercent = (int) Math.round((currentRep * 100.0) / targetRep);

                request.setAttribute("nextPriv", nextPrivilege);
                request.setAttribute("pointsNeeded", pointsNeeded);
                request.setAttribute("progressPercent", progressPercent);
            } else {
                request.setAttribute("isMaxLevel", true);
            }
        }

        // Chú ý: Đảm bảo tên file JSP dưới này khớp với tên file của bạn
        request.getRequestDispatcher("/View/User/badge.jsp").forward(request, response);
    }
}
