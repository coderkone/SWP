<%-- 
    Document   : tag.jsp
    Created on : Mar 10, 2026, 5:41:47 PM
    Author     : Asus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Tags</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
           .page-wrapper {
            display: flex;
            padding-top: 70px; /* tránh bị header che */
        }

        .main-content {
            flex: 1;
            padding: 24px;
            min-width: 0;
        }

        .tags-header h1 { font-size: 27px; font-weight: 400; margin-bottom: 12px; }
        .tags-header p  { color: #3b4045; line-height: 1.6; max-width: 640px; margin-bottom: 12px; }

        .tags-controls {
            display: flex;
            align-items: center;
            gap: 12px;
            flex-wrap: wrap;
            margin: 20px 0 16px;
        }

        .search-box { position: relative; width: 300px; }
        .search-box i {
            position: absolute; left: 10px; top: 50%;
            transform: translateY(-50%); color: #6a737c; font-size: 13px;
        }
        .search-box input {
            width: 100%; padding: 8px 12px 8px 32px;
            border: 1px solid #babfc4; border-radius: 4px;
            font-size: 13px; outline: none;
        }
        .search-box input:focus {
            border-color: #6bbbf7;
            box-shadow: 0 0 0 4px rgba(0,149,255,0.15);
        }

        .sort-buttons {
            display: flex;
            border: 1px solid #babfc4;
            border-radius: 4px;
            overflow: hidden;
        }
        .sort-buttons a {
            padding: 7px 12px; font-size: 13px; color: #3b4045;
            text-decoration: none; border-right: 1px solid #babfc4;
            background: #fff; cursor: pointer; transition: background 0.1s;
        }
        .sort-buttons a:last-child { border-right: none; }
        .sort-buttons a:hover { background: #f8f9f9; }
        .sort-buttons a.active { background: #e3e6e8; font-weight: 600; color: #0c0d0e; }

        .tags-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 12px;
        }
        @media (max-width: 1100px) { .tags-grid { grid-template-columns: repeat(3, 1fr); } }
        @media (max-width: 800px)  { .tags-grid { grid-template-columns: repeat(2, 1fr); } }
        @media (max-width: 500px)  { .tags-grid { grid-template-columns: 1fr; } }

        .tag-card {
            background: #fff; border: 1px solid #e3e6e8;
            border-radius: 4px; padding: 12px;
            display: flex; flex-direction: column; gap: 8px;
            transition: box-shadow 0.15s;
        }
        .tag-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.08); }

        .tag-badge {
            display: inline-block; background: #e1ecf4; color: #39739d;
            padding: 4px 8px; border-radius: 4px; font-size: 12px;
            font-weight: 500; text-decoration: none; align-self: flex-start;
        }
        .tag-badge:hover { background: #d0e3f1; }

        .tag-desc {
            font-size: 12px; color: #3b4045; line-height: 1.5; flex: 1;
            display: -webkit-box; -webkit-line-clamp: 4;
            -webkit-box-orient: vertical; overflow: hidden;
        }
        .tag-desc em { color: #9fa6ad; }

        

        .empty-state {
            grid-column: 1 / -1; text-align: center;
            padding: 60px 20px; color: #6a737c;
        }
        .empty-state i { font-size: 48px; margin-bottom: 16px; color: #babfc4; display: block; }
        .empty-state p { font-size: 15px; } 

        
           
        </style>
    </head>
    <body>
       <jsp:include page="/View/Common/header.jsp"/>

<div class="page-wrapper">

    <%-- Sidebar --%>
    <jsp:include page="/View/Common/sidebar.jsp">
        <jsp:param name="page" value="tags"/>
    </jsp:include>

    <main class="main-content">

        <div class="tags-header">
            <h1>Tags</h1>
            <p>
                Một tag là từ khóa giúp phân loại câu hỏi của bạn với các câu hỏi tương tự.
                Dùng đúng tag giúp người khác dễ tìm và trả lời câu hỏi của bạn hơn.
            </p>
        </div>

        <div class="tags-controls">
            <form method="get" action="${pageContext.request.contextPath}/tags"
                  style="display:flex; gap:12px; align-items:center; flex-wrap:wrap;">

                <div class="search-box">
                    <i class="fa-solid fa-magnifying-glass"></i>
                    <input type="text" name="search"
                           placeholder="Filter by tag name"
                           value="${param.search}" />
                </div>

                <div class="sort-buttons">
                    <a href="${pageContext.request.contextPath}/tags?sort=popular&search=${param.search}"
                       class="${empty param.sort || param.sort == 'popular' ? 'active' : ''}">Popular</a>
                    <a href="${pageContext.request.contextPath}/tags?sort=name&search=${param.search}"
                       class="${param.sort == 'name' ? 'active' : ''}">Name</a>
                    <a href="${pageContext.request.contextPath}/tags?sort=newest&search=${param.search}"
                       class="${param.sort == 'newest' ? 'active' : ''}">New</a>
                </div>

            </form>
        </div>

        <div class="tags-grid">
            <c:choose>
                <c:when test="${not empty tagList}">
                    <c:forEach var="tag" items="${tagList}">
                        <div class="tag-card">
                            <a href="${pageContext.request.contextPath}/tagsdetail?id=${tag.tagId}"
                               class="tag-badge">${tag.tagName}</a>

                            <p class="tag-desc">
                                <c:choose>
                                    <c:when test="${not empty tag.description}">${tag.description}</c:when>
                                    <c:otherwise><em>Chưa có mô tả.</em></c:otherwise>
                                </c:choose>
                            </p>

                           
                        </div>
                    </c:forEach>
                </c:when>

                <c:otherwise>
                    <div class="empty-state">
                        <i class="fa-solid fa-tags"></i>
                        <c:choose>
                            <c:when test="${not empty param.search}">
                                <p>Không tìm thấy tag "<strong>${param.search}</strong>"</p>
                            </c:when>
                            <c:otherwise>
                                <p>Chưa có tag nào trong hệ thống.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

    </main>
</div> 
   
        
        
    </body>
</html>
