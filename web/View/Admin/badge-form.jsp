<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${not empty badge ? 'Edit Badge' : 'Create Badge'} - DevQuery Admin</title>
        <style>
            /* 1. Biến dùng chung */
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

            /* 2. Sidebar (Đồng bộ) */
            .sidebar {
                width: 250px; background-color: var(--sidebar-bg); color: #AAB7C4;
                display: flex; flex-direction: column; position: fixed; height: 100%;
            }
            .logo-area {
                height: 60px; background-color: #233140; display: flex; align-items: center;
                justify-content: center; color: white; font-size: 18px; letter-spacing: 1px;
            }
            .nav-menu { list-style: none; margin-top: 20px; }
            .nav-item {
                display: flex; align-items: center; padding: 15px 25px; font-size: 14px;
                cursor: pointer; transition: 0.2s; text-decoration: none; color: inherit;
            }
            .nav-item:hover { background-color: var(--sidebar-hover); color: white; }
            .nav-item.active { background-color: var(--active-orange); color: white; border-left: 4px solid #cc5e05; }
            .nav-icon { margin-right: 12px; font-size: 16px; }
            .logout-area { margin-top: auto; margin-bottom: 20px; }

            /* 3. Main Content & Header */
            .main-content {
                flex-grow: 1; margin-left: 250px; padding-bottom: 30px;
            }
            .top-header {
                height: 60px; background-color: white; border-bottom: 1px solid var(--border-color);
                display: flex; align-items: center; justify-content: space-between; padding: 0 30px;
                position: sticky; top: 0; z-index: 10;
            }
            .page-title { font-size: 20px; font-weight: bold; color: var(--text-main); }
            .admin-profile { display: flex; align-items: center; gap: 10px; }
            .admin-name { font-size: 14px; font-weight: bold; color: var(--text-main); }
            .admin-avatar { width: 35px; height: 35px; border-radius: 50%; background-color: #e1ecf4; padding: 2px; }

            /* 4. Layout Container */
            .container { padding: 30px; max-width: 1200px; margin: 0 auto; }

            /* 5. Form Box Styling */
            .form-card {
                background: var(--card-bg);
                padding: 30px 40px;
                border-radius: 8px;
                max-width: 600px;
                margin: 0 auto; /* Căn giữa form */
                border: 1px solid var(--border-color);
                box-shadow: 0 4px 6px rgba(0,0,0,0.05);
            }
            .form-title {
                font-size: 22px; color: var(--text-main); margin-bottom: 25px; font-weight: bold;
                border-bottom: 1px solid var(--border-color); padding-bottom: 10px;
            }
            .form-group { margin-bottom: 20px; }
            
            .form-label {
                display: block; font-weight: 600; margin-bottom: 8px;
                color: var(--text-main); font-size: 14px;
            }
            
            /* Input Đẹp - Bắt mắt hơn */
            .form-control {
                width: 100%; padding: 10px 14px; border: 1px solid #babfc4;
                border-radius: 5px; font-size: 14px; color: #3b4045;
                transition: all 0.2s ease-in-out; font-family: inherit;
            }
            .form-control:focus {
                outline: none; border-color: #0a95ff;
                box-shadow: 0 0 0 4px rgba(0, 116, 204, 0.15); /* Hiệu ứng phát sáng viền */
            }
            textarea.form-control { resize: vertical; min-height: 100px; }

            /* 6. Buttons */
            .form-actions {
                display: flex; gap: 15px; margin-top: 30px;
            }
            .btn {
                padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer;
                font-size: 14px; font-weight: bold; text-decoration: none;
                display: inline-flex; align-items: center; justify-content: center;
                transition: 0.2s;
            }
            .btn-primary { background-color: #0a95ff; color: white; }
            .btn-primary:hover { background-color: #0074cc; }
            .btn-secondary { background-color: white; color: var(--text-main); border: 1px solid var(--border-color); }
            .btn-secondary:hover { background-color: #f8f9f9; }
        </style>
    </head>
    <body>

        <aside class="sidebar">
            <div class="logo-area"><b>QUERY</b>&nbsp;ADMIN</div>
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
                <a href="${pageContext.request.contextPath}/admin/reports" class="nav-item">
                    <span class="nav-icon">📋</span> Content Reports
                </a>
                <a href="${pageContext.request.contextPath}/admin/badges" class="nav-item active">
                    <span class="nav-icon">🏅</span> Badge Management
                </a>
                <a href="${pageContext.request.contextPath}/admin/blogs" class="nav-item">
                    <span class="nav-icon">📝</span> Blog Management
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
                <div class="page-title">Badge Management</div>
                <div class="admin-profile">
                    <span class="admin-name">${sessionScope.USER.username != null ? sessionScope.USER.username : 'Admin'}</span>
                    <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="Avatar" class="admin-avatar">
                </div>
            </header>

            <div class="container">
                <div class="form-card">
                    <h2 class="form-title">${not empty badge ? '✎ Edit Badge' : '+ Create New Badge'}</h2>

                    <form action="${pageContext.request.contextPath}/admin/badges/${not empty badge ? 'edit' : 'create'}" method="POST">
                        
                        <c:if test="${not empty badge}">
                            <input type="hidden" name="id" value="${badge.badgeId}">
                        </c:if>

                        <div class="form-group">
                            <label class="form-label">Badge Name *</label>
                            <input type="text" name="name" class="form-control" value="${badge.name}" placeholder="e.g. Helpful Hero" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Badge Type</label>
                            <select name="type" class="form-control">
                                <option value="Bronze" ${badge.type == 'Bronze' ? 'selected' : ''}>Bronze</option>
                                <option value="Silver" ${badge.type == 'Silver' ? 'selected' : ''}>Silver</option>
                                <option value="Gold" ${badge.type == 'Gold' ? 'selected' : ''}>Gold</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Required Reputation</label>
                            <input type="number" name="requiredReputation" class="form-control" value="${not empty badge ? badge.requiredReputation : 0}" required min="0">
                        </div>

                        <div class="form-group">
                            <label class="form-label">Description</label>
                            <textarea name="description" class="form-control" placeholder="Describe how users can earn this badge...">${badge.description}</textarea>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">${not empty badge ? 'Update Badge' : 'Save Badge'}</button>
                            <a href="${pageContext.request.contextPath}/admin/badges" class="btn btn-secondary">Cancel</a>
                        </div>
                    </form>
                </div>
            </div>
        </main>
    </body>
</html>