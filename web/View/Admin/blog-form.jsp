<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${not empty blog ? 'Edit Blog' : 'Create New Post'} - DevQuery Admin</title>
        <style>
            /* Shared Admin CSS */
            :root {
                --sidebar-bg: #2D3E50;
                --active-orange: #F48024;
                --body-bg: #F1F2F3;
                --border-color: #d6d9dc;
                --text-main: #2D3E50;
            }

            body {
                font-family: -apple-system, sans-serif;
                background: var(--body-bg);
                display: flex;
                margin: 0;
                min-height: 100vh;
            }
            .sidebar {
                width: 250px;
                background: var(--sidebar-bg);
                color: white;
                position: fixed;
                height: 100%;
            }
            .main-content {
                flex-grow: 1;
                margin-left: 250px;
                padding: 30px;
            }

            /* Form CSS */
            .form-container {
                background: white;
                padding: 30px;
                border-radius: 8px;
                border: 1px solid var(--border-color);
                max-width: 900px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            }

            .form-group {
                margin-bottom: 20px;
            }

            .form-group label {
                display: block;
                margin-bottom: 8px;
                font-weight: bold;
                color: var(--text-main);
                font-size: 14px;
            }

            .form-control {
                width: 100%;
                padding: 10px 12px;
                border: 1px solid var(--border-color);
                border-radius: 4px;
                font-size: 14px;
                font-family: inherit;
                box-sizing: border-box;
            }

            .form-control:focus {
                outline: none;
                border-color: #0a95ff;
                box-shadow: 0 0 0 4px rgba(0, 116, 204, 0.15);
            }

            textarea.form-control {
                resize: vertical;
                min-height: 300px;
                line-height: 1.5;
            }

            .btn {
                padding: 10px 20px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 14px;
                text-decoration: none;
                display: inline-block;
                font-weight: bold;
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
                margin-right: 10px;
            }
            .btn-secondary:hover {
                background-color: #f8f9f9;
            }

            .header-title {
                font-size: 24px;
                margin-bottom: 20px;
                color: var(--text-main);
                border-bottom: 2px solid var(--border-color);
                padding-bottom: 10px;
            }
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
        </style>
    </head>
    <body>

        <aside class="sidebar">
            <div class="logo-area">
                <b>QUERY</b>&nbsp;ADMIN
            </div>

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
            <div class="form-container">
                <h2 class="header-title">
                    <c:choose>
                        <c:when test="${not empty blog}">Edit Blog Post (ID: ${blog.blogId})</c:when>
                        <c:otherwise>Create New Post</c:otherwise>
                    </c:choose>
                </h2>

                <form action="${pageContext.request.contextPath}/admin/blogs/${not empty blog ? 'edit' : 'create'}" 
                      method="POST" enctype="multipart/form-data">

                    <c:if test="${not empty blog}">
                        <input type="hidden" name="id" value="${blog.blogId}">
                        <input type="hidden" name="oldThumbnailUrl" value="${blog.thumbnailUrl}">
                    </c:if>

                    <div class="form-group">
                        <label for="title">Blog Title <span style="color: red;">*</span></label>
                        <input type="text" id="title" name="title" class="form-control" 
                               value="${blog.title}" required placeholder="Enter an engaging title...">
                    </div>

                    <div class="form-group">
                        <label for="imageFile">Thumbnail Image</label>
                        <%-- Đổi type thành file --%>
                        <input type="file" id="imageFile" name="imageFile" class="form-control" accept="image/*">

                        <c:if test="${not empty blog.thumbnailUrl}">
                            <div style="margin-top: 10px;">
                                <p style="font-size: 12px; color: var(--text-sub);">Current Preview:</p>
                                <img src="${pageContext.request.contextPath}/${blog.thumbnailUrl}" style="width: 120px; border-radius: 4px; border: 1px solid var(--border-color);">
                            </div>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="status">Status</label>
                        <select id="status" name="status" class="form-control" style="width: 200px;">
                            <option value="1" ${blog.status == 1 ? 'selected' : ''}>Published</option>
                            <option value="0" ${blog.status == 0 ? 'selected' : ''}>Draft / Hidden</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="content">Content <span style="color: red;">*</span></label>
                        <textarea id="content" name="content" class="form-control" required 
                                  placeholder="Write your content here...">${blog.content}</textarea>
                    </div>

                    <div style="margin-top: 30px;">
                        <a href="${pageContext.request.contextPath}/admin/blogs" class="btn btn-secondary">Cancel</a>
                        <button type="submit" class="btn btn-primary">
                            ${not empty blog ? 'Save Changes' : 'Publish Post'}
                        </button>
                    </div>
                </form>
            </div>
        </main>

    </body>
</html>