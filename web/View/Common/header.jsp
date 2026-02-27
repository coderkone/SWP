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
</style>

<nav class="navbar navbar-expand-lg navbar-light fixed-top navbar-devquery">
    <div class="container-fluid">
        
        <a class="navbar-brand d-flex align-items-center me-4" href="${pageContext.request.contextPath}/home">
            <img src="${pageContext.request.contextPath}/assets/img/logo.png" 
                 alt="DevQuery" width="30" height="30" class="d-inline-block align-text-top me-2">
            <span class="fw-bold text-dark" style="font-family: Arial, sans-serif;">DevQuery</span>
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">
            
            <form class="d-flex flex-grow-1 mx-lg-4" action="${pageContext.request.contextPath}/search" method="GET">

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
                            <img src="${sessionScope.user.avatarUrl != null ? sessionScope.user.avatarUrl : 'https://via.placeholder.com/32'}" 
                                 alt="Avatar" width="32" height="32" class="rounded bg-light border avatar-img">
                            
                            <span class="ms-2 fw-bold text-dark small">${sessionScope.user.reputation != null ? sessionScope.user.reputation : 0}</span>
                        </a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link text-secondary" href="#"><i class="fa-solid fa-inbox fa-lg"></i></a>
                    </li>
                    
                    <li class="nav-item me-2">
                         <a class="nav-link text-secondary" href="#"><i class="fa-solid fa-circle-question fa-lg"></i></a>
                    </li>

                    <li class="nav-item">
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
                        <a class="btn btn-primary btn-sm btn-auth px-3" href="${pageContext.request.contextPath}/register.jsp">Sign up</a>
                    </li>
                </c:if>

            </ul>
        </div>
    </div>
</nav>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>