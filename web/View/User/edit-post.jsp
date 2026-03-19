<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dto.QuestionDTO" %>
<%@ page import="dto.AnswerDTO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Post - DevQuery</title>
    <style>
        body {
            font-family: "Segoe UI", sans-serif;
            background: #f1f2f3;
            margin: 0;
            color: #232629;
        }

        .container {
            max-width: 900px;
            margin: 32px auto;
            background: #fff;
            border: 1px solid #d6d9dc;
            border-radius: 8px;
            padding: 24px;
        }

        h1 {
            margin: 0 0 20px;
            font-size: 24px;
        }

        .field {
            margin-bottom: 16px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-size: 14px;
            font-weight: 600;
        }

        input[type="text"],
        textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #c8ccd0;
            border-radius: 4px;
            font-size: 14px;
            box-sizing: border-box;
        }

        textarea {
            min-height: 180px;
            resize: vertical;
        }

        .actions {
            display: flex;
            gap: 10px;
            align-items: center;
        }

        .btn {
            border: 1px solid #0a95ff;
            background: #0a95ff;
            color: #fff;
            border-radius: 4px;
            padding: 10px 14px;
            cursor: pointer;
            text-decoration: none;
            font-size: 14px;
        }

        .btn-secondary {
            border: 1px solid #c8ccd0;
            background: #fff;
            color: #3b4045;
        }

        .message {
            margin-bottom: 14px;
            color: #b00020;
            font-size: 13px;
        }
    </style>
</head>
<body>
<%
    String postType = (String) request.getAttribute("postType");
    QuestionDTO question = (QuestionDTO) request.getAttribute("question");
    AnswerDTO answer = (AnswerDTO) request.getAttribute("answer");
    String tagsInput = (String) request.getAttribute("tagsInput");
    String error = request.getParameter("error");

    if (tagsInput == null) {
        tagsInput = "";
    }

    long questionIdForBack = 0;
    if ("question".equals(postType) && question != null) {
        questionIdForBack = question.getQuestionId();
    } else if ("answer".equals(postType) && answer != null) {
        questionIdForBack = answer.getQuestionId();
    }
%>

<div class="container">
    <% if (error != null && !error.trim().isEmpty()) { %>
    <div class="message"><%= error %></div>
    <% } %>

    <% if ("question".equals(postType) && question != null) { %>
    <h1>Edit Question</h1>
    <form method="post" action="${pageContext.request.contextPath}/post/edit">
        <input type="hidden" name="type" value="question">
        <input type="hidden" name="id" value="<%= question.getQuestionId() %>">

        <div class="field">
            <label for="title">Title</label>
            <input id="title" type="text" name="title" value="<%= question.getTitle() %>" required>
        </div>

        <div class="field">
            <label for="body">Body</label>
            <textarea id="body" name="body" required><%= question.getBody() %></textarea>
        </div>

        <div class="field">
            <label for="codeSnippet">Code Block</label>
            <textarea id="codeSnippet" name="codeSnippet"><%= question.getCodeSnippet() == null ? "" : question.getCodeSnippet() %></textarea>
        </div>

        <div class="field">
            <label for="tags">Tags (comma separated)</label>
            <input id="tags" type="text" name="tags" value="<%= tagsInput %>">
        </div>

        <div class="actions">
            <button class="btn" type="submit">Save Changes</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/question/detail?id=<%= question.getQuestionId() %>">Cancel</a>
        </div>
    </form>
    <% } else if ("answer".equals(postType) && answer != null) { %>
    <h1>Edit Answer</h1>
    <form method="post" action="${pageContext.request.contextPath}/post/edit">
        <input type="hidden" name="type" value="answer">
        <input type="hidden" name="id" value="<%= answer.getAnswerId() %>">

        <div class="field">
            <label for="body">Body</label>
            <textarea id="body" name="body" required><%= answer.getBody() %></textarea>
        </div>

        <div class="field">
            <label for="codeSnippet">Code Block</label>
            <textarea id="codeSnippet" name="codeSnippet"><%= answer.getCodeSnippet() == null ? "" : answer.getCodeSnippet() %></textarea>
        </div>

        <div class="actions">
            <button class="btn" type="submit">Save Changes</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/question/detail?id=<%= answer.getQuestionId() %>#answer-<%= answer.getAnswerId() %>">Cancel</a>
        </div>
    </form>
    <% } else { %>
    <h1>Invalid edit request</h1>
    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/home">Back to home</a>
    <% } %>
</div>
</body>
</html>
