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
                                <i class="fa-solid fa-cake-candles"></i> Member since ${userProfile.createdAt}
                                <span class="mx-2">|</span> 
                                <i class="fa-solid fa-star text-warning"></i> ${userProfile.reputation} reputation
                            </div>

                            <ul class="nav profile-tabs">
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/profile">Profile</a></li>
                                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/activity">Activity</a></li>
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

                                <%-- 2. TAB REPUTATION --%>
                                <c:when test="${currentTab == 'reputation'}">
                                    <h3 style="font-size: 21px; margin-bottom: 20px;">Reputation History</h3>
                                    <table class="table activity-table table-borderless">
                                        <tbody>
                                            <c:forEach items="${repList}" var="rep">
                                                <tr>
                                                    <td style="width: 80px;">
                                                        <span class="rep-score ${rep.value < 0 ? 'negative' : ''}">
                                                            ${rep.value > 0 ? '+' : ''}${rep.value}
                                                        </span>
                                                    </td>
                                                    <td>${rep.actionType}</td>
                                                    <td class="text-end text-muted">
                                                        <fmt:formatDate value="${rep.createdAt}" pattern="MMM dd, yyyy" />
                                                    </td>
                                                </tr>
                                            </c:forEach>

                                            <c:if test="${empty repList}">
                                                <tr>
                                                    <td colspan="3" class="text-center text-muted py-4">You have no reputation changes yet.</td>
                                                </tr>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </c:when>

                                <%-- 3. TAB BADGES --%>
                                <c:when test="${currentTab == 'badges'}">
                                    <h3 style="font-size: 21px; margin-bottom: 20px;">Earned Badges</h3>
                                    <div class="row">
                                        <c:forEach items="${myBadges}" var="badge">
                                            <div class="col-md-3 mb-3">
                                                <div style="border: 1px solid #e3e6e8; padding: 15px; border-radius: 5px; text-align: center;">
                                                    <div style="font-size: 40px; margin-bottom: 10px;
                                                         color: ${badge.type == 'Gold' ? '#ffcc01' : (badge.type == 'Silver' ? '#b4b8bc' : '#d1a684')};">
                                                        <i class="fa-solid fa-medal"></i>
                                                    </div>

                                                    <span style="background: #f1f2f3; border: 1px solid #d6d9dc; padding: 3px 8px; border-radius: 3px; font-size: 12px; font-weight: bold;" title="${badge.description}">
                                                        <span class="badge-dot ${badge.type.toLowerCase()}"></span> ${badge.name}
                                                    </span>
                                                </div>
                                            </div>
                                        </c:forEach>

                                        <c:if test="${empty myBadges}">
                                            <div class="col-12">
                                                <p class="text-muted">You haven't earned any badges yet. Keep participating!</p>
                                            </div>
                                        </c:if>
                                    </div>
                                </c:when>

                                <%-- 4. TAB PRIVILEGES (Dữ liệu lấy từ System_Rules) --%>
                                <c:when test="${currentTab == 'privileges'}">
                                    <h3 style="font-size: 21px; margin-bottom: 10px;">Privileges</h3>
                                    <p class="text-muted mb-4">You unlock new privileges as you gain reputation.</p>

                                    <table class="table activity-table table-borderless">
                                        <thead>
                                            <tr>
                                                <th style="width: 150px;">Reputation</th>
                                                <th>Privilege</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${privilegesList}" var="rule">
                                                <c:set var="isUnlocked" value="${sessionScope.user.reputation >= rule.requiredReputation}" />

                                                <tr>
                                                    <td class="${isUnlocked ? 'fw-bold' : 'fw-bold text-muted'}">
                                                        ${rule.requiredReputation}
                                                    </td>

                                                    <td class="${isUnlocked ? '' : 'text-muted'}">
                                                        <c:choose>
                                                            <c:when test="${isUnlocked}">
                                                                <a href="#" style="text-decoration: none;">${rule.privilegeDescription}</a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <i class="fa-solid fa-lock me-2"></i> 
                                                                ${rule.privilegeDescription} <span style="font-size: 12px;">(You need ${rule.requiredReputation} rep)</span>
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