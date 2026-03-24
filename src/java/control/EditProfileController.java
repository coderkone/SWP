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
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
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

        // CHUYỂN DÒNG NÀY LÊN TRÊN CÙNG ĐỂ KHAI BÁO TRƯỚC KHI DÙNG
        ProfileDAO dao = new ProfileDAO();

        // 1. XỬ LÝ UPLOAD FILE HOẶC XÓA FILE TRƯỚC
        String avatarUrl = null;
        Part filePart = request.getPart("avatarFile");
        String deleteAvatarFlag = request.getParameter("deleteAvatar");

        if ("true".equals(deleteAvatarFlag)) {
            // Trường hợp user bấm nút DELETE
            dao.updateAvatar(currentUser.getUserId(), null);
            currentUser.setAvatarUrl(null);
        } else if (filePart != null && filePart.getSize() > 0) {
            // Trường hợp user CHỌN ẢNH MỚI
            String buildPath = "D:\\SWP391-Group3\\SWP\\build\\web\\assets\\img\\avatar";
            String srcPath = "D:\\SWP391-Group3\\SWP\\web\\assets\\img\\avatar";
            File buildDir = new File(buildPath);
            File srcDir = new File(srcPath);
            if (!buildDir.exists()) buildDir.mkdirs();
            if (!srcDir.exists()) srcDir.mkdirs();

            String fileName = "user_" + currentUser.getUserId() + "_" + System.currentTimeMillis() + ".png";

            // Lưu đúp vào 2 nơi
            filePart.write(buildPath + File.separator + fileName);
            try {
                java.nio.file.Files.copy(
                    new java.io.File(buildPath + File.separator + fileName).toPath(),
                    new java.io.File(srcPath + File.separator + fileName).toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
            } catch (Exception e) {
                System.out.println("Error copying file: " + e.getMessage());
            }
            avatarUrl = "assets/img/avatar/" + fileName;

            // Cập nhật Database và Session với đường dẫn mới
            dao.updateAvatar(currentUser.getUserId(), avatarUrl);
            currentUser.setAvatarUrl(avatarUrl);
        }

        // 2. LẤY DỮ LIỆU TEXT CẬP NHẬT THÔNG TIN CÁC FIELD KHÁC
        String displayName = request.getParameter("displayName");
        String bio = request.getParameter("bio");
        String location = request.getParameter("location");
        String github = request.getParameter("github");
        String linkedin = request.getParameter("linkedin");
        String website = request.getParameter("website");

        UserSocialLink linksObj = new UserSocialLink(github, linkedin, website);
        String websiteJson = new Gson().toJson(linksObj);

        // 3. Cập nhật thông tin cơ bản
        boolean isSuccess = dao.updateProfile(currentUser.getUserId(), displayName, bio, location, websiteJson);

        if (isSuccess) {
            currentUser.setUsername(displayName);
            // Ghi đè lại object user vào Session để Header nhận diện sự thay đổi
            session.setAttribute("user", currentUser); 
            response.sendRedirect("profile?id=" + currentUser.getUserId() + "&status=success");
        } else {
            request.setAttribute("ERROR", "Update failed! The display name might already be taken.");
            doGet(request, response);
        }
    }
}
