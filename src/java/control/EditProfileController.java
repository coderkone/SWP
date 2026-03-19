/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package control;

import com.google.gson.Gson;
import dal.ProfileDAO;
import model.User;
import dto.UserDTO;
import model.UserSocialLink;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "EditProfileController", urlPatterns = {"/edit-profile"})
public class EditProfileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect("View/User/login.jsp");
            return;
        }

        ProfileDAO profileDAO = new ProfileDAO();
        // ĐỔI TÊN HÀM Ở ĐÂY ĐỂ LẤY ĐẦY ĐỦ DỮ LIỆU
        UserDTO userProfile = profileDAO.getUserFullProfile(currentUser.getUserId());

        // Xử lý chuỗi JSON ra 3 link (Dùng UserSocialLink hoặc UserSocialLinks tùy project của bạn)
        model.UserSocialLink socialLinks = new model.UserSocialLink("", "", "");
        if (userProfile != null && userProfile.getWebsite() != null && userProfile.getWebsite().trim().startsWith("{")) {
            Gson gson = new Gson();
            socialLinks = gson.fromJson(userProfile.getWebsite(), model.UserSocialLink.class);
        }

        request.setAttribute("profile", userProfile);
        request.setAttribute("socialLinks", socialLinks);

        request.getRequestDispatcher("/View/User/editProfile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect("View/User/login.jsp");
            return;
        }

        // Lấy dữ liệu từ giao diện
        String displayName = request.getParameter("displayName"); // Thêm dòng này
        String bio = request.getParameter("bio");
        String location = request.getParameter("location");
        String github = request.getParameter("github");
        String linkedin = request.getParameter("linkedin");
        String website = request.getParameter("website");

        // Gom link thành JSON
        UserSocialLink linksObj = new UserSocialLink(github, linkedin, website);
        String websiteJson = new Gson().toJson(linksObj);

        // Gọi DAO cập nhật (Truyền thêm tham số displayName)
        ProfileDAO dao = new ProfileDAO();
        boolean isSuccess = dao.updateProfile(currentUser.getUserId(), displayName, bio, location, websiteJson);

        if (isSuccess) {
            // *** CỰC KỲ QUAN TRỌNG: Cập nhật lại tên mới vào Session ***
            currentUser.setUsername(displayName);
            session.setAttribute("user", currentUser);

            response.sendRedirect("profile?id=" + currentUser.getUserId() + "&status=success");
        } else {
            // Nếu trùng username thì báo lỗi
            request.setAttribute("ERROR", "Update failed! The display name might already be taken.");
            doGet(request, response);
        }
    }
}
