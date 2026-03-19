

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Tags</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            
            :root {
                --border-color: #d6d9dc;
                --blue-tag-bg: #e1ecf4;
                --blue-tag-text: #39739d;
            }

            * { box-sizing: border-box; margin: 0; padding: 0; }

            body {
                font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Arial, sans-serif;
                font-size: 13px;
                background: #fff;
                color: #0c0d0e;
            }

            
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
            }

            .main-content {
                flex-grow: 1;
                padding: 24px;
                border-left: 1px solid var(--border-color);
                min-width: 0;
            }

            
            .tags-header h1 { font-size: 27px; font-weight: 400; margin-bottom: 8px; }
            .tags-header p  { color: #3b4045; line-height: 1.6; max-width: 640px; margin-bottom: 16px; }

            
            .tags-controls {
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 12px;
                margin-bottom: 12px;
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
                border: 1px solid #9fa6ad;
                border-radius: 3px;
                overflow: hidden;
            }
            .sort-buttons a {
                padding: 8px 12px; font-size: 13px; color: #6a737c;
                text-decoration: none; border-right: 1px solid #9fa6ad;
                background: #fff; cursor: pointer; transition: background 0.1s;
            }
            .sort-buttons a:last-child { border-right: none; }
            .sort-buttons a:hover { background: #f8f9f9; color: #525960; }
            .sort-buttons a.active { background: #e3e6e8; font-weight: 500; color: #3b4045; }

            .search-result-count {
                font-size: 13px; color: #6a737c; margin-bottom: 12px;
            }
            .search-result-count strong { color: #3b4045; }

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
                display: inline-block; background: var(--blue-tag-bg); color: var(--blue-tag-text);
                padding: 4px 8px; border-radius: 4px; font-size: 12px;
                font-weight: 500; text-decoration: none; 
            }
            .tag-badge:hover { background: #d0e3f1; }
            .tag-badge.followed-badge {
                background: #fff3cd;
                color: #b8860b;
            }
            .tag-card-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            .btn-follow {
                background: none;
                border: none;
                cursor: pointer;
                padding: 2px 6px;
                border-radius: 3px;
                font-size: 14px;
                font-weight: bold;
                line-height: 1;
                transition: all 0.15s;
            }
            .btn-plus  { color: #6a737c; }
            .btn-plus:hover  { background: #e1ecf4; color: #39739d; }
            .btn-minus { color: #b8860b; }
            .btn-minus:hover { background: #ffe082; color: #8a6200; }


            .tag-desc {
                font-size: 12px; color: #3b4045; line-height: 1.5; flex: 1;
                display: -webkit-box; -webkit-line-clamp: 1;
                -webkit-box-orient: vertical; overflow: hidden;
            }
            .tag-desc em { color: #9fa6ad; }

            .highlight {
                background-color: #fff3cd; font-weight: 600;
                border-radius: 2px; padding: 0 2px;
            }

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

<div class="container">

    
    <div class="left-sidebar">
                <jsp:include page="/View/Common/sidebar.jsp">
                    <jsp:param name="page" value="tags"/>
                </jsp:include>
    </div>

    <main class="main-content">

        <div class="tags-header">
            <h1>Tags</h1>
            <p>
                A tag is a keyword or label that categorizes your question with other, similar questions. 
                Using the right tags makes it easier for others to find and answer your question.
            </p>
        </div>

        
            <form method="get" action="${pageContext.request.contextPath}/tags">
                <div class="tags-controls">

                <div class="search-box">
                    <i class="fa-solid fa-magnifying-glass"></i>
                    <input type="text"
                           id="searchInput"
                           name="search"
                           placeholder="Filter by tag name"
                           value="${param.search}" 
                           />
                </div>

                <div class="sort-buttons">
                    <a href="${pageContext.request.contextPath}/tags?sort=popular&search=${param.search}"
                       class="${sort == 'popular' ? 'active' : ''}">Popular</a>
                    <a href="${pageContext.request.contextPath}/tags?sort=name&search=${param.search}"
                       class="${empty sort || sort == 'name' ? 'active' : ''}">Name</a>
                    <a href="${pageContext.request.contextPath}/tags?sort=newest&search=${param.search}"
                       class="${sort == 'newest' ? 'active' : ''}">New</a>
                </div>
                </div>
            </form>
        
                <c:if test="${not empty keyword}">
                    <p class="search-result-count">
                        Found <strong>${tagList.size()}</strong>
                        tag for keyword "<strong>${keyword}</strong>"
                    </p>
                </c:if>

        <div class="tags-grid">
            <c:choose>
                <c:when test="${not empty tagList}">
                    <c:forEach var="tag" items="${tagList}">
                        <div class="tag-card${tag.followed ? 'followed' : ''}">
                            <div class="tag-card-header">
                                <a href="${pageContext.request.contextPath}/tagsdetail?id=${tag.tagId}"
                               class="tag-badge ${tag.followed ? 'followed-badge' : ''}">
                                            ${tag.tagName}</a>
                                <c:if test="${isLoggedIn}">
                                            <c:choose>
                                                <c:when test="${tag.followed}">
                                                    
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/follow-tags"
                                                          style="margin:0;">
                                                        <input type="hidden" name="tagId"  value="${tag.tagId}" />
                                                        <input type="hidden" name="action" value="unfollow" />
                                                        <input type="hidden" name="sort"   value="${sort}" />
                                                        <input type="hidden" name="search" value="${param.search}" />
                                                        <button type="submit"
                                                                class="btn-follow btn-minus"
                                                                title="Unfollow">✕</button>
                                                    </form>
                                                </c:when>
                                                <c:otherwise>
                                                    
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/follow-tags"
                                                          style="margin:0;">
                                                        <input type="hidden" name="tagId"  value="${tag.tagId}" />
                                                        <input type="hidden" name="action" value="follow" />
                                                        <input type="hidden" name="sort"   value="${sort}" />
                                                        <input type="hidden" name="search" value="${param.search}" />
                                                        <button type="submit"
                                                                class="btn-follow btn-plus"
                                                                title="Follow">+</button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>

                                
                            </div>

                            <p class="tag-desc">
                                <c:choose>
                                    <c:when test="${not empty tag.description}">${tag.description}</c:when>
                                    <c:otherwise><em>No description available.</em></c:otherwise>
                                </c:choose>
                            </p>

                           
                        </div>
                    </c:forEach>
                </c:when>

                <c:otherwise>
                    <div class="empty-state">
                        <i class="fa-solid fa-tags"></i>
                        <c:choose>
                            <c:when test="${not empty keyword}">
                                <p>No tags found with that keyword.
                                           "<strong>${keyword}</strong>"
                                        </p>
                            </c:when>
                            <c:otherwise>
                                <p>No cards are in the system.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

    </main>
</div> 
<script>
    let timer;
    const searchInput = document.getElementById('searchInput');

    
    searchInput.addEventListener('input', function () {
        clearTimeout(timer);
        timer = setTimeout(() => {
            this.form.submit();
        }, 500);
    });

    
    window.addEventListener('load', function () {
        const val = searchInput.value;
        if (val) {
            searchInput.focus();
            searchInput.setSelectionRange(val.length, val.length);
        }
    });

    
    const keyword = new URLSearchParams(window.location.search).get('search');
    if (keyword && keyword.trim() !== '') {
        const safeKeyword = keyword.replace(/[.*+$?^{}()|[\]\\]/g, '\\$&');
        document.querySelectorAll('.tag-badge').forEach(badge => {
            const text = badge.textContent;
            const regex = new RegExp('(' + keyword + ')', 'gi');
            badge.innerHTML = text.replace(regex, '<span class="highlight">$1</span>');
        });
    }
</script>
        
   
        
        
    </body>
</html>
