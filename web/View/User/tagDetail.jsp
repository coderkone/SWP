<%-- 
    Document   : tagDetail
    Created on : Mar 21, 2026, 11:37:39 PM
    Author     : Asus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>[${tag.tagName}] - Devquery </title>
        <%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
            flex-grow: 1; padding: 24px;
            border-left: 1px solid var(--border-color); min-width: 0;
        }

        /* Tag Header */
        .tag-name-badge {
            display: inline-block; background: #e1ecf4; color: #39739d;
            padding: 6px 12px; border-radius: 4px;
            font-size: 22px; font-weight: 500; margin-bottom: 12px;
        }
        .tag-description {
            color: #3b4045; line-height: 1.7;
            max-width: 720px; font-size: 13px; margin-bottom: 16px;
        }

        /* Action bar */
        .action-bar {
            display: flex; justify-content: space-between;
            align-items: center; margin-bottom: 16px;
        }

        /* Watch button */
        .btn-watch {
            padding: 8px 16px; border-radius: 4px; font-size: 13px;
            font-weight: 500; cursor: pointer; border: 1px solid;
            display: inline-flex; align-items: center; gap: 6px;
            text-decoration: none;
        }
        .btn-watch.not-followed {
            background: var(--blue); color: #fff; border-color: var(--blue);
        }
        .btn-watch.not-followed:hover { background: #005999; }
        .btn-watch.followed {
            background: #fff; color: #6a737c; border-color: #9fa6ad;
        }
        .btn-watch.followed:hover { background: #f8f9f9; }

        /* Filter buttons */
        .filter-buttons {
            display: flex; border: 1px solid #9fa6ad;
            border-radius: 3px; overflow: hidden;
        }
        .filter-buttons a {
            padding: 8px 12px; font-size: 13px; color: #6a737c;
            text-decoration: none; border-right: 1px solid #9fa6ad;
            background: #fff; transition: background 0.1s;
        }
        .filter-buttons a:last-child { border-right: none; }
        .filter-buttons a:hover { background: #f8f9f9; }
        .filter-buttons a.active {
            background: #e3e6e8; font-weight: 500; color: #3b4045;
        }

        /* Divider */
        .divider { border: none; border-top: 1px solid var(--border-color); margin: 12px 0; }

        /* Questions count */
        .questions-count {
            font-size: 15px; color: #3b4045; margin-bottom: 12px;
        }
        .questions-count strong { font-weight: 600; }

        /* Question item */
        .question-item {
            display: flex;
            flex-direction: column;   /* ✅ stats trên, content dưới */
            gap: 8px;
            padding: 16px 0;
            border-bottom: 1px solid var(--border-color);
        }

        /* Stats */
        .q-stats {
            display: flex;
            flex-direction: row;      /* ✅ ngang thay vì column */
            align-items: center;
            gap: 16px;
            min-width: fit-content;
            flex-shrink: 0;
            margin-bottom: 6px;
        }
        .q-stat { font-size: 12px; color: #6a737c; text-align: center;white-space: nowrap; }
        .q-stat strong { display: block; font-size: 14px; color: #3b4045; }
        .q-stat.has-answer strong { color: #5eba7d; }
        .q-stat.negative strong  { color: #d1383d; }

        /* Content */
        .q-content { flex: 1; min-width: 0; }

        .q-title {
            font-size: 15px; color: var(--blue); text-decoration: none;
            line-height: 1.4; display: block; margin-bottom: 6px;
        }
        .q-title:hover { color: #005999; }

        .q-excerpt {
            font-size: 12px; color: #3b4045; line-height: 1.5;
            margin-bottom: 8px;
            display: -webkit-box; -webkit-line-clamp: 2;
            -webkit-box-orient: vertical; overflow: hidden;
        }

        .q-footer {
            display: flex; justify-content: space-between;
            align-items: center; flex-wrap: wrap; gap: 8px;
        }

        .q-tags { display: flex; flex-wrap: wrap; gap: 4px; }
        .q-tag {
            background: #e1ecf4; color: #39739d;
            padding: 2px 6px; border-radius: 3px;
            font-size: 11px; text-decoration: none;
        }
        .q-tag:hover { background: #d0e3f1; }

        .q-meta { font-size: 11px; color: #6a737c; }
        .q-meta strong { color: var(--blue); }

        .closed-badge {
            display: inline-block; font-size: 11px;
            background: #f1f2f3; color: #6a737c;
            border: 1px solid #d6d9dc; border-radius: 3px;
            padding: 1px 5px; margin-left: 6px;
        }

        /* Empty */
        .empty-questions {
            padding: 40px 0; text-align: center; color: #6a737c;
        }
        .empty-questions i { font-size: 40px; margin-bottom: 12px; display: block; }

        /* Pagination */
        .pagination {
            display: flex; gap: 4px;
            margin-top: 20px; flex-wrap: wrap;
        }
        .pagination a, .pagination span {
            padding: 6px 10px; border: 1px solid #d6d9dc;
            border-radius: 3px; font-size: 13px;
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
                <jsp:param name="page" value="tags"/>
            </jsp:include>
        </div>

        <main class="main-content">

            <%-- Tag Header --%>
            <div>
                <div class="tag-name-badge">${tag.tagName}</div>
                <p class="tag-description">${tag.description}</p>
            </div>

            <%-- Action Bar --%>
            <div class="action-bar">

    <c:choose>
        <c:when test="${not isLoggedIn}">
            <a href="${pageContext.request.contextPath}/auth/login"
               class="btn-watch not-followed">
                <i class="fa-solid fa-eye"></i> Watch tag
            </a>
        </c:when>
        <c:when test="${tag.followed}">
            <form method="post"
                  action="${pageContext.request.contextPath}/follow-tags"
                  style="margin:0;">
                <input type="hidden" name="tagId"      value="${tag.tagId}" />
                <input type="hidden" name="action"     value="unfollow" />
                <input type="hidden" name="redirectTo"
                       value="/tagsdetail?id=${tag.tagId}&filter=${filter}&page=${currentPage}" />
                <button type="submit" class="btn-watch followed">
                    <i class="fa-solid fa-eye-slash"></i> Unwatch tag
                </button>
            </form>
        </c:when>
        <c:otherwise>
            <form method="post"
                  action="${pageContext.request.contextPath}/follow-tags"
                  style="margin:0;">
                <input type="hidden" name="tagId"      value="${tag.tagId}" />
                <input type="hidden" name="action"     value="follow" />
                <input type="hidden" name="redirectTo"
                       value="/tagsdetail?id=${tag.tagId}&filter=${filter}&page=${currentPage}" />
                <button type="submit" class="btn-watch not-followed">
                    <i class="fa-solid fa-eye"></i> Watch tag
                </button>
            </form>
        </c:otherwise>
    </c:choose>

    <div class="filter-buttons">
        <a href="?id=${tag.tagId}&filter=newest"
           class="${filter == 'newest' ? 'active' : ''}">Newest</a>
        <a href="?id=${tag.tagId}&filter=unanswered"
           class="${filter == 'unanswered' ? 'active' : ''}">Unanswered</a>
        <a href="?id=${tag.tagId}&filter=voted"
           class="${filter == 'voted' ? 'active' : ''}">Voted</a>
    </div>

</div>
            

            <hr class="divider"/>

            <%-- Questions count --%>
            <p class="questions-count">
                <strong>${totalQuestions}</strong> questions tagged
                [<strong>${tag.tagName}</strong>]
            </p>

            <%-- Question List --%>
            <c:choose>
                <c:when test="${not empty questions}">
                    <c:forEach var="q" items="${questions}">
                        <div class="question-item">

                            <%-- Stats --%>
                            <div class="q-stats">
                                <div class="q-stat ${q.score < 0 ? 'negative' : ''}">
                                    <strong>${q.score}</strong>
                                    votes
                                </div>
                                <div class="q-stat ${q.answerCount > 0 ? 'has-answer' : ''}">
                                    <strong>${q.answerCount}</strong>
                                    answers
                                </div>
                                <div class="q-stat">
                                    <strong>${q.viewCount}</strong>
                                    views
                                </div>
                            </div>

                            <%-- Content --%>
                            <div class="q-content">
                                <a href="${pageContext.request.contextPath}/question?id=${q.questionId}"
                                   class="q-title">
                                    ${q.title}
                                    <c:if test="${q.isClosed}">
                                        <span class="closed-badge">closed</span>
                                    </c:if>
                                </a>

                                <p class="q-excerpt">${q.body}</p>

                                <div class="q-footer">
                                    <div class="q-tags">
                                        <c:forEach var="t" items="${q.tags}">
                                            <a href="${pageContext.request.contextPath}/tagsdetail?id="
                                               class="q-tag">${t}</a>
                                        </c:forEach>
                                    </div>
                                    <div class="q-meta">
                                        asked by <strong>${q.authorName}</strong>
                                        &nbsp;·&nbsp;
                                        ${q.createdAt}
                                    </div>
                                </div>
                            </div>

                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="empty-questions">
                        <i class="fa-solid fa-circle-question"></i>
                        <p>No questions found for this filter.</p>
                    </div>
                </c:otherwise>
            </c:choose>

            <%-- Pagination --%>
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="?id=${tag.tagId}&filter=${filter}&page=${currentPage - 1}">Prev</a>
                    </c:if>
                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <c:choose>
                            <c:when test="${p == currentPage}">
                                <span class="current">${p}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="?id=${tag.tagId}&filter=${filter}&page=${p}">${p}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <c:if test="${currentPage < totalPages}">
                        <a href="?id=${tag.tagId}&filter=${filter}&page=${currentPage + 1}">Next</a>
                    </c:if>
                </div>
            </c:if>

        </main>
    </div>
        
    </body>
</html>
