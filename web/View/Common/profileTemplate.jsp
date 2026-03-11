<%-- 
    Document   : profile
    Created on : Mar 11, 2026, 9:01:42 AM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="d-flex justify-content-between mb-5">
    <div class="d-flex">
        <div class="me-4">
            <img src="${not empty uPro.avatarUrl ? uPro.avatarUrl : 'https://cdn-icons-png.flaticon.com/512/3135/3135715.png'}" 
                 alt="Avatar" style="width: 144px; height: 144px; border-radius: 5px; box-shadow: 0 1px 2px rgba(0,0,0,0.15); object-fit: cover;">
        </div>

        <div class="pt-1">
            <h1 class="user-name" style="margin-bottom: 8px;">${uPro.username}</h1>
            <div class="user-meta mb-3">
                <i class="fa-solid fa-cake-candles me-1"></i> Member since <fmt:formatDate value="${uPro.createdAt}" pattern="MMM dd, yyyy"/>
                <span class="mx-2">|</span> 
                <i class="fa-solid fa-star text-warning me-1"></i> ${uPro.reputation} reputation

                <c:if test="${not empty uPro.location}">
                    <span class="mx-2">|</span>
                    <i class="fa-solid fa-location-dot me-1"></i> ${uPro.location}
                </c:if>
            </div>

            <ul class="nav profile-tabs">
                <li class="nav-item">
                    <a class="nav-link ${param.activeTab == 'profile' ? 'active' : ''}" href="${pageContext.request.contextPath}/profile">Profile</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${param.activeTab == 'activity' ? 'active' : ''}" href="${pageContext.request.contextPath}/activity">Activity</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${param.activeTab == 'badge' ? 'active' : ''}" href="${pageContext.request.contextPath}/badge">Badge</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${param.activeTab == 'saves' ? 'active' : ''}" href="${pageContext.request.contextPath}/saves">Saves</a>
                </li>
            </ul>
        </div>
    </div>

    <div class="pt-2">
        <c:if test="${sessionScope.user != null && sessionScope.user.userId == uPro.userId}">
            <a href="${pageContext.request.contextPath}/edit-profile" class="btn btn-outline-secondary btn-sm" style="border-color: #9fa6ad; color: #525960;">
                <i class="fas fa-pencil-alt me-1"></i> Edit profile
            </a>
        </c:if>
    </div>
</div>
