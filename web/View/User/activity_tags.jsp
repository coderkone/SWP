<%-- 
    Document   : activity_tags
    Created on : Mar 11, 2026, 9:48:11 PM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>${uPro.username} - Tags - DevQuery</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <style>
            .user-name {
                font-size: 34px;
                font-weight: normal;
                margin-bottom: 4px;
            }
            .user-meta {
                list-style: none;
                padding: 0;
                margin: 0;
                color: #6a737c;
                font-size: 13px;
            }
            .profile-tabs {
                border-bottom: none;
                margin-bottom: 20px;
            }
            .profile-tabs .nav-link {
                color: #525960;
                border-radius: 20px;
                padding: 6px 12px;
                margin-right: 5px;
                border: none;
                font-size: 13px;
            }
            .profile-tabs .nav-link:hover {
                background-color: #e3e6e8;
                color: #0c0d0e;
            }
            .profile-tabs .nav-link.active {
                background-color: #f48024;
                color: white;
            }
            .list-group-item.active {
                background-color: #f48024 !important;
                border-color: #f48024 !important;
            }
        </style>
    </head>
    <body style="padding-top: 60px; background-color: #f8f9fa;">

        <jsp:include page="../Common/header.jsp"></jsp:include>

            <div class="container-fluid" style="max-width: 1264px; margin: 0 auto; background-color: #fff;">
                <div class="row">
                    <nav class="col-md-2 d-none d-md-block bg-light sidebar p-0 pt-4" style="border-right: 1px solid #d6d9dc; min-height: 100vh;">
                    <jsp:include page="../Common/sidebar.jsp" />
                </nav>

                <main class="col-md-10 px-md-4 pt-4">

                    <jsp:include page="../Common/profileTemplate.jsp">
                        <jsp:param name="activeTab" value="activity" />
                    </jsp:include>

                    <div class="row mt-4">
                        <div class="col-md-2">
                            <div class="list-group list-group-flush" style="font-size: 14px;">
                                <a href="?id=${uPro.userId}&tab=summary" class="list-group-item list-group-item-action">Summary</a>
                                <a href="?id=${uPro.userId}&tab=questions" class="list-group-item list-group-item-action">Questions</a>
                                <a href="?id=${uPro.userId}&tab=answers" class="list-group-item list-group-item-action">Answers</a>
                                <a href="?id=${uPro.userId}&tab=comments" class="list-group-item list-group-item-action">Comments</a>
                                <a href="?id=${uPro.userId}&tab=tags" class="list-group-item list-group-item-action active text-white">Tags</a>
                                <a href="?id=${uPro.userId}&tab=follows" class="list-group-item list-group-item-action">Follows</a>

                                <c:if test="${sessionScope.user != null && sessionScope.user.userId == uPro.userId}">
                                    <a href="?id=${uPro.userId}&tab=votes" class="list-group-item list-group-item-action">Votes</a>
                                </c:if>
                            </div>
                        </div>

                        <div class="col-md-10">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h4 style="font-weight: 400; font-size: 21px;">${totalRecords} Tags</h4>
                            </div>

                            <div class="row g-3">
                                <c:forEach items="${itemsList}" var="t">
                                    <div class="col-md-3">
                                        <div class="card border border-1 rounded-3 shadow-sm h-100">
                                            <div class="card-body p-3 d-flex flex-column justify-content-between">
                                                <div class="mb-3 text-start">
                                                    <a href="${pageContext.request.contextPath}/home?tag=${t.tagName}" class="badge text-decoration-none p-2" style="font-size: 13px; font-weight: normal; background-color: #e1ecf4; color: #39739d;">
                                                        ${t.tagName}
                                                    </a>
                                                </div>
                                                <div class="d-flex justify-content-between align-items-center text-muted pt-2 border-top" style="font-size: 12px;">
                                                    <span><strong class="text-dark fs-6">${t.score}</strong> score</span>
                                                    <span><strong class="text-dark fs-6">${t.postCount}</strong> posts</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>

                            <c:if test="${totalPage > 1}">
                                <div class="mt-4">
                                    <c:forEach begin="1" end="${totalPage}" var="i">
                                        <a href="?id=${uPro.userId}&tab=tags&page=${i}" class="btn btn-sm ${currentPage == i ? 'btn-primary' : 'btn-outline-secondary'}">${i}</a>
                                    </c:forEach>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </main>
            </div>
        </div>
    </body>
</html>
