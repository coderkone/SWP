<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>DevQuery Admin Dashboard</title>
        <!-- Chart.js -->
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
                        <div class="chart-area" style="height: 250px; padding-top: 10px;">
                            <canvas id="growthChart"></canvas>
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
                                <c:choose>
                                    <c:when test="${empty recentReports}">
                                        <tr><td colspan="3" style="text-align:center;">No reports found.</td></tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="r" items="${recentReports}">
                                            <tr>
                                                <td style="text-transform: capitalize;">${r.targetType} #${r.targetId}</td>
                                                <td>${r.reason}</td>
                                                <td>
                                                    <span class="status-badge ${r.status == 'open' ? 'status-pending' : 'status-active'}">
                                                        ${r.status}
                                                    </span>
                                                </td>
                                            </tr>
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
        <script>
            // Data from Java
            const userLabels = [];
            const userCounts = [];
            <c:forEach var="item" items="${userTrend}">
                userLabels.push('<fmt:formatDate value="${item.date}" pattern="dd/MM"/>');
                userCounts.push(${item.count});
            </c:forEach>

            const questionLabels = [];
            const questionCounts = [];
            <c:forEach var="item" items="${questionTrend}">
questionLabels.push('<fmt:formatDate value="${item.date}" pattern="dd/MM"/>');
                questionCounts.push(${item.count});
            </c:forEach>

            // Ensure all dates are present in both if needed, but for simplicity we'll just use questonLabels
            const labels = questionLabels.length > userLabels.length ? questionLabels : userLabels;

            const ctx = document.getElementById('growthChart').getContext('2d');
            const growthChart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: 'New Questions',
                            data: questionCounts,
                            borderColor: '#0A95FF',
                            backgroundColor: 'rgba(10, 149, 255, 0.1)',
                            fill: true,
                            tension: 0.4,
                            borderWidth: 3
                        },
                        {
                            label: 'New Users',
                            data: userCounts,
                            borderColor: '#F48024',
                            backgroundColor: 'rgba(244, 128, 36, 0.1)',
                            fill: true,
                            tension: 0.4,
                            borderWidth: 3
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'bottom',
                            labels: {
                                usePointStyle: true,
                                padding: 20
                            }
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            grid: {
                                color: '#f0f0f0'
                            }
                        },
                        x: {
                            grid: {
                                display: false
                            }
                        }
                    }
                }
            });
        </script>
    </body>
</html>