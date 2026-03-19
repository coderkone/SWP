<%@ page import="dto.UserDTO" %>
<%@ page import="model.User" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Report Content - DevQuery</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <style>
            * {
                box-sizing: border-box;
            }
            body {
                margin: 0;
                font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
                background: #f1f2f3;
                color: #232629;
            }
            .page {
                max-width: 760px;
                margin: 40px auto;
                padding: 0 16px;
            }
            .card {
                background: #fff;
                border: 1px solid #d6d9dc;
                border-radius: 8px;
                padding: 20px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
            }
            .title {
                margin: 0 0 8px;
                font-size: 24px;
                font-weight: 700;
            }
            .desc {
                margin: 0 0 18px;
                color: #525960;
                font-size: 14px;
                line-height: 1.5;
            }
            .field {
                margin-bottom: 14px;
            }
            .label {
                display: block;
                margin-bottom: 6px;
                font-size: 13px;
                font-weight: 600;
            }
            .input, .textarea {
                width: 100%;
                border: 1px solid #c8ccd0;
                border-radius: 6px;
                padding: 10px;
                font-size: 14px;
            }
            .input:focus, .textarea:focus {
                border-color: #0a95ff;
                outline: none;
                box-shadow: 0 0 0 3px rgba(10,149,255,0.15);
            }
            .textarea {
                min-height: 120px;
                resize: vertical;
            }
            .meta {
                background: #f8f9f9;
                border: 1px solid #e3e6e8;
                border-radius: 6px;
                padding: 10px 12px;
                margin-bottom: 14px;
                font-size: 13px;
                color: #525960;
            }
            .actions {
                display: flex;
                gap: 10px;
                justify-content: flex-end;
            }
            .btn {
                border: 1px solid #0a95ff;
                background: #0a95ff;
                color: #fff;
                border-radius: 6px;
                padding: 9px 14px;
                font-size: 13px;
                cursor: pointer;
                text-decoration: none;
            }
            .btn:hover {
                background: #0074cc;
            }
            .btn-secondary {
                border-color: #c8ccd0;
                background: #f1f2f3;
                color: #3b4045;
            }
            .btn-secondary:hover {
                background: #e3e6e8;
            }
        </style>
    </head>
    <body>
        <%
            Object principal = session.getAttribute("user");
            boolean isLoggedIn = (principal instanceof UserDTO) || (principal instanceof User);
            if (!isLoggedIn) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

            String postType = String.valueOf(request.getAttribute("postType"));
            String postId = String.valueOf(request.getAttribute("postId"));
            String questionId = String.valueOf(request.getAttribute("questionId"));
            String answerId = String.valueOf(request.getAttribute("answerId"));

            String backUrl = request.getContextPath() + "/question/detail?id=" + questionId;
            if ("answer".equals(postType) && answerId != null && answerId.matches("\\d+")) {
                backUrl += "#answer-" + answerId;
            }
        %>
        <div class="page">
            <div class="card">
                <h1 class="title">Report Content</h1>
                <p class="desc"><b>Choose a reason and submit your report. Thank you for helping keep the community safe.<b></p>



                <form method="post" action="${pageContext.request.contextPath}/flag/submit">
                    <input type="hidden" name="postType" value="<%= postType %>">
                    <input type="hidden" name="postId" value="<%= postId %>">
                    <input type="hidden" name="questionId" value="<%= questionId %>">
                    <input type="hidden" name="answerId" value="<%= answerId %>">

                    <div class="field">
                        <label class="label" for="reason">Reason</label>
                        <select id="reason" name="reason" class="input" required>
                            <option value="">Select a reason</option>
                            <option value="Spam">Spam</option>
                            <option value="Harassment or abusive language">Harassment or abusive language</option>
                            <option value="Misleading content">Misleading content</option>
                            <option value="Other">Other</option>
                        </select>
                    </div>

                    <div class="field">
                        <label class="label" for="note">Note (optional)</label>
                        <textarea id="note" name="note" class="textarea" maxlength="500" placeholder="Add a short note (optional)..."></textarea>
                    </div>

                    <div class="actions">
                        <a href="<%= backUrl %>" class="btn btn-secondary">Cancel</a>
                        <button type="submit" class="btn">Submit Report</button>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>
