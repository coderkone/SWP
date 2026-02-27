<%-- 
    Document   : header.jsp
    Created on : Jan 29, 2026, 10:15:30 PM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<style>
    /* CUSTOM CSS FOR HEADER */
    .navbar-devquery {
        border-top: 3px solid #f48024;
        background-color: #ffffff;
        box-shadow: 0 1px 2px rgba(0,0,0,0.05), 0 1px 4px rgba(0,0,0,0.05);
        height: 56px;
    }
    
    .search-box {
        transition: all 0.3s ease;
        border: 1px solid #babfc4;
    }
    .search-box:focus {
        border-color: #3b4045;
        box-shadow: 0 0 0 4px rgba(0, 149, 255, 0.15);
    }

    .btn-auth {
        font-weight: 500;
        font-size: 0.9rem;
    }
    
    .avatar-img {
        object-fit: cover;
    }
    .noti-badge {
    position: absolute; 
    top: 2px; 
    right: 2px; 
    background: #d0393e; 
    color: white; 
    border-radius: 50%; 
    padding: 2px 5px; 
    font-size: 10px; 
    font-weight: bold;
}

.noti-dropdown {
    width: 350px; 
    padding: 0;
}

.noti-header {
    padding: 10px 15px; 
    background: #f8f9f9; 
    font-weight: bold; 
    border-bottom: 1px solid #d6d9dc; 
    display: flex; 
    justify-content: space-between;
}

.noti-mark-all {
    font-weight: normal; 
    color: #0074cc; 
    text-decoration: none; 
    font-size: 13px;
}

.noti-mark-all:hover {
    text-decoration: underline;
}

.noti-body {
    max-height: 350px; 
    overflow-y: auto;
}

.noti-empty {
    padding: 15px; 
    text-align: center; 
    color: #666;
}

.noti-item {
    padding: 12px 15px; 
    border-bottom: 1px solid #e3e6e8;
    transition: background-color 0.2s ease;
}

.noti-item:hover {
    background-color: #f1f2f3; /* Hiệu ứng di chuột */
}

.noti-unread {
    background-color: #f0f8ff; /* Nền xanh nhạt cho tin chưa đọc */
}

.noti-read {
    background-color: #ffffff; /* Nền trắng cho tin đã đọc */
}

.noti-content-text {
    font-size: 13px; 
    color: #3b4045; 
    margin-bottom: 5px;
}

.noti-meta {
    font-size: 11px; 
    color: #838c95; 
    display: flex; 
    justify-content: space-between;
}

.noti-action {
    color: #0074cc; 
    text-decoration: none;
}

.noti-action:hover {
    text-decoration: underline;
}
</style>

<nav class="navbar navbar-expand-lg navbar-light fixed-top navbar-devquery">
    <div class="container-fluid">
        
        <a class="navbar-brand d-flex align-items-center me-4 ms-4" href="${pageContext.request.contextPath}/home">
            <img src="${pageContext.request.contextPath}/assets/img/LogoDQ.png" 
                 alt="DevQuery" width="30" height="30" class="d-inline-block align-text-top me-2">
            <span class="fw-bold text-dark" style="font-family: Arial, sans-serif;">DevQuery</span>
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">
            
            <form class="d-flex mx-auto" style="max-width: 500px; width: 100%;" action="${pageContext.request.contextPath}/SearchController" method="GET">
                <div class="input-group position-relative w-100">
                    <span class="position-absolute top-50 start-0 translate-middle-y ms-2 text-secondary z-index-1">
                        <i class="fa-solid fa-magnifying-glass"></i>
                    </span>
                    <input class="form-control rounded ps-5 search-box" type="search" name="q" 
                           placeholder="Search questions, tags, or users..." aria-label="Search">
                </div>
            </form>

            <ul class="navbar-nav ms-auto align-items-center gap-2 mt-2 mt-lg-0">
                
                <c:if test="${sessionScope.user != null}">
                    
                    <li class="nav-item">
                        <a class="nav-link d-flex align-items-center py-0" href="${pageContext.request.contextPath}/profile">
                            
                            <c:set var="avatarSrc" value="${sessionScope.user.avatarUrl}" />
                            <c:if test="${empty avatarSrc}">
                                <c:set var="avatarSrc" value="${pageContext.request.contextPath}/assets/img/Avatar.png" />
                            </c:if>

                            <img src="${avatarSrc}" 
                                 alt="Avatar" width="32" height="32" class="rounded bg-light border avatar-img">
                            
                            <span class="ms-2 fw-bold text-dark small">${sessionScope.user.reputation != null ? sessionScope.user.reputation : 0}</span>
                        </a>
                    </li>

<!--                    <li class="nav-item">
                        <a class="nav-link text-secondary" href="#"><i class="fa-solid fa-inbox fa-lg"></i></a>
                    </li>-->
                    <li class="nav-item position-relative notification-wrapper">
                        <a class="nav-link text-secondary" href="#" id="notiDropdownBtn" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="fa-solid fa-inbox fa-lg"></i>
                            
                            <c:if test="${requestScope.unreadNotification != null && requestScope.unreadNotification > 0}">
                                <span class="noti-badge">
                                    ${requestScope.unreadNotification > 99 ? '99+' : requestScope.unreadNotification}
                                </span>
                            </c:if>
                        </a>

                        <div class="dropdown-menu dropdown-menu-end shadow noti-dropdown" aria-labelledby="notiDropdownBtn">
                            
                            <div class="noti-header">
                                <span>Thông báo</span>
                                <a href="${pageContext.request.contextPath}/notification?action=allRead" class="noti-mark-all">Đánh dấu đã đọc tất cả</a>
                            </div>
                            
                            <div class="noti-body">
                                <c:if test="${empty requestScope.Notification}">
                                    <div class="noti-empty">Bạn không có thông báo nào.</div>
                                </c:if>

                                <c:forEach items="${requestScope.Notification}" var="noti">
                                    <div class="noti-item ${noti.isRead ? 'noti-read' : 'noti-unread'}">
                                        <div class="noti-content-text">${noti.content}</div>
                                        <div class="noti-meta">
                                            <span>${noti.createdAt}</span>
                                            <c:if test="${!noti.isRead}">
                                                <a href="${pageContext.request.contextPath}/notification?id=${noti.notificationId}" class="noti-action">Đánh dấu đã đọc</a>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                            
                        </div>
                    </li>
                    
                    <li class="nav-item me-4">
                        <a class="btn btn-outline-secondary btn-sm btn-auth" href="${pageContext.request.contextPath}/logout">
                            <i class="fa-solid fa-right-from-bracket"></i>
                        </a>
                    </li>
                </c:if>

                <c:if test="${sessionScope.user == null}">
                    <li class="nav-item">
                        <a class="btn btn-outline-primary btn-sm btn-auth px-3" href="${pageContext.request.contextPath}/login.jsp">Log in</a>
                    </li>
                    <li class="nav-item">
                        <a class="btn btn-primary btn-sm btn-auth px-3 me-4" href="${pageContext.request.contextPath}/register.jsp">Sign up</a>
                        </li>
                </c:if>

            </ul>
        </div>
    </div>
</nav>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
