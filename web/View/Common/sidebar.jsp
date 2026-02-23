<%-- 
    Document   : sidebar.jsp
    Created on : Jan 29, 2026, 10:56:23 PM
    Author     : ADMIN
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    /* CSS SIDEBAR */
    .sidebar-sticky {
        position: -webkit-sticky;
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
        padding: 8px 10px;
        margin-bottom: 4px;
    }

    .nav-link:hover {
        color: #0c0d0e;
        background-color: #f8f9fa;
    }

    .nav-link.active {
        font-weight: bold;
        color: #0c0d0e;
        background-color: #f1f2f3;
        border-right: 3px solid #f48024;
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
        <ul class="nav flex-column">
            
            <li class="nav-item">
                <a class="nav-link ${param.page == 'home' ? 'active' : ''}" href="${pageContext.request.contextPath}/home">
                    <i class="fa-solid fa-house me-2"></i> Home
                </a>
            </li>

            <li class="sidebar-heading">Public</li>

            <li class="nav-item">
                <a class="nav-link ${param.page == 'questions' ? 'active' : ''}" href="${pageContext.request.contextPath}/questions">
                    <i class="fa-solid fa-earth-americas me-2"></i> Questions
                </a>
            </li>

            <li class="nav-item">
                <a class="nav-link ${param.page == 'tags' ? 'active' : ''}" href="${pageContext.request.contextPath}/tags">
                    <i class="fa-solid fa-tags me-2"></i> Tags
                </a>
            </li>

            <c:if test="${sessionScope.user != null}">
                <li class="sidebar-heading">Personal</li>
                
                <li class="nav-item">
                    <a class="nav-link ${param.page == 'profile' ? 'active' : ''}" href="${pageContext.request.contextPath}/profile">
                        <i class="fa-solid fa-user me-2"></i> Profile
                    </a>
                </li>
                
                <li class="nav-item">
                    <a class="nav-link ${param.page == 'bookmarks' ? 'active' : ''}" href="${pageContext.request.contextPath}/bookmarks">
                        <i class="fa-solid fa-bookmark me-2"></i> Bookmarks
                    </a>
                </li>
            </c:if>

        </ul>
    </div>
</nav>
