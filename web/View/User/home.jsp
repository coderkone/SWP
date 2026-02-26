<!DOCTYPE html>
<html lang="en">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>DevQuery - Newest Questions</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <style>
            /* Reset & Base Variables */
            :root {
                --orange: #F48024;
                --blue-link: #0074cc;
                --blue-btn: #0a95ff;
                --blue-tag-bg: #e1ecf4;
                --blue-tag-text: #39739d;
                --black-text: #0c0d0e;
                --gray-text: #525960;
                --gray-sub: #6a737c;
                --border-color: #d6d9dc;
                --bg-body: #ffffff;
                --font-stack: -apple-system, BlinkMacSystemFont, "Segoe UI", Arial, sans-serif;
            }

            * {
                box-sizing: border-box;
                margin: 0;
                padding: 0;
            }

            body {
                font-family: var(--font-stack);
                background-color: var(--bg-body);
                color: var(--black-text);
                font-size: 13px;
            }

            /* Header */
            header {
                width: 100%;
                height: 56px;
                background-color: #f8f9f9;
                border-top: 3px solid var(--orange);
                box-shadow: 0 1px 2px rgba(0,0,0,0.05);
                display: flex;
                align-items: center;
                padding: 0 20px;
                position: fixed;
                top: 0;
                z-index: 100;
                border-bottom: 1px solid var(--border-color);
            }

            .header-container {
                width: 100%;
                max-width: 1264px;
                margin: 0 auto;
                display: flex;
                align-items: center;
                justify-content: space-between;
            }

            .logo {
                font-size: 18px;
                display: flex;
                align-items: center;
                gap: 5px;
                margin-right: 20px;
                cursor: pointer;
            }

            .logo img {
                width: 28px;
                height: 28px;
            }

            .search-bar {
                flex-grow: 1;
                max-width: 700px;
                position: relative;
                margin: 0 10px;
            }

            .search-bar input {
                width: 100%;
                padding: 8px 10px 8px 32px;
                border: 1px solid #babfc4;
                border-radius: 3px;
                font-size: 13px;
                outline: none;
            }

            .search-bar input:focus {
                border-color: #6bbbf7;
                box-shadow: 0 0 0 4px rgba(0, 149, 255, 0.15);
            }

            .search-icon {
                position: absolute;
                left: 10px;
                top: 50%;
                transform: translateY(-50%);
                color: #838C95;
                font-size: 14px;
            }

            .user-nav {
                display: flex;
                align-items: center;
                gap: 15px;
                margin-left: 20px;
            }

            .user-profile {
                display: flex;
                align-items: center;
                gap: 8px;
                font-weight: bold;
                color: #2D3E50;
                cursor: pointer;
            }

            .user-profile img {
                width: 24px;
                height: 24px;
                border-radius: 4px;
            }

            .btn-logout {
                background-color: var(--blue-tag-bg);
                color: var(--blue-tag-text);
                border: 1px solid #7aa7c7;
                padding: 8px 12px;
                border-radius: 3px;
                cursor: pointer;
                font-weight: normal;
            }

            .btn-logout:hover {
                background-color: #b3d3ea;
            }

            /* Main Layout */
            .container {
                max-width: 1264px;
                margin: 56px auto 0; /* Offset header */
                display: flex;
                min-height: 100vh;
            }

            /* Left Sidebar */
            .left-sidebar {
                width: 164px;
                flex-shrink: 0;
                padding-top: 25px;
                border-right: 1px solid var(--border-color); /* Optional visual separator */
            }

            .nav-link {
                display: block;
                padding: 8px 8px 8px 8px; /* Indent for sub-items */
                color: var(--gray-text);
                text-decoration: none;
                position: relative;
            }

            .nav-link.main {
                padding-left: 8px;
                margin-bottom: 5px;
            }

            .nav-link:hover {
                color: var(--black-text);
            }

            .nav-link.active {
                background-color: #F1F2F3;
                color: var(--black-text);
                font-weight: bold;
                border-right: 3px solid var(--orange);
            }

            /* Main Content */
            .main-content {
                flex-grow: 1;
                padding: 24px;
                border-left: 1px solid var(--border-color);
            }

            .content-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 24px;
            }

            .page-title {
                font-size: 27px;
                font-weight: 400;
                color: var(--black-text);
            }

            .btn-primary {
                background-color: var(--blue-btn);
                color: white;
                border: none;
                padding: 10px 14px;
                border-radius: 3px;
                font-size: 13px;
                cursor: pointer;
                box-shadow: inset 0 1px 0 0 rgba(255,255,255,0.4);
            }

            .btn-primary:hover {
                background-color: #0074cc;
            }

            .filters-container {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 12px;
            }

            .total-questions {
                font-size: 17px;
                color: var(--black-text);
            }

            .filter-btn-group {
                display: flex;
                border: 1px solid #9fa6ad;
                border-radius: 3px;
            }

            .filter-item {
                padding: 10px 12px;
                background: #fff;
                border-right: 1px solid #9fa6ad;
                color: #6a737c;
                cursor: pointer;
            }

            .filter-item:last-child {
                border-right: none;
            }

            .filter-item.active {
                background-color: #e3e6e8;
                color: #3b4045;
                font-weight: 500;
            }

            .filter-item:hover:not(.active) {
                background-color: #f8f9f9;
            }

            .btn-filter-toggle {
                margin-left: 15px;
                background-color: var(--blue-tag-bg);
                color: var(--blue-tag-text);
                border: 1px solid #7aa7c7;
                padding: 8px 10px;
                border-radius: 3px;
                cursor: pointer;
            }

            /* Question List */
            .question-item {
                display: flex;
                padding: 16px;
                border-top: 1px solid var(--border-color);
            }

            .stats-container {
                width: 108px;
                margin-right: 16px;
                flex-shrink: 0;
                display: flex;
                flex-direction: column;
                align-items: flex-end;
                gap: 6px;
                font-size: 13px;
                color: var(--gray-sub);
            }

            .stat-box {
                display: flex;
                align-items: center;
                gap: 4px;
            }

            .stat-box.votes {
                color: var(--black-text);
                font-weight: 500;
            }

            .stat-box.status-answered {
                border: 1px solid #2f6f44;
                color: #2f6f44;
                padding: 4px 6px;
                border-radius: 3px;
            }

            .stat-box.status-accepted {
                background-color: #2e7d32;
                color: white;
                border: 1px solid #2e7d32;
                padding: 4px 6px;
                border-radius: 3px;
            }

            .question-summary {
                flex-grow: 1;
            }

            .question-title {
                font-size: 17px;
                color: var(--blue-link);
                text-decoration: none;
                display: block;
                margin-bottom: 5px;
                cursor: pointer;
            }

            .question-title:hover {
                color: #0a95ff;
            }

            .question-excerpt {
                color: #3b4045;
                margin-bottom: 8px;
                line-height: 1.4;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
            }

            .meta-container {
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 10px;
            }

            .tags {
                display: flex;
                gap: 6px;
            }

            .tag {
                background-color: var(--blue-tag-bg);
                color: var(--blue-tag-text);
                padding: 4px 6px;
                border-radius: 3px;
                font-size: 12px;
                text-decoration: none;
            }

            .tag:hover {
                background-color: #d0e3f1;
            }

            .user-card {
                font-size: 12px;
                color: var(--gray-sub);
                display: flex;
                align-items: center;
                gap: 5px;
            }

            .user-card a {
                color: var(--blue-link);
                text-decoration: none;
            }

            /* Right Sidebar */
            .right-sidebar {
                width: 300px;
                flex-shrink: 0;
                padding: 24px 0 0 24px;
            }

            .widget {
                border-radius: 3px;
                box-shadow: 0 1px 2px rgba(0,0,0,0.05);
                margin-bottom: 20px;
                border: 1px solid;
                font-size: 13px;
            }

            .widget-yellow {
                background-color: #fdf7e2;
                border-color: #f1e5bc;
            }

            .widget-gray {
                background-color: #F8F9F9;
                border-color: #d6d9dc;
            }

            .widget-header {
                padding: 12px 15px;
                font-weight: bold;
                color: var(--gray-text);
                border-bottom: 1px solid rgba(0,0,0,0.05);
                background-color: rgba(0,0,0,0.02);
            }

            .widget-content {
                padding: 0;
            }

            .widget-list {
                list-style: none;
            }

            .widget-list li {
                padding: 12px 15px;
                display: flex;
                gap: 10px;
                color: #3b4045;
            }

            .widget-list li:not(:last-child) {
                border-bottom: 1px solid rgba(0,0,0,0.05);
            }

            .widget-icon {
                font-size: 16px;
            }

            .popular-tags h3 {
                font-size: 19px;
                font-weight: 400;
                margin-bottom: 15px;
                color: var(--black-text);
            }

            /* Utility */
            .u-blue {
                color: var(--blue-link);
            }
        </style>
    </head>
    <body>

        <header>
            <div class="header-container">
                <div class="logo">
                    <div style="background-color: #F48024; padding: 5px; border-radius: 3px;">
                        <img src="https://cdn-icons-png.flaticon.com/512/2111/2111628.png" alt="icon" style="filter: brightness(0) invert(1); width: 16px; height: 16px;">
                    </div>
                    <span><b>Dev</b>Query</span>
                </div>

                <div class="search-bar">
                    <span class="search-icon">?</span>
                    <input type="text" placeholder="Search...">
                </div>

                <div class="user-nav">
                    <div class="user-profile">
                        <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="avatar">
                        <span>User</span>
                    </div>
<a class="btn-logout" href="<%=request.getContextPath()%>/logout">Log out</a>
                </div>
            </div>
        </header>

        <div class="container">
            <aside class="left-sidebar">
                <a href="${pageContext.request.contextPath}/home" class="nav-link main active">Home</a>
                <a href="#" class="nav-link">Questions</a>
                <a href="#" class="nav-link">Tags</a>
                <a href="#" class="nav-link">Users</a>
            </aside>

            <main class="main-content">
                <div class="content-header">
                    <h1 class="page-title">Newest Questions</h1>
                     <a href="<%=request.getContextPath()%>/ask" style="text-decoration: none;">
                        <button class="btn-primary">Ask Question</button>
                    </a>
                 </div>

                <div class="filters-container">
                    <div class="total-questions">
                        <c:choose>
                            <c:when test="${totalQuestions > 0}">
                                <fmt:formatNumber value="${totalQuestions}" type="number"/> questions
                            </c:when>
                            <c:otherwise>
                                0 questions
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div style="display: flex; align-items: center;">
                        <div class="filter-btn-group">
                            <%-- Giữ lại từ khóa tìm kiếm nếu có --%>
                            <c:choose>
                                <c:when test="${not empty currentKeyword}">
                                    <c:set var="baseUrl" value="${pageContext.request.contextPath}/SearchController?q=${currentKeyword}&" />
                                </c:when>
                                <c:otherwise>
                                    <c:set var="baseUrl" value="${pageContext.request.contextPath}/SearchController?" />
                                </c:otherwise>
                            </c:choose>
                            <a href="${baseUrl}tab=newest" 
                               class="filter-item ${ (empty currentSort || currentSort == 'newest') && currentFilter != 'unanswered' ? 'active' : '' }">
                                Newest
                            </a>
                            <a href="${baseUrl}tab=active" 
                               class="filter-item ${ currentSort == 'active' ? 'active' : '' }">
                                Active
                            </a>
                            <a href="${baseUrl}tab=voted" 
                               class="filter-item ${ currentSort == 'voted' ? 'active' : '' }">
                                Voted
                            </a>
                            <a href="${baseUrl}filter=unanswered" 
                               class="filter-item ${ currentFilter == 'unanswered' ? 'active' : '' }">
                                Unanswered
                            </a>
                        </div>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${not empty questions}">
                        <c:forEach var="question" items="${questions}">
                            <div class="question-item">
                                <div class="stats-container">
                                    <div class="stat-box votes">${question.score} votes</div>
                                    <div class="stat-box ${question.acceptedAnswerId != null ? 'status-accepted' : (question.answerCount > 0 ? 'status-answered' : '')}">
                                        <c:if test="${question.acceptedAnswerId != null}"><i class="fa-solid fa-check" style="font-size: 11px;"></i> </c:if>${question.answerCount} ${question.answerCount == 1 ? 'answer' : 'answers'}
                                    </div>
                                    <div class="stat-box">${question.viewCount} views</div>
                                </div>
                                <div class="question-summary">
                                    <a href="${pageContext.request.contextPath}/question/detail?id=${question.questionId}" class="question-title">
                                        ${question.title}
                                    </a>
                                    <p class="question-excerpt">
                                        ${fn:substring(question.body, 0, 150)}${fn:length(question.body) > 150 ? '...' : ''}
                                    </p>
                                    <div class="meta-container">
                                        <div class="tags">
                                            <c:forEach var="tag" items="${question.tags}" varStatus="status">
                                                <c:if test="${status.index < 3}">
                                                    <a href="${pageContext.request.contextPath}/tag/${tag}" class="tag">${tag}</a>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                        <div class="user-card">
                                            asked <fmt:formatDate value="${question.createdAt}" pattern="MMM d 'at' HH:mm"/> by 
                                            <a href="${pageContext.request.contextPath}/user/${question.authorName}">${question.authorName}</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state-container text-center pb-5">
                            <div style="border-top: 1px solid #d6d9dc; width: 100%; margin-bottom: 40px;"></div>
                            <img src="${pageContext.request.contextPath}/assets/img/KinhLup.png" 
                                 alt="No results" 
                                 style="width: 120px; margin-bottom: 20px; opacity: 0.6;">
                            <h5 class="fw-bold mb-2" style="color: #232629;">We couldn't find anything matching your search</h5>
                            <p class="text-secondary mb-3" style="font-size: 15px; max-width: 400px; margin: 0 auto;">
                                Try different keywords or less specific search terms.
                            </p>
                            <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-primary btn-sm mt-2">
                                Clear search & Return home
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>

                <c:if test="${totalPage > 1}">
                    <div class="pagination">
                        <c:forEach begin="1" end="${totalPage}" var="i">
                            <a href="${pageContext.request.contextPath}/home?page=${i}&tab=${currentSort}&q=${currentKeyword}" 
                               class="${currentPage == i ? 'active' : ''}">${i}</a>
                        </c:forEach>
                    </div>
                </c:if>
                <c:if test="${totalPage <= 1}">
                    <div style="padding: 40px; text-align: center; color: #6a737c;">
                        <p style="font-size: 16px;">No questions found.</p>
                        <a href="${pageContext.request.contextPath}/ask" style="color: #0a95ff; text-decoration: none;">Ask the first question</a>
                    </div>
                </c:if>

            </main>

            <aside class="right-sidebar">
                <div class="widget widget-yellow">
                    <div class="widget-header">The Dev Blog</div>
                    <div class="widget-content">
                        <ul class="widget-list">
                            <li>
                                <span class="widget-icon">??</span>
                                <span>How Stack Overflow is taking on spam and bad actors</span>
                            </li>
                            <li>
                                <span class="widget-icon">??</span>
                                <span>How AWS re-invented the cloud</span>
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="widget widget-gray">
                    <div class="widget-header">Community Activity</div>
                    <div class="widget-content">
                        <ul class="widget-list" style="color: #6a737c; text-align: center; padding: 20px;">
                            <li>(Placeholder for community stats)</li>
                        </ul>
                    </div>
                </div>

                <div class="popular-tags">
                    <h3>Popular tags</h3>
                    <div class="tags" style="flex-wrap: wrap;">
                        <a href="#" class="tag">javascript</a>
                        <a href="#" class="tag">python</a>
                        <a href="#" class="tag">java</a>
                        <a href="#" class="tag">c#</a>
                        <a href="#" class="tag">php</a>
                        <a href="#" class="tag">android</a>
                    </div>
                </div>
            </aside>

        </div>

        <!-- Footer -->
        <%@ include file="../Common/footer.jsp" %>

    </body>
</html>
