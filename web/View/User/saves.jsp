<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Saves - DevQuery</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

        <style>
            body {
                background-color: #fff;
                padding-top: 60px
            }
            .user-avatar-lg {
                width: 128px;
                height: 128px;
                border-radius: 5px;
                object-fit: cover;
                border: 4px solid white;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }
            .user-name {
                font-size: 34px;
                font-weight: bold;
                margin-bottom: 4px;
            }
            .user-meta {
                font-size: 13px;
                color: #6a737c;
            }

            /* Tabs */
            .profile-tabs .nav-link {
                color: #525960;
                border-radius: 20px;
                padding: 6px 12px;
                margin-right: 5px;
                border: none;
            }
            .profile-tabs .nav-link:hover {
                background-color: #e3e6e8;
            }
            .profile-tabs .nav-link.active {
                background-color: #f48024;
                color: white;
            }

            /* Sidebar Saves */
            .saves-sidebar-item {
                display: block;
                padding: 8px 12px;
                color: #525960;
                text-decoration: none;
                font-size: 14px;
                border-radius: 100px;
                margin-bottom: 2px;
            }
            .saves-sidebar-item:hover {
                background-color: #f8f9f9;
                color: #000;
            }
            .saves-sidebar-item.active {
                font-weight: bold;
                color: #ffffff;
                background-color: #f48024 !important;
            }
            .saves-sidebar-header {
                font-size: 11px;
                font-weight: bold;
                color: #000;
                text-transform: uppercase;
                margin-top: 20px;
                margin-bottom: 10px;
            }

            /* Item List */
            .saved-item-card {
                padding: 16px;
                border-bottom: 1px solid #e3e6e8;
            }
            .saved-item-title {
                font-size: 1.1rem;
                color: #0074cc;
                text-decoration: none;
                font-weight: 400;
                display: block;
                margin-bottom: 5px;
            }
            .saved-item-title:hover {
                color: #0a95ff;
            }
            .saved-meta {
                font-size: 12px;
                color: #6a737c;
            }

            /* Empty State */
            .empty-state-box {
                background-color: #f8f9f9;
                border: 1px dashed #d6d9dc;
                border-radius: 5px;
                padding: 40px;
                text-align: center;
                margin-top: 30px;
                color: #6a737c;
            }

            /*Model create list*/
            .modal-custom {
                display: none;
                position: fixed;
                z-index: 1050;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                overflow: auto;
                background-color: rgba(0,0,0,0.4);
            }
            .modal-content-custom {
                background-color: #fefefe;
                margin: 15% auto;
                padding: 20px;
                border: 1px solid #888;
                width: 400px;
                border-radius: 5px;
            }
            .close-custom {
                color: #aaa;
                float: right;
                font-size: 28px;
                font-weight: bold;
                cursor: pointer;
            }
            .close-custom:hover {
                color: black;
            }
        </style>
    </head>
    <body>

        <jsp:include page="../Common/header.jsp" />

        <div class="container-fluid">
            <div class="row">
                <nav class="col-md-2 d-none d-md-block bg-light sidebar p-0">
                    <jsp:include page="../Common/sidebar.jsp">
                        <jsp:param name="page" value="bookmarks"/>
                    </jsp:include>
                </nav>

                <main class="col-md-10 ms-sm-auto px-md-4 pt-4">

                    <div class="d-flex align-items-start mb-4">
                        <div class="me-4">
                            <img src="${sessionScope.user.avatarUrl != null ? sessionScope.user.avatarUrl : 'assets/img/default-avatar.png'}" 
                                 class="user-avatar-lg" alt="User Avatar">
                        </div>
                        <div class="flex-grow-1">
                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <h1 class="user-name">${sessionScope.user.username != null ? sessionScope.user.username : 'User Name'}</h1>
                                    <div class="user-meta">
                                        <i class="fa-solid fa-cake-candles"></i> Member for 29 days 
                                        <span class="mx-2">|</span> 
                                        <i class="fa-regular fa-clock"></i> Last seen this week 
                                    </div>
                                </div>
                                <div>
                                    <button class="btn btn-outline-secondary btn-sm me-2">Edit profile</button>
                                    <button class="btn btn-outline-secondary btn-sm">Network profile</button>
                                </div>
                            </div>

                            <ul class="nav profile-tabs mt-4">
                                <li class="nav-item"><a class="nav-link" href="../DevQuery/profile">Profile</a></li>
                                <li class="nav-item"><a class="nav-link" href="#">Activity</a></li>
                                <li class="nav-item"><a class="nav-link active" href="#">Saves</a></li>
                                <li class="nav-item"><a class="nav-link" href="#">Settings</a></li>
                            </ul>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-2">
                            <nav class="nav flex-column">
                                <a class="saves-sidebar-item active" href="#">All saves</a>

                                <div class="d-flex justify-content-between align-items-center px-2 mt-3">
                                    <span class="saves-sidebar-header">MY LISTS</span>
                                    <a href="javascript:void(0)" onclick="document.getElementById('createListModal').style.display = 'block'" class="text-decoration-none small">
                                        <i class="fa-solid fa-plus"></i>
                                    </a>
                                </div>

                                <c:forEach items="${myCollections}" var="col">
                                    <div class="saves-sidebar-item d-flex justify-content-between align-items-center">

                                        <a href="#" class="text-decoration-none text-dark text-truncate" style="max-width: 120px;">
                                            ${col.name}
                                        </a>

                                        <a href="${pageContext.request.contextPath}/saves/delete?id=${col.collectionId}" 
                                           class="text-danger small" 
                                           title="Delete list"
                                           onclick="return confirm('Are you sure you want to delete list: ${col.name}? All items inside will be unsaved form this list.');">
                                            <i class="fa-regular fa-trash-can"></i>
                                        </a>
                                    </div>
                                </c:forEach>
                            </nav>
                        </div>

                        <div class="col-md-10">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <div>
                                    <h3 class="mb-0">All saves</h3>
                                    <span class="text-muted">${savedCount} saved items</span>
                                </div>
                                <button class="btn btn-primary btn-sm" style="background-color: #0a95ff;" 
                                        onclick="document.getElementById('createListModal').style.display = 'block'">
                                    Create new list
                                </button>
                            </div>

                            <c:choose>
                                <c:when test="${empty savedList}">
                                    <div class="empty-state-box">
                                        <div class="mb-3">
                                            <i class="fa-solid fa-layer-group fa-3x text-secondary opacity-25"></i>
                                            <i class="fa-solid fa-plus position-absolute ms-4 mt-4 fa-lg text-secondary opacity-50"></i>
                                        </div>
                                        <p class="mb-0">You have no saved items</p>
                                    </div>
                                </c:when>

                                <c:otherwise>
                                    <div class="list-group list-group-flush border-top">
                                        <c:forEach items="${savedList}" var="item">
                                            <div class="saved-item-card">
                                                <a href="${pageContext.request.contextPath}/questions?id=${item.questionId}" class="saved-item-title">
                                                    ${item.questionTitle}
                                                </a>
                                                <div class="saved-meta">
                                                    <span class="text-success fw-bold">Saved</span> 
                                                    <c:if test="${item.createdAt != null}">
                                                        <fmt:formatDate value="${item.createdAt}" pattern="MMM dd, yyyy" />
                                                    </c:if>
                                                    <span class="mx-1">•</span>
                                                    <span class="badge bg-light text-dark border">question</span>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                        </div>
                    </div>
                </main>
            </div>
        </div>
        <div id="createListModal" class="modal-custom">
            <div class="modal-content-custom">
                <span onclick="document.getElementById('createListModal').style.display = 'none'" class="close-custom">&times;</span>
                <h4 style="margin-top: 0;">Create a new list</h4>

                <form action="${pageContext.request.contextPath}/saves/create" method="post">
                    <div class="mb-3 mt-3">
                        <label class="form-label fw-bold">List Name</label>
                        <input type="text" name="listName" class="form-control" placeholder="e.g. Java Tips" required>
                    </div>
                    <div class="text-end">
                        <button type="button" onclick="document.getElementById('createListModal').style.display = 'none'" class="btn btn-light border me-2">Cancel</button>
                        <button type="submit" class="btn btn-primary">Create</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            // Đóng modal khi click ra vùng tối bên ngoài
            var modal = document.getElementById('createListModal');
            window.onclick = function (event) {
                if (event.target == modal) {
                    modal.style.display = "none";
                }
            }
        </script>                        
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>