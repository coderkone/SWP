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
        UserDTO userProfile = profileDAO.getUserFullProfile(currentUser.getUserId());

        // Xử lý chuỗi JSON ra 3 link 
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

        ProfileDAO dao = new ProfileDAO();

        // 1. XÓA HOẶC UPLOAD ẢNH MỚI
        Part filePart = request.getPart("avatarFile");
        String deleteAvatarFlag = request.getParameter("deleteAvatar");

        if ("true".equals(deleteAvatarFlag)) {
            // Click DELETE avatar
            dao.updateAvatar(currentUser.getUserId(), null);
            currentUser.setAvatarUrl(null);
            session.setAttribute("user", currentUser);

        } else if (filePart != null && filePart.getSize() > 0) {

            // Lấy đường dẫn động chuyển thành đường dẫn tĩnh
            String buildPath = getServletContext().getRealPath("");
            String sourcePath = buildPath.substring(0, buildPath.indexOf("build")) + "web";

            // Khai báo thư mục lưu ảnh 
            String buildAvatarDir = buildPath + File.separator + "assets" + File.separator + "img" + File.separator + "avatar";
            String sourceAvatarDir = sourcePath + File.separator + "assets" + File.separator + "img" + File.separator + "avatar";

            // Tạo thư mục nếu chưa có
            File bDir = new File(buildAvatarDir);
            if (!bDir.exists()) {
                bDir.mkdirs();
            }
            File sDir = new File(sourceAvatarDir);
            if (!sDir.exists()) {
                sDir.mkdirs();
            }

            String originalName = filePart.getSubmittedFileName();
            String ext = (originalName != null && originalName.contains("."))
                    ? originalName.substring(originalName.lastIndexOf(".")).toLowerCase()
                    : ".png";

            String fileName = "user_" + currentUser.getUserId() + "_" + System.currentTimeMillis() + ext;

            // Lưu vào build/web 
            filePart.write(buildAvatarDir + File.separator + fileName);

            // Copy sang source gốc
            Files.copy(
                    new File(buildAvatarDir + File.separator + fileName).toPath(),
                    new File(sourceAvatarDir + File.separator + fileName).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            String avatarUrl = "assets/img/avatar/" + fileName;

            // Cập nhật DB và session
            dao.updateAvatar(currentUser.getUserId(), avatarUrl);
            currentUser.setAvatarUrl(avatarUrl);
            session.setAttribute("user", currentUser);
        }

        // 2. CẬP NHẬT CÁC THÔNG TIN TEXT
        String displayName = request.getParameter("displayName");
        String bio = request.getParameter("bio");
        String location = request.getParameter("location");
        String github = request.getParameter("github");
        String linkedin = request.getParameter("linkedin");
        String website = request.getParameter("website");

        UserSocialLink linksObj = new UserSocialLink(
                github != null ? github : "",
                linkedin != null ? linkedin : "",
                website != null ? website : ""
        );
        String websiteJson = new Gson().toJson(linksObj);

        // 3. LƯU VÀO DB VÀ REDIRECT
        boolean isSuccess = dao.updateProfile(
                currentUser.getUserId(), displayName, bio, location, websiteJson);

        if (isSuccess) {
            currentUser.setUsername(displayName);
            session.setAttribute("user", currentUser);
            response.sendRedirect("profile?id=" + currentUser.getUserId() + "&status=success");
        } else {
            request.setAttribute("ERROR", "Update failed! The display name might already be taken.");
            
            // Tạo profile tạm thời
            UserDTO tempProfile = new UserDTO();
            tempProfile.setUsername(displayName);
            tempProfile.setBio(bio);
            tempProfile.setLocation(location);
            tempProfile.setAvatarUrl(currentUser.getAvatarUrl()); 
            
            request.setAttribute("profile", tempProfile);
            request.setAttribute("socialLinks", linksObj);
            
            request.getRequestDispatcher("/View/User/editProfile.jsp").forward(request, response);
        }
    }
}
