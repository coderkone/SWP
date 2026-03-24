<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>DevQuery - Register</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/so-auth.css">
    <style>
        body{font-family:Arial,sans-serif;background:#f6f6f6;}
        .wrap{max-width:420px;margin:60px auto;background:#fff;padding:28px;border-radius:10px;box-shadow:0 6px 20px rgba(0,0,0,.08);}
        .top{display:flex;justify-content:space-between;align-items:center;margin-bottom:18px;}
        .brand{font-weight:700;font-size:20px;}
        .links a{margin-left:10px;color:#0a95ff;text-decoration:none;}
        .err{background:#FDEDED;color:#B42318;padding:10px;border-radius:8px;margin:10px 0;}
        label{display:block;margin:12px 0 6px;font-weight:600;}
        input{width:100%;padding:10px;border:1px solid #d6d9dc;border-radius:8px;}
        button{width:100%;margin-top:14px;padding:10px;border:0;border-radius:8px;background:#0a95ff;color:#fff;font-weight:700;cursor:pointer;}
        button:hover{background:#0074cc;}
        .small{margin-top:12px;text-align:center;}
        .small a{color:#0a95ff;text-decoration:none;}
        .hint{font-size:12px;color:#6a737c;margin-top:6px;}
    </style>
</head>
<body>
<div class="wrap">
    <div class="top">
        <div class="brand">DevQuery </div>
        <div class="links">
            <a href="<%=request.getContextPath()%>/auth/login">Login</a>
            <a href="<%=request.getContextPath()%>/auth/register">Register</a>
        </div>
    </div>

    <% String error = (String) request.getAttribute("error"); %>
    <% if (error != null) { %>
        <div class="err"><%=error%></div>
    <% } %>

    <h2 style="margin:6px 0 10px;">Sign up</h2>

    <form method="post" action="<%=request.getContextPath()%>/auth/register">
        <label>Username</label>
        <input name="username" type="text" placeholder="yourname" required>

        <label>Email</label>
        <input name="email" type="email" placeholder="you@example.com" required>

        <label>Password</label>
        <input name="password" type="password" placeholder="min 8 chars" required>
        <div class="hint">Password tối thiểu 8 ký tự.</div>

        <label>Confirm password</label>
        <input name="confirm" type="password" placeholder="repeat password" required>

        <button type="submit">Register</button>
    </form>

    <div class="small">
        Already have an account? <a href="<%=request.getContextPath()%>/auth/login">Log in</a>
    </div>
</div>
</body>
</html>
