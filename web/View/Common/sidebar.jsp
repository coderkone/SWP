<%-- 
    Document   : sidebar.jsp
    Author     : ADMIN
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .sidebar-sticky {
        position: sticky;
        top: 70px;
        height: calc(100vh - 70px);
        overflow-x: hidden;
        overflow-y: auto;
        padding-top: 1rem;
        border-right: 1px solid #dee2e6;
    }
    .nav-link {
        color: #525960;
        font-size: 14px;
        padding: 8px 12px;
        margin-bottom: 4px;
        border-radius: 6px;
        transition: all 0.2s ease;
    }
    .nav-link:hover {
        color: #0c0d0e;
        background-color: #f1f2f3;
        transform: translateX(3px);
    }
    .nav-link.active {
        font-weight: 600;
        color: #0c0d0e;
        background-color: #e3e6e8;
        border-right: none;
        border-left: 3px solid #f48024;
    }
    .sidebar-heading {
        font-size: 11px;
        font-weight: bold;
        text-transform: uppercase;
        color: #6a737c;
        margin-top: 15px;
        margin-bottom: 5px;
        padding-left: 10px;
    }
</style>

<nav class="d-none d-md-block bg-light sidebar">
    <div class="sidebar-sticky">
        <c:set var="originalUri" value="${requestScope['javax.servlet.forward.request_uri']}" />
        <c:if test="${empty originalUri}">
            <c:set var="originalUri" value="${pageContext.request.requestURI}" />
        </c:if>
        <c:set var="uri" value="${originalUri.toLowerCase()}" />

        <ul class="nav flex-column">
            <li class="sidebar-heading">Public</li>

            <li class="nav-item">
                <a class="nav-link ${uri.contains('/home') || uri.endsWith('/DevQuery/') ? 'active' : ''}" href="${pageContext.request.contextPath}/home">
                    <i class="fa-solid fa-house me-2"></i> Home
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${uri.contains('/tag') ? 'active' : ''}" href="${pageContext.request.contextPath}/tags">
                    <i class="fa-solid fa-tags me-2"></i> Tags
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${uri.contains('user.jsp') || uri.contains('users.jsp') ? 'active' : ''}" href="${pageContext.request.contextPath}/users">
                    <i class="fa-solid fa-users me-2"></i> Users
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${uri.contains('/bounty') ? 'active' : ''}" href="${pageContext.request.contextPath}/bounty/questions">
                    <i class="fa-solid fa-coins me-2"></i> Bounties
                </a>
            </li>

            <c:if test="${sessionScope.user != null}">
                <li class="sidebar-heading">Personal</li>

                <li class="nav-item">
                    <a class="nav-link ${uri.contains('/profile') ? 'active' : ''}" href="${pageContext.request.contextPath}/profile">
                        <i class="fa-solid fa-user me-2"></i> Profile
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link ${uri.contains('/saves') ? 'active' : ''}" href="${pageContext.request.contextPath}/saves">
                        <i class="fa-solid fa-bookmark me-2"></i> Saves
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${uri.contains('/recommended-questions') ? 'active' : ''}" href="${pageContext.request.contextPath}/recommended-questions">
                        <i class="fa-solid fa-wand-magic-sparkles me-2"></i> Recommended
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${uri.contains('/blog') ? 'active' : ''}" href="${pageContext.request.contextPath}/blog">
                        <i class="fa-solid fa-book-open"></i> Dev Blog
                    </a>
                </li>
            </c:if>
        </ul>
    </div>
</nav>
