<%-- 
    Document   : changepassword
    Created on : Feb 20, 2026, 9:45:52 PM
    Author     : Asus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Change Password - DevQuery</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <style>
            body{
                font-family: Arial,sans-serif;
                background: #F5F5F5;
            }
            .warp{
                max-width: 420px;
                margin: 60px auto ;
                background: #FFFFFF;
                padding:28px;
                border-radius:10px;
                box-shadow:0 6px 20px rgba(0,0,0,.08);
            }
            .brand{
                font-weight:700;
                font-size:20px;
                margin-bottom: 15px;
            }
            label{
                display:block;
                margin:12px 0 6px;
                font-weight:600;
            }
            input{
                width:100%;
                padding:10px;
                border:1px solid #d6d9dc;
                border-radius:8px;
                box-sizing: border-box;
            }
            button, .btn-action {
                width:100%;
                margin-top:14px;
                padding:10px;
                border:0;
                border-radius:8px;
                background:#0a95ff;
                color:#fff;
                font-weight:700;
                cursor:pointer;
                text-align: center;
                display: block;
                text-decoration: none;
                box-sizing: border-box;
                font-size: 13.33px;
            }
            button:hover{
                background:#0074cc;
            }
            .btn-action:hover{
                background:#0074cc;
            }
            .alert-boxsuccessful{
                padding: 20px;
                border-radius: 8px;
                text-align: center;
                margin-bottom: 15px;
                background: #E3FCEF;
                color: #2f6f44;
            }
            .err{
                background:#FDEDED;
                color:#B42318;
                padding:10px;
                border-radius:8px;
                margin:10px 0;
            }
            .ok{
                background:#E3FCEF;
                color:#2f6f44;
                padding:10px;
                border-radius:8px;
                margin:10px 0;
            }
            .guide{
                font-size: 13px;
                color: #666;
                margin-top: 5px;
            }
            .user-email {
                color: #0a95ff;
                font-weight: bold;
            }
        </style>
    </head>
    <body>
        <div class="warp">
            <div class="brand">Change Password</div>
            <hr style="border: 0;border-top: 1px solid #000; margin: 0 0 20px 0 ; width: 100%;">
            <% String status = (String) request.getAttribute("status"); %>
            <% if("successful".equals(status)){ %>
            <div class="alert-boxsuccessful">
                <i class="fa-solid fa-circle-check fa-2x"></i><br><br>
                <strong>Successfully changed password</strong>
            </div>
            <a href="auth/login" class="btn-action">Go to Login</a>
            <% }else{ %>
            <c:choose>    
                <c:when test="${not empty sessionScope.user.provider and sessionScope.user.provider ne 'local'}">
                    <div class="alert-boxsocial">
                        <i class="fa-brands fa-${fn:toLowerCase(sessionScope.user.provider)} fa-3x" style="margin-bottom: 12px;"></i><br>
                        <strong style="font-size: 16px;">Linked Account</strong><br>
                        <p style="margin-top: 8px; font-size: 14px; line-height: 1.5;">
                            Your account is managed by <b style="text-transform: capitalize;">${sessionScope.user.provider}</b>.<br>
                            Password change is not applicable.
                        </p>
                    </div>
                    <a href="${pageContext.request.contextPath}/profile" class="btn-action">Back to Profile</a>
                </c:when>

                <c:otherwise>    
                    <h2 style="margin: 0 0 15px; font-size: 18px;">Change Password for <span class="user-email"><%=request.getAttribute("email")%></span></h2>
                        <% String error = (String) request.getAttribute("error"); %>
                        <% if (error != null) { %>
                    <div class="err"><%=error%></div>
                    <% } %>
                    <% String message = (String) request.getAttribute("message"); %>
                    <% if (message != null) { %>
                    <div class="ok"><%=message%></div>
                    <% } %>
                    <form method="post" action="${pageContext.request.contextPath}/ChangePassword">
                        <label>Current password</label>
                        <input name="old_password" type="password" required>
                        <label>New password</label>
                        <input name="new_password" type="password" required>
                        <label>Confirm new password</label>
                        <input name="confirm_password" type="password" required>
                        <div class="guide">Password must contain at least eight characters</div>
                        <button type="submit">Change Password</button>
                    </form>
                </c:otherwise>
            </c:choose>  
            <% } %>
        </div>
    </body>
</html>