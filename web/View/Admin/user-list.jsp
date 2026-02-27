<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - DevQuery Admin</title>
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
            max-width: 1400px;
            margin: 0 auto;
        }

        .toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            flex-wrap: wrap;
            gap: 15px;
        }

        .search-box {
            display: flex;
            gap: 10px;
        }

        .search-box input {
            padding: 8px 12px;
            border: 1px solid var(--border-color);
            border-radius: 4px;
            width: 250px;
            font-size: 14px;
        }

        .btn {
            padding: 8px 16px;
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

        .btn-success {
            background-color: #2f6f44;
            color: white;
        }

        .btn-danger {
            background-color: #D0393E;
            color: white;
        }

        .btn-warning {
            background-color: #f48024;
            color: white;
        }

        .btn-sm {
            padding: 4px 8px;
            font-size: 12px;
        }

        .section-box {
            background-color: var(--card-bg);
            border: 1px solid var(--border-color);
            border-radius: 5px;
            overflow: hidden;
        }

        .section-header {
            padding: 15px 20px;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .section-title {
            font-size: 16px;
            font-weight: bold;
            color: var(--text-main);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
        }

        th {
            background-color: #F8F9F9;
            color: var(--text-sub);
            font-weight: bold;
            text-align: left;
            padding: 12px 15px;
            border-bottom: 1px solid var(--border-color);
        }

        td {
            padding: 12px 15px;
            color: #3b4045;
            border-bottom: 1px solid #e3e6e8;
            vertical-align: middle;
        }

        tr:hover { background-color: #f8f9f9; }

        .status-badge {
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: 500;
            text-transform: uppercase;
        }

        .status-active { color: #2f6f44; background: #E3FCEF; }
        .status-inactive { color: #D0393E; background: #FDEDED; }

        .role-badge {
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 11px;
            font-weight: 500;
        }

        .role-admin { color: #0074cc; background: #e1ecf4; }
        .role-moderator { color: #5a32a3; background: #f3e8ff; }
        .role-member { color: #525960; background: #e3e6e8; }

        .actions { display: flex; gap: 5px; }

        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 5px;
            margin-top: 20px;
        }

        .pagination a, .pagination span {
            padding: 8px 12px;
            border: 1px solid var(--border-color);
            border-radius: 4px;
            text-decoration: none;
            color: var(--text-main);
            font-size: 13px;
        }

        .pagination a:hover { background-color: #f8f9f9; }
        .pagination .active {
            background-color: var(--active-orange);
            color: white;
            border-color: var(--active-orange);
        }

        .pagination .disabled {
            color: #ccc;
            pointer-events: none;
        }

        .alert {
            padding: 12px 16px;
            border-radius: 4px;
            margin-bottom: 20px;
            font-size: 14px;
        }

        .alert-success { background: #E3FCEF; color: #2f6f44; border: 1px solid #2f6f44; }
        .alert-error { background: #FDEDED; color: #D0393E; border: 1px solid #D0393E; }

        .empty-state {
            text-align: center;
            padding: 40px;
            color: var(--text-sub);
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
        <a href="${pageContext.request.contextPath}/admin/users" class="nav-item active">
            <span class="nav-icon">👥</span> User Management
        </a>
        <a href="${pageContext.request.contextPath}/admin/tags" class="nav-item">
            <span class="nav-icon">🏷️</span> Tag Management
        </a>
        <a href="#" class="nav-item">
            <span class="nav-icon">📋</span> Content Reports
        </a>
        <a href="#" class="nav-item">
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
        <div class="page-title">User Management</div>
        <div class="admin-profile">
            <span class="admin-name">${sessionScope.USER.username}</span>
            <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="Admin Avatar" class="admin-avatar">
        </div>
    </header>

    <div class="container">

        <c:if test="${param.success == 'created'}">
            <div class="alert alert-success">User đã được tạo thành công!</div>
        </c:if>
        <c:if test="${param.success == 'updated'}">
            <div class="alert alert-success">User đã được cập nhật thành công!</div>
        </c:if>
        <c:if test="${param.success == 'toggled'}">
            <div class="alert alert-success">Trạng thái user đã được thay đổi!</div>
        </c:if>
        <c:if test="${param.error == 'self-toggle'}">
            <div class="alert alert-error">Bạn không thể thay đổi trạng thái của chính mình!</div>
        </c:if>
        <c:if test="${param.error == 'self-deactivate'}">
            <div class="alert alert-error">Bạn không thể tự deactivate tài khoản của mình!</div>
        </c:if>

        <div class="toolbar">
            <form action="${pageContext.request.contextPath}/admin/users/search" method="get" class="search-box">
                <input type="text" name="q" placeholder="Tìm kiếm username hoặc email..."
                       value="${searchKeyword}">
                <button type="submit" class="btn btn-secondary">Tìm kiếm</button>
                <c:if test="${not empty searchKeyword}">
                    <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Xóa filter</a>
                </c:if>
            </form>

            <a href="${pageContext.request.contextPath}/admin/users/create" class="btn btn-primary">
                + Thêm User
            </a>
        </div>

        <!-- Filter Form -->
        <div class="toolbar" style="margin-bottom: 20px;">
            <form action="${pageContext.request.contextPath}/admin/users" method="get" style="display: flex; gap: 10px; align-items: center;">
                <label style="font-size: 14px; color: var(--text-sub);">Lọc theo:</label>
                <select name="role" style="padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 4px; font-size: 14px;">
                    <option value="">Tất cả Role</option>
                    <option value="admin" ${filterRole == 'admin' ? 'selected' : ''}>Admin</option>
                    <option value="moderator" ${filterRole == 'moderator' ? 'selected' : ''}>Moderator</option>
                    <option value="member" ${filterRole == 'member' ? 'selected' : ''}>Member</option>
                </select>
                <select name="status" style="padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 4px; font-size: 14px;">
                    <option value="">Tất cả Status</option>
                    <option value="active" ${filterStatus == 'active' ? 'selected' : ''}>Active</option>
                    <option value="inactive" ${filterStatus == 'inactive' ? 'selected' : ''}>Inactive</option>
                </select>
                <button type="submit" class="btn btn-secondary">Lọc</button>
                <c:if test="${not empty filterRole || not empty filterStatus}">
                    <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Xóa bộ lọc</a>
                </c:if>
            </form>
        </div>

        <div class="section-box">
            <div class="section-header">
                <div class="section-title">
                    Danh sách Users
                    <c:if test="${not empty searchKeyword}">
                        - Kết quả cho "${searchKeyword}"
                    </c:if>
                </div>
                <span style="color: var(--text-sub); font-size: 13px;">
                    Tổng: ${totalUsers} users
                </span>
            </div>

            <c:choose>
                <c:when test="${empty users}">
                    <div class="empty-state">
                        <p>Không tìm thấy user nào.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Created At</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td>${user.userId}</td>
                                <td><strong>${user.username}</strong></td>
                                <td>${user.email}</td>
                                <td>
                                    <span class="role-badge role-${user.role}">${user.role}</span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${user.status == 'active'}">
                                            <span class="status-badge status-active">Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge status-inactive">Inactive</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <fmt:formatDate value="${user.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </td>
                                <td class="actions">
                                    <a href="${pageContext.request.contextPath}/admin/users/edit?id=${user.userId}"
                                       class="btn btn-secondary btn-sm">Edit</a>

                                    <c:if test="${user.userId != sessionScope.USER.userId}">
                                        <form action="${pageContext.request.contextPath}/admin/users/toggle-status"
                                              method="post" style="display:inline;"
                                              onsubmit="return confirm('Bạn có chắc muốn thay đổi trạng thái user này?');">
                                            <input type="hidden" name="id" value="${user.userId}">
                                            <c:choose>
                                                <c:when test="${user.status == 'active'}">
                                                    <button type="submit" class="btn btn-danger btn-sm">Deactivate</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button type="submit" class="btn btn-success btn-sm">Activate</button>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>

        <c:if test="${totalPages > 1 && empty searchKeyword}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="${pageContext.request.contextPath}/admin/users?page=${currentPage - 1}&role=${filterRole}&status=${filterStatus}">« Prev</a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${i == currentPage}">
                            <span class="active">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/admin/users?page=${i}&role=${filterRole}&status=${filterStatus}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/admin/users?page=${currentPage + 1}&role=${filterRole}&status=${filterStatus}">Next »</a>
                </c:if>
            </div>
        </c:if>

    </div>
</main>

</body>
</html>
