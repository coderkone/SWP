<%-- 
    Document   : activity_follows
    Created on : Mar 11, 2026, 9:49:36 PM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>${uPro.username} - Follows - DevQuery</title>
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
                                <a href="?id=${uPro.userId}&tab=tags" class="list-group-item list-group-item-action">Tags</a>
                                <a href="?id=${uPro.userId}&tab=follows" class="list-group-item list-group-item-action active text-white">Follows</a>

                                <c:if test="${sessionScope.user != null && sessionScope.user.userId == uPro.userId}">
                                    <a href="?id=${uPro.userId}&tab=votes" class="list-group-item list-group-item-action">Votes</a>
                                </c:if>
                            </div>
                        </div>

                        <div class="col-md-10">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h4 style="font-weight: 400; font-size: 21px;">${totalRecords} Bookmarked Posts</h4>
                            </div>

                            <div class="list-group">
                                <c:forEach items="${itemsList}" var="f">
                                    <div class="list-group-item list-group-item-action d-flex p-3">
                                        <div class="text-center me-3" style="min-width: 60px;">
                                            <span class="badge ${f.score > 0 ? 'bg-success' : 'bg-secondary'} rounded-pill fs-6">${f.score}</span>
                                            <div class="text-muted" style="font-size: 11px; margin-top: 4px;">votes</div>
                                        </div>
                                        <div class="flex-grow-1">
                                            <a href="${pageContext.request.contextPath}/question?id=${f.questionId}" class="text-decoration-none fw-bold" style="color: #0074cc;">${f.title}</a>
                                            <div class="text-muted mt-1" style="font-size: 12px;">
                                                <i class="fa-solid fa-bookmark text-warning"></i> Saved on <fmt:formatDate value="${f.savedAt}" pattern="MMM dd, yyyy"/>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                                <c:if test="${empty itemsList}">
                                    <div class="text-center text-muted p-5">User hasn't bookmarked any posts yet.</div>
                                </c:if>
                            </div>

                            <c:if test="${totalPage > 1}">
                                <div class="mt-4">
                                    <c:forEach begin="1" end="${totalPage}" var="i">
                                        <a href="?id=${uPro.userId}&tab=follows&page=${i}" class="btn btn-sm ${currentPage == i ? 'btn-primary' : 'btn-outline-secondary'}">${i}</a>
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
