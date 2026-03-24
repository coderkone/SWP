<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dto.RevisionDTO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Revisions - DevQuery</title>

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
            margin: 0;
            font-size: 28px;
            color: #232629;
        }

        .page-subtitle {
            margin: 6px 0 0;
            color: #6a737c;
            font-size: 14px;
        }

        .top-links {
            margin-top: 18px;
        }

        .link {
            color: #0a95ff;
            text-decoration: none;
        }

        .link:hover {
            text-decoration: underline;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            text-align: left;
            padding: 12px 10px;
            border-bottom: 1px solid #e3e6e8;
            font-size: 14px;
            vertical-align: middle;
        }

        th {
            color: #6a737c;
            font-weight: 600;
        }

        .empty {
            color: #6a737c;
            padding-top: 18px;
        }

        @media (max-width: 767.98px) {
            body {
                padding-top: 72px;
            }

            .page-content {
                padding: 16px;
            }

            .revision-card {
                padding: 18px;
            }
        }
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

    String contextPath = request.getContextPath();
    String detailUrl = contextPath + "/question/detail?id=" + postId;
%>

<jsp:include page="../Common/header.jsp" />

<div class="container-fluid revision-page">
    <div class="row">

        <!-- Sidebar -->
        <nav class="col-md-2 d-none d-md-block bg-light p-0">
            <jsp:include page="../Common/sidebar.jsp" />
        </nav>

        <!-- Main content -->
        <main class="col-md-10 ms-sm-auto page-content">
            <section class="revision-card">

                <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
                    <div>
                        <h1 class="page-title">
                            <%= "question".equals(postType) ? "Question" : "Answer" %> Revisions
                        </h1>
                        <p class="page-subtitle">
                            Track every saved edit for this post
                        </p>
                    </div>
                </div>

                <div class="top-links">
                    <a class="link" href="<%= detailUrl %>">
                        <i class="fa-solid fa-arrow-left me-1"></i> Back to post
                    </a>
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
                            <td>
                                <a class="link"
                                   href="<%= contextPath %>/revision/view?id=<%= r.getHistoryId() %>">
                                    View snapshot
                                </a>
                            </td>
                        </tr>
                        <% } %>
                        </tbody>
                    </table>

                <% } %>

            </section>
        </main>

    </div>
</div>

</body>
</html>