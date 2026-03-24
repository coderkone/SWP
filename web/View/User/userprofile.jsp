<%-- 
    Document   : userprofile
    Created on : Mar 24, 2026, 9:10:39 PM
    Author     : Asus
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${profileUser.username} - DevQuery</title>
        <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
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
            flex-grow: 1; min-width: 0;
        }

        /* User Header */
        .user-header {
            display: flex; justify-content: space-between;
            align-items: center; padding: 24px;
            border-left: 1px solid var(--border-color);
            border-bottom: 1px solid var(--border-color);
        }
        .user-header-left {
            display: flex; gap: 16px; align-items: center;
        }
        .profile-avatar {
            width: 64px; height: 64px; border-radius: 6px;
            object-fit: cover;
        }
        .profile-avatar-default {
            width: 64px; height: 64px; border-radius: 6px;
            background: #e1ecf4; display: flex;
            align-items: center; justify-content: center;
            font-size: 28px; color: #39739d;
        }
        .profile-info h2 {
            font-size: 20px; font-weight: 500; margin-bottom: 4px;
        }
        .profile-info .meta {
            font-size: 12px; color: #6a737c; margin-bottom: 2px;
        }
        .profile-info .rep {
            font-size: 13px; font-weight: 700; color: #3b4045;
        }

        /* Follow button */
        .btn-follow {
            padding: 8px 16px; border-radius: 4px; font-size: 13px;
            font-weight: 500; cursor: pointer; border: 1px solid;
            display: inline-flex; align-items: center; gap: 6px;
            text-decoration: none;
        }
        .btn-follow.not-following {
            background: var(--blue); color: #fff; border-color: var(--blue);
        }
        .btn-follow.not-following:hover { background: #005999; }
        .btn-follow.following {
            background: #fff; color: #6a737c; border-color: #9fa6ad;
        }
        .btn-follow.following:hover { background: #f8f9f9; }

        /* Body layout */
        .body-layout {
            display: flex; align-items: flex-start;
            border-left: 1px solid var(--border-color);
        }

        /* Following list */
        .following-panel {
            width: 180px; flex-shrink: 0;
            border-right: 1px solid var(--border-color);
            padding: 16px 12px;
            max-height: calc(100vh - 160px);
            overflow-y: auto;
        }
        .following-panel h3 {
            font-size: 12px; font-weight: 700;
            text-transform: uppercase; color: #6a737c;
            margin-bottom: 12px; letter-spacing: 0.5px;
        }
        .following-item {
            display: flex; align-items: center; gap: 8px;
            padding: 6px 4px; text-decoration: none; color: inherit;
            border-radius: 4px; margin-bottom: 4px;
        }
        .following-item:hover { background: #f8f9f9; }
        .following-item img,
        .following-avatar-default {
            width: 28px; height: 28px; border-radius: 3px;
            object-fit: cover; flex-shrink: 0;
        }
        .following-avatar-default {
            background: #e1ecf4; display: flex;
            align-items: center; justify-content: center;
            font-size: 12px; color: #39739d;
        }
        .following-name {
            font-size: 12px; color: var(--blue);
            white-space: nowrap; overflow: hidden;
            text-overflow: ellipsis;
        }
        .following-empty {
            font-size: 12px; color: #9fa6ad;
            text-align: center; padding: 12px 0;
        }

        /* Questions panel */
        .questions-panel {
            flex: 1; padding: 16px 24px; min-width: 0;
        }

        /* Filter */
        .filter-row {
            display: flex; justify-content: space-between;
            align-items: center; margin-bottom: 12px;
        }
        .questions-count { font-size: 14px; color: #3b4045; }
        .questions-count strong { font-weight: 600; }

        .filter-buttons {
            display: flex; border: 1px solid #9fa6ad;
            border-radius: 3px; overflow: hidden;
        }
        .filter-buttons a {
            padding: 6px 12px; font-size: 12px; color: #6a737c;
            text-decoration: none; border-right: 1px solid #9fa6ad;
            background: #fff; transition: background 0.1s;
        }
        .filter-buttons a:last-child { border-right: none; }
        .filter-buttons a:hover { background: #f8f9f9; }
        .filter-buttons a.active {
            background: #e3e6e8; font-weight: 500; color: #3b4045;
        }

        /* Question item */
        .question-item {
            padding: 12px 0;
            border-bottom: 1px solid var(--border-color);
        }
        .q-stats {
            display: flex; gap: 12px;
            margin-bottom: 6px; font-size: 12px; color: #6a737c;
        }
        .q-stat strong { color: #3b4045; font-weight: 600; }
        .q-stat.has-answer strong { color: #5eba7d; }
        .q-stat.negative strong { color: #d1383d; }

        .q-title {
            font-size: 14px; color: var(--blue);
            text-decoration: none; display: block; margin-bottom: 4px;
            line-height: 1.4;
        }
        .q-title:hover { color: #005999; }

        .q-excerpt {
            font-size: 12px; color: #3b4045; line-height: 1.5;
            display: -webkit-box; -webkit-line-clamp: 2;
            -webkit-box-orient: vertical; overflow: hidden;
        }

        .closed-badge {
            font-size: 11px; background: #f1f2f3; color: #6a737c;
            border: 1px solid #d6d9dc; border-radius: 3px;
            padding: 1px 5px; margin-left: 6px;
        }

        /* Empty */
        .empty-questions {
            padding: 40px 0; text-align: center; color: #6a737c;
        }
        .empty-questions i { font-size: 36px; margin-bottom: 10px; display: block; }

        /* Pagination */
        .pagination {
            display: flex; gap: 4px; margin-top: 16px; flex-wrap: wrap;
        }
        .pagination a, .pagination span {
            padding: 5px 9px; border: 1px solid #d6d9dc;
            border-radius: 3px; font-size: 12px;
            text-decoration: none; color: var(--blue); background: #fff;
        }
        .pagination a:hover { background: #f8f9f9; }
        .pagination span.current {
            background: #f48024; color: #fff;
            border-color: #f48024; font-weight: 600;
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

            <%-- User Header --%>
            <div class="user-header">
                <div class="user-header-left">

                    <%-- Avatar --%>
                    <c:choose>
                        <c:when test="${not empty profileUser.avatarUrl}">
                            <img src="${profileUser.avatarUrl}"
                                 alt="${profileUser.username}"
                                 class="profile-avatar"
                                 onerror="this.style.display='none'" />
                        </c:when>
                        <c:otherwise>
                            <div class="profile-avatar-default">
                                <i class="fa-solid fa-user"></i>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <%-- Info --%>
                    <div class="profile-info">
                        <h2>${profileUser.username}</h2>
                        <div class="rep">${profileUser.reputation} reputation</div>
                        <div class="meta">
                            <c:if test="${not empty profileUser.createdAt}">
                                member since ${profileUser.createdAt.toString().substring(0, 10)}
                            </c:if>
                        </div>
                    </div>
                </div>

                <%-- Follow / Unfollow button --%>
                <c:choose>
                    <c:when test="${not isLoggedIn}">
                        <a href="${pageContext.request.contextPath}/auth/login"
                           class="btn-follow not-following">
                            <i class="fa-solid fa-user-plus"></i> Follow
                        </a>
                    </c:when>
                    <c:when test="${isFollowing}">
                        <form method="post"
                              action="${pageContext.request.contextPath}/user-follow"
                              style="margin:0;">
                            <input type="hidden" name="targetId" value="${profileUser.userId}" />
                            <input type="hidden" name="action"   value="unfollow" />
                            <input type="hidden" name="filter"   value="${filter}" />
                            <input type="hidden" name="page"     value="${currentPage}" />
                            <button type="submit" class="btn-follow following">
                                <i class="fa-solid fa-user-minus"></i> Unfollow
                            </button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <form method="post"
                              action="${pageContext.request.contextPath}/user-follow"
                              style="margin:0;">
                            <input type="hidden" name="targetId" value="${profileUser.userId}" />
                            <input type="hidden" name="action"   value="follow" />
                            <input type="hidden" name="filter"   value="${filter}" />
                            <input type="hidden" name="page"     value="${currentPage}" />
                            <button type="submit" class="btn-follow not-following">
                                <i class="fa-solid fa-user-plus"></i> Follow
                            </button>
                        </form>
                    </c:otherwise>
                </c:choose>

            </div>

            <%-- Body --%>
            <div class="body-layout">

                <%-- Following List — bên trái --%>
                <div class="following-panel">
                    <h3>Following</h3>
                    <c:choose>
                        <c:when test="${not empty followingList}">
                            <c:forEach var="fu" items="${followingList}">
                                <a href="${pageContext.request.contextPath}/userprofile?id=${fu.userId}"
                                   class="following-item">
                                    <c:choose>
                                        <c:when test="${not empty fu.avatarUrl}">
                                            <img src="${fu.avatarUrl}" alt="${fu.username}" />
                                        </c:when>
                                        <c:otherwise>
                                            <div class="following-avatar-default">
                                                <i class="fa-solid fa-user"></i>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="following-name">${fu.username}</span>
                                </a>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p class="following-empty">
                                <c:choose>
                                    <c:when test="${isLoggedIn}">Not following anyone yet.</c:when>
                                    <c:otherwise>Login to see your following list.</c:otherwise>
                                </c:choose>
                            </p>
                        </c:otherwise>
                    </c:choose>
                </div>

                <%-- Questions Panel — bên phải --%>
                <div class="questions-panel">

                    <div class="filter-row">
                        <p class="questions-count">
                            <strong>${totalQuestions}</strong> questions
                        </p>
                        <div class="filter-buttons">
                            <a href="?id=${profileUser.userId}&filter=popular"
                               class="${filter == 'popular' ? 'active' : ''}">Popular</a>
                            <a href="?id=${profileUser.userId}&filter=newest"
                               class="${filter == 'newest' ? 'active' : ''}">Newest</a>
                            <a href="?id=${profileUser.userId}&filter=name"
                               class="${filter == 'name' ? 'active' : ''}">Name</a>
                        </div>
                    </div>

                    <%-- Question list --%>
                    <c:choose>
                        <c:when test="${not empty questions}">
                            <c:forEach var="q" items="${questions}">
                                <div class="question-item">
                                    <div class="q-stats">
                                        <div class="q-stat ${q.score < 0 ? 'negative' : ''}">
                                            <strong>${q.score}</strong> votes
                                        </div>
                                        <div class="q-stat ${q.answerCount > 0 ? 'has-answer' : ''}">
                                            <strong>${q.answerCount}</strong> answers
                                        </div>
                                        <div class="q-stat">
                                            <strong>${q.viewCount}</strong> views
                                        </div>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/question?id=${q.questionId}"
                                       class="q-title">
                                        ${q.title}
                                        <c:if test="${q.isClosed}">
                                            <span class="closed-badge">closed</span>
                                        </c:if>
                                    </a>
                                    <p class="q-excerpt">${q.body}</p>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-questions">
                                <i class="fa-solid fa-circle-question"></i>
                                <p>No questions found.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <%-- Pagination --%>
                    <c:if test="${totalPages > 1}">
                        <div class="pagination">
                            <c:if test="${currentPage > 1}">
                                <a href="?id=${profileUser.userId}&filter=${filter}&page=${currentPage - 1}">Prev</a>
                            </c:if>
                            <c:forEach begin="1" end="${totalPages}" var="p">
                                <c:choose>
                                    <c:when test="${p == currentPage}">
                                        <span class="current">${p}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="?id=${profileUser.userId}&filter=${filter}&page=${p}">${p}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                            <c:if test="${currentPage < totalPages}">
                                <a href="?id=${profileUser.userId}&filter=${filter}&page=${currentPage + 1}">Next</a>
                            </c:if>
                        </div>
                    </c:if>

                </div>
            </div>

        </main>
    </div>
        
    </body>
</html>
