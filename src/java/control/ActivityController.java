//package control;
//
//import dal.ActivityDAO;
//import dal.UserDAO;
//import dto.BadgeDTO;
//import dto.ReputationDTO;
//import dto.SystemRuleDTO;
//import dto.UserDTO;
//import model.User;
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//@WebServlet(name = "ActivityController", urlPatterns = {"/activity"})
//public class ActivityController extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        if (session == null) {
//            response.sendRedirect(request.getContextPath() + "/auth/login");
//            return;
//        }
//
//        Object userObj = session.getAttribute("USER");
//        if (userObj == null) {
//            userObj = session.getAttribute("user");
//        }
//
//        long userId = -1;
//        if (userObj instanceof UserDTO) {
//            userId = ((UserDTO) userObj).getUserId();
//        } else if (userObj instanceof User) {
//            userId = ((User) userObj).getUserId();
//        }
//
//        if (userId <= 0) {
//            response.sendRedirect(request.getContextPath() + "/auth/login");
//            return;
//        }
//
//        UserDAO userDao = new UserDAO();
//        UserDTO userProfile = userDao.getUserProfileById(userId);
//        request.setAttribute("userProfile", userProfile);
//
//        String tab = request.getParameter("tab");
//        if (tab == null || tab.isEmpty()) {
//            tab = "summary";
//        }
//        request.setAttribute("currentTab", tab);
//
//        ActivityDAO dao = new ActivityDAO();
//
//        if ("summary".equals(tab)) {
//            Map<String, Integer> badgeCounts = dao.getBadgeCounts(userId);
//            request.setAttribute("badgeCounts", badgeCounts);
//
//        } else if ("reputation".equals(tab)) {
//            List<ReputationDTO> repList = dao.getReputationHistory(userId);
//            request.setAttribute("repList", repList);
//
//        } else if ("badges".equals(tab)) {
//            List<BadgeDTO> myBadges = dao.getUserBadges(userId);
//            request.setAttribute("myBadges", myBadges);
//
//        } else if ("privileges".equals(tab)) {
//            List<SystemRuleDTO> privilegesList = dao.getSystemPrivileges();
//            request.setAttribute("privilegesList", privilegesList);
//        }
//
//        request.getRequestDispatcher("/View/User/activity.jsp").forward(request, response);
//    }
//}