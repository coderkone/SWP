<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>${uPro.username} - User Profile - DevQuery</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        
        <style>
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

            * { box-sizing: border-box; margin: 0; padding: 0; }
            body { font-family: var(--font-stack); background-color: var(--bg-body); color: var(--black-text); font-size: 13px; }

            .container {
                max-width: 1264px;
                margin: 56px auto 0;
                display: flex;
                align-items: flex-start;
            }

            .left-sidebar {
                width: 164px;
                flex-shrink: 0;
                padding-top: 25px;
                border-right: 1px solid var(--border-color);
                min-height: calc(100vh - 56px);
            }

            .main-content {
                flex-grow: 1;
                padding: 24px;
                width: 100%;
            }

            /* --- LAYOUT HEADER MỚI (3 CỘT) --- */
            .profile-header { 
                display: flex; 
                gap: 24px; 
                margin-bottom: 24px; 
                align-items: flex-start; /* Căn hàng trên cùng */
            }

            /* Cột 1: Avatar */
            .avatar-container .profile-avatar { 
                width: 128px; 
                height: 128px; 
                object-fit: cover; 
                border-radius: 5px; 
                box-shadow: 0 1px 2px rgba(0,0,0,0.1); 
            }

            /* Cột 2: Thông tin giữa (Flex grow để đẩy cột 3 sang phải) */
            .user-details {
                flex-grow: 1;
            }
            .user-details h2 { font-size: 34px; margin-bottom: 4px; font-weight: 400; line-height: 1; }
            .user-meta { list-style: none; margin-top: 8px; color: var(--gray-sub); }
            .user-meta li { margin-bottom: 4px; display: flex; align-items: center; gap: 6px; }
            .user-meta a { color: var(--gray-sub); text-decoration: none; }
            .user-meta a:hover { color: var(--blue-link); }

            /* Cột 3: Cột bên phải (Chứa nút Edit và Stats) */
            .header-right {
                display: flex;
                flex-direction: column;
                align-items: flex-end; /* Căn lề phải toàn bộ nội dung */
                gap: 12px; /* Khoảng cách giữa nút Edit và Stats */
                min-width: 200px;
            }

            /* Nút Edit Profile */
            .btn-edit-profile {
                color: var(--gray-text); 
                border: 1px solid var(--gray-text); 
                padding: 6px 12px; 
                border-radius: 3px; 
                text-decoration: none;
                font-size: 12px;
                transition: all 0.2s;
            }
            .btn-edit-profile:hover {
                background-color: #f8f9f9;
                color: var(--black-text);
            }

            /* Ô chỉ số Stats */
            .stats-grid { display: flex; gap: 10px; }
            .stat-item { 
                border: 1px solid var(--border-color); 
                border-radius: 5px; 
                padding: 8px 12px; 
                text-align: center; 
                min-width: 80px; 
            }
            .stat-count { font-size: 17px; font-weight: bold; display: block; color: var(--black-text); }
            .stat-label { font-size: 11px; text-transform: uppercase; color: var(--gray-sub); margin-top: 2px; }

            .profile-tabs { display: flex; margin-bottom: 24px; margin-top: 30px; border-bottom: 1px solid var(--border-color); }
            .tab-link { padding: 8px 16px; text-decoration: none; color: var(--gray-text); border-radius: 1000px; margin-right: 4px; }
            .tab-link.active { background-color: #f48225; color: white; }
            .tab-link:hover:not(.active) { background-color: #e3e6e8; }

            .content-grid { display: flex; gap: 24px; }
            .col-half { flex: 1; }
            .panel { border: 1px solid var(--border-color); border-radius: 5px; padding: 12px; height: 100%; }
            .panel-header { font-size: 21px; font-weight: 400; margin-bottom: 12px; }
            
            .tag-badge { background: var(--blue-tag-bg); color: var(--blue-tag-text); padding: 4px 6px; border-radius: 3px; text-decoration: none; display: inline-block; margin: 2px; }
            .post-link { display: block; padding: 8px 0; border-bottom: 1px solid #eff0f1; text-decoration: none; color: var(--blue-link); }
            .post-link:last-child { border-bottom: none; }
            .post-score { display: inline-block; min-width: 24px; text-align: center; background: #5eba7d; color: white; padding: 2px 5px; border-radius: 3px; font-size: 11px; margin-right: 8px; }
            .post-score.zero { background: #e1ecf4; color: var(--blue-tag-text); }
        </style>
    </head>
    <body>
        
        <jsp:include page="../Common/header.jsp"></jsp:include>

        <div class="container">
            
            <div class="left-sidebar">
                <jsp:include page="../Common/sidebar.jsp">
                    <jsp:param name="page" value="profile"/>
                </jsp:include>
            </div>

            <main class="main-content">
                <div class="profile-header">
                    <div class="avatar-container">
                        <img src="${uPro.displayAvatar}" alt="${uPro.username}" class="profile-avatar">
                    </div>

                    <div class="user-details">
                        <h2>${uPro.username}</h2>
                        
                        <div style="margin-top: 8px; font-size: 1.1rem; color: var(--black-text);">
                            <c:choose>
                                <c:when test="${not empty uPro.bio}">
                                    ${uPro.bio}
                                </c:when>
                                <c:otherwise>
                                    <span style="color: var(--gray-sub); font-style: italic;">No bio yet.</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        
                        <ul class="user-meta">
                            <li>
                                <i class="fa fa-birthday-cake"></i> 
                                Member since <fmt:formatDate value="${uPro.createdAt}" pattern="MMM dd, yyyy"/>
                            </li>
                            <c:if test="${not empty uPro.location}">
                                <li><i class="fa fa-map-marker"></i> ${uPro.location}</li>
                            </c:if>
                            <c:if test="${not empty uPro.website}">
                                <li><i class="fa fa-link"></i> <a href="${uPro.website}" target="_blank">${uPro.website}</a></li>
                            </c:if>
                        </ul>
                    </div>

                    <div class="header-right">
                        
                        <c:if test="${sessionScope.user.userId == uPro.userId}">
                            <a href="profile-edit" class="btn-edit-profile">
                                <i class="fa fa-pencil"></i> Edit profile
                            </a>
                        </c:if>

                        <div class="stats-grid">
                            <div class="stat-item">
                                <span class="stat-count">${uPro.reputation}</span>
                                <span class="stat-label">Reputation</span>
                            </div>
                            <div class="stat-item">
                                <span class="stat-count">0</span>
                                <span class="stat-label">Reached</span>
                            </div>
                        </div>

                    </div>
                    
                </div>
                <div class="profile-tabs">
                    <a href="#" class="tab-link active">Profile</a>
                    <a href="#" class="tab-link">Activity</a>
                    <a href="../DevQuery/saves" class="tab-link">Saves</a>
                    <a href="#" class="tab-link">Settings</a>
                </div>

                <div class="content-grid">
                    
                    <div class="col-half">
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

                    <div class="col-half">
                        <h3 class="panel-header">Top Posts</h3>
                        <div class="panel">
                            <a href="#" class="post-link">
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
    </body>
</html>