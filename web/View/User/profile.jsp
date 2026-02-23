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
                    <a href="#" class="tab-link">Saves</a>
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
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="dto.UserDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ sơ - DevQuery</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Liberation Sans", sans-serif;
        }

        body {
            background-color: #f1f2f3;
            color: #3b4045;
        }

        /* Header */
        header {
            background-color: white;
            height: 53px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            box-shadow: 0 1px 2px rgba(0,0,0,0.05);
            border-top: 3px solid #f48024;
            position: fixed;
            top: 0;
            width: 100%;
            z-index: 1000;
            padding: 0 10px;
        }

        .header-left {
            display: flex;
            align-items: center;
        }

        .menu-btn {
            background: none;
            border: none;
            cursor: pointer;
            padding: 0 15px;
            font-size: 18px;
            color: #525960;
            transition: color 0.2s;
        }

        .menu-btn:hover {
            color: #232629;
        }

        .logo {
            display: flex;
            align-items: center;
            margin-left: 5px;
            text-decoration: none;
            color: black;
        }

        .logo i {
            color: #f48024;
            font-size: 24px;
            margin-right: 5px;
        }

        .logo span {
            font-size: 18px;
            font-weight: 400;
        }

        .logo span b {
            font-weight: 700;
        }

        .search-box {
            flex: 1;
            max-width: 600px;
            margin: 0 20px;
        }

        .search-input {
            width: 100%;
            padding: 8px 12px;
            font-size: 13px;
            border: 1px solid #c8ccd0;
            border-radius: 4px;
            background-color: #f8f9fa;
        }

        .search-input:focus {
            background-color: white;
            border-color: #0a95ff;
            outline: none;
        }

        .header-right {
            padding-right: 15px;
            font-size: 13px;
        }

        .header-right a {
            text-decoration: none;
            color: #525960;
            padding: 8px 12px;
            border-radius: 1000px;
        }

        .header-right a:hover {
            background-color: #e3e6e8;
        }

        /* Sidebar */
        .sidebar {
            position: fixed;
            top: 53px;
            left: -240px;
            width: 240px;
            height: calc(100vh - 53px);
            background-color: white;
            box-shadow: 1px 0 3px rgba(0,0,0,0.05);
            transition: left 0.3s ease;
            padding-top: 20px;
            overflow-y: auto;
            border-right: 1px solid #e3e6e8;
            z-index: 999;
        }

        .sidebar.active {
            left: 0;
        }

        .nav-list {
            list-style: none;
            padding: 0;
        }

        .nav-link {
            display: flex;
            align-items: center;
            padding: 10px 15px;
            color: #525960;
            text-decoration: none;
            font-size: 14px;
            transition: all 0.2s;
        }

        .nav-link:hover {
            color: #232629;
            background-color: #f1f2f3;
            border-right: 3px solid #f48024;
        }

        .nav-link i {
            width: 20px;
            text-align: center;
            margin-right: 10px;
            font-size: 15px;
        }

        /* Main Content */
        .container {
            max-width: 1200px;
            margin: 53px auto 0;
            padding: 0 16px;
            display: flex;
            gap: 32px;
            padding-top: 30px;
        }

        .main-content {
            flex: 1;
        }

        .profile-header {
            background: white;
            border: 1px solid #d6d9dc;
            border-radius: 6px;
            padding: 24px;
            margin-bottom: 24px;
            display: flex;
            gap: 20px;
            align-items: flex-start;
        }

        .profile-avatar {
            width: 128px;
            height: 128px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 48px;
            flex-shrink: 0;
        }

        .profile-info {
            flex: 1;
        }

        .profile-name {
            font-size: 28px;
            font-weight: bold;
            margin-bottom: 8px;
            color: #232629;
        }

        .profile-email {
            font-size: 14px;
            color: #6a737c;
            margin-bottom: 16px;
        }

        .profile-stats {
            display: flex;
            gap: 24px;
            margin-bottom: 16px;
        }

        .stat-item {
            display: flex;
            flex-direction: column;
        }

        .stat-value {
            font-size: 18px;
            font-weight: bold;
            color: #0a95ff;
        }

        .stat-label {
            font-size: 12px;
            color: #6a737c;
            text-transform: uppercase;
        }

        .profile-actions {
            display: flex;
            gap: 10px;
        }

        .btn {
            padding: 10px 16px;
            border-radius: 6px;
            border: 1px solid #c8ccd0;
            background: white;
            cursor: pointer;
            font-size: 14px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            transition: all 0.2s;
        }

        .btn:hover {
            background-color: #f8f9fa;
            border-color: #9199a3;
        }

        .btn-primary {
            background: #0a95ff;
            color: white;
            border-color: #0a95ff;
        }

        .btn-primary:hover {
            background: #0074cc;
            border-color: #0074cc;
        }

        /* Tabs */
        .tabs {
            display: flex;
            gap: 0;
            border-bottom: 1px solid #d6d9dc;
            margin-bottom: 24px;
            background: white;
            border-radius: 6px 6px 0 0;
            padding: 0 16px;
        }

        .tab-link {
            padding: 12px 16px;
            color: #6a737c;
            text-decoration: none;
            cursor: pointer;
            border-bottom: 3px solid transparent;
            transition: all 0.2s;
            font-size: 14px;
        }

        .tab-link.active {
            color: #0a95ff;
            border-bottom-color: #0a95ff;
        }

        .tab-link:hover {
            color: #232629;
        }

        /* Questions List */
        .questions-list {
            background: white;
            border: 1px solid #d6d9dc;
            border-radius: 6px;
            overflow: hidden;
        }

        .question-item {
            padding: 16px;
            border-bottom: 1px solid #e2e3e4;
            display: flex;
            gap: 16px;
            cursor: pointer;
            transition: background 0.2s;
        }

        .question-item:hover {
            background-color: #f8f9fa;
        }

        .question-item:last-child {
            border-bottom: none;
        }

        .question-stats {
            display: flex;
            flex-direction: column;
            align-items: center;
            min-width: 60px;
        }

        .stat-box {
            text-align: center;
            margin-bottom: 8px;
        }

        .stat-number {
            font-size: 18px;
            font-weight: bold;
            color: #232629;
        }

        .stat-name {
            font-size: 11px;
            color: #6a737c;
            text-transform: uppercase;
        }

        .question-content {
            flex: 1;
        }

        .question-title {
            font-size: 15px;
            color: #0a95ff;
            margin-bottom: 6px;
            text-decoration: none;
            cursor: pointer;
        }

        .question-title:hover {
            color: #0074cc;
        }

        .question-excerpt {
            font-size: 13px;
            color: #6a737c;
            margin-bottom: 8px;
            line-height: 1.4;
        }

        .question-meta {
            font-size: 12px;
            color: #9199a3;
        }

        .tag-badge {
            display: inline-block;
            background: #e1ecf4;
            border: 1px solid #bcd0e2;
            color: #3b4045;
            padding: 3px 6px;
            border-radius: 3px;
            font-size: 12px;
            margin-right: 4px;
            margin-bottom: 4px;
        }

        /* Sidebar Content */
        .sidebar-content {
            width: 280px;
        }

        .card {
            background: white;
            border: 1px solid #d6d9dc;
            border-radius: 6px;
            padding: 16px;
            margin-bottom: 16px;
        }

        .card-title {
            font-size: 13px;
            font-weight: bold;
            color: #3b4045;
            margin-bottom: 12px;
            text-transform: uppercase;
        }

        .badge-list {
            display: flex;
            flex-wrap: wrap;
            gap: 6px;
        }

        .badge {
            display: inline-block;
            background: #e1ecf4;
            border: 1px solid #bcd0e2;
            color: #3b4045;
            padding: 6px 8px;
            border-radius: 3px;
            font-size: 12px;
            cursor: pointer;
            transition: all 0.2s;
        }

        .badge:hover {
            background: #d0e1f7;
            border-color: #a8c5e0;
        }

        .empty-state {
            text-align: center;
            padding: 40px 20px;
            color: #6a737c;
        }

        .empty-state i {
            font-size: 48px;
            color: #d6d9dc;
            margin-bottom: 16px;
            display: block;
        }

    </style>
</head>
<body>

<header>
    <div class="header-left">
        <button class="menu-btn" onclick="toggleMenu()">
            <i class="fa-solid fa-bars"></i>
        </button>
        <a href="${pageContext.request.contextPath}/home" class="logo">
            <i class="fa-brands fa-stack-overflow"></i>
            <span>Dev<b>Query</b></span>
        </a>
    </div>

    <div class="search-box">
        <form method="get" action="${pageContext.request.contextPath}/home">
            <input type="text" name="q" class="search-input" placeholder="Search...">
        </form>
    </div>

    <div class="header-right">
        <a href="<%=request.getContextPath()%>/logout">Log out</a>
    </div>
</header>

<div class="sidebar" id="sidebar">
    <ul class="nav-list">
        <li><a href="${pageContext.request.contextPath}/home" class="nav-link"><i class="fa-solid fa-house"></i> Home</a></li>
        <li><a href="#" class="nav-link"><i class="fa-solid fa-earth-americas"></i> Questions</a></li>
        <li><a href="#" class="nav-link"><i class="fa-solid fa-tags"></i> Tags</a></li>
        <li><a href="#" class="nav-link"><i class="fa-solid fa-bookmark"></i> Saves</a></li>
        <li><a href="#" class="nav-link"><i class="fa-solid fa-message"></i> Chat</a></li>
    </ul>
</div>

<div class="container">
    <div class="main-content">
        <!-- Profile Header -->
        <div class="profile-header">
            <div class="profile-avatar">
                <i class="fa-solid fa-user"></i>
            </div>
            <div class="profile-info">
                <div class="profile-name">${profileUser.username}</div>
                <div class="profile-email">${profileUser.email}</div>
                <div class="profile-stats">
                    <div class="stat-item">
                        <div class="stat-value">${questionCount}</div>
                        <div class="stat-label">Questions</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value">0</div>
                        <div class="stat-label">Answers</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value">0</div>
                        <div class="stat-label">Reputation</div>
                    </div>
                </div>
                <div class="profile-actions">
                    <a href="#" class="btn">
                        <i class="fa-solid fa-edit"></i> Edit Profile
                    </a>
                </div>
            </div>
        </div>

        <!-- Tabs -->
        <div class="tabs">
            <a class="tab-link active">All Questions</a>
            <a class="tab-link">Answers</a>
            <a class="tab-link">Saves</a>
        </div>

        <!-- Questions List -->
        <div class="questions-list">
            <% 
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> userQuestions = (List<Map<String, Object>>) request.getAttribute("userQuestions");
                
                if (userQuestions != null && !userQuestions.isEmpty()) {
                    for (Map<String, Object> q : userQuestions) {
                        long questionId = ((Number) q.get("question_id")).longValue();
                        String title = (String) q.get("title");
                        String excerpt = (String) q.get("body");
                        if (excerpt != null && excerpt.length() > 100) {
                            excerpt = excerpt.substring(0, 100) + "...";
                        }
                        int viewCount = ((Number) q.get("view_count")).intValue();
                        String createdAt = (String) q.get("created_at");
            %>
                <div class="question-item">
                    <div class="question-stats">
                        <div class="stat-box">
                            <div class="stat-number"><%= viewCount %></div>
                            <div class="stat-name">Views</div>
                        </div>
                    </div>
                    <div class="question-content">
                        <a href="${pageContext.request.contextPath}/question?id=<%= questionId %>" class="question-title">
                            <%= title %>
                        </a>
                        <div class="question-excerpt"><%= excerpt %></div>
                        <div class="question-meta">asked <%= createdAt %></div>
                    </div>
                </div>
            <% 
                    }
                } else {
            %>
                <div class="empty-state">
                    <i class="fa-solid fa-inbox"></i>
                    <div>Chưa có câu hỏi</div>
                </div>
            <% } %>
        </div>
    </div>

    <!-- Sidebar -->
    <div class="sidebar-content">
        <!-- Popular Tags -->
        <div class="card">
            <div class="card-title"><i class="fa-solid fa-fire"></i> Popular Tags</div>
            <div class="badge-list">
                <% 
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> popularTags = (List<Map<String, Object>>) request.getAttribute("popularTags");
                    
                    if (popularTags != null && !popularTags.isEmpty()) {
                        for (Map<String, Object> tag : popularTags) {
                            String tagName = (String) tag.get("tagName");
                            int count = ((Number) tag.get("count")).intValue();
                %>
                    <span class="badge" title="<%= count %> questions">
                        <%= tagName %>
                    </span>
                <% 
                        }
                    } else {
                %>
                    <div style="text-align: center; color: #9199a3; font-size: 12px;">No tags yet</div>
                <% } %>
            </div>
        </div>

        <!-- About -->
        <div class="card">
            <div class="card-title">About</div>
            <div style="font-size: 13px; line-height: 1.6; color: #6a737c;">
                Member since today • Role: ${profileUser.role}
            </div>
        </div>
    </div>
</div>

<script>
    function toggleMenu() {
        var sidebar = document.getElementById('sidebar');
        sidebar.classList.toggle('active');
    }
</script>

</body>
</html>
