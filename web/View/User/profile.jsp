<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>${uPro.username} - User Profile - DevQuery</title>
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
            .user-bio {
                font-size: 15px;
                color: #232629;
                margin-top: 8px;
                margin-bottom: 8px;
            }

            /* User Meta Details */
            .user-meta {
                list-style: none;
                padding: 0;
                margin: 0;
                color: #6a737c;
                font-size: 13px;
            }
            .user-meta li {
                display: inline-flex;
                align-items: center;
                margin-right: 15px;
                margin-bottom: 4px;
                gap: 6px;
            }
            .user-meta a {
                color: #6a737c;
                text-decoration: none;
            }
            .user-meta a:hover {
                color: #0074cc;
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

            /* Buttons & Stats (Right Side) */
            .btn-edit-profile {
                color: #525960;
                border: 1px solid #9fa6ad;
                padding: 6px 12px;
                border-radius: 3px;
                text-decoration: none;
                font-size: 12px;
                transition: all 0.2s;
                background: white;
            }
            .btn-edit-profile:hover {
                background-color: #f8f9f9;
                color: #0c0d0e;
            }

            .stats-grid {
                display: flex;
                gap: 10px;
            }
            .stat-item {
                border: 1px solid #d6d9dc;
                border-radius: 5px;
                padding: 8px 12px;
                text-align: center;
                min-width: 80px;
            }
            .stat-count {
                font-size: 17px;
                font-weight: bold;
                display: block;
                color: #0c0d0e;
            }
            .stat-label {
                font-size: 11px;
                text-transform: uppercase;
                color: #6a737c;
                margin-top: 2px;
            }

            /* Content Panels */
            .panel-header {
                font-size: 21px;
                font-weight: normal;
                margin-bottom: 12px;
            }
            .panel {
                border: 1px solid #d6d9dc;
                border-radius: 5px;
                padding: 15px;
                height: 100%;
            }

            /* Tags & Posts */
            .tag-badge {
                background: #e1ecf4;
                color: #39739d;
                padding: 4px 6px;
                border-radius: 3px;
                text-decoration: none;
                display: inline-block;
                margin: 2px;
                font-size: 12px;
            }
            .tag-badge:hover {
                background: #d0e3f1;
                color: #2c5877;
            }

            .post-link {
                display: flex;
                align-items: center;
                padding: 8px 0;
                border-bottom: 1px solid #eff0f1;
                text-decoration: none;
                color: #0074cc;
                font-size: 14px;
            }
            .post-link:last-child {
                border-bottom: none;
            }
            .post-link:hover {
                color: #0a95ff;
            }
            .post-score {
                min-width: 38px;
                text-align: center;
                background: #5eba7d;
                color: white;
                padding: 3px 5px;
                border-radius: 3px;
                font-size: 12px;
                margin-right: 12px;
                font-weight: bold;
            }
            .post-score.zero {
                background: transparent;
                color: #6a737c;
                border: 1px solid #d6d9dc;
                font-weight: normal;
            }
            .badge-dashboard {
                background: #ffffff;
                border: 1px solid #e9ecef;
                border-radius: 12px;
                padding: 24px;
                box-shadow: 0 4px 6px rgba(0,0,0,0.02);
            }

            .sticker-box {
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                padding: 20px 10px;
                border-radius: 12px;
                transition: transform 0.2s ease, box-shadow 0.2s ease;
                height: 100%;
            }
            .sticker-box:hover {
                transform: translateY(-4px);
                box-shadow: 0 8px 15px rgba(0,0,0,0.05);
            }

            /* Màu Gradient cho từng Rank */
            .sticker-gold {
                background: linear-gradient(135deg, #fffcf0, #fef4cb);
                border: 1px solid #fde073;
            }
            .sticker-silver {
                background: linear-gradient(135deg, #f8f9fa, #e9ecef);
                border: 1px solid #ced4da;
            }
            .sticker-bronze {
                background: linear-gradient(135deg, #fdf8f5, #f5e2d3);
                border: 1px solid #e8bba1;
            }

            .sticker-icon {
                font-size: 2.5rem;
                margin-bottom: 8px;
            }
            .sticker-count {
                font-size: 1.8rem;
                font-weight: 700;
                color: #212529;
                line-height: 1.2;
            }
            .sticker-label {
                font-size: 0.85rem;
                color: #6c757d;
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            /* Các Chip hiển thị tên danh hiệu */
            .achievement-chip {
                display: inline-flex;
                align-items: center;
                padding: 6px 14px;
                border-radius: 20px;
                font-size: 0.85rem;
                font-weight: 500;
                margin: 0 8px 10px 0;
                background-color: #f8f9fa;
                border: 1px solid #e9ecef;
                color: #495057;
            }
            .achievement-chip i {
                margin-right: 6px;
                font-size: 1rem;
            }
            .chip-gold i {
                color: #f1c40f;
            }
            .chip-silver i {
                color: #adb5bd;
            }
            .chip-bronze i {
                color: #d35400;
            }
        </style>
    </head>
    <body>

        <jsp:include page="../Common/header.jsp"></jsp:include>

            <div class="container-fluid" style="max-width: 1264px; margin: 0 auto;">
                <div class="row">
                    <nav class="col-md-2 d-none d-md-block bg-light sidebar p-0 pt-4" style="border-right: 1px solid #d6d9dc; min-height: 100vh;">
                    <jsp:include page="../Common/sidebar.jsp" />
                </nav>

                <main class="col-md-10 px-md-4 pt-4">
                    <jsp:include page="../Common/profileTemplate.jsp">
                        <jsp:param name="activeTab" value="profile" />
                    </jsp:include>

                    <div class="row">

                        <div class="col-md-3">
                            <h3 style="font-size: 21px; margin-bottom: 16px; font-weight: 600; color: #2b2d42;">Stats</h3>
                            <div class="badge-dashboard mb-4" style="padding: 20px 15px;">
                                <div class="row g-3 text-center">
                                    <div class="col-6">
                                        <div style="font-size: 17px; font-weight: bold; color: #212529;">${uPro.reputation}</div>
                                        <div style="font-size: 11px; color: #6c757d; text-transform: uppercase; font-weight: 600;">reputation</div>
                                    </div>
                                    <div class="col-6">
                                        <div style="font-size: 17px; font-weight: bold; color: #212529;">${viewCount != null ? viewCount : 0}</div>
                                        <div style="font-size: 11px; color: #6c757d; text-transform: uppercase; font-weight: 600;">views</div>
                                    </div>
                                    <div class="col-6">
                                        <div style="font-size: 17px; font-weight: bold; color: #212529;">${answersCount != null ? answersCount : 0}</div>
                                        <div style="font-size: 11px; color: #6c757d; text-transform: uppercase; font-weight: 600;">answers</div>
                                    </div>
                                    <div class="col-6">
                                        <div style="font-size: 17px; font-weight: bold; color: #212529;">${questionsCount != null ? questionsCount : 0}</div>
                                        <div style="font-size: 11px; color: #6c757d; text-transform: uppercase; font-weight: 600;">questions</div>
                                    </div>
                                </div>
                            </div>
                            <h3 style="font-size: 21px; margin-bottom: 16px; font-weight: 600; color: #2b2d42;">Links</h3>
                            <div class="badge-dashboard mb-4" style="padding: 15px;">
                                <c:choose>
                                    <c:when test="${not empty userLinks}">
                                        <ul class="list-unstyled mb-0" style="font-size: 14px;">
                                            <c:forEach var="link" items="${userLinks}">
                                                <%-- Chỉ hiển thị những link người dùng có nhập dữ liệu --%>
                                                <c:if test="${not empty link.value}">
                                                    <li class="mb-2">
                                                        <c:choose>
                                                            <c:when test="${link.key == 'github'}">
                                                                <i class="fa-brands fa-github me-2 text-dark"></i>
                                                                <a href="${link.value}" target="_blank" class="text-decoration-none" style="color: #0074cc;">GitHub</a>
                                                            </c:when>
                                                            <c:when test="${link.key == 'linkedin'}">
                                                                <i class="fa-brands fa-linkedin me-2" style="color: #0077b5;"></i>
                                                                <a href="${link.value}" target="_blank" class="text-decoration-none" style="color: #0074cc;">LinkedIn</a>
                                                            </c:when>
                                                            <c:when test="${link.key == 'website'}">
                                                                <i class="fa-solid fa-link me-2 text-muted"></i>
                                                                <a href="${link.value}" target="_blank" class="text-decoration-none" style="color: #0074cc;">Website</a>
                                                            </c:when>
                                                        </c:choose>
                                                    </li>
                                                </c:if>
                                            </c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted mb-0" style="font-size: 13px;">No links added.</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>            
                        </div>

                        <div class="col-md-9">
                            <h3 style="font-size: 21px; margin-bottom: 16px; font-weight: 600; color: #2b2d42;">About</h3>
                            <div class="badge-dashboard mb-4" style="text-align: left; min-height: 150px;">
                                <c:choose>
                                    <c:when test="${not empty uPro.bio}">
                                        <div style="color: #495057; font-size: 15px; line-height: 1.6; white-space: pre-wrap; word-break: break-word;"><c:out value="${uPro.bio}" />
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center py-3">
                                            <p class="text-muted mb-3">Your about me section is currently blank.</p>
                                            <a href="${pageContext.request.contextPath}/edit-profile" class="btn btn-outline-primary btn-sm">Edit Profile</a>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <h3 style="font-size: 21px; margin-bottom: 16px; font-weight: 600; color: #2b2d42;">Achievements</h3>
                            <div class="badge-dashboard mb-5">
                                <div class="row g-3 mb-4">
                                    <div class="col-4">
                                        <div class="sticker-box sticker-gold">
                                            <i class="fa-solid fa-medal sticker-icon" style="color: #f1c40f;"></i>
                                            <span class="sticker-count">${empty goldBadges ? 0 : fn:length(goldBadges)}</span>
                                            <span class="sticker-label">Gold</span>
                                        </div>
                                    </div>
                                    <div class="col-4">
                                        <div class="sticker-box sticker-silver">
                                            <i class="fa-solid fa-medal sticker-icon" style="color: #adb5bd;"></i>
                                            <span class="sticker-count">${empty silverBadges ? 0 : fn:length(silverBadges)}</span>
                                            <span class="sticker-label">Silver</span>
                                        </div>
                                    </div>
                                    <div class="col-4">
                                        <div class="sticker-box sticker-bronze">
                                            <i class="fa-solid fa-medal sticker-icon" style="color: #d35400;"></i>
                                            <span class="sticker-count">${empty bronzeBadges ? 0 : fn:length(bronzeBadges)}</span>
                                            <span class="sticker-label">Bronze</span>
                                        </div>
                                    </div>
                                </div>

                                <div class="achievement-details border-top pt-4">
                                    <c:choose>
                                        <c:when test="${empty goldBadges && empty silverBadges && empty bronzeBadges}">
                                            <div class="text-center py-3">
                                                <p class="text-muted mb-3">You haven't earned any badges yet. Keep participating to unlock achievements!</p>
                                                <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-primary btn-sm">Explore Community</a>
                                            </div>
                                        </c:when>

                                        <c:otherwise>
                                            <c:if test="${not empty goldBadges}">
                                                <div class="mb-3">
                                                    <c:forEach items="${goldBadges}" var="b">
                                                        <span class="achievement-chip chip-gold" title="${b.description}">
                                                            <i class="fa-solid fa-award"></i> ${b.name}
                                                        </span>
                                                    </c:forEach>
                                                </div>
                                            </c:if>

                                            <c:if test="${not empty silverBadges}">
                                                <div class="mb-3">
                                                    <c:forEach items="${silverBadges}" var="b">
                                                        <span class="achievement-chip chip-silver" title="${b.description}">
                                                            <i class="fa-solid fa-award"></i> ${b.name}
                                                        </span>
                                                    </c:forEach>
                                                </div>
                                            </c:if>

                                            <c:if test="${not empty bronzeBadges}">
                                                <div class="mb-1">
                                                    <c:forEach items="${bronzeBadges}" var="b">
                                                        <span class="achievement-chip chip-bronze" title="${b.description}">
                                                            <i class="fa-solid fa-award"></i> ${b.name}
                                                        </span>
                                                    </c:forEach>
                                                </div>
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                        </div>
                    </div>
                </main>
            </div>
        </div>
    </body>
</html>