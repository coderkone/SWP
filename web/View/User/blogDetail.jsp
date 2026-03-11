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

        <jsp:include page="../Common/header.jsp" />

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
                                <div class="comment-item mb-4">
                                    <div class="d-flex">
                                        <img src="${not empty cmt.userAvatar ? cmt.userAvatar : 'https://cdn-icons-png.flaticon.com/512/149/149071.png'}" 
                                             onerror="this.onerror=null; this.src='https://cdn-icons-png.flaticon.com/512/149/149071.png';" 
                                             class="comment-avatar me-3" alt="Avatar">
                                        <div class="flex-grow-1">
                                            <div class="bg-light p-3 rounded">
                                                <h6 class="fw-bold mb-1">${cmt.username != null ? cmt.username : 'Unknown User'} 
                                                    <span class="text-muted fw-normal small ms-2"><fmt:formatDate value="${cmt.createdAt}" pattern="dd/MM/yyyy HH:mm" /></span>
                                                </h6>

                                                <div id="commentText${cmt.commentId}" class="mb-0 text-dark">
                                                    ${cmt.content.replaceAll("(@\\w+)", "<span class='text-primary fw-bold'>$1</span>")}
                                                </div>

                                                <form id="editForm${cmt.commentId}" action="${pageContext.request.contextPath}/blog/comment" method="post" class="d-none mt-2">
                                                    <input type="hidden" name="action" value="edit">
                                                    <input type="hidden" name="commentId" value="${cmt.commentId}">
                                                    <input type="hidden" name="blogId" value="${blog.blogId}">
                                                    <textarea name="content" class="form-control mb-2" rows="2" required>${cmt.content}</textarea>
                                                    <div class="text-end">
                                                        <button type="button" class="btn btn-link btn-sm text-muted" onclick="toggleEditForm('${cmt.commentId}')">Cancel</button>
                                                        <button type="submit" class="btn btn-primary btn-sm">Save Changes</button>
                                                    </div>
                                                </form>
                                            </div>

                                            <div class="mt-1 ms-2">
                                                <a href="javascript:void(0)" class="text-muted small fw-bold text-decoration-none" onclick="toggleReplyForm('replyForm${cmt.commentId}')">
                                                    <i class="fa-solid fa-reply"></i> Reply
                                                </a>

                                                <c:if test="${sessionScope.user.userId == cmt.userId}">
                                                    <a href="javascript:void(0)" class="text-primary small fw-bold text-decoration-none ms-3" onclick="toggleEditForm('${cmt.commentId}')">
                                                        <i class="fa-solid fa-pen"></i> Edit
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/blog/comment?action=delete&commentId=${cmt.commentId}&blogId=${blog.blogId}" 
                                                       class="text-danger small fw-bold text-decoration-none ms-3" 
                                                       onclick="return confirm('Delete this comment and all its replies?');">
                                                        <i class="fa-solid fa-trash"></i> Delete
                                                    </a>
                                                </c:if>
                                            </div>

                                            <form id="replyForm${cmt.commentId}" action="${pageContext.request.contextPath}/blog/comment" method="post" class="d-none mt-2">
                                                <input type="hidden" name="action" value="add">
                                                <input type="hidden" name="blogId" value="${blog.blogId}">
                                                <input type="hidden" name="parentId" value="${cmt.commentId}"> 
                                                <div class="d-flex">
                                                    <textarea name="content" class="form-control form-control-sm me-2" rows="1" placeholder="Write a reply..." required></textarea>
                                                    <button type="submit" class="btn btn-secondary btn-sm text-nowrap">Reply</button>
                                                </div>
                                            </form>

                                            <c:if test="${not empty cmt.replies}">
                                                <div class="reply-box">
                                                    <c:forEach items="${cmt.replies}" var="reply">
                                                        <div class="d-flex mb-3">
                                                            <img src="${not empty reply.userAvatar ? reply.userAvatar : 'https://cdn-icons-png.flaticon.com/512/149/149071.png'}" 
                                                                 onerror="this.onerror=null; this.src='https://cdn-icons-png.flaticon.com/512/149/149071.png';" 
                                                                 class="reply-avatar me-2" alt="Avatar">
                                                            <div class="flex-grow-1">
                                                                <div class="bg-light p-2 rounded w-100">
                                                                    <div class="d-flex justify-content-between align-items-center">
                                                                        <span class="fw-bold" style="font-size: 14px;">${reply.username != null ? reply.username : 'Unknown User'}</span>
                                                                        <div class="text-muted small"><fmt:formatDate value="${reply.createdAt}" pattern="dd/MM/yyyy HH:mm" /></div>
                                                                    </div>

                                                                    <div id="commentText${reply.commentId}" class="text-dark mt-1" style="font-size: 14px;">
                                                                        ${reply.content.replaceAll("(@\\w+)", "<span class='text-primary fw-bold'>$1</span>")}
                                                                    </div>

                                                                    <form id="editForm${reply.commentId}" action="${pageContext.request.contextPath}/blog/comment" method="post" class="d-none mt-2">
                                                                        <input type="hidden" name="action" value="edit">
                                                                        <input type="hidden" name="commentId" value="${reply.commentId}">
                                                                        <input type="hidden" name="blogId" value="${blog.blogId}">
                                                                        <textarea name="content" class="form-control form-control-sm mb-2" rows="2" required>${reply.content}</textarea>
                                                                        <div class="text-end">
                                                                            <button type="button" class="btn btn-link btn-sm text-muted py-0" onclick="toggleEditForm('${reply.commentId}')">Cancel</button>
                                                                            <button type="submit" class="btn btn-primary btn-sm py-0">Save</button>
                                                                        </div>
                                                                    </form>
                                                                </div>

                                                                <c:if test="${sessionScope.user.userId == reply.userId}">
                                                                    <div class="mt-1 ms-1">
                                                                        <a href="javascript:void(0)" class="text-primary small fw-bold text-decoration-none" onclick="toggleEditForm('${reply.commentId}')">
                                                                            <i class="fa-solid fa-pen"></i> Edit
                                                                        </a>
                                                                        <a href="${pageContext.request.contextPath}/blog/comment?action=delete&commentId=${reply.commentId}&blogId=${blog.blogId}" 
                                                                           class="text-danger small fw-bold text-decoration-none ms-3" 
                                                                           onclick="return confirm('Delete this reply?');">
                                                                            <i class="fa-solid fa-trash"></i> Delete
                                                                        </a>
                                                                    </div>
                                                                </c:if>
                                                            </div>
                                                        </div>
                                                    </c:forEach>
                                                </div>
                                            </c:if>

                                        </div>
                                    </div>
                                </div>
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
    </body>
</html>