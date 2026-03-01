<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tags - DevQuery</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
            background-color: #f1f2f3;
            color: #3b4045;
        }

        header {
            background: white;
            border-bottom: 1px solid #d6d9dc;
            padding: 12px 0;
            position: sticky;
            top: 0;
            z-index: 100;
            border-top: 3px solid #f48024;
        }

        .header-content {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 15px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .logo {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 19px;
            font-weight: 700;
            color: #222;
            text-decoration: none;
        }

        .logo i {
            color: #f48024;
            font-size: 24px;
        }

        .user-section a {
            color: #0074cc;
            text-decoration: none;
            font-weight: 500;
        }

        .container {
            max-width: 1200px;
            margin: 30px auto;
            padding: 0 15px;
        }

        .breadcrumb {
            margin-bottom: 20px;
            font-size: 12px;
        }

        .breadcrumb a {
            color: #0074cc;
            text-decoration: none;
        }

        .breadcrumb span {
            color: #6a737c;
            margin: 0 8px;
        }

        .page-header {
            margin-bottom: 30px;
        }

        .page-title {
            font-size: 32px;
            font-weight: 700;
            color: #222;
            margin-bottom: 10px;
        }

        .page-subtitle {
            font-size: 15px;
            color: #6a737c;
            margin-bottom: 20px;
        }

        .search-box {
            display: flex;
            gap: 10px;
            margin-bottom: 30px;
        }

        .search-input {
            flex: 1;
            max-width: 400px;
            padding: 12px 16px;
            border: 1px solid #c8ccd0;
            border-radius: 3px;
            font-size: 13px;
        }

        .search-input:focus {
            outline: none;
            border-color: #0074cc;
            box-shadow: 0 0 0 4px rgba(0, 116, 204, 0.15);
        }

        .search-btn {
            padding: 10px 16px;
            background: #0a95ff;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 13px;
            font-weight: 500;
        }

        .search-btn:hover {
            background: #0074cc;
        }

        .tags-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .tag-card {
            background: white;
            border: 1px solid #d6d9dc;
            border-radius: 5px;
            padding: 20px;
            transition: all 0.2s;
        }

        .tag-card:hover {
            border-color: #0a95ff;
            box-shadow: 0 2px 8px rgba(0, 116, 204, 0.15);
            transform: translateY(-2px);
        }

        .tag-name {
            font-size: 18px;
            font-weight: 600;
            color: #222;
            margin-bottom: 8px;
        }

        .tag-name a {
            color: #0074cc;
            text-decoration: none;
        }

        .tag-name a:hover {
            color: #0a95ff;
        }

        .tag-badge {
            display: inline-block;
            background: #e1ecf4;
            border: 1px solid #cee0ed;
            color: #0074cc;
            padding: 4px 8px;
            border-radius: 3px;
            font-size: 11px;
            margin-bottom: 12px;
        }

        .tag-description {
            font-size: 13px;
            color: #6a737c;
            margin-bottom: 12px;
            line-height: 1.5;
        }

        .tag-stats {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-top: 12px;
            border-top: 1px solid #e3e6e8;
        }

        .tag-count {
            font-size: 13px;
            color: #6a737c;
        }

        .tag-count-number {
            font-weight: 600;
            color: #222;
        }

        .view-questions {
            padding: 6px 12px;
            background: white;
            border: 1px solid #0074cc;
            color: #0074cc;
            border-radius: 3px;
            text-decoration: none;
            font-size: 12px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.2s;
        }

        .view-questions:hover {
            background: #0074cc;
            color: white;
        }

        .no-results {
            background: white;
            border: 1px solid #d6d9dc;
            border-radius: 5px;
            padding: 30px;
            text-align: center;
            color: #6a737c;
        }

        .no-results h3 {
            color: #222;
            margin-bottom: 10px;
        }

        .stats-summary {
            background: white;
            border: 1px solid #d6d9dc;
            border-radius: 5px;
            padding: 20px;
            margin-bottom: 30px;
            display: flex;
            gap: 30px;
        }

        .stat {
            flex: 1;
        }

        .stat-number {
            font-size: 28px;
            font-weight: 700;
            color: #222;
        }

        .stat-label {
            font-size: 13px;
            color: #6a737c;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <header>
        <div class="header-content">
            <a href="${pageContext.request.contextPath}/home" class="logo">
                <i class="fa-brands fa-stack-overflow"></i>
                <span>Dev<b>Query</b></span>
            </a>
            <div class="user-section">
                <a href="${pageContext.request.contextPath}/auth/logout">Đăng xuất</a>
            </div>
        </div>
    </header>

    <div class="container">
        <!-- Breadcrumb -->
        <div class="breadcrumb">
            <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
            <span>/</span>
            <span>Tags</span>
        </div>

        <!-- Page Header -->
        <div class="page-header">
            <h1 class="page-title">Tags</h1>
            <p class="page-subtitle">Tìm kiếm tags để khám phá câu hỏi có liên quan</p>
        </div>

        <!-- Search Box -->
        <div class="search-box">
            <form method="GET" action="${pageContext.request.contextPath}/tags" style="display: flex; gap: 10px; width: 100%; max-width: 500px;">
                <input type="text" name="search" class="search-input" 
                       placeholder="Tìm tag..." 
                       value="${search != null ? search : ''}">
                <button type="submit" class="search-btn">Tìm kiếm</button>
            </form>
        </div>

        <!-- Stats Summary -->
        <c:if test="${tags != null && tags.size() > 0}">
            <div class="stats-summary">
                <div class="stat">
                    <div class="stat-number">${tags.size()}</div>
                    <div class="stat-label">Tags tìm thấy</div>
                </div>
                <div class="stat">
                    <div class="stat-number">
                        <c:set var="totalQuestions" value="0" />
                        <c:forEach var="tag" items="${tags}">
                            <c:set var="totalQuestions" value="${totalQuestions + tag.questionCount}" />
                        </c:forEach>
                        ${totalQuestions}
                    </div>
                    <div class="stat-label">Tổng số câu hỏi</div>
                </div>
            </div>
        </c:if>

        <!-- Tags Grid -->
        <c:if test="${tags != null && tags.size() > 0}">
            <div class="tags-grid">
                <c:forEach var="tag" items="${tags}">
                    <div class="tag-card">
                        <div class="tag-name">
                            <a href="#tag-${tag.tagId}">${tag.tagName}</a>
                        </div>
                        <div class="tag-badge">${tag.tagName}</div>
                        <div class="tag-description">
                            Có <strong>${tag.questionCount}</strong> câu hỏi sử dụng tag này
                        </div>
                        <div class="tag-stats">
                            <div class="tag-count">
                                <span class="tag-count-number">${tag.questionCount}</span> câu hỏi
                            </div>
                            <a href="${pageContext.request.contextPath}/home?tag=${tag.tagName}" class="view-questions">
                                Xem câu hỏi →
                            </a>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:if>

        <!-- No Results -->
        <c:if test="${tags == null || tags.size() == 0}">
            <div class="no-results">
                <h3>🔍 Không tìm thấy tags</h3>
                <p>Không có tags phù hợp với tìm kiếm của bạn. Vui lòng thử tên khác.</p>
            </div>
        </c:if>
    </div>

    <script src="${pageContext.request.contextPath}/assets/js/vendor/jquery-3.6.0.min.js"></script>
</body>
</html>
