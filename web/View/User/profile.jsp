<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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

                    <div class="dd-flex align-items-start mb-4">

                        <div class="d-flex">
                            <div class="me-4">
                                <img src="${sessionScope.user.avatarUrl != null ? sessionScope.user.avatarUrl : 'https://cdn-icons-png.flaticon.com/512/3135/3135715.png'}" 
                                     class="user-avatar-lg" alt="Avatar">
                            </div>
                            <div class="flex-grow-1">
                                <h1 class="user-name">${uPro.username}</h1>
                                <div class="user-meta mb-3">
                                    <c:choose>
                                        <c:when test="${not empty uPro.bio}">
                                            ${uPro.bio}
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted fst-italic">No bio yet.</span>
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="mx-2">|</span>      
                                    <i class="fa-solid fa-cake-candles"></i> Member since ${userProfile.createdAt}
                                    <span class="mx-2">|</span> 
                                    <i class="fa-solid fa-star text-warning"></i> ${userProfile.reputation} reputation
                                </div>
                                <ul class="nav profile-tabs">
                                    <li class="nav-item"><a class="nav-link active" href="#">Profile</a></li>
                                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/activity">Activity</a></li>
                                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/saves">Saves</a></li>
                                    <li class="nav-item"><a class="nav-link" href="#">Settings</a></li>
                                </ul>

                            </div>
                        </div>
                    </div>


                    <div class="row">
                        <div class="col-md-6 mb-4">
                            <h3 class="panel-header">Top Tags</h3>
                            <div class="panel">
                                <c:if test="${true}"> 
                                    <div>
                                        <a href="#" class="tag-badge">java</a>
                                        <a href="#" class="tag-badge">spring-boot</a>
                                        <a href="#" class="tag-badge">jsp</a>
                                        <a href="#" class="tag-badge">servlet</a>
                                    </div>
                                </c:if>
                            </div>
                        </div>

                        <div class="col-md-6 mb-4">
                            <h3 class="panel-header">Top Posts</h3>
                            <div class="panel p-0 px-3"> <a href="#" class="post-link">
                                    <span class="post-score">5</span>
                                    How to fix NullPointerException in Java?
                                </a>
                                <a href="#" class="post-link">
                                    <span class="post-score zero">0</span>
                                    Understanding Servlet Lifecycle
                                </a>
                            </div>
                        </div>
                    </div>

                </main>
            </div>
        </div>

    </body>
</html>