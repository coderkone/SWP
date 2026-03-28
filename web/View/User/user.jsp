<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Users - DevQuery</title>
        <link rel="stylesheet"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <style>
            :root {
                --border-color: #d6d9dc;
                --blue: #0074cc;
            }
            * {
                box-sizing: border-box;
                margin: 0;
                padding: 0;
            }
            body {
                font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Arial, sans-serif;
                font-size: 13px;
                background: #fff;
                color: #0c0d0e;
            }
            .container {
                max-width: 1264px;
                margin: 56px auto 0;
                display: flex;
                align-items: flex-start;
            }
            .left-sidebar {
                width: 164px;
                flex-shrink: 0;
                padding-top: 0;
                border-right: 1px solid var(--border-color);
            }
            .main-content {
                flex-grow: 1;
                padding: 24px;
                border-left: 1px solid var(--border-color);
                min-width: 0;
            }
            .page-header {
                margin-bottom: 16px;
            }
            .page-header h1 {
                font-size: 27px;
                font-weight: 400;
            }
            .search-box {
                position: relative;
                width: 300px;
                margin-bottom: 20px;
            }
            .search-box i {
                position: absolute;
                left: 10px;
                top: 50%;
                transform: translateY(-50%);
                color: #6a737c;
                font-size: 13px;
            }
            .search-box input {
                width: 100%;
                padding: 8px 12px 8px 32px;
                border: 1px solid #babfc4;
                border-radius: 4px;
                font-size: 13px;
                outline: none;
            }
            .search-box input:focus {
                border-color: #6bbbf7;
                box-shadow: 0 0 0 4px rgba(0,149,255,0.15);
            }
            .filter-row {
                display: flex;
                justify-content: flex-end;
                align-items: center;
                margin-bottom: 16px;
            }
            .sort-buttons {
                display: flex;
                border: 1px solid #9fa6ad;
                border-radius: 3px;
                overflow: hidden;
            }
            .sort-buttons a {
                padding: 7px 12px;
                font-size: 13px;
                color: #6a737c;
                text-decoration: none;
                border-right: 1px solid #9fa6ad;
                background: #fff;
                transition: background 0.1s;
            }
            .sort-buttons a:last-child {
                border-right: none;
            }
            .sort-buttons a:hover {
                background: #f8f9f9;
            }
            .sort-buttons a.active {
                background: #e3e6e8;
                font-weight: 500;
                color: #3b4045;
            }
            .users-grid {
                display: grid;
                grid-template-columns: repeat(4, 1fr);
                gap: 12px;
            }
            @media (max-width: 1100px) {
                .users-grid {
                    grid-template-columns: repeat(3, 1fr);
                }
            }
            @media (max-width: 800px)  {
                .users-grid {
                    grid-template-columns: repeat(2, 1fr);
                }
            }
            @media (max-width: 500px)  {
                .users-grid {
                    grid-template-columns: 1fr;
                }
            }
            .user-card {
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 12px;
                border: 1px solid #e3e6e8;
                border-radius: 4px;
                background: #fff;
                transition: box-shadow 0.15s;
            }
            .user-card:hover {
                box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            }
            .user-avatar {
                width: 48px;
                height: 48px;
                border-radius: 4px;
                object-fit: cover;
                flex-shrink: 0;
            }
            .user-avatar-default {
                width: 48px;
                height: 48px;
                border-radius: 4px;
                background: #e1ecf4;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 20px;
                color: #39739d;
                flex-shrink: 0;
            }
            .user-info {

                min-width: 0;
            }
            .user-name {
                font-size: 13px;
                font-weight: 500;
                color: var(--blue);
                margin-bottom: 4px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
            .user-rep {
                font-size: 13px;
                font-weight: 700;
                color: #3b4045;
                margin-bottom: 2px;
            }
            .user-date {
                font-size: 11px;
                color: #9fa6ad;
            }

            /* Empty state */
            .empty-state {
                grid-column: 1 / -1;
                text-align: center;
                padding: 40px;
                color: #6a737c;
            }
            .empty-state i {
                font-size: 40px;
                margin-bottom: 12px;
                display: block;
            }
            .user-card-link {
                display: flex;
                align-items: flex-start;
                gap: 12px;
                text-decoration: none;
                color: inherit;
                flex: 1;
                min-width: 0;
            }
            .btn-more {
                font-size: 12px;
                color: #6a737c;
                text-decoration: none;
                padding: 4px 8px;
                border: 1px solid #d6d9dc;
                border-radius: 3px;
                white-space: nowrap;
                flex-shrink: 0;
                align-self: flex-start;
            }
            .btn-more:hover {
                background: #f8f9f9;
                color: #3b4045;
            }
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
                <div class="page-header">
                    <h1>Users</h1>
                </div>
                <form method="get" action="${pageContext.request.contextPath}/users">
                    <div class="search-box">
                        <i class="fa-solid fa-magnifying-glass"></i>
                        <input type="text"
                               id="searchInput"
                               name="search"
                               placeholder="Filter by username"
                               value="${keyword}" />
                        <input type="hidden" name="sort" value="${sort}" />
                    </div>
                </form>
                <div class="filter-row">
                    <div class="sort-buttons">
                        <a href="${pageContext.request.contextPath}/users?sort=reputation&search=${keyword}"
                           class="${sort == 'reputation'|| empty sort ? 'active' : ''}">Reputation</a>
                        <a href="${pageContext.request.contextPath}/users?sort=date&search=${keyword}"
                           class="${sort == 'date' ? 'active' : ''}">Date</a>
                        <a href="${pageContext.request.contextPath}/users?sort=name&search=${keyword}"
                           class="${sort == 'name' ? 'active' : ''}">Name</a>
                    </div>
                </div>
                <div class="users-grid">
                    <c:choose>
                        <c:when test="${not empty users}">
                            <c:forEach var="u" items="${users}">

                                <div class="user-card">

                                    
                                    <a href="${pageContext.request.contextPath}/profile?id=${u.userId}"
                                       class="user-card-link">

                                        <c:choose>
                                            <c:when test="${not empty u.avatarUrl}">
                                                <img src="${u.avatarUrl}" alt="${u.username}"
                                                     class="user-avatar"
                                                     onerror="this.style.display='none'" />
                                            </c:when>
                                            <c:otherwise>
                                                <div class="user-avatar-default">
                                                    <i class="fa-solid fa-user"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>

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

                                    
                                    <a href="${pageContext.request.contextPath}/userprofile?id=${u.userId}"
                                       class="btn-more">More</a>

                                </div>

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

        
        <script>

            let timer;
            const searchInput = document.getElementById('searchInput');
            if (searchInput) {
                searchInput.addEventListener('input', function () {
                    clearTimeout(timer);
                    timer = setTimeout(() => {
                        this.form.submit();
                    }, 500);
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
        <jsp:include page="../Common/footer.jsp" />
    </body>
</html>