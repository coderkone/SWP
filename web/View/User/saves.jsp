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
        body { background-color: #fff; padding-top: 60px}
        .user-avatar-lg { width: 128px; height: 128px; border-radius: 5px; object-fit: cover; border: 4px solid white; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .user-name { font-size: 34px; font-weight: bold; margin-bottom: 4px; }
        .user-meta { font-size: 13px; color: #6a737c; }
        
        /* Tabs */
        .profile-tabs .nav-link { color: #525960; border-radius: 20px; padding: 6px 12px; margin-right: 5px; border: none; }
        .profile-tabs .nav-link:hover { background-color: #e3e6e8; }
        .profile-tabs .nav-link.active { background-color: #f48024; color: white; }

        /* Sidebar Saves */
        .saves-sidebar-item { display: block; padding: 8px 12px; color: #525960; text-decoration: none; font-size: 14px; border-radius: 100px; margin-bottom: 2px;}
        .saves-sidebar-item:hover { background-color: #f8f9f9; color: #000; }
        .saves-sidebar-item.active { font-weight: bold; color: #ffffff; background-color: #f48024 !important; }
        .saves-sidebar-header { font-size: 11px; font-weight: bold; color: #000; text-transform: uppercase; margin-top: 20px; margin-bottom: 10px;}

        /* Item List */
        .saved-item-card { padding: 16px; border-bottom: 1px solid #e3e6e8; }
        .saved-item-title { font-size: 1.1rem; color: #0074cc; text-decoration: none; font-weight: 400; display: block; margin-bottom: 5px; }
        .saved-item-title:hover { color: #0a95ff; }
        .saved-meta { font-size: 12px; color: #6a737c; }

        /* Empty State */
        .empty-state-box { background-color: #f8f9f9; border: 1px dashed #d6d9dc; border-radius: 5px; padding: 40px; text-align: center; margin-top: 30px; color: #6a737c; }
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

                                <div class="mb-3 px-2">
                                    <div class="input-group input-group-sm">
                                        <span class="input-group-text bg-white border-end-0 text-muted">
                                            <i class="fa-solid fa-magnifying-glass"></i>
                                        </span>
                                        <input type="text" id="collectionSearchInput" class="form-control border-start-0 ps-0 shadow-none" placeholder="Find a collection...">
                                    </div>
                                </div>

                                <c:forEach items="${myCollections}" var="col">
                                    <div class="saves-sidebar-item collection-item d-flex justify-content-between align-items-center ${activeListId == col.collectionId ? 'active' : ''}" style="padding-right: 8px;">
                                        <a href="${pageContext.request.contextPath}/saves?listId=${col.collectionId}" 
                                           class="text-decoration-none text-dark text-truncate flex-grow-1 collection-name-text" style="max-width: 90px;">
                                            ${col.name}
                                        </a>
                                        <div class="d-flex">
                                            <a href="javascript:void(0)" class="text-secondary small ms-2 opacity-75 hover-opacity-100" title="Rename" onclick="openRenameModal(${col.collectionId}, '${col.name}')">
                                                <i class="fa-solid fa-pen"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/saves/delete?id=${col.collectionId}" class="text-danger small ms-2 opacity-75 hover-opacity-100" title="Delete" onclick="return confirm('Delete list: ${col.name}?');">
                                                <i class="fa-regular fa-trash-can"></i>
                                            </a>
                                        </div>
                                    </div>
                                </c:forEach>

                                <c:if test="${totalColPages > 1}">
                                    <div class="d-flex justify-content-between align-items-center px-2 mt-3 pt-2 border-top">
                                        <a href="?colPage=${currentColPage - 1}&page=${currentPage}&listId=${activeListId}" class="btn btn-link btn-sm p-0 shadow-none ${currentColPage == 1 ? 'disabled' : ''}">
                                            <i class="fa-solid fa-chevron-left"></i>
                                        </a>
                                        <span class="small text-muted" style="font-size: 11px;">${currentColPage}/${totalColPages}</span>
                                        <a href="?colPage=${currentColPage + 1}&page=${currentPage}&listId=${activeListId}" class="btn btn-link btn-sm p-0 shadow-none ${currentColPage == totalColPages ? 'disabled' : ''}">
                                            <i class="fa-solid fa-chevron-right"></i>
                                        </a>
                                    </div>
                                </c:if>
                            </nav>
                        </div>

                        <div class="col-md-10">
                            <c:set var="currentListName" value="All saves" />
                            <c:forEach items="${myCollections}" var="col">
                                <c:if test="${activeListId == col.collectionId}">
                                    <c:set var="currentListName" value="${col.name}" />
                                </c:if>
                            </c:forEach>

                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <div class="d-flex flex-column w-100 me-3">
                                    <div class="d-flex align-items-baseline mb-2">
                                        <h3 class="mb-0 fw-bold">${currentListName}</h3>
                                        <span class="text-muted ms-3" style="font-size: 14px;">${savedCount} saved items</span>
                                    </div>
                                    <div class="input-group input-group-sm mt-1" style="max-width: 400px;">
                                        <span class="input-group-text bg-white border-end-0 text-muted">
                                            <i class="fa-solid fa-magnifying-glass"></i>
                                        </span>
                                        <input type="text" id="savedItemSearchInput" class="form-control border-start-0 ps-0 shadow-none" placeholder="Search in this list...">
                                    </div>
                                </div>
                                <button class="btn btn-primary btn-sm text-nowrap" style="background-color: #0a95ff; border: none;" onclick="document.getElementById('createListModal').style.display = 'block'">
                                    <i class="fa-solid fa-plus me-1"></i> Create new list
                                </button>
                            </div>

                            <c:choose>
                                <c:when test="${empty savedList}">
                                    <div class="empty-state-box text-center p-5 rounded" style="background-color: #f8f9fa; border: 1px dashed #d6d9dc;">
                                        <i class="fa-solid fa-layer-group fa-3x text-secondary opacity-25"></i>
                                        <p class="mt-3 text-muted fw-medium">You have no saved items</p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="list-group list-group-flush border-top">
                                        <c:forEach items="${savedList}" var="item">
                                            <div class="saved-item-card saved-item-row d-flex justify-content-between align-items-center py-3">
                                                <div>
                                                    <a href="${pageContext.request.contextPath}/question?id=${item.questionId}" 
                                                       class="saved-item-title saved-title-text fw-bold text-decoration-none" style="color: #0074cc; font-size: 16px;">
                                                        ${item.questionTitle} 
                                                    </a>
                                                    <div class="saved-meta mt-1 text-muted">
                                                        Saved on <fmt:formatDate value="${item.createdAt}" pattern="MMM dd, yyyy" />
                                                    </div>
                                                </div>
                                                <div class="d-flex align-items-center">
                                                    <a href="javascript:void(0)" class="text-secondary small ms-3" onclick="openMoveModal(${item.questionId})"><i class="fa-solid fa-folder-tree"></i> Move</a>
                                                    <a href="${pageContext.request.contextPath}/saves/remove?questionId=${item.questionId}&fromCollectionId=${activeListId}" class="text-danger small ms-3" onclick="return confirm('Remove bookmark?');"><i class="fa-solid fa-bookmark-slash"></i> Unsave</a>
                                                </div>

                                            </div>
                                        </c:forEach>
                                    </div>

                                    <div id="noSavedItemsFound" class="text-center p-4 d-none">
                                        <p class="text-muted mb-0"><i class="fa-regular fa-face-frown"></i> No items match your search.</p>
                                    </div>

                                    <c:if test="${totalItemPages > 1}">
                                        <nav aria-label="Page navigation" class="mt-4">
                                            <ul class="pagination pagination-sm justify-content-center">
                                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                    <a class="page-link" href="?listId=${activeListId}&page=${currentPage - 1}&colPage=${currentColPage}">Prev</a>
                                                </li>
                                                <c:forEach begin="1" end="${totalItemPages}" var="i">
                                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                        <a class="page-link" href="?listId=${activeListId}&page=${i}&colPage=${currentColPage}">${i}</a>
                                                    </li>
                                                </c:forEach>
                                                <li class="page-item ${currentPage == totalItemPages ? 'disabled' : ''}">
                                                    <a class="page-link" href="?listId=${activeListId}&page=${currentPage + 1}&colPage=${currentColPage}">Next</a>
                                                </li>
                                            </ul>
                                        </nav>
                                    </c:if>
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
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const searchInput = document.getElementById("collectionSearchInput");
                const collectionItems = document.querySelectorAll(".collection-item");

                if (searchInput) {
                    searchInput.addEventListener("input", function () {
                        // Lấy từ khóa người dùng gõ và viết thường
                        let filterText = this.value.toLowerCase().trim();

                        collectionItems.forEach(function (item) {
                            // Trỏ thẳng vào thẻ <a> chứa tên để lấy văn bản chính xác nhất
                            const nameTag = item.querySelector(".collection-name-text");

                            if (nameTag) {
                                let itemName = nameTag.textContent || nameTag.innerText;
                                itemName = itemName.toLowerCase().trim();
                                // Nếu tên chứa từ khóa tìm kiếm
                                if (itemName.includes(filterText)) {
                                    // Hiện thẻ (Tắt d-none, bật d-flex)
                                    item.classList.remove("d-none");
                                    item.classList.add("d-flex");
                                } else {
                                    // Ẩn thẻ (Tắt d-flex, bật d-none để đè Bootstrap)
                                    item.classList.remove("d-flex");
                                    item.classList.add("d-none");
                                }
                            }
                        });
                    });
                }
            });
        </script>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const itemSearchInput = document.getElementById("savedItemSearchInput");
                const savedItemRows = document.querySelectorAll(".saved-item-row");
                const noItemsMsg = document.getElementById("noSavedItemsFound");

                if (itemSearchInput) {
                    itemSearchInput.addEventListener("input", function () {
                        let filterText = this.value.toLowerCase().trim();
                        let visibleCount = 0;

                        savedItemRows.forEach(function (row) {
                            const titleTag = row.querySelector(".saved-title-text");

                            if (titleTag) {
                                let titleText = titleTag.textContent || titleTag.innerText;
                                titleText = titleText.toLowerCase().trim();

                                if (titleText.includes(filterText)) {
                                    // Hiện bài viết
                                    row.classList.remove("d-none");
                                    row.classList.add("d-flex");
                                    visibleCount++;
                                } else {
                                    // Giấu bài viết
                                    row.classList.remove("d-flex");
                                    row.classList.add("d-none");
                                }
                            }
                        });

                        // Hiện thông báo nếu không tìm thấy bài nào
                        if (visibleCount === 0 && savedItemRows.length > 0) {
                            noItemsMsg.classList.remove("d-none");
                        } else {
                            noItemsMsg.classList.add("d-none");
                        }
                    });
                }
            });
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>