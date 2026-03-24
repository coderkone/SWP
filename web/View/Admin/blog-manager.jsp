<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Blog Management - DevQuery Admin</title>
        <style>
            /* Core CSS */
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

            /* Sidebar */
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

            /* Main Content */
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

            /* Layout & Controls */
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
            .btn-secondary {
                background-color: white;
                color: var(--text-main);
                border: 1px solid var(--border-color);
            }
            .btn-sm {
                padding: 4px 10px;
                font-size: 12px;
            }
            .btn-edit {
                background: white;
                color: var(--text-main);
                border: 1px solid var(--border-color);
            }
            .btn-edit:hover {
                background: #f8f9f9;
            }

            /* Table */
            .section-box {
                background-color: var(--card-bg);
                border: 1px solid var(--border-color);
                border-radius: 5px;
                overflow: hidden;
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
            tr:hover {
                background-color: #f8f9f9;
            }
            .sort-link {
                text-decoration: none;
                color: inherit;
                display: inline-flex;
                align-items: center;
                gap: 4px;
            }
            .blog-title {
                font-weight: bold;
                color: #0074cc;
                display: block;
                max-width: 400px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }

            /* Status Toggle Button CSS */
            .status-toggle {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 8px 16px;
                border-radius: 20px;
                font-size: 11px;
                font-weight: bold;
                text-transform: uppercase;
                border: none;
                cursor: pointer;
                transition: 0.3s ease-in-out;
                font-family: inherit;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }
            .status-toggle:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 6px rgba(0,0,0,0.15);
            }
            .status-active {
                color: #fff;
                background: linear-gradient(135deg, #2ecc71, #27ae60);
            }
            .status-inactive {
                color: #fff;
                background: linear-gradient(135deg, #e67e22, #c0392b);
            }
            .status-text {
                letter-spacing: 1px;
            }

<<<<<<< HEAD
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
            /* Utilities */
            .alert {
                padding: 12px 16px;
                border-radius: 4px;
                margin-bottom: 20px;
                font-size: 14px;
            }
            .alert-success {
                background: #E3FCEF;
                color: #2f6f44;
                border: 1px solid #2f6f44;
            }
            .empty-state {
                text-align: center;
                padding: 40px;
                color: var(--text-sub);
            }
            /* Pagination CSS */
            .pagination-container {
                padding: 20px;
                display: flex;
                justify-content: center;
                gap: 5px;
            }
            .page-link {
                padding: 8px 14px;
                border: 1px solid var(--border-color);
                border-radius: 4px;
                text-decoration: none;
                color: var(--text-main);
                font-size: 13px;
                transition: 0.2s;
            }
            .page-link:hover {
                background-color: #f8f9f9;
                border-color: #babfc4;
            }
            .page-link.active {
                background-color: var(--active-orange);
                color: white;
                border-color: var(--active-orange);
                font-weight: bold;
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
                <a href="#" class="nav-item">
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
                    <span class="admin-name">${sessionScope.USER.username}</span>
                    <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="Admin Avatar" class="admin-avatar">
                </div>
            </header>

            <div class="container">
                <c:if test="${param.success == 'status_updated'}"><div class="alert alert-success">Blog status updated successfully!</div></c:if>
                <c:if test="${param.success == 'created'}"><div class="alert alert-success">Blog created successfully!</div></c:if>
                <c:if test="${param.success == 'updated'}"><div class="alert alert-success">Blog updated successfully!</div></c:if>
                <c:if test="${param.success == 'deleted'}"><div class="alert alert-success">Blog deleted successfully!</div></c:if>

                    <div class="toolbar">
                        <form action="${pageContext.request.contextPath}/admin/blogs" method="get" class="search-box">
                        <input type="text" name="q" placeholder="Search blog titles..." value="${searchKeyword}">

                        <select name="status" class="btn btn-secondary" style="border: 1px solid var(--border-color);" onchange="this.form.submit()">
                            <option value="">All Status</option>
                            <option value="1" ${selectedStatus == '1' ? 'selected' : ''}>Published</option>
                            <option value="0" ${selectedStatus == '0' ? 'selected' : ''}>Hidden</option>
                        </select>
                        <a href="${pageContext.request.contextPath}/admin/blogs" class="btn btn-edit" style="text-decoration: none;">
                            ✖ Clear
                        </a>
                    </form>
                    <a href="${pageContext.request.contextPath}/admin/blogs/create" class="btn btn-primary">+ Create New Post</a>
                </div>

                <div class="section-box">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Title</th>
                                <th>
                                    <a href="?q=${searchKeyword}&status=${selectedStatus}&sort=views&order=${param.sort == 'views' && param.order == 'asc' ? 'desc' : 'asc'}" class="sort-link">
                                        Views ${param.sort == 'views' ? (param.order == 'asc' ? '▲' : '▼') : '↕'}
                                    </a>
                                </th>
                                <th>
                                    <a href="?q=${searchKeyword}&status=${selectedStatus}&sort=comments&order=${param.sort == 'comments' && param.order == 'asc' ? 'desc' : 'asc'}" class="sort-link">
                                        Comments ${param.sort == 'comments' ? (param.order == 'asc' ? '▲' : '▼') : '↕'}
                                    </a>
                                </th>
                                <th>Created At</th>
                                <th>Status</th>
                                <th style="text-align: center;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="blog" items="${blogList}">
                                <tr>
                                    <td>${blog.blogId}</td>
                                    <td><span class="blog-title" title="${blog.title}">${blog.title}</span></td>
                                    <td><fmt:formatNumber value="${blog.viewCount}" type="number"/></td>
                                    <td>${blog.commentCount}</td>
                                    <td><fmt:formatDate value="${blog.createdAt}" pattern="dd/MM/yyyy"/></td>

                                    <td>
                                        <form action="${pageContext.request.contextPath}/admin/blogs/toggle-status" method="POST" style="margin:0;">
                                            <input type="hidden" name="id" value="${blog.blogId}">
                                            <input type="hidden" name="newStatus" value="${blog.status == 1 ? 0 : 1}">
                                            <button type="submit" class="status-toggle ${blog.status == 1 ? 'status-active' : 'status-inactive'}" title="Click to change status">
                                                <span class="status-text">${blog.status == 1 ? 'PUBLISHED' : 'HIDDEN'}</span>
                                            </button>
                                        </form>
                                    </td>

                                    <td style="text-align: center;">
                                        <a href="blogs/edit?id=${blog.blogId}" class="btn btn-edit btn-sm">Edit</a>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty blogList}">
                                <tr>
                                    <td colspan="7" class="empty-state">No blogs were found matching your criteria.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>

                    <c:if test="${totalPages > 1}">
                        <div class="pagination-container">
                            <c:if test="${currentPage > 1}">
                                <a href="?page=${currentPage - 1}&q=${searchKeyword}&status=${selectedStatus}&sort=${param.sort}&order=${param.order}" class="page-link">« Prev</a>
                            </c:if>

                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <a href="?page=${i}&q=${searchKeyword}&status=${selectedStatus}&sort=${param.sort}&order=${param.order}" 
                                   class="page-link ${i == currentPage ? 'active' : ''}">
                                    ${i}
                                </a>
                            </c:forEach>

                            <c:if test="${currentPage < totalPages}">
                                <a href="?page=${currentPage + 1}&q=${searchKeyword}&status=${selectedStatus}&sort=${param.sort}&order=${param.order}" class="page-link">Next »</a>
                            </c:if>
                        </div>
                    </c:if>
                </div>
            </div>
        </main>
    </body>
</html>