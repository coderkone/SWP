<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>DevQuery Admin Dashboard</title>
        <style>
            /* BASE & RESET */
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

            /* SIDEBAR */
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

            /* MAIN CONTENT */
            .main-content {
                flex-grow: 1;
                margin-left: 250px;
                padding-bottom: 30px;
            }

            /* TOP HEADER */
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

            /* DASHBOARD GRID */
            .dashboard-container {
                padding: 30px;
                max-width: 1400px;
                margin: 0 auto;
            }

            /* STAT CARDS */
            .stats-grid {
                display: grid;
                grid-template-columns: repeat(4, 1fr);
                gap: 20px;
                margin-bottom: 30px;
            }

            .card {
                background-color: var(--card-bg);
                border-radius: 5px;
                padding: 20px;
                border: 1px solid var(--border-color);
                position: relative;
                display: block;
                text-decoration: none;
                color: inherit;
                transition: transform 0.2s;
            }
            
            .card:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            }

            .card-title {
                font-size: 11px;
                font-weight: bold;
                color: var(--text-sub);
                text-transform: uppercase;
                margin-bottom: 10px;
            }

            .card-value {
                font-size: 28px;
                font-weight: bold;
                color: var(--text-main);
                margin-bottom: 10px;
            }

            .card-value.red {
                color: #D0393E;
            }

            .card-trend {
                font-size: 12px;
                color: #6a737c;
            }

            .card-icon-bg {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 20px;
                position: absolute;
                top: 20px;
                right: 20px;
            }

            /* MIDDLE SECTION (Chart & Reports) */
            .middle-section {
                display: grid;
                grid-template-columns: 2fr 1fr;
                gap: 20px;
                margin-bottom: 30px;
            }

            .section-box {
                background-color: var(--card-bg);
                border: 1px solid var(--border-color);
                border-radius: 5px;
                padding: 20px;
            }

            .section-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 20px;
            }

            .section-title {
                font-size: 16px;
                font-weight: bold;
                color: var(--text-main);
            }

            /* CHART */
            .chart-placeholder {
                height: 200px;
                width: 100%;
                border-left: 1px solid #d6d9dc;
                border-bottom: 1px solid #d6d9dc;
                position: relative;
                margin-top: 30px;
            }

            .chart-svg {
                width: 100%;
                height: 100%;
                overflow: visible;
            }

            .chart-path {
                fill: none;
                stroke: #0A95FF;
                stroke-width: 3;
                stroke-linecap: round;
            }

            /* TABLE STYLES */
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
                padding: 10px 15px;
                border-bottom: 1px solid var(--border-color);
            }

            td {
                padding: 12px 15px;
                color: #3b4045;
                border-bottom: 1px solid #e3e6e8;
            }

            .status-badge {
                padding: 4px 8px;
                border-radius: 12px;
                font-size: 11px;
                font-weight: 500;
            }
            .status-active { color: #2f6f44; background: #E3FCEF; }
            .status-pending { color: #D0393E; background: #FDEDED; }

            .btn-link {
                color: #0074cc;
                text-decoration: none;
                font-weight: bold;
                font-size: 12px;
            }
            .btn-link:hover { text-decoration: underline; }

            .mini-title {
                font-size: 12px;
                color: #6a737c;
            }

        </style>
    </head>
    <body>

        <aside class="sidebar">
            <div class="logo-area">
                <b>QUERY</b>&nbsp;ADMIN
            </div>

            <nav class="nav-menu">
                <a href="${pageContext.request.contextPath}/dashboard" class="nav-item active">
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
                <div class="page-title">Dashboard Overview</div>
                <div class="admin-profile">
                    <span class="admin-name">${sessionScope.USER.username}</span>
                    <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="Admin Avatar" class="admin-avatar">
                </div>
            </header>

            <div class="dashboard-container">

                <!-- Stats Grid -->
                <div class="stats-grid">
                    <a href="${pageContext.request.contextPath}/admin/users" class="card">
                        <div class="card-title">Total Users</div>
                        <div class="card-value">
                            <fmt:formatNumber value="${totalUsers}" pattern="#,###"/>
                        </div>
                        <div class="card-trend">View all users →</div>
                        <div class="card-icon-bg" style="background-color: #E1ECF4;">👥</div>
                    </a>

                    <div class="card">
                        <div class="card-title">Questions</div>
                        <div class="card-value">
                            <fmt:formatNumber value="${totalQuestions}" pattern="#,###"/>
                        </div>
                        <div class="card-trend">Total questions posted</div>
                        <div class="card-icon-bg" style="background-color: #FFF4E5;">❓</div>
                    </div>

                    <div class="card">
                        <div class="card-title">Answers</div>
                        <div class="card-value">
                            <fmt:formatNumber value="${totalAnswers}" pattern="#,###"/>
                        </div>
                        <div class="card-trend">Total answers posted</div>
                        <div class="card-icon-bg" style="background-color: #E3FCEF;">💬</div>
                    </div>

                    <a href="${pageContext.request.contextPath}/admin/reports" class="card">
                        <div class="card-title">Pending Reports</div>
                        <div class="card-value red">${totalReports != null ? totalReports : 0}</div>
                        <div class="card-trend">Click to view all reports</div>
                        <div class="card-icon-bg" style="background-color: #FDEDED;">⚠️</div>
                    </a>
                </div>

                <!-- Middle Section -->
                <div class="middle-section">
                    <div class="section-box">
                        <div class="section-header">
                            <div class="section-title">Platform Growth (Last 7 Days)</div>
                            <div style="font-size: 12px; color: #525960;">Activity Chart</div>
                        </div>
                        <div class="chart-placeholder">
                            <svg class="chart-svg" viewBox="0 0 600 200" preserveAspectRatio="none">
                                <line x1="0" y1="50" x2="600" y2="50" stroke="#f0f0f0" />
                                <line x1="0" y1="100" x2="600" y2="100" stroke="#f0f0f0" />
                                <line x1="0" y1="150" x2="600" y2="150" stroke="#f0f0f0" />
                                <path class="chart-path" d="M0,150 C100,140 200,80 300,60 S500,70 600,20" />
                            </svg>
                        </div>
                    </div>

                    <div class="section-box">
                        <div class="section-header">
                            <div class="section-title">Recent Reports</div>
                            <a href="${pageContext.request.contextPath}/admin/reports" class="btn-link">View All</a>
                        </div>
                        <table>
                            <thead>
                                <tr>
                                    <th>Target</th>
                                    <th>Reason</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>Question #102</td>
                                    <td>Spam content</td>
                                    <td><span class="status-badge status-pending">Pending</span></td>
                                </tr>
                                <tr>
                                    <td>Answer #55</td>
                                    <td>Harassment</td>
                                    <td><span class="status-badge status-pending">Pending</span></td>
                                </tr>
                                <tr>
                                    <td>User @Spammer</td>
                                    <td>Fake Account</td>
                                    <td><span class="status-badge status-active">Resolved</span></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Bottom Sections -->
                <div class="middle-section" style="grid-template-columns: 1fr 1fr; margin-bottom: 30px;">
                    <div class="section-box">
                        <div class="section-header">
                            <div class="section-title">Questions By Tag</div>
                            <div class="mini-title">Top tags this month</div>
                        </div>
                        <table>
                            <thead>
                                <tr><th>Tag</th><th>Count</th></tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty questionByTagCurrentMonth}">
                                        <tr><td colspan="2" style="text-align:center;">No data</td></tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="item" items="${questionByTagCurrentMonth}">
                                            <tr><td><strong>${item.tagName}</strong></td><td>${item.questionCount}</td></tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <div class="section-box">
                        <div class="section-header">
                            <div class="section-title">Tag Monthly Stats</div>
                            <div class="mini-title">Last 6 months</div>
                        </div>
                        <table>
                            <thead>
                                <tr><th>Month</th><th>Active</th><th>Questions</th></tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty tagMonthlyStats}">
                                        <tr><td colspan="3" style="text-align:center;">No data</td></tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="row" items="${tagMonthlyStats}">
                                            <tr><td><strong>${row.monthLabel}</strong></td><td>${row.activeTagCount}</td><td>${row.questionCount}</td></tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Newest Users Table -->
                <div class="section-box">
                    <div class="section-header">
                        <div class="section-title">Newest Users</div>
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn-link">View All</a>
                    </div>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th>Created At</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty newestUsers}">
                                    <tr><td colspan="6" style="text-align:center;">No users found.</td></tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="user" items="${newestUsers}">
                                        <tr>
                                            <td>${user.userId}</td>
                                            <td><strong>${user.username}</strong></td>
                                            <td>${user.email}</td>
                                            <td><span style="text-transform: capitalize;">${user.role}</span></td>
                                            <td>
                                                <span class="status-badge ${user.status == 'active' ? 'status-active' : 'status-pending'}">
                                                    ${user.status}
                                                </span>
                                            </td>
                                            <td><fmt:formatDate value="${user.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

            </div>
        </main>
    </body>
</html>
