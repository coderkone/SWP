<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>System Rules - DevQuery Admin</title>
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

        .btn-danger {
            background-color: #D0393E;
            color: white;
        }

        .btn-danger:hover {
            background-color: #b52e33;
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

        .content-preview {
            max-width: 400px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
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
        <div class="page-title">System Rules</div>
        <div class="admin-profile">
            <span class="admin-name">${sessionScope.USER.username}</span>
            <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="Admin Avatar" class="admin-avatar">
        </div>
    </header>

    <div class="container">

        <c:if test="${param.success == 'created'}">
            <div class="alert alert-success">Nội quy đã được tạo thành công!</div>
        </c:if>
        <c:if test="${param.success == 'updated'}">
            <div class="alert alert-success">Nội quy đã được cập nhật thành công!</div>
        </c:if>
        <c:if test="${param.success == 'deleted'}">
            <div class="alert alert-success">Nội quy đã được xóa thành công!</div>
        </c:if>
        <c:if test="${param.error == 'notfound'}">
            <div class="alert alert-error">Không tìm thấy nội quy!</div>
        </c:if>
        <c:if test="${param.error == 'deletefailed'}">
            <div class="alert alert-error">Không thể xóa nội quy. Vui lòng thử lại!</div>
        </c:if>

        <div class="toolbar">
            <form action="${pageContext.request.contextPath}/admin/rules/search" method="get" class="search-box">
                <input type="text" name="q" placeholder="Tìm kiếm theo tiêu đề..."
                       value="${searchKeyword}">
                <button type="submit" class="btn btn-secondary">Tìm kiếm</button>
                <c:if test="${not empty searchKeyword}">
                    <a href="${pageContext.request.contextPath}/admin/rules" class="btn btn-secondary">Xóa filter</a>
                </c:if>
            </form>

            <a href="${pageContext.request.contextPath}/admin/rules/create" class="btn btn-primary">
                + Thêm Nội quy
            </a>
        </div>

        <div class="section-box">
            <div class="section-header">
                <div class="section-title">
                    Danh sách Nội quy
                    <c:if test="${not empty searchKeyword}">
                        - Kết quả cho "${searchKeyword}"
                    </c:if>
                </div>
                <span style="color: var(--text-sub); font-size: 13px;">
                    Tổng: ${totalRules} nội quy
                </span>
            </div>

            <c:choose>
                <c:when test="${empty rules}">
                    <div class="empty-state">
                        <p>Chưa có nội quy nào.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                        <tr>
                            <th style="width: 60px;">ID</th>
                            <th style="width: 200px;">Tiêu đề</th>
                            <th>Nội dung</th>
                            <th style="width: 120px;">Người tạo</th>
                            <th style="width: 140px;">Ngày tạo</th>
                            <th style="width: 120px;">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="rule" items="${rules}">
                            <tr>
                                <td>${rule.ruleId}</td>
                                <td><strong>${rule.title}</strong></td>
                                <td class="content-preview">${rule.getTruncatedContent(100)}</td>
                                <td>${rule.createdByUsername}</td>
                                <td>
                                    <fmt:formatDate value="${rule.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </td>
                                <td class="actions">
                                    <a href="${pageContext.request.contextPath}/admin/rules/edit?id=${rule.ruleId}"
                                       class="btn btn-secondary btn-sm">Edit</a>

                                    <form action="${pageContext.request.contextPath}/admin/rules/delete"
                                          method="post" style="display:inline;"
                                          onsubmit="return confirm('Bạn có chắc muốn xóa nội quy này?');">
                                        <input type="hidden" name="id" value="${rule.ruleId}">
                                        <button type="submit" class="btn btn-danger btn-sm">Xóa</button>
                                    </form>
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
                    <a href="${pageContext.request.contextPath}/admin/rules?page=${currentPage - 1}">« Prev</a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${i == currentPage}">
                            <span class="active">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/admin/rules?page=${i}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/admin/rules?page=${currentPage + 1}">Next »</a>
                </c:if>
            </div>
        </c:if>

    </div>
</main>

</body>
</html>
