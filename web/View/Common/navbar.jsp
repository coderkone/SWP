http://localhost:8080/DevQuery/<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
    .navbar-custom {
        background: white;
        border-bottom: 1px solid #e1e4e8;
        padding: 12px 0;
        margin-bottom: 20px;
    }
    
    .navbar-content {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0 15px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    
    .navbar-brand {
        font-size: 24px;
        font-weight: 700;
        color: #222;
        text-decoration: none;
    }
    
    .navbar-nav {
        display: flex;
        gap: 20px;
        align-items: center;
        margin: 0;
        list-style: none;
    }
    
    .navbar-nav a {
        color: #222;
        text-decoration: none;
        font-size: 14px;
        transition: color 0.2s;
    }
    
    .navbar-nav a:hover {
        color: #0074cc;
    }
    
    .btn-ask {
        background-color: #0a95ff;
        color: white;
        padding: 10px 15px;
        border-radius: 3px;
        text-decoration: none;
        font-size: 14px;
        font-weight: 500;
        transition: background-color 0.2s;
    }
    
    .btn-ask:hover {
        background-color: #0074cc;
        color: white;
    }
    
    .btn-logout {
        background: none;
        border: none;
        color: #222;
        cursor: pointer;
        font-size: 14px;
        text-decoration: none;
        transition: color 0.2s;
    }
    
    .btn-logout:hover {
        color: #0074cc;
    }
</style>

<nav class="navbar-custom">
    <div class="navbar-content">
        <a href="${pageContext.request.contextPath}/home" class="navbar-brand">DevQuery</a>
        
        <ul class="navbar-nav">
            <li><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
            <li><a href="${pageContext.request.contextPath}/ask" class="btn-ask">Đặt câu hỏi</a></li>
            
            <% 
                Object userObj = session.getAttribute("USER");
                if (userObj != null) {
                    dto.UserDTO user = (dto.UserDTO) userObj;
            %>
                <li>
                    <span style="color: #666;">Xin chào, <%= user.getUsername() %></span>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/auth/logout" class="btn-logout">Đăng xuất</a>
                </li>
            <% } else { %>
                <li><a href="${pageContext.request.contextPath}/auth/login">Đăng nhập</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/register">Đăng ký</a></li>
            <% } %>
        </ul>
    </div>
</nav>
