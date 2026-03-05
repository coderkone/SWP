<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
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

            /* Main Layout */
            .container {
                max-width: 1264px;
                margin: 56px auto 0;
                display: flex;
                align-items: flex-start;
            }

            /* Left Sidebar */
            .left-sidebar {
                width: 164px;
                flex-shrink: 0;
                padding-top: 25px;
                border-right: 1px solid var(--border-color);
            }

            /* Main Content */
            .main-content {
                flex-grow: 1;
                padding: 24px;
                border-left: 1px solid var(--border-color);
                width: 100%;
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
                text-decoration: none;
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
                text-decoration: none;
                font-size: 13px;
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
                color: #525960;
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

            .stat-box.status-none {
                color: #6a737c;
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

            /* CSS Phân trang */
            .pagination {
                display: flex;
                list-style: none;
                margin-top: 30px;
                gap: 5px;
            }
            .pagination a {
                padding: 5px 10px;
                border: 1px solid #d6d9dc;
                border-radius: 3px;
                text-decoration: none;
                color: var(--black-text);
            }
            .pagination a.active {
                background-color: var(--orange);
                color: white;
                border-color: var(--orange);
            }
        </style>
    </head>
    <body>
        <jsp:include page="../Common/header.jsp" />

        <div class="container">

            <div class="left-sidebar">
                <jsp:include page="../Common/sidebar.jsp">
                    <jsp:param name="page" value="bookmarks"/>
                </jsp:include>
            </div>

            <main class="main-content">
                <div class="content-header">
                    <h1 class="page-title">
                        <c:if test="${currentKeyword != null}">Results for "${currentKeyword}"</c:if>
                        <c:if test="${currentKeyword == null}">Top Questions</c:if>
                        </h1>
                        <a href="${pageContext.request.contextPath}/create" class="btn-primary">Ask Question</a>
                </div>

                <div class="filters-container">
                    <div class="total-questions">${questions.size()} results</div>

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

                <c:forEach items="${questions}" var="q">
                    <div class="question-item">
                        <div class="stats-container">
                            <div class="stat-box votes">${q.score} votes</div>
                            <div class="stat-box ${q.answerCount > 0 ? 'status-answered' : 'status-none'}">
                                ${q.answerCount} answers
                            </div>
                            <div class="stat-box">${q.viewCount} views</div>
                        </div>

                        <div class="question-summary">
                            <a href="${pageContext.request.contextPath}/question?id=${q.questionId}" class="question-title">${q.title}</a>
                            <p class="question-excerpt">
                                <c:choose>
                                    <c:when test="${q.body != null && q.body.length() > 200}">
                                        ${q.body.substring(0, 200)}...
                                    </c:when>
                                    <c:otherwise>${q.body}</c:otherwise>
                                </c:choose>
                            </p>
                            <div class="meta-container">
                                <div class="tags">
                                    <c:if test="${not empty q.tags}">
                                        <c:forEach items="${q.tags}" var="t">
                                            <a href="${pageContext.request.contextPath}/home?q=[${t}]" class="tag">${t}</a>
                                        </c:forEach>
                                    </c:if>
                                    <c:if test="${empty q.tags}">
                                        <a href="#" class="tag">java</a>
                                        <a href="#" class="tag">web</a>
                                    </c:if>
                                </div>
                                <div class="user-card">
                                    <a href="${pageContext.request.contextPath}/profile?id=${q.userId}">
                                        <img src="${(q.authorAvatar != null && not empty q.authorAvatar) ? q.authorAvatar : 'https://cdn-icons-png.flaticon.com/512/3135/3135715.png'}" 
                                             width="16" height="16" style="border-radius: 3px; vertical-align: middle;">
                                        ${q.authorName}
                                    </a>
                                    <span style="margin-left: 5px;">asked <fmt:formatDate value="${q.createdAt}" pattern="MMM dd, yyyy"/></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>

                <c:if test="${empty questions}">
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
                </c:if>
                <c:if test="${totalPage > 1}">
                    <div class="pagination">
                        <c:forEach begin="1" end="${totalPage}" var="i">
                            <a href="${pageContext.request.contextPath}/home?page=${i}&tab=${currentSort}&q=${currentKeyword}" 
                               class="${currentPage == i ? 'active' : ''}">${i}</a>
                        </c:forEach>
                    </div>
                </c:if>
            </main>

            <aside class="right-sidebar">
                <div class="widget widget-yellow">
                    <div class="widget-header">The Dev Blog</div>
                    <div class="widget-content">
                        <ul class="widget-list">
                            <li><span class="widget-icon">📝</span><span>How Stack Overflow is taking on spam</span></li>
                            <li><span class="widget-icon">🎧</span><span>How AWS re-invented the cloud</span></li>
                        </ul>
                    </div>
                </div>
                <div class="popular-tags">
                    <h3>Popular tags</h3>
                    <div class="tags" style="flex-wrap: wrap;">
                        <a href="#" class="tag">javascript</a>
                        <a href="#" class="tag">python</a>
                        <a href="#" class="tag">java</a>
                    </div>
                </div>
            </aside>
        </div>
    </body>
</html>