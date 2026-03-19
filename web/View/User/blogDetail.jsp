<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>${blog.title} - DevQuery Blog</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="https://zurb.github.io/tribute/dist/tribute.css" />
        <script src="https://zurb.github.io/tribute/dist/tribute.js"></script>
        <style>
            body {
                background-color: #f8f9fa;
                padding-top: 60px;
            }
            .blog-container {
                max-width: 800px;
                margin: 0 auto;
                background: #fff;
                padding: 40px;
                border-radius: 8px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            }
            .blog-title {
                font-size: 2.5rem;
                font-weight: 700;
                color: #232629;
                margin-bottom: 20px;
            }
            .blog-content {
                font-size: 1.1rem;
                line-height: 1.8;
                color: #3b4045;
                margin-top: 30px;
            }
            .comment-avatar {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                object-fit: cover;
            }
            .reply-avatar {
                width: 32px;
                height: 32px;
                border-radius: 50%;
                object-fit: cover;
            }
            .reply-box {
                border-left: 3px solid #e3e6e8;
                padding-left: 20px;
                margin-top: 15px;
            }
        </style>
    </head>
    <body>

        <jsp:include page="../Common/blogHeader.jsp" />

        <div class="container mt-4 mb-5">
            <div class="blog-container">

                <a href="${pageContext.request.contextPath}/blog" class="text-decoration-none text-muted mb-3 d-inline-block">
                    <i class="fa-solid fa-arrow-left"></i> Back to Blogs
                </a>

                <h1 class="blog-title">${blog.title}</h1>

                <div class="d-flex align-items-center text-muted mb-4 border-bottom pb-3">
                    <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" class="rounded-circle me-2" width="40" height="40" alt="Admin Avatar">
                    <div>
                        <div class="fw-bold text-dark">${blog.authorName} <span class="badge bg-primary ms-1">Admin</span></div>
                        <div class="small">
                            Published on <fmt:formatDate value="${blog.createdAt}" pattern="MMM dd, yyyy" />
                            <span class="ms-3"><i class="fa-solid fa-eye"></i> ${blog.viewCount} Views</span>
                        </div>
                    </div>
                </div>

                <img src="${not empty blog.thumbnailUrl ? blog.thumbnailUrl : 'https://placehold.co/800x400/eeeeee/999999?text=DevQuery+Blog'}" 
                     onerror="this.onerror=null; this.src='https://placehold.co/800x400/eeeeee/999999?text=DevQuery+Blog';" 
                     class="img-fluid rounded mb-4 w-100" alt="Thumbnail">

                <div class="blog-content">
                    ${blog.content} 
                </div>

                <hr class="mt-5 mb-4">

                <h4 class="fw-bold mb-4">Comments (${blog.commentCount})</h4>

                <form action="${pageContext.request.contextPath}/blog/comment" method="post" class="mb-5">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="blogId" value="${blog.blogId}">
                    <div class="d-flex">
                        <img src="${not empty sessionScope.user.avatarUrl ? sessionScope.user.avatarUrl : 'https://cdn-icons-png.flaticon.com/512/149/149071.png'}" 
                             onerror="this.onerror=null; this.src='https://cdn-icons-png.flaticon.com/512/149/149071.png';" 
                             class="comment-avatar me-3" alt="Your Avatar">
                        <div class="flex-grow-1">
                            <textarea name="content" class="form-control mb-2" rows="3" placeholder="Write a comment..." required></textarea>
                            <button type="submit" class="btn btn-primary btn-sm px-4">Post Comment</button>
                        </div>
                    </div>
                </form>

                <div class="comments-list">
                    <c:choose>
                        <c:when test="${empty rootComments}">
                            <p class="text-muted text-center py-4 bg-light rounded">There are no comments yet. Be the first to start the discussion!</p>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${rootComments}" var="cmt">
                                <c:set var="depthLevel" value="0" scope="request" />
                                <c:set var="node" value="${cmt}" scope="request" />
                                <jsp:include page="commentItem.jsp" />
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <script>
            function toggleReplyForm(formId) {
                const form = document.getElementById(formId);
                if (form.classList.contains('d-none')) {
                    document.querySelectorAll('form[id^="replyForm"]').forEach(f => f.classList.add('d-none'));
                    form.classList.remove('d-none');
                    form.querySelector('textarea').focus();
                } else {
                    form.classList.add('d-none');
                }
            }

            function toggleEditForm(cmtId) {
                const textDiv = document.getElementById('commentText' + cmtId);
                const editForm = document.getElementById('editForm' + cmtId);

                if (editForm.classList.contains('d-none')) {
                    editForm.classList.remove('d-none');
                    textDiv.classList.add('d-none');
                } else {
                    editForm.classList.add('d-none');
                    textDiv.classList.remove('d-none');
                }
            }
        </script>
        <script>
            // Khởi tạo tính năng Tag người dùng
            var tribute = new Tribute({
                values: function (remoteQuery, callback) {
                    // Gọi API lấy danh sách user từ server
                    fetch('${pageContext.request.contextPath}/api/users?blogId=${blog.blogId}')
                                        .then(res => res.json())
                                        .then(data => callback(data))
                                        .catch(err => console.error('Lỗi khi tải danh sách tag:', err));
                            },
                            selectTemplate: function (item) {
                                return '@' + item.original.value;
                            }
                        });

                        // Kích hoạt Tribute cho mọi ô nhập liệu content trên trang khi load xong
                        document.addEventListener("DOMContentLoaded", function () {
                            var textareas = document.querySelectorAll('textarea[name="content"]');
                            tribute.attach(textareas);
                        });
        </script>
        <script>
            function toggleReplies(cmtId) {
                const box = document.getElementById('repliesBox' + cmtId);
                const btn = document.getElementById('toggleBtn' + cmtId);

                // Nếu đang bị ẩn thì mở ra, đổi icon thành mũi tên lên
                if (box.classList.contains('d-none')) {
                    box.classList.remove('d-none');
                    btn.innerHTML = '<i class="fa-solid fa-chevron-up"></i> Hide Replies';
                }
                // Nếu đang hiện thì ẩn đi, đổi icon thành mũi tên xuống
                else {
                    box.classList.add('d-none');
                    btn.innerHTML = '<i class="fa-solid fa-chevron-down"></i> Show Replies';
                }
            }
        </script>
    </body>
</html>