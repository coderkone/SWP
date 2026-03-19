<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Profile - DevQuery</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body { 
            background-color: #f4f6f8; 
            color: #3b4045; 
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
        }
        .profile-container { max-width: 850px; margin: 40px auto; }
        .edit-card { 
            background: #ffffff; 
            border: none; 
            border-radius: 12px; 
            box-shadow: 0 5px 15px rgba(0,0,0,0.05); 
            padding: 30px; 
            margin-bottom: 25px;
        }
        .section-title { 
            font-size: 1.25rem; 
            font-weight: 700; 
            color: #f48024; /* Màu cam chủ đạo */
            margin-bottom: 20px; 
            padding-bottom: 10px;
            border-bottom: 2px solid #eff0f1;
            display: flex;
            align-items: center;
        }
        .section-title i { margin-right: 10px; color: #0a95ff; } /* Icon màu xanh */
        .form-label { font-weight: 600; color: #232629; }
        .form-control:focus { 
            border-color: #0a95ff; 
            box-shadow: 0 0 0 0.25rem rgba(10, 149, 255, 0.15); 
        }
        .btn-save { 
            background: #0a95ff; 
            border: none; 
            color: white; 
            font-weight: 600; 
            padding: 10px 25px; 
        }
        .btn-save:hover { background: #0074cc; color: white; }
        .avatar-placeholder {
            width: 140px; height: 140px;
            background: #e3e6e8; border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            font-size: 3.5rem; color: #adb5bd; margin: 0 auto 20px auto;
            border: 4px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body>
    <div class="profile-container">
        <h2 class="mb-4 fw-bold">Edit Profile</h2>
        
        <c:if test="${not empty ERROR}">
            <div class="alert alert-danger shadow-sm">${ERROR}</div>
        </c:if>

        <form action="edit-profile" method="POST">
            <div class="row">
                <div class="col-md-3 text-center mb-4">
                    <div class="edit-card h-100 p-3">
                        <div class="avatar-placeholder">
                            <i class="fas fa-user"></i>
                        </div>
                        <button type="button" class="btn btn-outline-secondary btn-sm w-100">Change Picture</button>
                    </div>
                </div>

                <div class="col-md-9">
                    <div class="edit-card">
                        <h4 class="section-title"><i class="fas fa-id-card"></i> Public Information</h4>
                        
                        <div class="mb-4">
                            <label class="form-label">Display Name</label>
                            <input type="text" class="form-control" name="displayName" value="${profile.username}" required>
                            <div class="form-text small">Tên này sẽ hiển thị với mọi người trên diễn đàn.</div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label">Location</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-map-marker-alt"></i></span>
                                <input type="text" class="form-control" name="location" value="${profile.location}" placeholder="Ví dụ: Hà Nội, Việt Nam">
                            </div>
                        </div>

                        <div class="mb-0">
                            <label class="form-label">About Me</label>
                            <textarea class="form-control" name="bio" rows="6" placeholder="Giới thiệu một chút về bản thân bạn...">${profile.bio}</textarea>
                        </div>
                    </div>

                    <div class="edit-card">
                        <h4 class="section-title"><i class="fas fa-share-alt"></i> Social Links</h4>
                        
                        <div class="mb-3">
                            <label class="form-label">GitHub URL</label>
                            <div class="input-group">
                                <span class="input-group-text bg-dark text-white"><i class="fab fa-github"></i></span>
                                <input type="url" class="form-control" name="github" value="${socialLinks.github}" placeholder="https://github.com/username">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">LinkedIn URL</label>
                            <div class="input-group">
                                <span class="input-group-text bg-primary text-white"><i class="fab fa-linkedin-in"></i></span>
                                <input type="url" class="form-control" name="linkedin" value="${socialLinks.linkedin}" placeholder="https://linkedin.com/in/username">
                            </div>
                        </div>

                        <div class="mb-0">
                            <label class="form-label">Personal Website</label>
                            <div class="input-group">
                                <span class="input-group-text bg-secondary text-white"><i class="fas fa-link"></i></span>
                                <input type="url" class="form-control" name="website" value="${socialLinks.website}" placeholder="https://yourwebsite.com">
                            </div>
                        </div>
                    </div>

                    <div class="d-flex justify-content-end mb-5">
                        <a href="profile?id=${sessionScope.user.userId}" class="btn btn-light border me-3 px-4">Cancel</a>
                        <button type="submit" class="btn btn-save shadow-sm px-4">Save Changes</button>
                    </div>
                </div>
            </div>
        </form>
    </div>
</body>
</html>