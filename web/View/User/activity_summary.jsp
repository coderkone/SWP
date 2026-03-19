<%-- 
    Document   : activity_summary
    Created on : Mar 11, 2026, 9:23:24 AM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>${uPro.username} - Activity - DevQuery</title>
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
                                <a href="?id=${uPro.userId}&tab=summary" class="list-group-item list-group-item-action ${currentActivityTab == 'summary' ? 'active text-white' : ''}">Summary</a>
                                <a href="?id=${uPro.userId}&tab=questions" class="list-group-item list-group-item-action ${currentActivityTab == 'questions' ? 'active text-white' : ''}">Questions</a>
                                <a href="?id=${uPro.userId}&tab=answers" class="list-group-item list-group-item-action ${currentActivityTab == 'answers' ? 'active text-white' : ''}">Answers</a>
                                <a href="?id=${uPro.userId}&tab=comments" class="list-group-item list-group-item-action ${currentActivityTab == 'comments' ? 'active text-white' : ''}">Comments</a>
                                <a href="?id=${uPro.userId}&tab=tags" class="list-group-item list-group-item-action ${currentActivityTab == 'tags' ? 'active text-white' : ''}">Tags</a>
                                <a href="?id=${uPro.userId}&tab=follows" class="list-group-item list-group-item-action ${currentActivityTab == 'follows' ? 'active text-white' : ''}">Follows</a>
                                
                                <c:if test="${sessionScope.user != null && sessionScope.user.userId == uPro.userId}">
                                    <a href="?id=${uPro.userId}&tab=votes" class="list-group-item list-group-item-action ${currentActivityTab == 'votes' ? 'active text-white' : ''}">Votes</a>
                                </c:if>
                            </div>
                        </div>

                        <div class="col-md-10">
                            <h4 class="mb-4" style="font-weight: 400; font-size: 21px;">Summary</h4>

                            <div class="row">
                                <div class="col-md-7 mb-4">
                                    <div class="card shadow-sm border-0 h-100">
                                        <div class="card-header bg-white border-0 pt-3 pb-0" style="font-weight: 600;">Questions (Last 6 Months)</div>
                                        <div class="card-body">
                                            <canvas id="activityChart" height="200"></canvas>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-5 mb-4">
                                    <div class="card shadow-sm border-0 h-100">
                                        <div class="card-header bg-white border-0 pt-3 pb-0 d-flex justify-content-between align-items-center">
                                            <span style="font-weight: 600;">Top Tags</span>
                                            <a href="?id=${uPro.userId}&tab=tags" style="font-size: 13px; text-decoration: none; color: #0074cc;">View all</a>
                                        </div>
                                        <div class="card-body">
                                            <canvas id="tagsChart" height="250"></canvas>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </main>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>                                
        <script>
            // --- Xử lý dữ liệu cho Biểu đồ 1 (Activity by Month) ---
            const activityLabels = [];
            const activityData = [];
            
            <c:forEach items="${chartActivityMap}" var="entry">
                // Dùng unshift để đảo ngược mảng: hiển thị từ tháng cũ nhất -> tháng mới nhất
                activityLabels.unshift('${entry.key}');
                activityData.unshift(${entry.value});
            </c:forEach>

            const ctxActivity = document.getElementById('activityChart').getContext('2d');
            new Chart(ctxActivity, {
                type: 'bar', // Biểu đồ cột dọc
                data: {
                    labels: activityLabels,
                    datasets: [{
                        label: 'Number of Questions',
                        data: activityData,
                        backgroundColor: 'rgba(54, 162, 235, 0.6)',
                        borderColor: 'rgba(54, 162, 235, 1)',
                        borderWidth: 1,
                        borderRadius: 4
                    }]
                },
                options: {
                    responsive: true,
                    scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } },
                    plugins: { legend: { display: false } } // Ẩn chú thích vì chỉ có 1 cột
                }
            });

            // --- Xử lý dữ liệu cho Biểu đồ 2 (Top Tags by Score) ---
            const tagLabels = [];
            const tagData = [];
            
            <c:forEach items="${chartTopTagsMap}" var="entry">
                tagLabels.push('${entry.key}');
                tagData.push(${entry.value});
            </c:forEach>

            const ctxTags = document.getElementById('tagsChart').getContext('2d');
            new Chart(ctxTags, {
                type: 'bar', 
                data: {
                    labels: tagLabels,
                    datasets: [{
                        label: 'Total Score',
                        data: tagData,
                        backgroundColor: 'rgba(244, 128, 36, 0.7)', 
                        borderColor: 'rgba(244, 128, 36, 1)',
                        borderWidth: 1,
                        borderRadius: 4
                    }]
                },
                options: {
                    indexAxis: 'y', // Xoay thành biểu đồ cột ngang
                    responsive: true,
                    scales: { x: { beginAtZero: true } },
                    plugins: { legend: { display: false } }
                }
            });
        </script>
    </body>
</html>
