<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dto.RevisionDTO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Revision Snapshot - DevQuery</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        body {
            font-family: "Segoe UI", sans-serif;
            background: #ffffff;
            margin: 0;
            color: #232629;
            padding-top: 56px;
        }

        .revision-page {
            min-height: calc(100vh - 56px);
        }

        .page-content {
            padding: 24px;
        }

        .revision-card {
            max-width: 980px;
            background: #fff;
            border: 1px solid #d6d9dc;
            border-radius: 8px;
            padding: 24px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
        }

        .page-title {
            margin-bottom: 12px;
            font-size: 28px;
        }

        .meta {
            font-size: 14px;
            color: #6a737c;
            margin-bottom: 20px;
        }

        .title {
            font-size: 24px;
            font-weight: 700;
            margin-bottom: 14px;
        }

        .body-copy {
            font-size: 15px;
            line-height: 1.7;
            margin-bottom: 18px;
            color: #3b4045;
            white-space: pre-wrap;
        }

        .code {
            background: #f6f6f6;
            border: 1px solid #d6d9dc;
            border-radius: 6px;
            padding: 14px;
            font-family: Consolas, monospace;
            overflow-x: auto;
            margin-bottom: 18px;
            white-space: pre-wrap;
        }

        .tags {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            margin-bottom: 18px;
        }

        .tag {
            background: #e1ecf4;
            border: 1px solid #bcd0e2;
            border-radius: 3px;
            padding: 4px 8px;
            font-size: 12px;
        }

        .link {
            color: #0a95ff;
            text-decoration: none;
        }

        .link:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<%
    RevisionDTO revision = (RevisionDTO) request.getAttribute("revision");

    String contextPath = request.getContextPath();

    String revisionsUrl = contextPath + (
        "question".equals(revision.getPostType())
        ? "/question/" + revision.getPostId() + "/revisions"
        : "/answer/" + revision.getPostId() + "/revisions"
    );
%>

<jsp:include page="../Common/header.jsp" />

<div class="container-fluid revision-page">
    <div class="row">

        <!-- Sidebar -->
        <nav class="col-md-2 d-none d-md-block bg-light p-0">
            <jsp:include page="../Common/sidebar.jsp" />
        </nav>

        <!-- Main -->
        <main class="col-md-10 ms-sm-auto page-content">
            <section class="revision-card">

                <h1 class="page-title">Revision Snapshot</h1>

                <div class="meta">
                    <strong>Post:</strong> <%= revision.getPostType() %> #<%= revision.getPostId() %>
                    <span class="mx-1">|</span>
                    <strong>Editor:</strong> <%= revision.getEditorName() %>
                    <span class="mx-1">|</span>
                    <strong>Edited at:</strong> <%= revision.getEditedAt() %>
                </div>

                <% if ("question".equals(revision.getPostType()) 
                        && revision.getTitle() != null 
                        && !revision.getTitle().trim().isEmpty()) { %>
                    <div class="title"><%= revision.getTitle() %></div>
                <% } %>

                <div class="body-copy"><%= revision.getBody() %></div>

                <% if (revision.getCodeSnippet() != null && !revision.getCodeSnippet().trim().isEmpty()) { %>
                    <div class="code">
                        <%= revision.getCodeSnippet()
                                .replace("<", "&lt;")
                                .replace(">", "&gt;") %>
                    </div>
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

                <a class="link" href="<%= revisionsUrl %>">
                    <i class="fa-solid fa-arrow-left me-1"></i> Back to revisions
                </a>

            </section>
        </main>

    </div>
</div>

</body>
</html>