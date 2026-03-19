<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dto.RevisionDTO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Revisions - DevQuery</title>
    <style>
        body { font-family: "Segoe UI", sans-serif; background: #f1f2f3; margin: 0; }
        .container { max-width: 900px; margin: 24px auto; background: #fff; border: 1px solid #d6d9dc; border-radius: 8px; padding: 20px; }
        h1 { margin: 0 0 14px; font-size: 24px; color: #232629; }
        table { width: 100%; border-collapse: collapse; }
        th, td { text-align: left; padding: 10px; border-bottom: 1px solid #e3e6e8; font-size: 14px; }
        th { color: #6a737c; font-weight: 600; }
        .link { color: #0a95ff; text-decoration: none; }
        .link:hover { text-decoration: underline; }
        .empty { color: #6a737c; padding: 14px 0; }
        .top-links { margin-bottom: 14px; }
    </style>
</head>
<body>
<%
    String postType = (String) request.getAttribute("postType");
    Long postId = (Long) request.getAttribute("postId");
    List<RevisionDTO> revisions = (List<RevisionDTO>) request.getAttribute("revisions");
    if (revisions == null) {
        revisions = new java.util.ArrayList<>();
    }

    String detailUrl;
    if ("answer".equals(postType)) {
        detailUrl = request.getContextPath() + "/question/detail?id=" + postId;
    } else {
        detailUrl = request.getContextPath() + "/question/detail?id=" + postId;
    }
%>
<div class="container">
    <h1><%= "question".equals(postType) ? "Question" : "Answer" %> Revisions</h1>
    <div class="top-links">
        <a class="link" href="<%= detailUrl %>">Back to post</a>
    </div>

    <% if (revisions.isEmpty()) { %>
    <div class="empty">No revisions found.</div>
    <% } else { %>
    <table>
        <thead>
            <tr>
                <th>Version</th>
                <th>Editor</th>
                <th>Edited At</th>
                <th>View</th>
            </tr>
        </thead>
        <tbody>
        <%
            int version = revisions.size();
            for (RevisionDTO r : revisions) {
        %>
            <tr>
                <td>v<%= version-- %></td>
                <td><%= r.getEditorName() %></td>
                <td><%= r.getEditedAt() %></td>
                <td><a class="link" href="${pageContext.request.contextPath}/revision/view?id=<%= r.getHistoryId() %>">View snapshot</a></td>
            </tr>
        <% } %>
        </tbody>
    </table>
    <% } %>
</div>
</body>
</html>
