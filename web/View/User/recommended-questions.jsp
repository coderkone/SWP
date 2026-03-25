<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>DevQuery - Recommended Questions</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <style>
            :root {
                --ink: #1f2937;
                --muted: #6b7280;
                --border: #d6d9dc;
                --panel: #ffffff;
                --soft: #f8fafc;
                --accent: #0a95ff;
                --accent-deep: #0550ae;
                --tag-bg: #e1ecf4;
                --tag-text: #39739d;
                --highlight: linear-gradient(135deg, #fff6e5 0%, #eef6ff 100%);
            }

            * {
                box-sizing: border-box;
            }

            body {
                margin: 0;
                font-family: "Segoe UI", Arial, sans-serif;
                background: #fff;
                color: var(--ink);
            }

            .page-shell {
                max-width: 1264px;
                margin: 56px auto 0;
                display: flex;
                align-items: flex-start;
            }

            .left-sidebar {
                width: 164px;
                flex-shrink: 0;
                padding-top: 25px;
                border-right: 1px solid var(--border);
            }

            .main-content {
                flex: 1;
                padding: 24px;
                min-width: 0;
            }

            .right-sidebar {
                width: 300px;
                flex-shrink: 0;
                padding: 24px 0 0 24px;
            }

            .hero {
                background: var(--highlight);
                border: 1px solid #e9d8b5;
                border-radius: 16px;
                padding: 24px;
                margin-bottom: 24px;
            }

            .hero h1 {
                margin: 0 0 8px;
                font-size: 30px;
                font-weight: 700;
            }

            .hero p {
                margin: 0;
                color: var(--muted);
                max-width: 760px;
                line-height: 1.6;
            }

            .insight-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
                gap: 12px;
                margin-top: 18px;
            }

            .insight-card {
                background: rgba(255, 255, 255, 0.82);
                border: 1px solid rgba(255, 255, 255, 0.9);
                border-radius: 12px;
                padding: 14px 16px;
            }

            .insight-label {
                font-size: 12px;
                text-transform: uppercase;
                letter-spacing: 0.08em;
                color: var(--muted);
                margin-bottom: 6px;
            }

            .insight-value {
                font-size: 22px;
                font-weight: 700;
            }

            .content-card,
            .widget {
                background: var(--panel);
                border: 1px solid var(--border);
                border-radius: 14px;
                overflow: hidden;
            }

            .section-head {
                padding: 18px 20px 10px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 12px;
            }

            .section-head h2 {
                margin: 0;
                font-size: 22px;
            }

            .section-head span {
                color: var(--muted);
                font-size: 14px;
            }

            .question-item {
                display: flex;
                gap: 18px;
                padding: 18px 20px;
                border-top: 1px solid var(--border);
            }

            .question-item:first-of-type {
                border-top: none;
            }

            .stats {
                width: 105px;
                flex-shrink: 0;
                text-align: right;
                color: var(--muted);
                font-size: 13px;
                display: flex;
                flex-direction: column;
                gap: 8px;
            }

            .stats strong {
                color: var(--ink);
            }

            .question-body {
                flex: 1;
                min-width: 0;
            }

            .question-title {
                color: var(--accent-deep);
                text-decoration: none;
                font-size: 18px;
                font-weight: 600;
            }

            .question-title:hover {
                color: var(--accent);
            }

            .question-excerpt {
                margin: 10px 0 14px;
                color: #3b4045;
                line-height: 1.5;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
            }

            .meta-row {
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 12px;
                flex-wrap: wrap;
            }

            .tags {
                display: flex;
                flex-wrap: wrap;
                gap: 8px;
            }

            .tag {
                display: inline-flex;
                align-items: center;
                padding: 4px 8px;
                border-radius: 999px;
                text-decoration: none;
                background: var(--tag-bg);
                color: var(--tag-text);
                font-size: 12px;
            }

            .user-meta {
                color: var(--muted);
                font-size: 13px;
            }

            .user-meta a {
                color: var(--accent-deep);
                text-decoration: none;
                font-weight: 600;
            }

            .profile-panel {
                padding: 20px;
            }

            .profile-panel h3,
            .widget-title {
                margin: 0 0 14px;
                font-size: 18px;
            }

            .chips {
                display: flex;
                flex-wrap: wrap;
                gap: 8px;
            }

            .chip {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 7px 10px;
                border-radius: 999px;
                background: var(--soft);
                border: 1px solid #e5e7eb;
                color: #374151;
                font-size: 13px;
            }

            .widget {
                margin-bottom: 18px;
            }

            .widget-body {
                padding: 18px;
            }

            .widget-list {
                list-style: none;
                padding: 0;
                margin: 0;
            }

            .widget-list li + li {
                margin-top: 12px;
            }

            .widget-link {
                color: var(--accent-deep);
                text-decoration: none;
                font-weight: 600;
            }

            .widget-subtle {
                margin-top: 4px;
                color: var(--muted);
                font-size: 13px;
            }

            .empty-state {
                padding: 36px 20px 40px;
                text-align: center;
                color: var(--muted);
            }

            .empty-state i {
                font-size: 42px;
                color: #f59e0b;
                margin-bottom: 14px;
            }

            .empty-state a {
                color: var(--accent-deep);
                text-decoration: none;
                font-weight: 600;
            }

            @media (max-width: 991px) {
                .page-shell {
                    display: block;
                }

                .left-sidebar,
                .right-sidebar {
                    width: auto;
                    border-right: none;
                    padding: 0 24px;
                }

                .main-content {
                    padding-top: 12px;
                }
            }

            @media (max-width: 640px) {
                .main-content,
                .left-sidebar,
                .right-sidebar {
                    padding-left: 16px;
                    padding-right: 16px;
                }

                .question-item {
                    flex-direction: column;
                }

                .stats {
                    width: auto;
                    text-align: left;
                    flex-direction: row;
                    flex-wrap: wrap;
                }
            }
        </style>
    </head>
    <body>
        <jsp:include page="../Common/header.jsp" />

        <div class="page-shell">
            <div class="left-sidebar">
                <jsp:include page="../Common/sidebar.jsp" />
            </div>

            <main class="main-content">
                <section class="hero">
                    <h1>Recommended Questions</h1>
                    <p>
                        Danh sách này được cá nhân hóa từ lịch sử các câu hỏi bạn đã xem. Hệ thống trích xuất tag và từ khóa nổi bật, sau đó ưu tiên những câu hỏi vừa liên quan vừa đang được cộng đồng quan tâm.
                    </p>

                    <div class="insight-grid">
                        <div class="insight-card">
                            <div class="insight-label">Viewed Questions</div>
                            <div class="insight-value">${viewedHistoryCount}</div>
                        </div>
                        <div class="insight-card">
                            <div class="insight-label">Interest Tags</div>
                            <div class="insight-value">${fn:length(profileTags)}</div>
                        </div>
                        <div class="insight-card">
                            <div class="insight-label">Extracted Keywords</div>
                            <div class="insight-value">${fn:length(profileKeywords)}</div>
                        </div>
                    </div>
                </section>

                <section class="content-card">
                    <div class="section-head">
                        <h2>Questions for You</h2>
                        <span>${fn:length(recommendedQuestions)} suggestions</span>
                    </div>

                    <c:choose>
                        <c:when test="${not empty recommendedQuestions}">
                            <c:forEach items="${recommendedQuestions}" var="q">
                                <article class="question-item">
                                    <div class="stats">
                                        <div><strong>${q.score}</strong> votes</div>
                                        <div><strong>${q.answerCount}</strong> answers</div>
                                        <div><strong>${q.viewCount}</strong> views</div>
                                    </div>

                                    <div class="question-body">
                                        <a href="${pageContext.request.contextPath}/question?id=${q.questionId}" class="question-title">${q.title}</a>

                                        <p class="question-excerpt">
                                            <c:choose>
                                                <c:when test="${q.body != null && q.body.length() > 220}">
                                                    ${q.body.substring(0, 220)}...
                                                </c:when>
                                                <c:otherwise>${q.body}</c:otherwise>
                                            </c:choose>
                                        </p>

                                        <div class="meta-row">
                                            <div class="tags">
                                                <c:forEach items="${q.tags}" var="t">
                                                    <a href="${pageContext.request.contextPath}/home?tag=${t}" class="tag">${t}</a>
                                                </c:forEach>
                                            </div>

                                            <div class="user-meta">
                                                by
                                                <a href="${pageContext.request.contextPath}/profile?id=${q.userId}">${q.authorName}</a>
                                                on <fmt:formatDate value="${q.createdAt}" pattern="MMM dd, yyyy"/>
                                            </div>
                                        </div>
                                    </div>
                                </article>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <i class="fa-solid fa-compass"></i>
                                <p>Bạn chưa có đủ lịch sử xem để tạo gợi ý cá nhân hóa.</p>
                                <p>Hãy mở thêm vài câu hỏi ở trang <a href="${pageContext.request.contextPath}/home">Home</a>, sau đó quay lại đây.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </section>
            </main>

            <aside class="right-sidebar">
                <div class="widget">
                    <div class="widget-body">
                        <div class="widget-title">Your Interest Profile</div>

                        <c:if test="${not empty profileTags}">
                            <h3 style="margin: 0 0 10px; font-size: 14px; color: var(--muted);">Top tags</h3>
                            <div class="chips" style="margin-bottom: 16px;">
                                <c:forEach items="${profileTags}" var="tag">
                                    <a class="chip" href="${pageContext.request.contextPath}/home?tag=${tag}">
                                        <i class="fa-solid fa-tag"></i>${tag}
                                    </a>
                                </c:forEach>
                            </div>
                        </c:if>

                        <c:if test="${not empty profileKeywords}">
                            <h3 style="margin: 0 0 10px; font-size: 14px; color: var(--muted);">Top keywords</h3>
                            <div class="chips">
                                <c:forEach items="${profileKeywords}" var="keyword">
                                    <span class="chip"><i class="fa-solid fa-key"></i>${keyword}</span>
                                </c:forEach>
                            </div>
                        </c:if>

                        <c:if test="${empty profileTags and empty profileKeywords}">
                            <p style="margin: 0; color: var(--muted); line-height: 1.6;">
                                Hệ thống sẽ bắt đầu xây hồ sơ sở thích sau khi bạn xem một vài câu hỏi.
                            </p>
                        </c:if>
                    </div>
                </div>

                <div class="widget">
                    <div class="widget-body">
                        <div class="widget-title">How ranking works</div>
                        <ul class="widget-list">
                            <li>
                                <div class="widget-link">Relevance first</div>
                                <div class="widget-subtle">Ưu tiên câu hỏi trùng tag và từ khóa với lịch sử xem gần đây.</div>
                            </li>
                            <li>
                                <div class="widget-link">Popularity matters</div>
                                <div class="widget-subtle">Điểm vote, lượt xem và câu trả lời được chấp nhận giúp đẩy nội dung chất lượng lên cao hơn.</div>
                            </li>
                            <li>
                                <div class="widget-link">No manual search needed</div>
                                <div class="widget-subtle">Bạn chỉ cần đọc nội dung quan tâm, hệ thống sẽ tự làm phần còn lại.</div>
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="widget">
                    <div class="widget-body">
                        <div class="widget-title">Popular tags</div>
                        <div class="chips">
                            <c:forEach items="${popularTags}" var="tag">
                                <a href="${pageContext.request.contextPath}/home?tag=${tag}" class="chip">${tag}</a>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </aside>
        </div>

        <jsp:include page="../Common/footer.jsp" />
    </body>
</html>
