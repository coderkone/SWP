<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>User Activity - DevQuery</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

        <style>
            body {
                background-color: #fff;
                padding-top: 60px;
                color: #232629;
                font-size: 14px;
            }

            /* Profile Header Box */
            .user-avatar-lg {
                width: 128px;
                height: 128px;
                border-radius: 5px;
                object-fit: cover;
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            }
            .user-name {
                font-size: 34px;
                font-weight: normal;
                margin-bottom: 4px;
            }
            .user-meta {
                font-size: 13px;
                color: #6a737c;
            }

            /* Main Navigation Tabs */
            .profile-tabs {
                border-bottom: none;
                margin-bottom: 20px;
            }
            .profile-tabs .nav-link {
                color: #525960;
                border-radius: 20px;
                padding: 6px 12px;
                margin-right: 5px;
                border: none;
                font-size: 13px;
            }
            .profile-tabs .nav-link:hover {
                background-color: #e3e6e8;
                color: #0c0d0e;
            }
            .profile-tabs .nav-link.active {
                background-color: #f48024;
                color: white;
            }

            /* Inner Left Sidebar */
            .inner-nav-item {
                display: block;
                padding: 6px 12px;
                color: #525960;
                text-decoration: none;
                border-radius: 100px;
                margin-bottom: 2px;
            }
            .inner-nav-item:hover {
                background-color: #f8f9f9;
                color: #0c0d0e;
            }
            .inner-nav-item.active {
                font-weight: bold;
                background-color: #f1f2f3;
                color: #0c0d0e;
            }

            /* Content Boxes */
            .summary-box {
                border: 1px solid #d6d9dc;
                border-radius: 5px;
                padding: 20px;
                height: 100%;
                text-align: center;
            }
            .summary-box h4 {
                font-size: 15px;
                font-weight: normal;
                color: #6a737c;
                margin-bottom: 15px;
                text-transform: uppercase;
                font-size: 12px;
                text-align: left;
            }

            /* Badges Indicator */
            .badge-dot {
                display: inline-block;
                width: 6px;
                height: 6px;
                border-radius: 50%;
                margin-right: 3px;
                vertical-align: middle;
            }
            .gold {
                background-color: #ffcc01;
            }
            .silver {
                background-color: #b4b8bc;
            }
            .bronze {
                background-color: #d1a684;
            }

            /* Table / List styling */
            .activity-table th {
                font-weight: normal;
                color: #6a737c;
                border-bottom: 1px solid #e3e6e8;
            }
            .activity-table td {
                padding: 12px 0;
                border-bottom: 1px solid #e3e6e8;
            }
            .rep-score {
                color: #28a745;
                font-weight: bold;
            }
            .rep-score.negative {
                color: #dc3545;
            }
            .badge-title-group {
                border-bottom: 2px solid #f1f2f3;
                padding-bottom: 10px;
                margin-bottom: 20px;
            }

            /* Chỉnh lại Card cho nhỏ gọn và căn giữa */
            .badge-item-card {
                border: 1px solid #e3e6e8;
                border-radius: 8px;
                padding: 12px 16px;
                background-color: #fff;
                display: flex;
                align-items: center;
                justify-content: center; /* Căn giữa nội dung */
                height: 100%;
                cursor: pointer;
                position: relative; /* Rất quan trọng để định vị Tooltip */
            }

            .badge-dot {
                display: inline-block;
                width: 10px;
                height: 10px;
                border-radius: 50%;
                margin-right: 8px;
            }
            .badge-dot.gold {
                background-color: #ffcc01;
                box-shadow: 0 0 4px rgba(255, 204, 1, 0.4);
            }
            .badge-dot.silver {
                background-color: #b4b8bc;
            }
            .badge-dot.bronze {
                background-color: #d1a684;
            }

            /* ---------------------------------- */
            /* CSS CHO CUSTOM TOOLTIP HIỆN ĐẠI   */
            /* ---------------------------------- */
            .badge-custom-tooltip {
                visibility: hidden;
                opacity: 0;
                position: absolute;
                z-index: 1000;
                bottom: 120%; /* Đẩy tooltip lên phía trên card */
                left: 50%;
                transform: translateX(-50%);
                width: 250px;
                background-color: #242729; /* Màu nền tối ngầu ngầu */
                color: #fff;
                text-align: left;
                border-radius: 6px;
                padding: 12px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.15);

                /* Logic thời gian: Biến mất thì ngay lập tức (delay 0s) */
                transition: opacity 0.2s, visibility 0.2s;
                transition-delay: 0s;
                pointer-events: none; /* Tránh cản trở click chuột */
            }

            .badge-custom-tooltip {
                visibility: hidden;
                opacity: 0;
                position: absolute;
                z-index: 1000;
                bottom: 120%;
                left: 50%;
                transform: translateX(-50%);
                width: 250px;
                background-color: #ffffff;
                color: #242729;
                text-align: left;
                border-radius: 8px;
                padding: 12px;
                border: 1px solid #d6d9dc;
                box-shadow: 0 8px 24px rgba(0,0,0,0.12);

                transition: opacity 0.2s, visibility 0.2s;
                transition-delay: 0s;
                pointer-events: none;
            }

            .badge-custom-tooltip::after {
                content: "";
                position: absolute;
                top: 100%;
                left: 50%;
                margin-left: -7px;
                border-width: 7px;
                border-style: solid;
                border-color: #ffffff transparent transparent transparent;
            }

            .badge-item-card:hover .badge-custom-tooltip {
                visibility: visible;
                opacity: 1;
                transition-delay: 1s;
            }
        </style>
    </head>
    <body>

        <jsp:include page="../Common/header.jsp" />

        <div class="container-fluid" style="max-width: 1264px; margin: 0 auto;">
            <div class="row">
                <nav class="col-md-2 d-none d-md-block bg-light sidebar p-0 pt-4" style="border-right: 1px solid #d6d9dc; min-height: 100vh;">
                    <jsp:include page="../Common/sidebar.jsp" />
                </nav>

                <main class="col-md-10 px-md-4 pt-4">

                    <div class="d-flex align-items-start mb-4">
                        <div class="me-4">
                            <img src="${sessionScope.user.avatarUrl != null ? sessionScope.user.avatarUrl : 'https://cdn-icons-png.flaticon.com/512/3135/3135715.png'}" 
                                 class="user-avatar-lg" alt="Avatar">
                        </div>
                        <div class="flex-grow-1">
                            <h1 class="user-name">${userProfile.username != null ? userProfile.username : 'Developer'}</h1>
                            <div class="user-meta mb-3">
                                <i class="fa-solid fa-cake-candles"></i> Member since <fmt:formatDate value="${userProfile.createdAt}" pattern="dd/MM/yyyy" />
                                <span class="mx-2">|</span> 
                                <i class="fa-solid fa-star text-warning"></i> ${userProfile.reputation} reputation
                            </div>

                            <ul class="nav profile-tabs">
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/profile">Profile</a></li>
                                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/badge">Badge</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/saves">Saves</a></li>
                                <li class="nav-item"><a class="nav-link" href="#">Settings</a></li>
                            </ul>
                        </div>
                    </div>

                    <div class="row mt-4">

                        <div class="col-md-2">
                            <nav class="nav flex-column">
                                <a class="inner-nav-item ${currentTab == 'summary' ? 'active' : ''}" href="?tab=summary">Summary</a>
                                <a class="inner-nav-item ${currentTab == 'reputation' ? 'active' : ''}" href="?tab=reputation">Reputation</a>
                                <a class="inner-nav-item ${currentTab == 'badges' ? 'active' : ''}" href="?tab=badges">Badges</a>
                                <a class="inner-nav-item ${currentTab == 'privileges' ? 'active' : ''}" href="?tab=privileges">Privileges</a>
                            </nav>
                        </div>

                        <div class="col-md-10 ps-4">

                            <c:choose>

                                <%-- 1. TAB SUMMARY --%>
                                <c:when test="${currentTab == 'summary'}">
                                    <div class="row">
                                        <div class="col-md-4">
                                            <div class="summary-box">
                                                <h4>Reputation</h4>
                                                <i class="fa-solid fa-chart-line fa-3x text-secondary opacity-25 mt-3 mb-3"></i>
                                                <p style="font-size: 13px;">Reputation is how the community thanks you. When users upvote your posts, you earn reputation.</p>
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="summary-box">
                                                <h4>Badges</h4>
                                                <div style="background: #f8f9f9; padding: 10px; border: 1px solid #e3e6e8; border-radius: 3px; display: flex; align-items: center; margin-bottom: 10px;">
                                                    <span class="badge-dot gold"></span> <span class="me-3 fw-bold">${badgeCounts['gold']}</span>
                                                    <span class="badge-dot silver"></span> <span class="me-3 fw-bold">${badgeCounts['silver']}</span>
                                                    <span class="badge-dot bronze"></span> <span class="fw-bold">${badgeCounts['bronze']}</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:when>

                                <%-- 2. TAB REPUTATION HISTORY --%>
                                <c:when test="${currentTab == 'reputation'}">

                                    <div class="d-flex justify-content-between align-items-center mb-4 mt-2">
                                        <h3 class="fw-bold" style="font-size: 24px; margin-bottom: 0;">Reputation History</h3>
                                        <div class="text-muted">
                                            Total Reputation: <span class="fw-bold text-dark fs-5 ml-1"><i class="fa-solid fa-star text-warning"></i> ${userProfile.reputation}</span>
                                        </div>
                                    </div>

                                    <div class="card shadow-sm border-0" style="border-radius: 8px; overflow: hidden;">
                                        <div class="card-body p-0">
                                            <c:if test="${empty repList}">
                                                <div class="text-center text-muted py-5" style="background-color: #f8f9fa;">
                                                    <i class="fa-solid fa-clock-rotate-left fa-2x mb-2 d-block text-secondary"></i>
                                                    You have no reputation history yet. Keep contributing!
                                                </div>
                                            </c:if>

                                            <c:if test="${not empty repList}">
                                                <div class="list-group list-group-flush">
                                                    <c:forEach items="${repList}" var="rep">
                                                        <div class="list-group-item d-flex justify-content-between align-items-center py-3" style="transition: background-color 0.2s;">

                                                            <div class="d-flex align-items-center" style="flex: 1;">
                                                                <div style="width: 60px; flex-shrink: 0;">
                                                                    <c:choose>
                                                                        <c:when test="${rep.value > 0}">
                                                                            <span class="badge bg-success bg-opacity-10 text-success border border-success rounded-pill px-3 py-2 fw-bold" style="font-size: 14px;">
                                                                                +${rep.value}
                                                                            </span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="badge bg-danger bg-opacity-10 text-danger border border-danger rounded-pill px-3 py-2 fw-bold" style="font-size: 14px;">
                                                                                ${rep.value}
                                                                            </span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>

                                                                <div class="ms-3 text-dark fw-medium" style="font-size: 15px;">
                                                                    <c:choose>
                                                                        <c:when test="${rep.actionType == 'question_upvoted'}">
                                                                            <i class="fa-solid fa-arrow-up text-secondary me-2"></i> Your question was upvoted
                                                                        </c:when>
                                                                        <c:when test="${rep.actionType == 'answer_upvoted'}">
                                                                            <i class="fa-solid fa-arrow-up text-secondary me-2"></i> Your answer was upvoted
                                                                        </c:when>
                                                                        <c:when test="${rep.actionType == 'answer_accepted'}">
                                                                            <i class="fa-solid fa-check text-success me-2"></i> Your answer was accepted
                                                                        </c:when>
                                                                        <c:when test="${rep.actionType == 'question_downvoted'}">
                                                                            <i class="fa-solid fa-arrow-down text-danger me-2"></i> Your question was downvoted
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <i class="fa-solid fa-bolt text-warning me-2"></i> ${rep.actionType}
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                            </div>

                                                            <div class="text-muted small ms-3" style="white-space: nowrap;">
                                                                <i class="fa-regular fa-calendar me-1"></i> 
                                                                <fmt:formatDate value="${rep.createdAt}" pattern="MMM dd, yyyy"/>
                                                            </div>

                                                        </div>
                                                    </c:forEach>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:when>

                                <%-- 3. TAB BADGES --%>
                                <c:when test="${currentTab == 'badges'}">
                                    <div class="d-flex justify-content-between align-items-center mb-4 mt-2">
                                        <h3 class="fw-bold" style="font-size: 24px; margin-bottom: 0;">Earned Badges</h3>

                                        <form action="${pageContext.request.contextPath}/badge" method="get" class="d-flex align-items-center">
                                            <input type="hidden" name="tab" value="badges">
                                            <label for="sortBadges" class="me-2 text-muted small mb-0 text-nowrap">Sort by:</label>
                                            <select name="sort" id="sortBadges" class="form-select form-select-sm shadow-sm" onchange="this.form.submit()" style="width: auto; border-radius: 6px;">
                                                <option value="newest" ${currentSort == 'newest' ? 'selected' : ''}>Newest</option>
                                                <option value="name" ${currentSort == 'name' ? 'selected' : ''}>Name</option>
                                            </select>
                                        </form>
                                    </div>

                                    <c:if test="${empty myBadges}">
                                        <div class="empty-badge-box mt-4">
                                            <i class="fa-solid fa-medal fa-2x text-muted mb-2 d-block"></i>
                                            You haven't earned any badges yet. Keep participating to unlock them!
                                        </div>
                                    </c:if>

                                    <c:if test="${not empty myBadges}">

                                        <div class="badge-title-group mt-4">
                                            <h5 class="mb-0 fw-bold" style="color: #ab825f;">
                                                <i class="fa-solid fa-medal" style="color: #d1a684;"></i> Bronze Badges
                                            </h5>
                                        </div>
                                        <div class="row mb-4">
                                            <c:set var="hasBronze" value="false" />
                                            <c:forEach items="${myBadges}" var="badge">
                                                <c:if test="${badge.type.toLowerCase() == 'bronze'}">
                                                    <c:set var="hasBronze" value="true" />
                                                    <div class="col-md-3 col-sm-6 mb-3">
                                                        <div class="badge-item-card">
                                                            <span class="badge-dot bronze"></span>
                                                            <span class="fw-bold text-dark" style="font-size: 15px;">${badge.name}</span>

                                                            <div class="badge-custom-tooltip">
                                                                <div class="fw-bold mb-2 pb-1" style="font-size: 15px; border-bottom: 1px solid #4a4e51;">
                                                                    ${badge.name}
                                                                </div>
                                                                <div style="font-size: 13px; margin-bottom: 4px;">
                                                                    <span class="text-muted">Level:</span> <span class="text-capitalize fw-bold" style="color: #d1a684;">${badge.type}</span>
                                                                </div>
                                                                <div style="font-size: 13px; margin-bottom: 8px;">
                                                                    <span class="text-muted">Earned:</span> <fmt:formatDate value="${badge.earnedAt}" pattern="dd/MM/yyyy" />                                                                                                                                </div>
                                                                <div style="font-size: 13px; line-height: 1.4;">
                                                                    ${badge.description}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:if>
                                            </c:forEach>
                                            <c:if test="${!hasBronze}">
                                                <div class="col-12"><div class="empty-badge-box">No bronze badges earned yet.</div></div>
                                            </c:if>
                                        </div>

                                        <div class="badge-title-group mt-4">
                                            <h5 class="mb-0 fw-bold" style="color: #838c95;">
                                                <i class="fa-solid fa-medal" style="color: #b4b8bc;"></i> Silver Badges
                                            </h5>
                                        </div>
                                        <div class="row mb-4">
                                            <c:set var="hasSilver" value="false" />
                                            <c:forEach items="${myBadges}" var="badge">
                                                <c:if test="${badge.type.toLowerCase() == 'silver'}">
                                                    <c:set var="hasSilver" value="true" />
                                                    <div class="col-md-3 col-sm-6 mb-3">
                                                        <div class="badge-item-card">
                                                            <span class="badge-dot silver"></span>
                                                            <span class="fw-bold text-dark" style="font-size: 15px;">${badge.name}</span>

                                                            <div class="badge-custom-tooltip">
                                                                <div class="fw-bold mb-2 pb-1" style="font-size: 15px; border-bottom: 1px solid #4a4e51;">
                                                                    ${badge.name}
                                                                </div>
                                                                <div style="font-size: 13px; margin-bottom: 4px;">
                                                                    <span class="text-muted">Level:</span> <span class="text-capitalize fw-bold" style="color: #b4b8bc;">${badge.type}</span>
                                                                </div>
                                                                <div style="font-size: 13px; margin-bottom: 8px;">
                                                                    <span class="text-muted">Earned:</span> <fmt:formatDate value="${badge.earnedAt}" pattern="dd/MM/yyyy" />                                                                                                                            </div>
                                                                <div style="font-size: 13px; line-height: 1.4;">
                                                                    ${badge.description}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:if>
                                            </c:forEach>
                                            <c:if test="${!hasSilver}">
                                                <div class="col-12"><div class="empty-badge-box">No silver badges earned yet.</div></div>
                                            </c:if>
                                        </div>

                                        <div class="badge-title-group mt-4">
                                            <h5 class="mb-0 fw-bold" style="color: #d4a81e;">
                                                <i class="fa-solid fa-medal" style="color: #ffcc01;"></i> Gold Badges
                                            </h5>
                                        </div>
                                        <div class="row mb-4">
                                            <c:set var="hasGold" value="false" />
                                            <c:forEach items="${myBadges}" var="badge">
                                                <c:if test="${badge.type.toLowerCase() == 'gold'}">
                                                    <c:set var="hasGold" value="true" />
                                                    <div class="col-md-3 col-sm-6 mb-3">
                                                        <div class="badge-item-card">
                                                            <span class="badge-dot gold"></span>
                                                            <span class="fw-bold text-dark" style="font-size: 15px;">${badge.name}</span>

                                                            <div class="badge-custom-tooltip">
                                                                <div class="fw-bold mb-2 pb-1" style="font-size: 15px; border-bottom: 1px solid #4a4e51;">
                                                                    ${badge.name}
                                                                </div>
                                                                <div style="font-size: 13px; margin-bottom: 4px;">
                                                                    <span class="text-muted">Level:</span> <span class="text-capitalize fw-bold" style="color: #ffcc01;">${badge.type}</span>
                                                                </div>
                                                                <div style="font-size: 13px; margin-bottom: 8px;">
                                                                    <span class="text-muted">Earned:</span> <fmt:formatDate value="${badge.earnedAt}" pattern="dd/MM/yyyy" />                                                                </div>
                                                                <div style="font-size: 13px; line-height: 1.4;">
                                                                    ${badge.description}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:if>
                                            </c:forEach>
                                            <c:if test="${!hasGold}">
                                                <div class="col-12"><div class="empty-badge-box">No gold badges earned yet.</div></div>
                                            </c:if>
                                        </div>

                                    </c:if>
                                </c:when>

                                <%-- 4. TAB PRIVILEGES (Dữ liệu lấy từ bảng Privileges) --%>
                                <c:when test="${currentTab == 'privileges'}">
                                    <h3 style="font-size: 21px; margin-bottom: 10px;">Privileges</h3>
                                    <p class="text-muted mb-4">You unlock new privileges as you gain reputation.</p>

                                    <div class="card mb-4 border-0" style="background-color: #f8f9f9; border-radius: 5px;">
                                        <div class="card-body">
                                            <h5 class="card-title text-primary" style="font-size: 15px;">
                                                <i class="fa-solid fa-trophy text-warning"></i> Next Privilege
                                            </h5>

                                            <c:choose>
                                                <c:when test="${not empty isMaxLevel}">
                                                    <p class="text-success fw-bold mb-0 mt-2">🎉 Congratulations! You have unlocked all privileges on DevQuery!</p>
                                                </c:when>
                                                <c:otherwise>
                                                    <p class="mb-2 mt-2">
                                                        You need <strong>${pointsNeeded} more reputation</strong> to unlock: 
                                                        <span class="badge bg-warning text-dark">${nextPriv.name}</span>
                                                    </p>
                                                    <div class="progress" style="height: 20px; background-color: #e3e6e8;">
                                                        <div class="progress-bar progress-bar-striped progress-bar-animated bg-success" 
                                                             role="progressbar" 
                                                             style="width: ${progressPercent}%;" 
                                                             aria-valuenow="${progressPercent}" 
                                                             aria-valuemin="0" aria-valuemax="100">
                                                            ${userProfile.reputation} / ${nextPriv.requiredReputation} XP
                                                        </div>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>

                                    <table class="table activity-table table-borderless">
                                        <thead>
                                            <tr>
                                                <th style="width: 150px;">Reputation</th>
                                                <th>Privilege</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${privilegesList}" var="rule">
                                                <c:set var="isUnlocked" value="${userProfile.reputation >= rule.requiredReputation}" />

                                                <tr>
                                                    <td class="${isUnlocked ? 'fw-bold' : 'fw-bold text-muted'}">
                                                        ${rule.requiredReputation}
                                                    </td>
                                                    <td class="${isUnlocked ? '' : 'text-muted'}">
                                                        <c:choose>
                                                            <c:when test="${isUnlocked}">
                                                                <a href="#" style="text-decoration: none;">${rule.name}</a> 
                                                                <span class="text-muted d-block" style="font-size: 13px;">${rule.description}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <i class="fa-solid fa-lock me-2"></i> 
                                                                ${rule.name} 
                                                                <span class="text-muted d-block" style="font-size: 13px;">${rule.description}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>

                                            <c:if test="${empty privilegesList}">
                                                <tr>
                                                    <td colspan="2" class="text-center text-muted py-4">System rules have not been configured yet.</td>
                                                </tr>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </c:when>

                            </c:choose>

                        </div>
                    </div>

                </main>
            </div>
        </div>

    </body>
</html>