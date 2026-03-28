<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Badge Management - DevQuery Admin</title>
        <style>
            /* Core CSS - Đồng bộ hoàn toàn với Blog Manager */
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
            .btn-del {
                background: #dc3545;
                color: white;
                border: none;
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

            /* Badge Type Styling */
            .badge-tag {
                padding: 4px 10px;
                border-radius: 20px;
                font-size: 11px;
                font-weight: bold;
                text-transform: uppercase;
            }
            .badge-gold {
                background: #FFF4D5;
                color: #856404;
                border: 1px solid #FFE69C;
            }
            .badge-silver {
                background: #F2F2F2;
                color: #383d41;
                border: 1px solid #D6D8DB;
            }
            .badge-bronze {
                background: #FDEBD0;
                color: #784212;
                border: 1px solid #FAD7A0;
            }

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
                <a href="${pageContext.request.contextPath}/admin/badges" class="nav-item active">
                    <span class="nav-icon">🏅</span> Badge Management
                </a>
                <a href="${pageContext.request.contextPath}/admin/blogs" class="nav-item">
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
                <div class="page-title">Badge Management</div>
                <div class="admin-profile">
                    <span class="admin-name">${sessionScope.USER.username}</span>
                    <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="Admin Avatar" class="admin-avatar">
                </div>
            </header>

            <div class="container">
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success">Action completed successfully!</div>
                </c:if>

                <div class="toolbar">
                    <form action="${pageContext.request.contextPath}/admin/badges" method="get" class="search-box">
                        <input type="text" name="q" placeholder="Search badges..." value="${param.q}">
                        <select name="type" style="padding: 8px; border: 1px solid var(--border-color); border-radius: 4px;" onchange="this.form.submit()">
                            <option value="">All Ranks</option>
                            <option value="Gold" ${param.type == 'Gold' ? 'selected' : ''}>Gold</option>
                            <option value="Silver" ${param.type == 'Silver' ? 'selected' : ''}>Silver</option>
                            <option value="Bronze" ${param.type == 'Bronze' ? 'selected' : ''}>Bronze</option>
                        </select>
                        <a href="${pageContext.request.contextPath}/admin/badges" class="btn btn-edit" style="text-decoration: none;">
                            ✖ Clear
                        </a>
                    </form>
                    <a href="${pageContext.request.contextPath}/admin/badges/create" class="btn btn-primary">+ Create New Badge</a>
                </div>

                <div class="section-box">
                    <table>
                        <thead>
                            <tr>
                                <th style="width: 80px;">ID</th>
                                <th>Badge Details</th>
                                <th style="width: 150px;">Rank</th>
                                <th style="width: 150px;">Rep Required</th>
                                <th style="text-align: center; width: 180px;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="b" items="${badgeList}">
                                <tr>
                                    <td>#${b.badgeId}</td>
                                    <td>
                                        <div style="font-weight: bold; color: #0074cc; font-size: 14px;">${b.name}</div>
                                        <div style="color: var(--text-sub); font-size: 12px; margin-top: 2px;">${b.description}</div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${b.type.toLowerCase() == 'gold'}"><span class="badge-tag badge-gold">Gold</span></c:when>
                                            <c:when test="${b.type.toLowerCase() == 'silver'}"><span class="badge-tag badge-silver">Silver</span></c:when>
                                            <c:otherwise><span class="badge-tag badge-bronze">Bronze</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="font-weight: bold;">${b.requiredReputation} pts</td>
                                    <td style="text-align: center;">
                                        <div style="display: flex; gap: 6px; justify-content: center;">
                                            <a href="${pageContext.request.contextPath}/admin/badges/edit?id=${b.badgeId}" class="btn btn-edit btn-sm">Edit</a>
                                            <form action="${pageContext.request.contextPath}/admin/badges/delete" method="post" style="margin:0;" onsubmit="return confirm('Delete badge: ${b.name}?');">
                                                <input type="hidden" name="id" value="${b.badgeId}">
                                                <button type="submit" class="btn btn-del btn-sm">Delete</button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty badgeList}">
                                <tr>
                                    <td colspan="5" class="empty-state">No badges found matching your criteria.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </main>
    </body>
</html>