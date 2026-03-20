<%-- 
    Document   : createQuestion.jsp
    Created on : Feb 27, 2026, 11:39:16 AM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Đăng câu hỏi - DevQuery</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://unpkg.com/easymde/dist/easymde.min.css">
        <style>
            body {
                padding-top: 20px;
            }
            header, .navbar {
                z-index: 99999 !important;
                position: fixed; 
                top: 0;
                width: 100%;
            }

            .editor-toolbar.fullscreen {
                top: 56px !important;
                z-index: 9999 !important;
            }
            .CodeMirror-fullscreen,
            .editor-preview-side {
                top: 106px !important;
                z-index: 9999 !important;
                height: calc(100vh - 106px) !important; 
            }
        </style>
    </head>
    <body class="bg-light">
        <jsp:include page="../Common/header.jsp"></jsp:include>

            <div class="container mt-5">
                <div class="row justify-content-center">
                    <div class="col-md-9">
                        <h2 class="mb-4">Ask Question</h2>
                        
                    <% if (request.getAttribute("errorMessage") != null) { %>
                        <div class="alert alert-danger" role="alert">
                            <%= request.getAttribute("errorMessage") %>
                        </div>
                    <% } %>
                    
                    <form action="${pageContext.request.contextPath}/create" method="POST">

                        <div class="mb-3">
                            <label for="title" class="form-label fw-bold">Title</label>
                            <input type="text" class="form-control" id="title" name="title" 
                                value="<%= request.getAttribute("oldTitle") != null ? request.getAttribute("oldTitle") : "" %>" 
                                minlength = "15" required>
                            <div class="form-text">Keep your title concise and to the point.</div>
                        </div>

                        <div class="mb-3">
                            <label for="body" class="form-label fw-bold">Body</label>
                            <textarea class="form-control" id="body" name="body"><%= request.getAttribute("oldBody") != null ? request.getAttribute("oldBody") : "" %></textarea>
                            <div class="form-text">Use the toolbar to format or preview.</div>
                        </div>

                        <div class="mb-4">
                            <label for="tags" class="form-label fw-bold">Tags</label>
                            <input type="text" class="form-control" id="tags" name="tags" 
                                placeholder="java, servlet, sql-server" 
                                value="<%= request.getAttribute("oldTags") != null ? request.getAttribute("oldTags") : "" %>" required>
                            <div class="form-text">Add up to 5 tags to categorize your question</div>
                        </div>

                        <button type="submit" class="btn btn-primary px-4">Post Question</button>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://unpkg.com/easymde/dist/easymde.min.css"></script>
        <script src="https://unpkg.com/easymde/dist/easymde.min.js"></script>
        <script>
            // Khởi tạo Markdown Editor cho thẻ textarea có id="body"
            var easyMDE = new EasyMDE({
                element: document.getElementById('body'),
                spellChecker: false, // Tắt check lỗi chính tả tiếng Anh
                placeholder: "Describe your problem or share your code snippet here",
                toolbar: [
                    "bold", "italic", "heading", "|",
                    "quote", "unordered-list", "ordered-list", "|",
                    "link", "image", "code", "|", // Nút code sẽ nằm cạnh nút ảnh
                    "preview", "side-by-side", "fullscreen", "|",
                    "guide"
                ]
            });
        </script>
    </body>
</html>