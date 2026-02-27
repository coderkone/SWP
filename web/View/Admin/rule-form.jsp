<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${editMode ? 'Sửa Nội quy' : 'Thêm Nội quy'} - DevQuery Admin</title>
    <style>
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

        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: var(--font-stack);
            background-color: var(--body-bg);
            display: flex;
            min-height: 100vh;
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

        .nav-menu { list-style: none; margin-top: 20px; }

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

        .nav-icon { margin-right: 12px; font-size: 16px; }

        .logout-area { margin-top: auto; margin-bottom: 20px; }

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

        .container {
            padding: 30px;
            max-width: 900px;
            margin: 0 auto;
        }

        .form-box {
            background-color: var(--card-bg);
            border: 1px solid var(--border-color);
            border-radius: 5px;
            padding: 30px;
        }

        .form-title {
            font-size: 18px;
            font-weight: bold;
            color: var(--text-main);
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 1px solid var(--border-color);
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: var(--text-main);
            font-size: 14px;
        }

        .form-group label span.required {
            color: #D0393E;
        }

        .form-control {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid var(--border-color);
            border-radius: 4px;
            font-size: 14px;
            font-family: inherit;
            transition: border-color 0.2s;
        }

        .form-control:focus {
            outline: none;
            border-color: #0a95ff;
            box-shadow: 0 0 0 3px rgba(10, 149, 255, 0.1);
        }

        textarea.form-control {
            min-height: 200px;
            resize: vertical;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 6px;
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

        .form-actions {
            display: flex;
            gap: 10px;
            margin-top: 25px;
            padding-top: 20px;
            border-top: 1px solid var(--border-color);
        }

        .alert {
            padding: 12px 16px;
            border-radius: 4px;
            margin-bottom: 20px;
            font-size: 14px;
        }

        .alert-error { background: #FDEDED; color: #D0393E; border: 1px solid #D0393E; }

        .char-count {
            font-size: 12px;
            color: var(--text-sub);
            margin-top: 5px;
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
        <a href="#" class="nav-item">
            <span class="nav-icon">📋</span> Content Reports
        </a>
        <a href="${pageContext.request.contextPath}/admin/rules" class="nav-item active">
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
        <div class="page-title">${editMode ? 'Sửa Nội quy' : 'Thêm Nội quy mới'}</div>
        <div class="admin-profile">
            <span class="admin-name">${sessionScope.USER.username}</span>
            <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="Admin Avatar" class="admin-avatar">
        </div>
    </header>

    <div class="container">

        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <div class="form-box">
            <div class="form-title">
                ${editMode ? 'Chỉnh sửa nội quy' : 'Tạo nội quy mới'}
            </div>

            <form action="${pageContext.request.contextPath}/admin/rules/${editMode ? 'edit' : 'create'}"
                  method="post">

                <c:if test="${editMode}">
                    <input type="hidden" name="id" value="${rule.ruleId}">
                </c:if>

                <div class="form-group">
                    <label for="title">
                        Tiêu đề <span class="required">*</span>
                    </label>
                    <input type="text"
                           id="title"
                           name="title"
                           class="form-control"
                           maxlength="255"
                           required
                           placeholder="Nhập tiêu đề nội quy..."
                           value="${editMode ? rule.title : title}">
                    <div class="char-count">Tối đa 255 ký tự</div>
                </div>

                <div class="form-group">
                    <label for="content">
                        Nội dung <span class="required">*</span>
                    </label>
                    <textarea id="content"
                              name="content"
                              class="form-control"
                              required
                              placeholder="Nhập nội dung chi tiết của nội quy...">${editMode ? rule.content : content}</textarea>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">
                        ${editMode ? 'Cập nhật' : 'Tạo mới'}
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/rules" class="btn btn-secondary">
                        Hủy
                    </a>
                </div>
            </form>
        </div>

    </div>
</main>

</body>
</html>
