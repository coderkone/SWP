<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Users - DevQuery</title>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.0/chart.umd.min.js"></script>
    <style>
        :root { --border-color: #d6d9dc; --blue: #0074cc; }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Arial, sans-serif;
            font-size: 13px; background: #fff; color: #0c0d0e;
        }

        /* Layout */
        .container {
            max-width: 1264px; margin: 56px auto 0;
            display: flex; align-items: flex-start;
        }
        .left-sidebar {
            width: 164px; flex-shrink: 0;
            padding-top: 25px; border-right: 1px solid var(--border-color);
        }
        .main-content {
            flex-grow: 1; padding: 24px;
            border-left: 1px solid var(--border-color); min-width: 0;
        }

        /* Page header */
        .page-header { margin-bottom: 16px; }
        .page-header h1 { font-size: 27px; font-weight: 400; }

        /* Search */
        .search-box {
            position: relative; width: 300px; margin-bottom: 20px;
        }
        .search-box i {
            position: absolute; left: 10px; top: 50%;
            transform: translateY(-50%); color: #6a737c; font-size: 13px;
        }
        .search-box input {
            width: 100%; padding: 8px 12px 8px 32px;
            border: 1px solid #babfc4; border-radius: 4px;
            font-size: 13px; outline: none;
        }
        .search-box input:focus {
            border-color: #6bbbf7;
            box-shadow: 0 0 0 4px rgba(0,149,255,0.15);
        }

        /* Chart section */
        .chart-section {
            background: #f8f9f9;
            border: 1px solid var(--border-color);
            border-radius: 6px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .chart-section h2 {
            font-size: 15px; font-weight: 600;
            color: #3b4045; margin-bottom: 16px;
        }
        .chart-wrapper {
            max-width: 700px; 
            height: 300px;
        }

        /* Filter + sort row */
        .filter-row {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            margin-bottom: 16px;
        }
        .sort-buttons {
            display: flex; border: 1px solid #9fa6ad;
            border-radius: 3px; overflow: hidden;
        }
        .sort-buttons a {
            padding: 7px 12px; font-size: 13px; color: #6a737c;
            text-decoration: none; border-right: 1px solid #9fa6ad;
            background: #fff; transition: background 0.1s;
        }
        .sort-buttons a:last-child { border-right: none; }
        .sort-buttons a:hover { background: #f8f9f9; }
        .sort-buttons a.active {
            background: #e3e6e8; font-weight: 500; color: #3b4045;
        }

        /* Users grid */
        .users-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 12px;
        }
        @media (max-width: 1100px) { .users-grid { grid-template-columns: repeat(3, 1fr); } }
        @media (max-width: 800px)  { .users-grid { grid-template-columns: repeat(2, 1fr); } }
        @media (max-width: 500px)  { .users-grid { grid-template-columns: 1fr; } }

        /* User card */
        .user-card {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            padding: 12px;
            border: 1px solid #e3e6e8;
            border-radius: 4px;
            text-decoration: none;
            color: inherit;
            transition: box-shadow 0.15s;
        }
        .user-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.08); }

        .user-avatar {
            width: 48px; height: 48px;
            border-radius: 4px; object-fit: cover;
            flex-shrink: 0;
        }
        .user-avatar-default {
            width: 48px; height: 48px;
            border-radius: 4px; background: #e1ecf4;
            display: flex; align-items: center; justify-content: center;
            font-size: 20px; color: #39739d; flex-shrink: 0;
        }

        .user-info { min-width: 0; }
        .user-name {
            font-size: 13px; font-weight: 500;
            color: var(--blue); margin-bottom: 4px;
            white-space: nowrap; overflow: hidden;
            text-overflow: ellipsis;
        }
        .user-rep {
            font-size: 13px; font-weight: 700;
            color: #3b4045; margin-bottom: 2px;
        }
        .user-date {
            font-size: 11px; color: #9fa6ad;
        }

        /* Empty state */
        .empty-state {
            grid-column: 1 / -1; text-align: center;
            padding: 40px; color: #6a737c;
        }
        .empty-state i { font-size: 40px; margin-bottom: 12px; display: block; }
    </style>
</head>
<body>

    <jsp:include page="/View/Common/header.jsp"/>

    <div class="container">
        <div class="left-sidebar">
            <jsp:include page="/View/Common/sidebar.jsp">
                <jsp:param name="page" value="users"/>
            </jsp:include>
        </div>

        <main class="main-content">

            <%-- Page header --%>
            <div class="page-header">
                <h1>Users</h1>
            </div>

            <%-- Search — trên cùng --%>
            <form method="get" action="${pageContext.request.contextPath}/users">
                <div class="search-box">
                    <i class="fa-solid fa-magnifying-glass"></i>
                    <input type="text"
                           id="searchInput"
                           name="search"
                           placeholder="Filter by username"
                           value="${keyword}" />
                    <%-- Giữ sort khi search --%>
                    <input type="hidden" name="sort" value="${sort}" />
                </div>
            </form>

            <%-- Biểu đồ Top 3 --%>
            <div class="chart-section">
                <h2>🏆 Top 10 Reputation</h2>
                <div class="chart-wrapper">
                    <canvas id="topChart"></canvas>
                </div>
            </div>

            <%-- Filter — bên phải --%>
            <div class="filter-row">
                <div class="sort-buttons">
                    <a href="${pageContext.request.contextPath}/users?sort=name&search=${keyword}"
                       class="${sort == 'name' || empty sort ? 'active' : ''}">Name</a>
                    <a href="${pageContext.request.contextPath}/users?sort=date&search=${keyword}"
                       class="${sort == 'date' ? 'active' : ''}">Date</a>
                    <a href="${pageContext.request.contextPath}/users?sort=reputation&search=${keyword}"
                       class="${sort == 'reputation' ? 'active' : ''}">Reputation</a>
                </div>
            </div>

            <%-- Users Grid --%>
            <div class="users-grid">
                <c:choose>
                    <c:when test="${not empty users}">
                        <c:forEach var="u" items="${users}">
                            <a href="${pageContext.request.contextPath}/profile?id=${u.userId}"
                               class="user-card">

                                <%-- Avatar --%>
                                <c:choose>
                                    <c:when test="${not empty u.avatarUrl}">
                                        <img src="${u.avatarUrl}"
                                             alt="${u.username}"
                                             class="user-avatar"
                                             onerror="this.style.display='none'" />
                                    </c:when>
                                    <c:otherwise>
                                        <div class="user-avatar-default">
                                            <i class="fa-solid fa-user"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <%-- Info --%>
                                <div class="user-info">
                                    <div class="user-name">${u.username}</div>
                                    <div class="user-rep">${u.reputation}</div>
                                    <div class="user-date">
                                        <c:if test="${not empty u.createdAt}">
                                            member since ${u.createdAt.toString().substring(0, 10)}
                                        </c:if>
                                    </div>
                                </div>

                            </a>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <i class="fa-solid fa-users"></i>
                            <p>No users found.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </main>
    </div>

    <%-- Chart.js render Top 3 --%>
    <script>
        const ctx = document.getElementById('topChart').getContext('2d');

        // ✅ Data từ Controller qua JSP
        const labels = [
            <c:forEach var="u" items="${top10}" varStatus="s">
                '${u.username}'<c:if test="${!s.last}">,</c:if>
            </c:forEach>
        ];
        const data = [
            <c:forEach var="u" items="${top10}" varStatus="s">
                ${u.reputation}<c:if test="${!s.last}">,</c:if>
            </c:forEach>
        ];

        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Reputation',
                    data: data,
                    backgroundColor: [
                        '#FFD700',  // Top 1 - Vàng
                        '#C0C0C0',  // Top 2 - Bạc
                        '#CD7F32',  // Top 3 - Đồng
                        '#5b9bd5',  // Top 4
                        '#5b9bd5',  // Top 5
                        '#5b9bd5',  // Top 6
                        '#5b9bd5',  // Top 7
                        '#5b9bd5',  // Top 8
                        '#5b9bd5',  // Top 9
                        '#5b9bd5'   // Top 10
                    ],
                    borderColor: [
                        '#FFC000',
                        '#A0A0A0',
                        '#B06020',
                        '#4a8ac4',
                        '#4a8ac4',
                        '#4a8ac4',
                        '#4a8ac4',
                        '#4a8ac4',
                        '#4a8ac4',
                        '#4a8ac4'
                    ],
                    borderWidth: 1,
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: ctx => ctx.parsed.y + ' rep'
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: { font: { size: 11 } }
                    },
                    x: {
                        ticks: { font: { size: 12, weight: '600' } }
                    }
                }
            }
        });

        let timer;
const searchInput = document.getElementById('searchInput');
if (searchInput) {
    searchInput.addEventListener('input', function () {
        clearTimeout(timer);
        timer = setTimeout(() => { this.form.submit(); }, 500);
    });

    //  Tự động focus + đặt con trỏ cuối chữ sau khi reload
    window.addEventListener('load', function () {
        const val = searchInput.value;
        if (val && val.trim() !== '') {
            searchInput.focus();
            searchInput.setSelectionRange(val.length, val.length);
        }
    });
}
    </script>

</body>
</html>
