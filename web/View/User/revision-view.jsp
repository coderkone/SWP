<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dto.RevisionDTO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Revision Snapshot - DevQuery</title>
    <style>
        body { font-family: "Segoe UI", sans-serif; background: #f1f2f3; margin: 0; color: #232629; }
        .container { max-width: 900px; margin: 24px auto; background: #fff; border: 1px solid #d6d9dc; border-radius: 8px; padding: 20px; }
        h1 { margin: 0 0 12px; font-size: 24px; }
        .meta { font-size: 13px; color: #6a737c; margin-bottom: 16px; }
        .title { font-size: 22px; font-weight: 700; margin-bottom: 14px; }
        .body { font-size: 14px; line-height: 1.6; margin-bottom: 14px; color: #3b4045; }
        .code { background: #f6f6f6; border: 1px solid #d6d9dc; border-radius: 4px; padding: 10px; font-family: Consolas, monospace; overflow-x: auto; margin-bottom: 14px; }
        .tags { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 10px; }
        .tag { background: #e1ecf4; border: 1px solid #bcd0e2; border-radius: 3px; padding: 4px 8px; font-size: 12px; color: #3b4045; }
        .link { color: #0a95ff; text-decoration: none; }
        .link:hover { text-decoration: underline; }
    </style>
</head>
<body>
<%
    RevisionDTO revision = (RevisionDTO) request.getAttribute("revision");
    String revisionsUrl = request.getContextPath() + ("question".equals(revision.getPostType())
            ? "/question/" + revision.getPostId() + "/revisions"
            : "/answer/" + revision.getPostId() + "/revisions");
%>
<div class="container">
    <h1>Revision Snapshot</h1>
    <div class="meta">
        <strong>Post:</strong> <%= revision.getPostType() %> #<%= revision.getPostId() %>
        | <strong>Editor:</strong> <%= revision.getEditorName() %>
        | <strong>Edited at:</strong> <%= revision.getEditedAt() %>
    </div>

    <% if ("question".equals(revision.getPostType()) && revision.getTitle() != null && !revision.getTitle().trim().isEmpty()) { %>
    <div class="title"><%= revision.getTitle() %></div>
    <% } %>

    <div class="body"><%= revision.getBody() %></div>

    <% if (revision.getCodeSnippet() != null && !revision.getCodeSnippet().trim().isEmpty()) { %>
    <div class="code"><%= revision.getCodeSnippet().replace("<", "&lt;").replace(">", "&gt;") %></div>
    <% } %>

    <% if (revision.getTags() != null && !revision.getTags().trim().isEmpty()) { %>
    <div class="tags">
        <%
            String[] tags = revision.getTags().split(",");
            for (String tag : tags) {
                String cleanTag = tag == null ? "" : tag.trim();
                if (cleanTag.isEmpty()) continue;
        %>
        <span class="tag"><%= cleanTag %></span>
        <% } %>
    </div>
    <% } %>

    <a class="link" href="<%= revisionsUrl %>">Back to revisions</a>
</div>
</body>
</html>
