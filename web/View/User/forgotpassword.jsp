<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>DevQuery - Forgot Password</title>
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
        input{width:100%;padding:10px;border:1px solid #d6d9dc;border-radius:8px;box-sizing: border-box;}
        button{width:100%;margin-top:14px;padding:10px;border:0;border-radius:8px;background:#0a95ff;color:#fff;font-weight:700;cursor:pointer;}
        button:hover{background:#0074cc;}
        .small{margin-top:12px;text-align:center; font-size: 13px; color: #6a737c;}
        .small a{color:#0a95ff;text-decoration:none;}
        .small a:hover{text-decoration: underline;}
        </style>
    </head>
    <body>
        <div class="wrap">
        <p class="small"style="text-align: left; margin-bottom: 20px;">
            Forgot your account's password? Enter your email address and we'll send you a recovery link.
        </p>
        <% String error = (String) request.getAttribute("error"); %>
    <% if (error != null) { %>
        <div class="err"><%=error%></div>
    <% } %>
        <% String message = (String) request.getAttribute("message"); %>
<% if (message != null) { %>
    <div class="ok"><%=message%></div>
<% } %>
        <form action="ForgotPassword" method="post">
            <label>Email</label>
            <input type="email" name="email" placeholder="Example: abc@gmail.com" required>
            
            <button type="submit">Send recovery email</button>
        </form>
    </div>
    </body>
</html>
