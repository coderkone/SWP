<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Bounty Questions - DevQuery</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <style>
            :root {
                --bg: #f5f1e8;
                --surface: #fffdf8;
                --border: #e7d8b6;
                --text: #2f2416;
                --muted: #7a6a54;
                --accent: #9a6400;
            }

            body {
                background: radial-gradient(circle at top, #fff7df 0%, var(--bg) 45%, #f3eee3 100%);
                color: var(--text);
                padding-top: 60px;
            }

            .page-shell {
                max-width: 1280px;
                margin: 0 auto;
                padding: 24px 16px 48px;
                display: grid;
                grid-template-columns: 180px minmax(0, 1fr) 280px;
                gap: 24px;
            }

            .content-card,
            .widget-card {
                background: rgba(255, 253, 248, 0.95);
                border: 1px solid var(--border);
                border-radius: 18px;
                box-shadow: 0 16px 40px rgba(81, 58, 18, 0.08);
            }

            .content-card {
                padding: 28px;
            }

            .hero {
                display: flex;
                justify-content: space-between;
                gap: 20px;
                align-items: flex-start;
                margin-bottom: 22px;
            }

            .hero h1 {
                margin: 0 0 8px;
                font-size: 34px;
                line-height: 1.1;
            }

            .hero p {
                margin: 0;
                max-width: 720px;
                color: var(--muted);
                line-height: 1.6;
            }

            .sort-strip {
                display: flex;
                gap: 10px;
                flex-wrap: wrap;
                margin-bottom: 22px;
            }

            .sort-pill {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 10px 14px;
                border-radius: 999px;
                border: 1px solid var(--border);
                background: #fff;
                color: var(--muted);
                text-decoration: none;
                font-size: 14px;
                font-weight: 600;
            }

            .sort-pill.active {
                color: #fff;
                background: var(--accent);
                border-color: var(--accent);
            }

            .question-list {
                display: grid;
                gap: 16px;
            }

            .question-card {
                display: grid;
                grid-template-columns: 120px minmax(0, 1fr);
                gap: 18px;
                padding: 18px;
                border: 1px solid var(--border);
                border-radius: 16px;
                background: linear-gradient(180deg, #fffef9 0%, #fff9ea 100%);
            }

            .bounty-stack {
                display: flex;
                flex-direction: column;
                gap: 10px;
                align-items: stretch;
            }

            .bounty-pill {
                display: inline-flex;
                justify-content: center;
                align-items: center;
                gap: 8px;
                background: var(--accent);
                color: #fff;
                border-radius: 999px;
                padding: 9px 12px;
                font-weight: 700;
            }

            .mini-stat {
                border: 1px solid var(--border);
                border-radius: 12px;
                background: rgba(255,255,255,0.8);
                padding: 10px 12px;
                text-align: center;
                font-size: 13px;
            }

            .mini-stat strong {
                display: block;
                font-size: 18px;
                color: var(--text);
            }

            .question-card h2 {
                margin: 0 0 10px;
                font-size: 22px;
            }

            .question-card h2 a {
                text-decoration: none;
                color: var(--text);
            }

            .question-card h2 a:hover {
                color: var(--accent);
            }

            .question-excerpt {
                margin: 0 0 14px;
                color: var(--muted);
                line-height: 1.6;
            }

            .meta-row {
                display: flex;
                justify-content: space-between;
                gap: 16px;
                flex-wrap: wrap;
                align-items: flex-end;
            }

            .tag-list {
                display: flex;
                gap: 8px;
                flex-wrap: wrap;
            }

            .tag-chip {
                display: inline-flex;
                align-items: center;
                padding: 5px 10px;
                border-radius: 999px;
                background: #efe3c1;
                color: #5f4a24;
                text-decoration: none;
                font-size: 12px;
                font-weight: 600;
            }

            .subtle {
                color: var(--muted);
                font-size: 13px;
            }

            .countdown {
                color: var(--accent);
                font-weight: 700;
            }

            .widget-card {
                padding: 18px;
            }

            .widget-card h3 {
                margin: 0 0 14px;
                font-size: 15px;
                text-transform: uppercase;
                letter-spacing: 0.06em;
                color: var(--muted);
            }

            .widget-list {
                list-style: none;
                padding: 0;
                margin: 0;
                display: grid;
                gap: 12px;
            }

            .widget-list li {
                color: var(--muted);
                line-height: 1.5;
                font-size: 13px;
            }

            @media (max-width: 991px) {
                .page-shell {
                    grid-template-columns: 1fr;
                }
            }

            @media (max-width: 680px) {
                .content-card {
                    padding: 20px;
                }

                .hero {
                    flex-direction: column;
                }

                .question-card {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>
    <body>
        <jsp:include page="../Common/header.jsp" />

        <div class="page-shell">
            <aside>
                <jsp:include page="../Common/sidebar.jsp" />
            </aside>

            <main>
                <section class="content-card">
                    <div class="hero">
                        <div>
                            <h1>Bounty Questions</h1>
                            <p>Danh sách các câu hỏi đang có bounty hoạt động. Bạn có thể ưu tiên xem các bounty cao nhất hoặc những bounty sắp hết hạn để trả lời đúng lúc.</p>
                        </div>
                    </div>

                    <div class="sort-strip">
                        <a href="${pageContext.request.contextPath}/bounty/questions?sort=amount" class="sort-pill ${currentSort == 'amount' ? 'active' : ''}">
                            <i class="fa-solid fa-ranking-star"></i> Bounty cao
                        </a>
                        <a href="${pageContext.request.contextPath}/bounty/questions?sort=expiring" class="sort-pill ${currentSort == 'expiring' ? 'active' : ''}">
                            <i class="fa-solid fa-hourglass-end"></i> Sắp hết hạn
                        </a>
                        <a href="${pageContext.request.contextPath}/bounty/questions?sort=newest" class="sort-pill ${currentSort == 'newest' ? 'active' : ''}">
                            <i class="fa-solid fa-clock"></i> Mới đăng
                        </a>
                    </div>

                    <c:choose>
                        <c:when test="${not empty bountyQuestions}">
                            <div class="question-list">
                                <c:forEach items="${bountyQuestions}" var="q">
                                    <article class="question-card">
                                        <div class="bounty-stack">
                                            <div class="bounty-pill"><i class="fa-solid fa-coins"></i> +${q.bountyAmount}</div>
                                            <div class="mini-stat"><strong>${q.score}</strong>votes</div>
                                            <div class="mini-stat"><strong>${q.answerCount}</strong>answers</div>
                                        </div>

                                        <div>
                                            <h2><a href="${pageContext.request.contextPath}/question/detail?id=${q.questionId}">${q.title}</a></h2>
                                            <p class="question-excerpt">
                                                <c:choose>
                                                    <c:when test="${q.body != null && q.body.length() > 220}">
                                                        ${q.body.substring(0, 220)}...
                                                    </c:when>
                                                    <c:otherwise>${q.body}</c:otherwise>
                                                </c:choose>
                                            </p>

                                            <div class="meta-row">
                                                <div>
                                                    <div class="tag-list">
                                                        <c:forEach items="${q.tags}" var="tag">
                                                            <a href="${pageContext.request.contextPath}/home?tag=${tag}" class="tag-chip">${tag}</a>
                                                        </c:forEach>
                                                    </div>
                                                    <div class="subtle" style="margin-top: 12px;">
                                                        by <a href="${pageContext.request.contextPath}/profile?id=${q.userId}">${q.authorName}</a>
                                                        • <fmt:formatDate value="${q.createdAt}" pattern="MMM dd, yyyy"/>
                                                    </div>
                                                </div>

                                                <div class="subtle" style="text-align: right;">
                                                    <div class="countdown" data-bounty-expiry="${q.bountyExpiresAt.time}"></div>
                                                    <div>expires <fmt:formatDate value="${q.bountyExpiresAt}" pattern="MMM dd, yyyy HH:mm"/></div>
                                                </div>
                                            </div>
                                        </div>
                                    </article>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="subtle">Hiện chưa có câu hỏi nào đang có bounty hoạt động.</div>
                        </c:otherwise>
                    </c:choose>
                </section>
            </main>

            <aside>
                <div class="widget-card">
                    <h3>Rules</h3>
                    <ul class="widget-list">
                        <li>Chỉ chủ câu hỏi mới được đặt bounty.</li>
                        <li>Phải đủ reputation để chi trả bounty.</li>
                        <li>Bounty có thời hạn cố định và không hoàn lại.</li>
                        <li>Câu hỏi đã có accepted answer sẽ không được đặt bounty mới.</li>
                    </ul>
                </div>

                <div class="widget-card" style="margin-top: 18px;">
                    <h3>Popular Tags</h3>
                    <div class="tag-list">
                        <c:forEach items="${popularTags}" var="tag">
                            <a href="${pageContext.request.contextPath}/home?tag=${tag}" class="tag-chip">${tag}</a>
                        </c:forEach>
                    </div>
                </div>
            </aside>
        </div>

        <jsp:include page="../Common/footer.jsp" />

        <script>
            function renderBountyCountdowns() {
                const elements = document.querySelectorAll('[data-bounty-expiry]');
                const now = Date.now();

                elements.forEach(function(element) {
                    const expiry = Number(element.getAttribute('data-bounty-expiry'));
                    if (!expiry) {
                        return;
                    }

                    const remaining = expiry - now;
                    if (remaining <= 0) {
                        element.textContent = 'Expired';
                        return;
                    }

                    const totalSeconds = Math.floor(remaining / 1000);
                    const days = Math.floor(totalSeconds / 86400);
                    const hours = Math.floor((totalSeconds % 86400) / 3600);
                    const minutes = Math.floor((totalSeconds % 3600) / 60);

                    if (days > 0) {
                        element.textContent = days + 'd ' + hours + 'h left';
                    } else if (hours > 0) {
                        element.textContent = hours + 'h ' + minutes + 'm left';
                    } else {
                        element.textContent = Math.max(minutes, 0) + 'm left';
                    }
                });
            }

            renderBountyCountdowns();
            window.setInterval(renderBountyCountdowns, 60000);
        </script>
    </body>
</html>