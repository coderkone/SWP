<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<<<<<<< Updated upstream
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đặt câu hỏi - DevQuery</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

        <style>
            /* --- 1. CÀI ĐẶT CƠ BẢN & MENU (CODE MỚI) --- */
            * {
                box-sizing: border-box;
                margin: 0;
                padding: 0;
                font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Liberation Sans", sans-serif;
            }

            body {
                background-color: #f1f2f3;
                color: #3b4045;
            }

            /* --- Header Mới --- */
            header {
                background-color: white;
                height: 53px; /* 50px + 3px border */
                display: flex;
                align-items: center;
                justify-content: space-between; /* Để đẩy nút Logout sang phải */
                box-shadow: 0 1px 2px rgba(0,0,0,0.05);
                border-top: 3px solid #f48024;
                position: fixed;
                top: 0;
                width: 100%;
                z-index: 1000;
                padding: 0 10px;
            }

            .header-left {
                display: flex;
                align-items: center;
            }

            /* Nút Hamburger */
            .menu-btn {
                background: none;
                border: none;
                cursor: pointer;
                padding: 0 15px;
                font-size: 18px;
                color: #525960;
                transition: color 0.2s;
            }
            .menu-btn:hover {
                color: #232629;
            }

            /* Logo Stack Overflow Style */
            .logo {
                display: flex;
                align-items: center;
                margin-left: 5px;
                cursor: pointer;
                text-decoration: none;
                color: black;
            }
            .logo i {
                color: #f48024;
                font-size: 24px;
                margin-right: 5px;
            }
            .logo span {
                font-size: 18px;
                font-weight: 400;
            }
            .logo span b {
                font-weight: 700;
            }

            /* Logout Link */
            .header-right {
                padding-right: 15px;
                font-size: 13px;
            }
            .header-right a {
                text-decoration: none;
                color: #525960;
                padding: 8px 12px;
                border-radius: 1000px;
            }
            .header-right a:hover {
                background-color: #e3e6e8;
                color: #232629;
            }

            /* --- Sidebar (Menu Trái) --- */
            .sidebar {
                position: fixed;
                top: 53px;
                left: -240px; /* Ẩn mặc định */
                width: 240px;
                height: calc(100vh - 53px);
                background-color: white;
                box-shadow: 1px 0 3px rgba(0,0,0,0.05);
                transition: left 0.3s ease;
                padding-top: 20px;
                overflow-y: auto;
                border-right: 1px solid #e3e6e8;
                z-index: 999;
            }
            .sidebar.active {
                left: 0;
            }

            .nav-list {
                list-style: none;
                padding: 0;
            }
            .nav-link {
                display: flex;
                align-items: center;
                padding: 10px 15px;
                color: #525960;
                text-decoration: none;
                font-size: 14px;
                transition: all 0.2s;
            }
            .nav-link:hover {
                color: #232629;
                background-color: #f1f2f3;
                border-right: 3px solid #f48024;
            }
            .nav-link i {
                width: 20px;
                text-align: center;
                margin-right: 10px;
                font-size: 15px;
            }
            .nav-link.selected {
                background-color: #f1f2f3;
                color: #232629;
                font-weight: bold;
                border-right: 3px solid #f48024;
            }

            /* Collectives Section */
            .collectives-section {
                margin-top: 20px;
                padding: 0 15px;
            }
            .collectives-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                font-size: 11px;
                font-weight: bold;
                color: #525960;
                margin-bottom: 10px;
                text-transform: uppercase;
            }
            .collectives-text {
                font-size: 13px;
                color: #3b4045;
                margin-bottom: 8px;
                line-height: 1.4;
            }
            .collectives-link {
                font-size: 13px;
                color: #0074cc;
                text-decoration: none;
            }

            /* --- 2. CSS CHO FORM (CODE CŨ GIỮ LẠI) --- */

            /* Layout Chính */
            .main-wrap {
                max-width: 1200px;
                /* Tăng margin-top để tránh bị header che (53px header + 30px khoảng cách) */
                margin: 83px auto 28px auto;
                padding: 0 16px;
                display: flex;
                gap: 32px;
            }

            /* Left Form */
            .form-left {
                flex: 2.2;
            }
            .section-box {
                background: white;
                border: 1px solid #d6d9dc;
                border-radius: 6px;
                padding: 22px;
                margin-bottom: 20px;
            }
            .section-title {
                font-weight: bold;
                font-size: 15px;
                margin-bottom: 14px;
                padding-bottom: 12px;
                border-bottom: 1px solid #e2e3e4;
            }
            .form-group {
                margin-bottom: 20px;
            }
            .label {
                font-size: 14px;
                font-weight: 600;
                margin-bottom: 6px;
                display: block;
            }
            .label-help {
                margin-top: 4px;
                font-size: 12px;
                color: #6a737c;
            }

            /* Inputs */
            .input {
                width: 100%;
                padding: 10px;
                font-size: 13px;
                border-radius: 6px;
                border: 1px solid #c8ccd0;
            }
            .input:focus {
                border-color: #0a95ff;
                outline: none;
                box-shadow: 0 0 0 4px rgba(10,149,255,0.15);
            }

            /* Editor */
            .editor-wrapper {
                border: 1px solid #c8ccd0;
                border-radius: 6px;
                background: white;
            }
            .toolbar {
                border-bottom: 1px solid #c8ccd0;
                padding: 6px;
                background: #fbfbfb;
            }
            .toolbar button {
                padding: 4px 7px;
                margin-right: 4px;
                border: 1px solid #d6d9dc;
                background: white;
                font-size: 12px;
                border-radius: 4px;
                cursor: pointer;
            }
            textarea.editor {
                width: 100%;
                height: 180px;
                padding: 10px;
                border: none;
                font-family: Consolas, monospace;
                font-size: 12px;
                resize: vertical;
                outline: none;
            }

            /* Tags */
            .tag-badge {
                background: #e1ecf4;
                border: 1px solid #bcd0e2;
                color: #3b4045;
                padding: 4px 8px;
                border-radius: 3px;
                font-size: 12px;
                margin-right: 5px;
                display: inline-block;
            }

            /* Buttons */
            .btn-primary {
                background: #0a95ff;
                color: white;
                padding: 10px 16px;
                border-radius: 6px;
                border: none;
                cursor: pointer;
                font-size: 14px;
                font-weight: 500;
            }
            .btn-primary:hover {
                background: #0074cc;
            }
            .btn-secondary {
                background: white;
                border-radius: 6px;
                border: 1px solid #c8ccd0;
                padding: 10px 14px;
                cursor: pointer;
                text-decoration: none;
                color: #3b4045;
                display: inline-block;
            }

            /* Right Sidebar (Tips) */
            .side-right {
                flex: 1;
            }
            .side-box {
                background: white;
                border: 1px solid #d6d9dc;
                border-radius: 6px;
                padding: 14px;
                margin-bottom: 15px;
                font-size: 13px;
            }

            .btn-logout {
                background-color: var(--blue-tag-bg);
                color: var(--blue-tag-text);
                border: 1px solid #7aa7c7;
                padding: 8px 12px;
                border-radius: 3px;
                cursor: pointer;
                font-weight: normal;
            }

            .btn-logout:hover {
                background-color: #b3d3ea;
            }

            /* Search Box */
            .search-box {
                flex: 1;
                max-width: 600px;
                margin: 0 20px;
            }
            .search-input {
                width: 100%;
                padding: 8px 12px;
                font-size: 13px;
                border: 1px solid #c8ccd0;
                border-radius: 4px;
                background-color: #f8f9fa;
            }
            .search-input:focus {
                background-color: white;
                border-color: #0a95ff;
                outline: none;
            }

        </style>
    </head>

    <body>

        <header>
            <div class="header-left">
                <button class="menu-btn" onclick="toggleMenu()">
                    <i class="fa-solid fa-bars"></i>
                </button>

                <a href="${pageContext.request.contextPath}/home" class="logo">
                    <i class="fa-brands fa-stack-overflow"></i>
                    <span>Dev<b>Query</b></span>
                </a>
            </div>

            <div class="search-box">
                <form method="get" action="${pageContext.request.contextPath}/home">
                    <input type="text" name="q" class="search-input" placeholder="Search...">
                </form>
            </div>

            <div class="header-right">
                <a class="btn-logout" href="<%=request.getContextPath()%>/logout">Log out</a>
            </div>
        </header>

        <div class="sidebar" id="sidebar">
            <ul class="nav-list">
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/home" class="nav-link">
                        <i class="fa-solid fa-house"></i>
                        <span>Home</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link selected"> <i class="fa-solid fa-earth-americas"></i>
                        <span>Questions</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fa-solid fa-tags"></i>
                        <span>Tags</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fa-solid fa-bookmark"></i>
                        <span>Saves</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fa-solid fa-message"></i>
                        <span>Chat</span>
                    </a>
                </li>
            </ul>

            <div class="collectives-section">
                <div class="collectives-header">
                    <span>Collectives</span>
                    <i class="fa-solid fa-plus"></i>
                </div>
                <p class="collectives-text">Communities for your favorite technologies.</p>
                <a href="#" class="collectives-link">Explore all Collectives</a>
            </div>
        </div>

        <div class="main-wrap">

            <div class="form-left">

                <% if (request.getAttribute("error") != null) { %>
                <div class="side-box" style="border-left: 4px solid #d9534f;">
                    ⚠️ <%= request.getAttribute("error") %>
                </div>
                <% } %>

                <form id="askForm" method="post" action="${pageContext.request.contextPath}/questions/create">
                    <div class="section-box">
                        <div class="section-title">Tiêu đề</div>

                        <div class="form-group">
                            <label class="label">Tiêu đề câu hỏi</label>
                            <input type="text" name="title" class="input"
                                   placeholder="VD: Làm sao sửa lỗi NullPointerException trong Java?"
                                   minlength="10" maxlength="150" required>
                            <div class="label-help">Hãy mô tả ngắn gọn và rõ ràng vấn đề</div>
                        </div>
                    </div>

                    <div class="section-box">
                        <div class="section-title">Chi tiết vấn đề</div>

                        <div class="form-group">
                            <label class="label">Mô tả vấn đề</label>

                            <div class="editor-wrapper">
                                <div class="toolbar">
                                    <button type="button">B</button>
                                    <button type="button">I</button>
                                    <button type="button">`</button>
                                    <button type="button">{ }</button>
                                </div>
                                <textarea name="body" class="editor"
                                          placeholder="- Bạn đang cố làm gì?
                                          - Bạn đã thử gì?
                                          - Kết quả nhận được?
                                          - Bạn mong đợi gì?"
                                          minlength="20" required></textarea>
                            </div>

                        </div>
                    </div>

                    <div class="section-box">
                        <div class="section-title">Code liên quan (tùy chọn)</div>

                        <textarea name="codeSnippet" class="input"
                                  style="min-height:120px; font-family:monospace;"
                                  placeholder="// Dán code của bạn tại đây"></textarea>
                    </div>

                    <div class="section-box">
                        <div class="section-title">Tags</div>

                        <div class="form-group">
                            <label class="label">Nhập tối đa 5 tags (cách nhau bằng dấu phẩy)</label>
                            <input type="text" id="tags" name="tags" class="input" placeholder="vd: java, spring, api">
                            <div id="tagsPreview" style="margin-top:10px;"></div>
                        </div>
                    </div>

                    <button type="submit" class="btn-primary">Đăng câu hỏi</button>
                    <a href="${pageContext.request.contextPath}/home" class="btn-secondary">Hủy</a>
                </form>

            </div>

            <div class="side-right">
                <div class="side-box">
                    <strong>1. Viết tiêu đề rõ ràng</strong>
                    <p>Mô tả đúng lỗi hoặc mục tiêu của bạn.</p>
                </div>
                <div class="side-box">
                    <strong>2. Giải thích bạn đã thử gì</strong>
                    <p>Thêm code và mô tả kết quả đã xảy ra.</p>
                </div>
                <div class="side-box">
                    <strong>3. Cung cấp chi tiết đầy đủ</strong>
                    <p>Giúp người khác tái hiện lỗi để trả lời chính xác.</p>
                </div>
            </div>

        </div>

        <script>
            // Xử lý bật tắt Menu
            function toggleMenu() {
                var sidebar = document.getElementById('sidebar');
                sidebar.classList.toggle('active');
            }

            // Xử lý Preview Tags (Code cũ)
            document.getElementById("tags").addEventListener("input", function () {
                const preview = document.getElementById("tagsPreview");
                preview.innerHTML = "";

                let tags = this.value.split(",").map(t => t.trim()).filter(t => t).slice(0, 5);

                tags.forEach(t => {
                    const span = document.createElement("span");
                    span.className = "tag-badge";
                    span.innerText = t;
                    preview.appendChild(span);
                });
            });
        </script>

    </body>
=======
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt câu hỏi - DevQuery</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        /* --- 1. CÀI ĐẶT CƠ BẢN --- */
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Liberation Sans", sans-serif;
        }

        body {
            background-color: #f1f2f3;
            color: #3b4045;
            /* Thêm transition cho body nếu cần, nhưng chủ yếu xử lý ở main-wrap */
            overflow-x: hidden; /* Tránh thanh cuộn ngang khi slide */
        }

        /* --- HEADER (FIXED) --- */
        header {
            background-color: white;
            height: 53px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            box-shadow: 0 1px 2px rgba(0,0,0,0.05);
            border-top: 3px solid #f48024;
            position: fixed;
            top: 0;
            width: 100%;
            z-index: 1000;
            padding: 0 10px;
        }

        .header-left { display: flex; align-items: center; }

        .menu-btn {
            background: none;
            border: none;
            cursor: pointer;
            padding: 0 15px;
            font-size: 18px;
            color: #525960;
            transition: color 0.2s;
        }
        .menu-btn:hover { color: #232629; }

        .logo {
            display: flex; align-items: center;
            margin-left: 5px; cursor: pointer;
            text-decoration: none; color: black;
        }
        .logo i { color: #f48024; font-size: 24px; margin-right: 5px; }
        .logo span { font-size: 18px; font-weight: 400; }
        .logo span b { font-weight: 700; }

        .header-right { padding-right: 15px; font-size: 13px; }
        .btn-logout {
            background-color: #e1ecf4; color: #39739d;
            border: 1px solid #7aa7c7; padding: 8px 12px;
            border-radius: 3px; cursor: pointer; text-decoration: none;
        }
    
.btn-logout {
    background-color: #e1ecf4;
    color: #39739d;
    border: 1px solid #7aa7c7;
    padding: 8px 12px;
    border-radius: 1000px; /* Đã sửa: Bo tròn tối đa */
    cursor: pointer;
    text-decoration: none;
    font-size: 13px;
    transition: all 0.2s; /* Thêm hiệu ứng mượt mà */
}

.btn-logout:hover {
    background-color: #b3d3ea;
    color: #2c5777;
    border-color: #39739d;
}

        .btn-logout:hover { background-color: #b3d3ea; }

        /* Search Box */
        .search-box { flex: 1; max-width: 600px; margin: 0 20px; }
        .search-input {
            width: 100%; padding: 8px 12px; font-size: 13px;
            border: 1px solid #c8ccd0; border-radius: 4px; background-color: #f8f9fa;
        }
        .search-input:focus { background-color: white; border-color: #0a95ff; outline: none; }


        /* --- SIDEBAR & SLIDE LOGIC --- */
        .sidebar {
            position: fixed;
            top: 53px; /* Chiều cao header */
            left: -240px; /* Ẩn mặc định */
            width: 240px;
            height: calc(100vh - 53px);
            background-color: white;
            box-shadow: 1px 0 3px rgba(0,0,0,0.05);
            transition: left 0.3s ease; /* Hiệu ứng trượt */
            padding-top: 20px;
            overflow-y: auto;
            border-right: 1px solid #e3e6e8;
            z-index: 999;
        }

        .nav-list { list-style: none; padding: 0; }
        .nav-link {
            display: flex; align-items: center; padding: 10px 15px;
            color: #525960; text-decoration: none; font-size: 14px; transition: all 0.2s;
        }
        .nav-link:hover { color: #232629; background-color: #f1f2f3; border-right: 3px solid #f48024; }
        .nav-link i { width: 20px; text-align: center; margin-right: 10px; font-size: 15px; }
        .nav-link.selected {
            background-color: #f1f2f3; color: #232629; font-weight: bold; border-right: 3px solid #f48024;
        }

        .collectives-section { margin-top: 20px; padding: 0 15px; }
        .collectives-header { display: flex; justify-content: space-between; font-size: 11px; font-weight: bold; color: #525960; margin-bottom: 10px; text-transform: uppercase; }
        .collectives-text { font-size: 13px; color: #3b4045; margin-bottom: 8px; line-height: 1.4; }
        .collectives-link { font-size: 13px; color: #0074cc; text-decoration: none; }

        /* --- MAIN WRAPPER (NỘI DUNG CHÍNH) --- */
        .main-wrap {
            max-width: 1200px;
            /* Margin top 83px để tránh header, auto để căn giữa lúc đầu */
            margin: 83px auto 28px auto;
            padding: 0 16px;
            display: flex;
            gap: 32px;
            transition: margin-left 0.3s ease, width 0.3s ease; /* Hiệu ứng đẩy */
            position: relative;
        }

        /* --- LOGIC TRƯỢT/ĐẨY (SLIDE PUSH) --- */

        /* Khi body có class 'menu-open', sidebar trượt ra */
        body.menu-open .sidebar {
            left: 0;
        }

        /* 1. TRÊN DESKTOP (Màn hình lớn >= 992px): Đẩy nội dung */
        @media (min-width: 992px) {
            body.menu-open .main-wrap {
                /* Đẩy nội dung sang phải bằng đúng chiều rộng sidebar + khoảng đệm */
                margin-left: 240px; 
                /* Tính toán lại chiều rộng để không bị tràn màn hình */
                width: calc(100% - 240px);
                max-width: none; /* Bỏ max-width mặc định để dàn trải đẹp hơn */
                padding-right: 30px; /* Thêm padding phải cho cân đối */
            }
        }

        /* 2. TRÊN MOBILE (Màn hình nhỏ < 992px): Đè lên (Overlay) */
        @media (max-width: 991px) {
            body.menu-open .sidebar {
                box-shadow: 5px 0 15px rgba(0,0,0,0.2);
            }
            
            /* Lớp phủ tối màu khi mở menu trên mobile */
            body.menu-open::before {
                content: '';
                position: fixed;
                top: 53px; left: 0; right: 0; bottom: 0;
                background: rgba(0,0,0,0.4);
                z-index: 998;
                animation: fadeIn 0.3s;
            }
            @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
        }


        /* --- CSS FORM & INPUTS (GIỮ NGUYÊN) --- */
        .form-left { flex: 2.2; }
        .side-right { flex: 1; }
        
        .section-box { background: white; border: 1px solid #d6d9dc; border-radius: 6px; padding: 22px; margin-bottom: 20px; }
        .section-title { font-weight: bold; font-size: 15px; margin-bottom: 14px; padding-bottom: 12px; border-bottom: 1px solid #e2e3e4; }
        
        .form-group { margin-bottom: 20px; }
        .label { font-size: 14px; font-weight: 600; margin-bottom: 6px; display: block; }
        .label-help { margin-top: 4px; font-size: 12px; color: #6a737c; }
        
        .input { width: 100%; padding: 10px; font-size: 13px; border-radius: 6px; border: 1px solid #c8ccd0; }
        .input:focus { border-color: #0a95ff; outline: none; box-shadow: 0 0 0 4px rgba(10,149,255,0.15); }
        
        .editor-wrapper { border: 1px solid #c8ccd0; border-radius: 6px; background: white; }
        .toolbar { border-bottom: 1px solid #c8ccd0; padding: 6px; background: #fbfbfb; }
        .toolbar button { padding: 4px 7px; margin-right: 4px; border: 1px solid #d6d9dc; background: white; font-size: 12px; border-radius: 4px; cursor: pointer; }
        textarea.editor { width: 100%; height: 180px; padding: 10px; border: none; font-family: Consolas, monospace; font-size: 12px; resize: vertical; outline: none; }
        
        .tag-badge { background: #e1ecf4; border: 1px solid #bcd0e2; color: #3b4045; padding: 4px 8px; border-radius: 3px; font-size: 12px; margin-right: 5px; display: inline-block; }
        
        .btn-primary { background: #0a95ff; color: white; padding: 10px 16px; border-radius: 6px; border: none; cursor: pointer; font-size: 14px; font-weight: 500; }
        .btn-primary:hover { background: #0074cc; }
        .btn-secondary { background: white; border-radius: 6px; border: 1px solid #c8ccd0; padding: 10px 14px; cursor: pointer; text-decoration: none; color: #3b4045; display: inline-block; }
        
        .side-box { background: white; border: 1px solid #d6d9dc; border-radius: 6px; padding: 14px; margin-bottom: 15px; font-size: 13px; }
    </style>
</head>

<body>

    <header>
        <div class="header-left">
            <button class="menu-btn" onclick="toggleMenu()">
                <i class="fa-solid fa-bars"></i>
            </button>

            <a href="${pageContext.request.contextPath}/home" class="logo">
                <i class="fa-brands fa-stack-overflow"></i>
                <span>Dev<b>Query</b></span>
            </a>
        </div>

        <div class="search-box">
            <form method="get" action="${pageContext.request.contextPath}/home">
                <input type="text" name="q" class="search-input" placeholder="Search...">
            </form>
        </div>

        <div class="header-right">
            <a class="btn-logout" href="<%=request.getContextPath()%>/logout">Log out</a>
        </div>
    </header>

    <div class="sidebar" id="sidebar">
        <ul class="nav-list">
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/home" class="nav-link">
                    <i class="fa-solid fa-house"></i> <span>Home</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="#" class="nav-link selected">
                    <i class="fa-solid fa-earth-americas"></i> <span>Questions</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="#" class="nav-link">
                    <i class="fa-solid fa-tags"></i> <span>Tags</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="#" class="nav-link">
                    <i class="fa-solid fa-bookmark"></i> <span>Saves</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="#" class="nav-link">
                    <i class="fa-solid fa-message"></i> <span>Chat</span>
                </a>
            </li>
        </ul>

        <div class="collectives-section">
            <div class="collectives-header">
                <span>Collectives</span> <i class="fa-solid fa-plus"></i>
            </div>
            <p class="collectives-text">Communities for your favorite technologies.</p>
            <a href="#" class="collectives-link">Explore all Collectives</a>
        </div>
    </div>

    <div class="main-wrap">
        
        <div class="form-left">
            
            <% if (request.getAttribute("error") != null) { %>
            <div class="side-box" style="border-left: 4px solid #d9534f;">
                ⚠️ <%= request.getAttribute("error") %>
            </div>
            <% } %>

            <form id="askForm" method="post" action="${pageContext.request.contextPath}/questions/đetail">
                <div class="section-box">
                    <div class="section-title">Tiêu đề</div>
                    <div class="form-group">
                        <label class="label">Tiêu đề câu hỏi</label>
                        <input type="text" name="title" class="input"
                               placeholder="VD: Làm sao sửa lỗi NullPointerException trong Java?"
                               minlength="10" maxlength="150" required>
                        <div class="label-help">Hãy mô tả ngắn gọn và rõ ràng vấn đề</div>
                    </div>
                </div>

                <div class="section-box">
                    <div class="section-title">Chi tiết vấn đề</div>
                    <div class="form-group">
                        <label class="label">Mô tả vấn đề</label>
                        <div class="editor-wrapper">
                            <div class="toolbar">
                                <button type="button">B</button>
                                <button type="button">I</button>
                                <button type="button">`</button>
                                <button type="button">{ }</button>
                            </div>
                            <textarea name="body" class="editor"
                                      placeholder="- Bạn đang cố làm gì?&#10;- Bạn đã thử gì?&#10;- Kết quả nhận được?&#10;- Bạn mong đợi gì?"
                                      minlength="20" required></textarea>
                        </div>
                    </div>
                </div>

                <div class="section-box">
                    <div class="section-title">Code liên quan (tùy chọn)</div>
                    <textarea name="codeSnippet" class="input"
                              style="min-height:120px; font-family:monospace;"
                              placeholder="// Dán code của bạn tại đây"></textarea>
                </div>

                <div class="section-box">
                    <div class="section-title">Tags</div>
                    <div class="form-group">
                        <label class="label">Nhập tối đa 5 tags (cách nhau bằng dấu phẩy)</label>
                        <input type="text" id="tags" name="tags" class="input" placeholder="vd: java, spring, api">
                        <div id="tagsPreview" style="margin-top:10px;"></div>
                    </div>
                </div>

                <button type="submit" class="btn-primary">Đăng câu hỏi</button>
                <a href="${pageContext.request.contextPath}/home" class="btn-secondary">Hủy</a>
            </form>
        </div>

        <div class="side-right">
            <div class="side-box">
                <strong>1. Viết tiêu đề rõ ràng</strong>
                <p>Mô tả đúng lỗi hoặc mục tiêu của bạn.</p>
            </div>
            <div class="side-box">
                <strong>2. Giải thích bạn đã thử gì</strong>
                <p>Thêm code và mô tả kết quả đã xảy ra.</p>
            </div>
            <div class="side-box">
                <strong>3. Cung cấp chi tiết đầy đủ</strong>
                <p>Giúp người khác tái hiện lỗi để trả lời chính xác.</p>
            </div>
        </div>

    </div>

    <script>
        // Xử lý bật tắt Menu bằng cách thêm class vào Body
        function toggleMenu() {
            // Thay vì tác động vào #sidebar, ta tác động vào body
            // để CSS có thể điều khiển cả sidebar và main-wrap
            document.body.classList.toggle('menu-open');
        }

        // Xử lý Preview Tags
        document.getElementById("tags").addEventListener("input", function () {
            const preview = document.getElementById("tagsPreview");
            preview.innerHTML = "";

            let tags = this.value.split(",").map(t => t.trim()).filter(t => t).slice(0, 5);

            tags.forEach(t => {
                const span = document.createElement("span");
                span.className = "tag-badge";
                span.innerText = t;
                preview.appendChild(span);
            });
        });
    </script>

</body>
>>>>>>> Stashed changes
</html>