<%-- 
    Document   : systemRules
    Created on : Feb 13, 2026, 11:30:27 PM
    Author     : Asus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>System Notification - DevQuery</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            .nav-top { background: #333; padding: 10px 0; }
        .nav-top a { text-decoration: none; }
        .nav-link-custom { color: #fff; margin-right: 20px; font-weight: 500; }
        .nav-link-custom:hover { color: #0d6efd; text-decoration: underline; }
        
        
        .nav-logo { height: 40px; border-radius: 4px; margin-right: 10px; }
        .nav-title { color: #fff; font-weight: bold; font-size: 1.4rem; vertical-align: middle; }

        
        body { background-color: #f8f9fa; } 
        
        
        .page-header {
            border-bottom: 2px solid #dee2e6;
            margin-bottom: 20px;
            padding-bottom: 10px;
        }
        .page-title {
            font-weight: bold;
            color: #333;
            display: flex;
            align-items: center;
        }
        .page-title i { color: #dc3545; margin-right: 10px; transform: rotate(45deg); }

       
        .scroll-area {
            max-height: 600px; 
            overflow-y: auto;
            padding-right: 5px;
        }
        
        .rule-card {
            border: 1px solid #9ec5fe; 
            border-radius: 0; 
            margin-bottom: 15px;
            background: #fff;
        }
        
        .rule-header {
            background-color: #cfe2ff; 
            color: #084298; 
            padding: 10px 15px;
            font-weight: bold;
            display: flex;
            justify-content: space-between; 
            align-items: center;
            border-bottom: 1px solid #9ec5fe;
        }
        
        .rule-body {
            padding: 15px;
            color: #212529;
        }

        
        .sidebar-card {
            border: 1px solid #ffe69c; 
            border-radius: 0;
            background: #fff;
        }
        .sidebar-header {
            background-color: #fff3cd; 
            color: #664d03; 
            padding: 10px 15px;
            font-weight: bold;
            border-bottom: 1px solid #ffe69c;
        }
        .sidebar-body { padding: 15px; }
        .sidebar-list li { margin-bottom: 8px; }

        </style>
    </head>
    <body>
        <nav class="nav-top mb-4">
        <div class="container d-flex justify-content-between align-items-center">
            <a href="#" class="d-flex align-items-center">
                <img src="${pageContext.request.contextPath}/assets/img/LogoDQ.png" alt="Logo" class="nav-logo">
                <span class="nav-title">DevQuery</span>
            </a>
            
            <div class="d-flex align-items-center text-white">
                <a href="${pageContext.request.contextPath}/home" class="nav-link-custom">Home</a>
                
                <c:if test="${not empty sessionScope.account}">
                    <span class="border-start ps-3 ms-2">
                        <i class="fas fa-user-circle me-1"></i>
                        ${sessionScope.account.username}
                    </span>
                </c:if>
            </div>
        </div>
    </nav>

    <div class="container bg-white p-4 shadow-sm" style="min-height: 80vh;">
        
        <div class="page-header">
            <h4 class="page-title">
                <i class="fas fa-thumbtack"></i> 
                Announcement from the Admin
            </h4>
        </div>

        <div class="row">
            <div class="col-md-8">
                <div class="scroll-area">
                    
                    <c:forEach items="${rules}" var="r">
                        <div class="card rule-card">
                            <div class="rule-header">
                                <span>${r.title}</span>
                                <small style="font-weight: normal; font-size: 0.85em;">
                                    ${r.createdAt}
                                </small>
                            </div>
                            <div class="rule-body">
                                <p class="m-0" style="white-space: pre-line;">${r.content}</p>
                            </div>
                        </div>
                    </c:forEach>

                    <c:if test="${empty rules}">
                        <div class="alert alert-secondary text-center">
                            No announcements have been made yet.
                        </div>
                    </c:if>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card sidebar-card">
                    <div class="sidebar-header">
                        <i class="fas fa-exclamation-triangle me-2"></i> Caution
                    </div>
                    <div class="sidebar-body">
                        <ul class="sidebar-list ps-3 mb-0">
                            <li> No spamming with junk links or advertisements.</li>
                            <li> Respect other members.</li>
                            <li> The code must be placed within the format (Markdown) tags.</li>
                            <li> Please double-check the content before publishing.</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <div class="mt-5 pt-3 border-top">
            <p class="text-muted mb-2">Have you understood the rules? Join the discussion now!</p>
            
        </div>

    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
