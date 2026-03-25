<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${not empty blog ? 'Edit Blog' : 'Create New Post'} - DevQuery Admin</title>
        <style>
            /* 1. Biến dùng chung */
            :root {
                --sidebar-bg: #2D3E50;
                --sidebar-hover: #3A4B5D;
                --active-orange: #F48024;
                --body-bg: #F1F2F3;
                --card-bg: #ffffff;
                --text-main: #2D3E50;
                --text-sub: #838C95;
                --border-color: #d6d9dc;
                --font-stack: -apple-system, BlinkMacSystemFont, "Segoe UI", Arial, sans-serif;
            }

            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: var(--font-stack);
                background-color: var(--body-bg);
                display: flex;
                min-height: 100vh;
            }

            /* 2. Sidebar */
            .sidebar {
                width: 250px;
                background-color: var(--sidebar-bg);
                color: #AAB7C4;
                display: flex;
                flex-direction: column;
                position: fixed;
                height: 100%;
            }
            .logo-area {
                height: 60px;
                background-color: #233140;
                display: flex;
                align-items: center;
                justify-content: center;
                color: white;
                font-size: 18px;
                letter-spacing: 1px;
            }
            .nav-menu {
                list-style: none;
                margin-top: 20px;
            }
            .nav-item {
                display: flex;
                align-items: center;
                padding: 15px 25px;
                font-size: 14px;
                cursor: pointer;
                transition: 0.2s;
                text-decoration: none;
                color: inherit;
            }
            .nav-item:hover {
                background-color: var(--sidebar-hover);
                color: white;
            }
            .nav-item.active {
                background-color: var(--active-orange);
                color: white;
                border-left: 4px solid #cc5e05;
            }
            .nav-icon {
                margin-right: 12px;
                font-size: 16px;
            }
            .logout-area {
                margin-top: auto;
                margin-bottom: 20px;
            }

            /* 3. Main Content & Header */
            .main-content {
                flex-grow: 1;
                margin-left: 250px;
                padding-bottom: 30px;
            }
            .top-header {
                height: 60px;
                background-color: white;
                border-bottom: 1px solid var(--border-color);
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 0 30px;
                position: sticky;
                top: 0;
                z-index: 10;
            }
            .page-title {
                font-size: 20px;
                font-weight: bold;
                color: var(--text-main);
            }
            .admin-profile {
                display: flex;
                align-items: center;
                gap: 10px;
            }
            .admin-name {
                font-size: 14px;
                font-weight: bold;
                color: var(--text-main);
            }
            .admin-avatar {
                width: 35px;
                height: 35px;
                border-radius: 50%;
                background-color: #e1ecf4;
                padding: 2px;
            }

            /* 4. Layout Container */
            .container {
                padding: 30px;
                max-width: 1200px;
                margin: 0 auto;
            }

            /* 5. Form Box Styling (Dành riêng cho Blog form rộng hơn) */
            .form-card {
                background: var(--card-bg);
                padding: 30px 40px;
                border-radius: 8px;
                max-width: 900px; /* Rộng hơn form badge để nhập text dễ hơn */
                margin: 0 auto;
                border: 1px solid var(--border-color);
                box-shadow: 0 4px 6px rgba(0,0,0,0.05);
            }
            .form-title {
                font-size: 22px;
                color: var(--text-main);
                margin-bottom: 25px;
                font-weight: bold;
                border-bottom: 1px solid var(--border-color);
                padding-bottom: 10px;
            }
            .form-group {
                margin-bottom: 20px;
            }

            .form-label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                color: var(--text-main);
                font-size: 14px;
            }

            /* Inputs */
            .form-control {
                width: 100%;
                padding: 10px 14px;
                border: 1px solid #babfc4;
                border-radius: 5px;
                font-size: 14px;
                color: #3b4045;
                transition: all 0.2s ease-in-out;
                font-family: inherit;
            }
            .form-control:focus {
                outline: none;
                border-color: #0a95ff;
                box-shadow: 0 0 0 4px rgba(0, 116, 204, 0.15);
            }
            textarea.form-control {
                resize: vertical;
                min-height: 350px;
                line-height: 1.6;
            }

            /* 6. Buttons */
            .form-actions {
                display: flex;
                gap: 15px;
                margin-top: 30px;
                border-top: 1px solid var(--border-color);
                padding-top: 20px;
            }
            .btn {
                padding: 10px 20px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 14px;
                font-weight: bold;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                transition: 0.2s;
            }
            .btn-primary {
                background-color: #0a95ff;
                color: white;
            }
            .btn-primary:hover {
                background-color: #0074cc;
            }
            .btn-secondary {
                background-color: white;
                color: var(--text-main);
                border: 1px solid var(--border-color);
            }
            .btn-secondary:hover {
                background-color: #f8f9f9;
            }

            /* Tiện ích text */
            .text-danger {
                color: #dc3545;
            }
            .text-muted {
                font-size: 13px;
                color: var(--text-sub);
                margin-bottom: 5px;
            }
            .image-preview {
                margin-top: 10px;
                width: 150px;
                border-radius: 4px;
                border: 1px solid var(--border-color);
            }
        </style>
    </head>
    <body>

        <aside class="sidebar">
            <div class="logo-area"><b>QUERY</b>&nbsp;ADMIN</div>
            <nav class="nav-menu">
                <a href="${pageContext.request.contextPath}/dashboard" class="nav-item">
                    <span class="nav-icon">📊</span> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/admin/users" class="nav-item">
                    <span class="nav-icon">👥</span> User Management
                </a>
                <a href="${pageContext.request.contextPath}/admin/tags" class="nav-item">
                    <span class="nav-icon">🏷️</span> Tag Management
                </a>
                <a href="${pageContext.request.contextPath}/admin/reports" class="nav-item">
                    <span class="nav-icon">📋</span> Content Reports
                </a>
                <a href="${pageContext.request.contextPath}/admin/badges" class="nav-item">
                    <span class="nav-icon">🏅</span> Badge Management
                </a>
                <a href="${pageContext.request.contextPath}/admin/blogs" class="nav-item active">
                    <span class="nav-icon">📝</span> Blog Management
                </a> 
                <a href="${pageContext.request.contextPath}/admin/rules" class="nav-item">
                    <span class="nav-icon">⚙️</span> System Rules
                </a>                
            </nav>
            <div class="logout-area">
                <a href="${pageContext.request.contextPath}/logout" class="nav-item">
                    <span class="nav-icon">🚪</span> Log Out
                </a>
            </div>
        </aside>

        <main class="main-content">
            <header class="top-header">
                <div class="page-title">Blog Management</div>
                <div class="admin-profile">
                    <span class="admin-name">${sessionScope.USER.username != null ? sessionScope.USER.username : 'Admin'}</span>
                    <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="Avatar" class="admin-avatar">
                </div>
            </header>

            <div class="container">
                <div class="form-card">
                    <h2 class="form-title">
                        <c:choose>
                            <c:when test="${not empty blog}">✎ Edit Blog Post (ID: ${blog.blogId})</c:when>
                            <c:otherwise>+ Create New Post</c:otherwise>
                        </c:choose>
                    </h2>

                    <form action="${pageContext.request.contextPath}/admin/blogs/${not empty blog ? 'edit' : 'create'}" 
                          method="POST" enctype="multipart/form-data">

                        <c:if test="${not empty blog}">
                            <input type="hidden" name="id" value="${blog.blogId}">
                            <input type="hidden" name="oldThumbnailUrl" value="${blog.thumbnailUrl}">
                        </c:if>

                        <div class="form-group">
                            <label for="title" class="form-label">Blog Title <span class="text-danger">*</span></label>
                            <input type="text" id="title" name="title" class="form-control" 
                                   value="${blog.title}" required placeholder="Enter an engaging title...">
                        </div>

                        <div class="form-group">
                            <label for="imageFile" class="form-label">Thumbnail Image</label>
                            <input type="file" id="imageFile" name="imageFile" class="form-control" accept="image/*">

                            <c:if test="${not empty blog.thumbnailUrl}">
                                <div style="margin-top: 15px;">
                                    <p class="text-muted">Current Preview:</p>
                                    <img src="${pageContext.request.contextPath}/${blog.thumbnailUrl}" class="image-preview" alt="Thumbnail Preview">
                                </div>
                            </c:if>
                        </div>

                        <div class="form-group">
                            <label for="status" class="form-label">Status</label>
                            <select id="status" name="status" class="form-control" style="width: 250px;">
                                <option value="1" ${blog.status == 1 ? 'selected' : ''}>Published</option>
                                <option value="0" ${blog.status == 0 ? 'selected' : ''}>Draft / Hidden</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="content" class="form-label">Content <span class="text-danger">*</span></label>
                            <textarea id="content" name="content" class="form-control" required 
                                      placeholder="Write your content here... (HTML tags supported if handled by backend)">${blog.content}</textarea>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">
                                ${not empty blog ? 'Save Changes' : 'Publish Post'}
                            </button>
                            <a href="${pageContext.request.contextPath}/admin/blogs" class="btn btn-secondary">Cancel</a>
                        </div>
                    </form>
                </div>
            </div>
        </main>
        <script src="https://cdn.ckeditor.com/4.22.1/standard-all/ckeditor.js"></script>

        <script>
            // Gắn CKEditor vào thẻ textarea có id="content"
            CKEDITOR.replace('content', {
                versionCheck: false,
                extraPlugins: 'codesnippet', // Bật tính năng chèn code
                codeSnippet_theme: 'monokai_sublime', // Giao diện nền tối cho code
                height: 400,
                removeButtons: 'PasteFromWord' // Xóa bớt nút thừa cho gọn
            });
        </script>
    </body>
</html>