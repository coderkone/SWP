<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tag Management - DevQuery Admin</title>
    <style>
        :root {
            --sidebar-bg: #2D3E50;
            --sidebar-hover: #3A4B5D;
            --active-orange: #F48024;
            --body-bg: #F1F2F3;
            --card-bg: #ffffff;
            --text-main: #2D3E50;
            --text-sub: #838C95;
            --border-color: #d6d9dc;
            --font-stack: -apple-system, BlinkMacSystemFont, "Segoe UI", Arial, sans-serif;
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: var(--font-stack);
            background-color: var(--body-bg);
            display: flex;
            min-height: 100vh;
        }

        .sidebar {
            width: 250px;
            background-color: var(--sidebar-bg);
            color: #AAB7C4;
            display: flex;
            flex-direction: column;
            position: fixed;
            height: 100%;
        }

        .logo-area {
            height: 60px;
            background-color: #233140;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 18px;
            letter-spacing: 1px;
        }

        .nav-menu { list-style: none; margin-top: 20px; }

        .nav-item {
            display: flex;
            align-items: center;
            padding: 15px 25px;
            font-size: 14px;
            cursor: pointer;
            transition: 0.2s;
            text-decoration: none;
            color: inherit;
        }

        .nav-item:hover {
            background-color: var(--sidebar-hover);
            color: white;
        }

        .nav-item.active {
            background-color: var(--active-orange);
            color: white;
            border-left: 4px solid #cc5e05;
        }

        .nav-icon { margin-right: 12px; font-size: 16px; }

        .logout-area { margin-top: auto; margin-bottom: 20px; }

        .main-content {
            flex-grow: 1;
            margin-left: 250px;
            padding-bottom: 30px;
        }

        .top-header {
            height: 60px;
            background-color: white;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 30px;
            position: sticky;
            top: 0;
            z-index: 10;
        }

        .page-title {
            font-size: 20px;
            font-weight: bold;
            color: var(--text-main);
        }

        .admin-profile {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .admin-name {
            font-size: 14px;
            font-weight: bold;
            color: var(--text-main);
        }

        .admin-avatar {
            width: 35px;
            height: 35px;
            border-radius: 50%;
            background-color: #e1ecf4;
            padding: 2px;
        }

        .container {
            padding: 30px;
            max-width: 1400px;
            margin: 0 auto;
        }

        .toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            flex-wrap: wrap;
            gap: 15px;
        }

        .search-box {
            display: flex;
            gap: 10px;
        }

        .search-box input {
            padding: 8px 12px;
            border: 1px solid var(--border-color);
            border-radius: 4px;
            width: 250px;
            font-size: 14px;
        }

        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            transition: 0.2s;
        }

        .btn-primary {
            background-color: #0a95ff;
            color: white;
        }

        .btn-primary:hover {
            background-color: #0074cc;
        }

        .btn-secondary {
            background-color: white;
            color: var(--text-main);
            border: 1px solid var(--border-color);
        }

        .btn-secondary:hover {
            background-color: #f8f9f9;
        }

        .btn-success {
            background-color: #2f6f44;
            color: white;
        }

        .btn-danger {
            background-color: #D0393E;
            color: white;
        }

        .btn-warning {
            background-color: #f48024;
            color: white;
        }

        .btn-sm {
            padding: 4px 8px;
            font-size: 12px;
        }

        .section-box {
            background-color: var(--card-bg);
            border: 1px solid var(--border-color);
            border-radius: 5px;
            overflow: hidden;
        }

        .section-header {
            padding: 15px 20px;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .section-title {
            font-size: 16px;
            font-weight: bold;
            color: var(--text-main);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
        }

        th {
            background-color: #F8F9F9;
            color: var(--text-sub);
            font-weight: bold;
            text-align: left;
            padding: 12px 15px;
            border-bottom: 1px solid var(--border-color);
        }

        td {
            padding: 12px 15px;
            color: #3b4045;
            border-bottom: 1px solid #e3e6e8;
            vertical-align: middle;
        }

        tr:hover { background-color: #f8f9f9; }

        .status-badge {
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: 500;
            text-transform: uppercase;
        }

        .status-active { color: #2f6f44; background: #E3FCEF; }
        .status-inactive { color: #D0393E; background: #FDEDED; }

        .tag-badge {
            display: inline-block;
            padding: 4px 10px;
            background-color: #e1ecf4;
            color: #39739d;
            border-radius: 3px;
            font-size: 12px;
            font-weight: 500;
        }

        .count-badge {
            display: inline-block;
            padding: 2px 8px;
            background-color: #f8f9f9;
            color: var(--text-sub);
            border-radius: 10px;
            font-size: 11px;
        }

        .actions { display: flex; gap: 5px; flex-wrap: wrap; }

        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 5px;
            margin-top: 20px;
        }

        .pagination a, .pagination span {
            padding: 8px 12px;
            border: 1px solid var(--border-color);
            border-radius: 4px;
            text-decoration: none;
            color: var(--text-main);
            font-size: 13px;
        }

        .pagination a:hover { background-color: #f8f9f9; }
        .pagination .active {
            background-color: var(--active-orange);
            color: white;
            border-color: var(--active-orange);
        }

        .alert {
            padding: 12px 16px;
            border-radius: 4px;
            margin-bottom: 20px;
            font-size: 14px;
        }

        .alert-success { background: #E3FCEF; color: #2f6f44; border: 1px solid #2f6f44; }
        .alert-error { background: #FDEDED; color: #D0393E; border: 1px solid #D0393E; }

        .empty-state {
            text-align: center;
            padding: 40px;
            color: var(--text-sub);
        }

        .description-cell {
            max-width: 300px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        /* Modal styles */
        .modal {
            display: none;
            position: fixed;
            z-index: 100;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }

        .modal-content {
            background-color: white;
            margin: 10% auto;
            padding: 25px;
            border-radius: 8px;
            width: 90%;
            max-width: 500px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.3);
        }

        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .modal-title {
            font-size: 18px;
            font-weight: bold;
            color: var(--text-main);
        }

        .modal-close {
            font-size: 24px;
            cursor: pointer;
            color: var(--text-sub);
        }

        .modal-close:hover { color: var(--text-main); }

        .modal-body { margin-bottom: 20px; }

        .modal-body label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: var(--text-main);
        }

        .modal-body select {
            width: 100%;
            padding: 10px;
            border: 1px solid var(--border-color);
            border-radius: 4px;
            font-size: 14px;
        }

        .modal-warning {
            background-color: #FFF4E5;
            border: 1px solid #f48024;
            color: #925d22;
            padding: 12px;
            border-radius: 4px;
            margin-top: 15px;
            font-size: 13px;
        }

        .modal-footer {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }
    </style>
</head>
<body>

<aside class="sidebar">
    <div class="logo-area">
        <b>QUERY</b>&nbsp;ADMIN
    </div>

    <nav class="nav-menu">
        <a href="${pageContext.request.contextPath}/dashboard" class="nav-item">
            <span class="nav-icon">📊</span> Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/admin/users" class="nav-item">
            <span class="nav-icon">👥</span> User Management
        </a>
        <a href="${pageContext.request.contextPath}/admin/tags" class="nav-item active">
            <span class="nav-icon">🏷️</span> Tag Management
        </a>
        <a href="#" class="nav-item">
            <span class="nav-icon">📋</span> Content Reports
        </a>
        <a href="#" class="nav-item">
            <span class="nav-icon">⚙️</span> System Rules
        </a>
    </nav>

    <div class="logout-area">
        <a href="${pageContext.request.contextPath}/logout" class="nav-item">
            <span class="nav-icon">🚪</span> Log Out
        </a>
    </div>
</aside>

<main class="main-content">
    <header class="top-header">
        <div class="page-title">Tag Management</div>
        <div class="admin-profile">
            <span class="admin-name">${sessionScope.USER.username}</span>
            <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="Admin Avatar" class="admin-avatar">
        </div>
    </header>

    <div class="container">

        <!-- Success/Error messages -->
        <c:if test="${param.success == 'created'}">
            <div class="alert alert-success">Tag đã được tạo thành công!</div>
        </c:if>
        <c:if test="${param.success == 'updated'}">
            <div class="alert alert-success">Tag đã được cập nhật thành công!</div>
        </c:if>
        <c:if test="${param.success == 'toggled'}">
            <div class="alert alert-success">Trạng thái tag đã được thay đổi!</div>
        </c:if>
        <c:if test="${param.success == 'merged'}">
            <div class="alert alert-success">Tags đã được gộp thành công!</div>
        </c:if>
        <c:if test="${param.error == 'notfound'}">
            <div class="alert alert-error">Không tìm thấy tag!</div>
        </c:if>
        <c:if test="${param.error == 'merge-same'}">
            <div class="alert alert-error">Không thể gộp tag vào chính nó!</div>
        </c:if>
        <c:if test="${param.error == 'merge-failed'}">
            <div class="alert alert-error">Không thể gộp tags. Vui lòng thử lại!</div>
        </c:if>
        <c:if test="${param.error == 'merge-invalid'}">
            <div class="alert alert-error">Thông tin gộp tag không hợp lệ!</div>
        </c:if>

        <div class="toolbar">
            <form action="${pageContext.request.contextPath}/admin/tags/search" method="get" class="search-box">
                <input type="text" name="q" placeholder="Tìm kiếm tag..."
                       value="${searchKeyword}">
                <button type="submit" class="btn btn-secondary">Tìm kiếm</button>
                <c:if test="${not empty searchKeyword}">
                    <a href="${pageContext.request.contextPath}/admin/tags" class="btn btn-secondary">Xóa filter</a>
                </c:if>
            </form>

            <a href="${pageContext.request.contextPath}/admin/tags/create" class="btn btn-primary">
                + Thêm Tag
            </a>
        </div>

        <!-- Filter Form -->
        <div class="toolbar" style="margin-bottom: 20px;">
            <form action="${pageContext.request.contextPath}/admin/tags" method="get" style="display: flex; gap: 10px; align-items: center;">
                <label style="font-size: 14px; color: var(--text-sub);">Lọc theo Status:</label>
                <select name="status" style="padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 4px; font-size: 14px;">
                    <option value="">Tất cả</option>
                    <option value="active" ${filterStatus == 'active' ? 'selected' : ''}>Active</option>
                    <option value="inactive" ${filterStatus == 'inactive' ? 'selected' : ''}>Inactive</option>
                </select>
                <button type="submit" class="btn btn-secondary">Lọc</button>
                <c:if test="${not empty filterStatus}">
                    <a href="${pageContext.request.contextPath}/admin/tags" class="btn btn-secondary">Xóa bộ lọc</a>
                </c:if>
            </form>
        </div>

        <div class="section-box">
            <div class="section-header">
                <div class="section-title">
                    Danh sách Tags
                    <c:if test="${not empty searchKeyword}">
                        - Kết quả cho "${searchKeyword}"
                    </c:if>
                </div>
                <span style="color: var(--text-sub); font-size: 13px;">
                    Tổng: ${totalTags} tags
                </span>
            </div>

            <c:choose>
                <c:when test="${empty tags}">
                    <div class="empty-state">
                        <p>Không tìm thấy tag nào.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tag Name</th>
                            <th>Description</th>
                            <th>Questions</th>
                            <th>Followers</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="tag" items="${tags}">
                            <tr>
                                <td>${tag.tagId}</td>
                                <td>
                                    <span class="tag-badge">${tag.tagName}</span>
                                </td>
                                <td class="description-cell" title="${tag.description}">
                                    ${tag.description}
                                </td>
                                <td>
                                    <span class="count-badge">${tag.questionCount}</span>
                                </td>
                                <td>
                                    <span class="count-badge">${tag.followerCount}</span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${tag.active}">
                                            <span class="status-badge status-active">Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge status-inactive">Inactive</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="actions">
                                    <a href="${pageContext.request.contextPath}/admin/tags/edit?id=${tag.tagId}"
                                       class="btn btn-secondary btn-sm">Edit</a>

                                    <form action="${pageContext.request.contextPath}/admin/tags/toggle-status"
                                          method="post" style="display:inline;"
                                          onsubmit="return confirm('Bạn có chắc muốn thay đổi trạng thái tag này?');">
                                        <input type="hidden" name="id" value="${tag.tagId}">
                                        <c:choose>
                                            <c:when test="${tag.active}">
                                                <button type="submit" class="btn btn-danger btn-sm">Deactivate</button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="submit" class="btn btn-success btn-sm">Activate</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>

                                    <button type="button" class="btn btn-warning btn-sm"
                                            onclick="openMergeModal(${tag.tagId}, '${tag.tagName}', ${tag.questionCount})">
                                        Merge
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>

        <c:if test="${totalPages > 1 && empty searchKeyword}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="${pageContext.request.contextPath}/admin/tags?page=${currentPage - 1}&status=${filterStatus}">« Prev</a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${i == currentPage}">
                            <span class="active">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/admin/tags?page=${i}&status=${filterStatus}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/admin/tags?page=${currentPage + 1}&status=${filterStatus}">Next »</a>
                </c:if>
            </div>
        </c:if>

    </div>
</main>

<!-- Merge Modal -->
<div id="mergeModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <div class="modal-title">Gộp Tag</div>
            <span class="modal-close" onclick="closeMergeModal()">&times;</span>
        </div>
        <form action="${pageContext.request.contextPath}/admin/tags/merge" method="post" id="mergeForm">
            <input type="hidden" name="sourceId" id="mergeSourceId">
            <div class="modal-body">
                <p style="margin-bottom: 15px;">
                    Gộp tag <strong id="mergeSourceName"></strong> vào tag khác:
                </p>
                <label for="targetId">Chọn tag đích:</label>
                <select name="targetId" id="targetId" required>
                    <option value="">-- Chọn tag --</option>
                    <c:forEach var="t" items="${allActiveTags}">
                        <option value="${t.tagId}">${t.tagName} (${t.questionCount} questions)</option>
                    </c:forEach>
                </select>
                <div class="modal-warning">
                    <strong>Cảnh báo:</strong> Tất cả <span id="mergeQuestionCount">0</span> questions và followers
                    sẽ được chuyển sang tag đích. Tag <span id="mergeSourceName2"></span> sẽ bị xóa vĩnh viễn.
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeMergeModal()">Hủy</button>
                <button type="submit" class="btn btn-warning">Xác nhận Merge</button>
            </div>
        </form>
    </div>
</div>

<script>
    function openMergeModal(sourceId, sourceName, questionCount) {
        document.getElementById('mergeSourceId').value = sourceId;
        document.getElementById('mergeSourceName').textContent = sourceName;
        document.getElementById('mergeSourceName2').textContent = sourceName;
        document.getElementById('mergeQuestionCount').textContent = questionCount;

        // Remove source tag from target options
        var targetSelect = document.getElementById('targetId');
        for (var i = 0; i < targetSelect.options.length; i++) {
            if (targetSelect.options[i].value == sourceId) {
                targetSelect.options[i].style.display = 'none';
            } else {
                targetSelect.options[i].style.display = '';
            }
        }
        targetSelect.value = '';

        document.getElementById('mergeModal').style.display = 'block';
    }

    function closeMergeModal() {
        document.getElementById('mergeModal').style.display = 'none';
    }

    // Close modal when clicking outside
    window.onclick = function(event) {
        var modal = document.getElementById('mergeModal');
        if (event.target == modal) {
            closeMergeModal();
        }
    }

    // Confirm merge before submit
    document.getElementById('mergeForm').onsubmit = function() {
        var targetSelect = document.getElementById('targetId');
        if (!targetSelect.value) {
            alert('Vui lòng chọn tag đích!');
            return false;
        }
        return confirm('Bạn có chắc chắn muốn gộp tag này? Hành động này không thể hoàn tác!');
    }
</script>

</body>
</html>
