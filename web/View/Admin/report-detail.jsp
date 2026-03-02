<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiet Bao cao - DevQuery Admin</title>
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
            max-width: 1000px;
            margin: 0 auto;
        }

        .back-link {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            color: #0074cc;
            text-decoration: none;
            font-size: 14px;
            margin-bottom: 20px;
        }

        .back-link:hover {
            text-decoration: underline;
        }

        .section-box {
            background-color: var(--card-bg);
            border: 1px solid var(--border-color);
            border-radius: 5px;
            margin-bottom: 20px;
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

        .section-body {
            padding: 20px;
        }

        .info-grid {
            display: grid;
            grid-template-columns: 150px 1fr;
            gap: 12px 20px;
        }

        .info-label {
            font-weight: 500;
            color: var(--text-sub);
            font-size: 13px;
        }

        .info-value {
            color: var(--text-main);
            font-size: 14px;
        }

        .status-badge {
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: 500;
            text-transform: uppercase;
        }

        .status-open { color: #925d22; background: #FFF4E5; }
        .status-resolved { color: #2f6f44; background: #E3FCEF; }

        .type-badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 3px;
            font-size: 12px;
            font-weight: 500;
        }

        .type-question { background-color: #e1ecf4; color: #39739d; }
        .type-answer { background-color: #E3FCEF; color: #2f6f44; }
        .type-comment { background-color: #f8f9f9; color: #6a737c; }

        .content-preview {
            background-color: #f8f9f9;
            border: 1px solid var(--border-color);
            border-radius: 4px;
            padding: 15px;
            margin-top: 10px;
            white-space: pre-wrap;
            word-wrap: break-word;
            max-height: 300px;
            overflow-y: auto;
            font-size: 14px;
            line-height: 1.6;
        }

        .reason-box {
            background-color: #FFF4E5;
            border: 1px solid #f48024;
            border-radius: 4px;
            padding: 15px;
            margin-top: 10px;
        }

        .action-box {
            background-color: #f8f9f9;
            padding: 20px;
            border-top: 1px solid var(--border-color);
        }

        .action-box h4 {
            margin-bottom: 15px;
            color: var(--text-main);
        }

        .btn {
            padding: 10px 20px;
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

        .btn-success {
            background-color: #2f6f44;
            color: white;
        }

        .btn-success:hover {
            background-color: #236335;
        }

        .btn-danger {
            background-color: #D0393E;
            color: white;
        }

        .btn-danger:hover {
            background-color: #b52f33;
        }

        .btn-secondary {
            background-color: white;
            color: var(--text-main);
            border: 1px solid var(--border-color);
        }

        .btn-secondary:hover {
            background-color: #f8f9f9;
        }

        .action-buttons {
            display: flex;
            gap: 10px;
            margin-top: 15px;
        }

        .note-input {
            width: 100%;
            padding: 10px;
            border: 1px solid var(--border-color);
            border-radius: 4px;
            font-size: 14px;
            resize: vertical;
            min-height: 80px;
        }

        .note-label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: var(--text-main);
        }

        .warning-text {
            color: #D0393E;
            font-size: 13px;
            margin-top: 10px;
        }

        .link-original {
            color: #0074cc;
            text-decoration: none;
        }

        .link-original:hover {
            text-decoration: underline;
        }

        .disabled-actions {
            opacity: 0.6;
            pointer-events: none;
        }

        .resolved-notice {
            background-color: #E3FCEF;
            color: #2f6f44;
            padding: 15px;
            border-radius: 4px;
            text-align: center;
            font-weight: 500;
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
        <a href="${pageContext.request.contextPath}/admin/tags" class="nav-item">
            <span class="nav-icon">🏷️</span> Tag Management
        </a>
        <a href="${pageContext.request.contextPath}/admin/reports" class="nav-item active">
            <span class="nav-icon">📋</span> Content Reports
        </a>
        <a href="${pageContext.request.contextPath}/admin/rules" class="nav-item">
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
        <div class="page-title">Chi tiet Bao cao #${report.reportId}</div>
        <div class="admin-profile">
            <span class="admin-name">${sessionScope.USER.username}</span>
            <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="Admin Avatar" class="admin-avatar">
        </div>
    </header>

    <div class="container">

        <a href="${pageContext.request.contextPath}/admin/reports" class="back-link">
            ← Quay lai danh sach
        </a>

        <!-- Report Info -->
        <div class="section-box">
            <div class="section-header">
                <div class="section-title">Thong tin Bao cao</div>
                <c:choose>
                    <c:when test="${report.status == 'open'}">
                        <span class="status-badge status-open">Cho xu ly</span>
                    </c:when>
                    <c:otherwise>
                        <span class="status-badge status-resolved">Da xu ly</span>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="section-body">
                <div class="info-grid">
                    <span class="info-label">ID Bao cao:</span>
                    <span class="info-value">#${report.reportId}</span>

                    <span class="info-label">Loai noi dung:</span>
                    <span class="info-value">
                        <c:choose>
                            <c:when test="${report.targetType == 'question'}">
                                <span class="type-badge type-question">Cau hoi</span>
                            </c:when>
                            <c:when test="${report.targetType == 'answer'}">
                                <span class="type-badge type-answer">Tra loi</span>
                            </c:when>
                            <c:when test="${report.targetType == 'comment'}">
                                <span class="type-badge type-comment">Binh luan</span>
                            </c:when>
                        </c:choose>
                    </span>

                    <span class="info-label">Nguoi bao cao:</span>
                    <span class="info-value">${report.reporterName} (${report.reporterEmail})</span>

                    <span class="info-label">Ngay bao cao:</span>
                    <span class="info-value">
                        <fmt:formatDate value="${report.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/>
                    </span>
                </div>

                <div style="margin-top: 20px;">
                    <span class="info-label">Ly do bao cao:</span>
                    <div class="reason-box">
                        ${report.reason}
                    </div>
                </div>
            </div>
        </div>

        <!-- Target Content -->
        <div class="section-box">
            <div class="section-header">
                <div class="section-title">Noi dung bi bao cao</div>
                <c:if test="${report.questionId > 0}">
                    <a href="${pageContext.request.contextPath}/question?id=${report.questionId}"
                       class="link-original" target="_blank">
                        Xem trang goc →
                    </a>
                </c:if>
            </div>
            <div class="section-body">
                <div class="info-grid">
                    <span class="info-label">Tac gia:</span>
                    <span class="info-value">${report.targetAuthorName}</span>

                    <c:if test="${not empty report.targetTitle}">
                        <span class="info-label">Tieu de:</span>
                        <span class="info-value">${report.targetTitle}</span>
                    </c:if>
                </div>

                <div style="margin-top: 15px;">
                    <span class="info-label">Noi dung:</span>
                    <div class="content-preview">
                        <c:choose>
                            <c:when test="${not empty report.targetBody}">
                                ${report.targetBody}
                            </c:when>
                            <c:otherwise>
                                <em style="color: var(--text-sub);">Khong the tai noi dung</em>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <!-- Action Section -->
        <div class="section-box">
            <div class="section-header">
                <div class="section-title">Xu ly Bao cao</div>
            </div>

            <c:choose>
                <c:when test="${report.status == 'resolved'}">
                    <div class="section-body">
                        <div class="resolved-notice">
                            Bao cao nay da duoc xu ly truoc do.
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="action-box">
                        <label class="note-label">Ghi chu (khong bat buoc):</label>
                        <textarea id="adminNote" class="note-input" placeholder="Nhap ghi chu cho hanh dong nay..."></textarea>

                        <div class="action-buttons">
                            <form action="${pageContext.request.contextPath}/admin/reports/approve" method="post" style="display: inline;">
                                <input type="hidden" name="id" value="${report.reportId}">
                                <input type="hidden" name="note" id="approveNote">
                                <button type="submit" class="btn btn-danger" onclick="document.getElementById('approveNote').value = document.getElementById('adminNote').value; return confirm('Ban co chac muon XAC NHAN VI PHAM? Noi dung se bi an/dong.');">
                                    Xac nhan Vi pham
                                </button>
                            </form>

                            <form action="${pageContext.request.contextPath}/admin/reports/reject" method="post" style="display: inline;">
                                <input type="hidden" name="id" value="${report.reportId}">
                                <input type="hidden" name="note" id="rejectNote">
                                <button type="submit" class="btn btn-success" onclick="document.getElementById('rejectNote').value = document.getElementById('adminNote').value; return confirm('Ban co chac noi dung nay KHONG VI PHAM?');">
                                    Khong Vi pham
                                </button>
                            </form>

                            <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-secondary">
                                Huy
                            </a>
                        </div>

                        <p class="warning-text">
                            * "Xac nhan Vi pham": Noi dung se bi an/dong va bao cao se duoc danh dau da xu ly.<br>
                            * "Khong Vi pham": Bao cao se duoc danh dau da xu ly, noi dung khong thay doi.
                        </p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

    </div>
</main>

</body>
</html>
