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
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            }
            .user-name {
                font-size: 34px;
                font-weight: normal;
                margin-bottom: 4px;
            }
            .user-meta {
                font-size: 13px;
                color: #6a737c;
            }

            /* Tabs */
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

            /* Sidebar Saves */
            .saves-sidebar-item {
                display: block;
                padding: 6px 12px;
                color: #525960;
                text-decoration: none;
                font-size: 13px;
                border-radius: 100px;
                margin-bottom: 2px;
            }
            .saves-sidebar-item:hover {
                background-color: #f8f9f9;
                color: #0c0d0e;
            }
            /* Đổi màu active thành xám nhạt thay vì cam */
            .saves-sidebar-item.active {
                font-weight: bold;
                background-color: #f1f2f3;
                color: #0c0d0e;
            }
            .saves-sidebar-header {
                font-size: 11px;
                font-weight: bold;
                color: #6a737c;
                text-transform: uppercase;
                margin-top: 20px;
                margin-bottom: 5px;
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

        <div class="container-fluid" style="max-width: 1264px; margin: 0 auto;">
            <div class="row">
                <nav class="col-md-2 d-none d-md-block bg-light sidebar p-0 pt-4" style="border-right: 1px solid #d6d9dc; min-height: 100vh;">
                    <jsp:include page="../Common/sidebar.jsp" />
                </nav>

                <main class="col-md-10 ms-sm-auto px-md-4 pt-4">

                    <div class="d-flex align-items-start mb-4">
                        <div class="me-4">
                            <img src="${sessionScope.user.avatarUrl != null ? sessionScope.user.avatarUrl : 'https://cdn-icons-png.flaticon.com/512/3135/3135715.png'}" 
                                 class="user-avatar-lg" alt="Avatar">
                        </div>
                        <div class="flex-grow-1">
                            <h1 class="user-name">${userProfile.username != null ? userProfile.username : 'Developer'}</h1>
                            <div class="user-meta mb-3">
                                <i class="fa-solid fa-cake-candles"></i> Member since <fmt:formatDate value="${userProfile.createdAt}" pattern="dd/MM/yyyy" />
                                <span class="mx-2">|</span> 
                                <i class="fa-solid fa-star text-warning"></i> ${userProfile.reputation} reputation
                            </div>

                            <ul class="nav profile-tabs">
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/profile">Profile</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/badge">Badge</a></li>
                                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/saves">Saves</a></li>
                                <li class="nav-item"><a class="nav-link" href="#">Settings</a></li>
                            </ul>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-2">
                            <nav class="nav flex-column">
                                <a href="${pageContext.request.contextPath}/saves" 
                                   class="saves-sidebar-item ${activeListId == '' ? 'active fw-bold' : ''}" 
                                   style="display: block; text-decoration: none; color: #232629; padding: 6px 12px; border-radius: 100px;">
                                    All saves
                                </a>

                                <div class="d-flex justify-content-between align-items-center px-2 mt-3">
                                    <span class="saves-sidebar-header">MY LISTS</span>
                                    <a href="javascript:void(0)" onclick="document.getElementById('createListModal').style.display = 'block'" class="text-decoration-none small">
                                        <i class="fa-solid fa-plus"></i>
                                    </a>
                                </div>

                                <c:forEach items="${myCollections}" var="col">
                                    <div class="saves-sidebar-item d-flex justify-content-between align-items-center ${activeListId == col.collectionId ? 'active' : ''}" style="padding-right: 8px;">

                                        <a href="${pageContext.request.contextPath}/saves?listId=${col.collectionId}" 
                                           class="text-decoration-none text-dark text-truncate flex-grow-1" style="max-width: 90px;">
                                            ${col.name}
                                        </a>

                                        <div class="d-flex">
                                            <a href="javascript:void(0)" 
                                               class="text-secondary small ms-2 opacity-75 hover-opacity-100" 
                                               title="Rename list"
                                               onclick="openRenameModal(${col.collectionId}, '${col.name}')">
                                                <i class="fa-solid fa-pen"></i>
                                            </a>

                                            <a href="${pageContext.request.contextPath}/saves/delete?id=${col.collectionId}" 
                                               class="text-danger small ms-2 opacity-75 hover-opacity-100" 
                                               title="Delete list"
                                               onclick="return confirm('Are you sure you want to delete list: ${col.name}?');">
                                                <i class="fa-regular fa-trash-can"></i>
                                            </a>
                                        </div>
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
                                            <div class="saved-item-card d-flex justify-content-between align-items-center">

                                                <div>
                                                    <a href="${pageContext.request.contextPath}/question?id=${item.questionId}" class="saved-item-title fw-bold">
                                                        ${item.title} 
                                                    </a>
                                                    <div class="saved-meta mt-1">
                                                        Saved on <fmt:formatDate value="${item.createdAt}" pattern="MMM dd, yyyy" />
                                                    </div>
                                                </div>

                                                <div class="d-flex align-items-center">
                                                    <a href="javascript:void(0)" 
                                                       class="text-secondary small mt-1 ms-3 hover-opacity-100" 
                                                       style="text-decoration: none;"
                                                       onclick="openMoveModal(${item.questionId})"
                                                       title="Move to another list">
                                                        <i class="fa-solid fa-folder-tree"></i> Move
                                                    </a>

                                                    <a href="${pageContext.request.contextPath}/saves/remove?questionId=${item.questionId}&fromCollectionId=${activeListId}" 
                                                       class="text-danger small mt-1 ms-3" 
                                                       style="text-decoration: none;"
                                                       onclick="return confirm('Bạn có chắc chắn muốn bỏ lưu bài viết này?');"
                                                       title="Unsave">
                                                        <i class="fa-solid fa-bookmark-slash"></i> Unsave
                                                    </a>
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
        <div id="renameListModal" class="modal-custom">
            <div class="modal-content-custom">
                <span onclick="document.getElementById('renameListModal').style.display = 'none'" class="close-custom">&times;</span>
                <h4 style="margin-top: 0;">Rename list</h4>

                <form action="${pageContext.request.contextPath}/saves/rename" method="post">
                    <input type="hidden" id="renameCollectionId" name="collectionId" value="">

                    <div class="mb-3 mt-3">
                        <label class="form-label fw-bold">New List Name</label>
                        <input type="text" id="renameInput" name="newName" class="form-control" required>
                    </div>
                    <div class="text-end">
                        <button type="button" onclick="document.getElementById('renameListModal').style.display = 'none'" class="btn btn-light border me-2">Cancel</button>
                        <button type="submit" class="btn btn-primary">Save</button>
                    </div>
                </form>
            </div>
        </div>
        <div id="moveBookmarkModal" class="modal-custom">
            <div class="modal-content-custom">
                <span onclick="document.getElementById('moveBookmarkModal').style.display = 'none'" class="close-custom">&times;</span>
                <h4 style="margin-top: 0;">Move saved item</h4>

                <form action="${pageContext.request.contextPath}/saves/move" method="post">
                    <input type="hidden" id="moveQuestionId" name="questionId" value="">

                    <div class="mb-4 mt-3">
                        <label class="form-label fw-bold">Select destination list</label>
                        <select name="collectionId" class="form-select">
                            <option value="">-- All saves (No specific list) --</option>

                            <c:forEach items="${myCollections}" var="col">
                                <option value="${col.collectionId}">${col.name}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="text-end">
                        <button type="button" onclick="document.getElementById('moveBookmarkModal').style.display = 'none'" class="btn btn-light border me-2">Cancel</button>
                        <button type="submit" class="btn btn-primary">Move Item</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            // 1. Hàm mở Modal Rename
            function openRenameModal(id, currentName) {
                document.getElementById('renameCollectionId').value = id;
                document.getElementById('renameInput').value = currentName;
                document.getElementById('renameListModal').style.display = 'block';
            }

            // 2. Modal Move
            function openMoveModal(questionId) {
                document.getElementById('moveQuestionId').value = questionId;
                document.getElementById('moveBookmarkModal').style.display = 'block';
            }

            // 3. Close event
            window.addEventListener('click', function (event) {
                var createModal = document.getElementById('createListModal');
                var renameModal = document.getElementById('renameListModal');
                var moveModal = document.getElementById('moveBookmarkModal');

                if (event.target == createModal) {
                    createModal.style.display = "none";
                }
                if (event.target == renameModal) {
                    renameModal.style.display = "none";
                }
                if (event.target == moveModal) {
                    moveModal.style.display = "none";
                }
            });
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>