<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>DevQuery - Log in</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/so-auth.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <style>
        body{font-family:Arial,sans-serif;background:#f6f6f6;}
        .wrap{max-width:420px;margin:60px auto;background:#fff;padding:28px;border-radius:10px;box-shadow:0 6px 20px rgba(0,0,0,.08);}
        .top{display:flex;justify-content:space-between;align-items:center;margin-bottom:18px;}
        .brand{font-weight:700;font-size:20px;}
        .links a{margin-left:10px;color:#0a95ff;text-decoration:none;}
        .err{background:#FDEDED;color:#B42318;padding:10px;border-radius:8px;margin:10px 0;}
        .ok{background:#E3FCEF;color:#2f6f44;padding:10px;border-radius:8px;margin:10px 0;}
        label{display:block;margin:12px 0 6px;font-weight:600;}
        input{width:100%;padding:10px;border:1px solid #d6d9dc;border-radius:8px;box-sizing: border-box;} /* Thêm box-sizing để không bị vỡ layout */
        
        button{width:100%;margin-top:14px;padding:10px;border:0;border-radius:8px;background:#0a95ff;color:#fff;font-weight:700;cursor:pointer;}
        button:hover{background:#0074cc;}
        
        .small{margin-top:12px;text-align:center;}
        .small a{color:#0a95ff;text-decoration:none;}

        .divider { text-align: center; margin: 20px 0; position: relative; }
        .divider span { background: #fff; padding: 0 10px; color: #6a737c; position: relative; z-index: 1; font-size: 13px; }
        .divider:after { content: ""; position: absolute; top: 50%; left: 0; width: 100%; height: 1px; background: #e3e6e8; z-index: 0; }
        
        .social-btn {
            display: flex; align-items: center; justify-content: center;
            width: 100%; padding: 10px; margin-bottom: 10px;
            border-radius: 8px; text-decoration: none; font-weight: 600;
            border: 1px solid #d6d9dc; transition: 0.2s;
            box-sizing: border-box;
        }
        .social-btn i { margin-right: 10px; font-size: 18px; }
        
        .btn-google { background: #fff; color: #3b4045; }
        .btn-google:hover { background: #f8f9f9; }
        
        .btn-github { background: #24292e; color: #fff; border-color: #24292e; }
        .btn-github:hover { background: #2f363d; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="top">
        <div class="brand">DevQuery</div>
        <div class="links">
            <a href="<%=request.getContextPath()%>/auth/login">Login</a>
            <a href="<%=request.getContextPath()%>/auth/register">Register</a>
        </div>
    </div>

    <% if ("1".equals(request.getParameter("registered"))) { %>
        <div class="ok">Đăng ký thành công! Vui lòng đăng nhập.</div>
    <% } %>

    <% String error = (String) request.getAttribute("error"); %>
    <% if (error != null) { %>
        <div class="err"><%=error%></div>
    <% } %>

    <div class="social-buttons">
        <a href="<%=request.getContextPath()%>/auth/google" class="social-btn btn-google">
            <i class="fab fa-google" style="color: #DB4437;"></i> Log in with Google
        </a>
        
        <a href="<%=request.getContextPath()%>/auth/github" class="social-btn btn-github">
            <i class="fab fa-github"></i> Log in with GitHub
        </a>
    </div>

    <div class="divider"><span>OR</span></div>

    <form method="post" action="<%=request.getContextPath()%>/auth/login">
        <label>Email</label>
        <input name="email" type="email" placeholder="you@example.com" required>

        <label>Password</label>
        <input name="password" type="password" placeholder="********" required>

        <button type="submit">Login</button>
    </form>

    <div class="small">
        Don't have an account? <a href="<%=request.getContextPath()%>/auth/register">Sign up</a>
    </div>
</div>
</body>
</html>