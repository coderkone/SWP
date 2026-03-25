<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="depthLevel" value="${depthLevel + 1}" scope="request" />
<div class="d-flex mb-3">
    <img src="${not empty node.userAvatar ? node.userAvatar : 'https://cdn-icons-png.flaticon.com/512/149/149071.png'}" 
         onerror="this.onerror=null; this.src='https://cdn-icons-png.flaticon.com/512/149/149071.png';" 
         class="${node.parentId == null ? 'comment-avatar' : 'reply-avatar'} me-3" alt="Avatar">

    <div class="flex-grow-1 w-100">
        <div class="bg-light p-3 rounded">
            <div class="d-flex justify-content-between align-items-center mb-1">
                <h6 class="fw-bold mb-0">${node.username != null ? node.username : 'Unknown User'}</h6>
                <span class="text-muted fw-normal small"><fmt:formatDate value="${node.createdAt}" pattern="dd/MM/yyyy HH:mm" /></span>
            </div>

            <div id="commentText${node.commentId}" class="comment-text-content mb-0 text-dark" style="font-size: 15px;"><c:out value="${node.content}" /></div>

            <form id="editForm${node.commentId}" action="${pageContext.request.contextPath}/blog/comment" method="post" class="d-none mt-2">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="commentId" value="${node.commentId}">
                <input type="hidden" name="blogId" value="${blog.blogId}">

                <div class="border rounded overflow-hidden mb-2">
                    <div class="bg-light border-bottom p-1 d-flex gap-1">
                        <button type="button" class="btn btn-sm btn-light p-0 px-1 text-secondary" title="Bold" 
                                onclick="insertMd('edit-text-${node.commentId}', '**', '**')">
                            <i class="fa-solid fa-bold" style="font-size: 11px;"></i>
                        </button>
                        <button type="button" class="btn btn-sm btn-light p-0 px-1 text-secondary" title="Italic" 
                                onclick="insertMd('edit-text-${node.commentId}', '*', '*')">
                            <i class="fa-solid fa-italic" style="font-size: 11px;"></i>
                        </button>
                        <div class="vr mx-1"></div>
                        <button type="button" class="btn btn-sm btn-light p-0 px-1 text-secondary" title="Code" 
                                onclick="insertMd('edit-text-${node.commentId}', '`', '`')">
                            <i class="fa-solid fa-code" style="font-size: 11px;"></i>
                        </button>
                        <button type="button" class="btn btn-sm btn-light text-secondary" title="Code Block" 
                                onclick="insertMd('edit-text-${node.commentId}', '\n```java\n', '\n```\n')">
                            <i class="fa-solid fa-file-code" style="font-size: 11px;"></i>
                        </button>
                    </div>

                    <textarea id="edit-text-${node.commentId}" name="content" 
                              class="form-control border-0 shadow-none" 
                              rows="3" required>${node.content}</textarea>
                </div>

                <div class="text-end">
                    <button type="button" class="btn btn-link btn-sm text-muted" onclick="toggleEditForm('${node.commentId}')">Cancel</button>
                    <button type="submit" class="btn btn-primary btn-sm px-3">Save</button>
                </div>
            </form>
        </div>

        <div class="mt-1 ms-2">
            <a href="javascript:void(0)" class="text-muted small fw-bold text-decoration-none" onclick="toggleReplyForm('replyForm${node.commentId}')">
                <i class="fa-solid fa-reply"></i> Reply
            </a>

            <c:if test="${not empty node.replies}">
                <a href="javascript:void(0)" class="text-secondary small fw-bold text-decoration-none ms-3" onclick="toggleReplies('${node.commentId}')" id="toggleBtn${node.commentId}">
                    <i class="fa-solid fa-chevron-up"></i> Hide Replies
                </a>
            </c:if>

            <c:if test="${sessionScope.user.userId == node.userId}">
                <a href="javascript:void(0)" class="text-primary small fw-bold text-decoration-none ms-3" onclick="toggleEditForm('${node.commentId}')">
                    <i class="fa-solid fa-pen"></i> Edit
                </a>

                <a href="${pageContext.request.contextPath}/blog/comment?action=delete&commentId=${node.commentId}&blogId=${blog.blogId}" 
                   class="text-danger small fw-bold text-decoration-none ms-3" 
                   onclick="return confirm('Bạn có chắc muốn xóa bình luận này và tất cả phản hồi của nó?')">
                    <i class="fa-solid fa-trash"></i> Delete
                </a>
            </c:if>
        </div>

        <form id="replyForm${node.commentId}" action="${pageContext.request.contextPath}/blog/comment" method="post" class="d-none mt-2">
            <input type="hidden" name="action" value="add">
            <input type="hidden" name="blogId" value="${blog.blogId}">
            <input type="hidden" name="parentId" value="${node.commentId}"> 

            <div class="border rounded overflow-hidden">
                <div class="bg-light border-bottom p-1 d-flex gap-1">
                    <button type="button" class="btn btn-sm btn-light p-0 px-1 text-secondary" title="Bold" 
                            onclick="insertMd('reply-text-${node.commentId}', '**', '**')">
                        <i class="fa-solid fa-bold" style="font-size: 11px;"></i>
                    </button>
                    <button type="button" class="btn btn-sm btn-light p-0 px-1 text-secondary" title="Italic" 
                            onclick="insertMd('reply-text-${node.commentId}', '*', '*')">
                        <i class="fa-solid fa-italic" style="font-size: 11px;"></i>
                    </button>
                    <div class="vr mx-1"></div>
                    <button type="button" class="btn btn-sm btn-light p-0 px-1 text-secondary" title="Code" 
                            onclick="insertMd('reply-text-${node.commentId}', '`', '`')">
                        <i class="fa-solid fa-code" style="font-size: 11px;"></i>
                    </button>
                    <button type="button" class="btn btn-sm btn-light p-0 px-1 text-secondary" title="Code Block" 
                            onclick="insertMd('reply-text-${node.commentId}', '\n```java\n', '\n```\n')">
                        <i class="fa-solid fa-file-code" style="font-size: 11px;"></i>
                    </button>
                </div>

                <textarea id="reply-text-${node.commentId}" name="content" 
                          class="form-control form-control-sm border-0 shadow-none" 
                          rows="2" placeholder="Write a reply... (Markdown supported)" required></textarea>
            </div>

            <div class="d-flex justify-content-end mt-2">
                <button type="submit" class="btn btn-secondary btn-sm px-3">Reply</button>
            </div>
        </form>

        <c:if test="${not empty node.replies}">
            <div id="repliesBox${node.commentId}" 
                 class="mt-3 ${depthLevel < 4 ? 'border-start border-2 border-secondary border-opacity-25 ps-3 ms-2' : ''}">

                <c:set var="backupNode" value="${node}" /> 

                <c:forEach items="${backupNode.replies}" var="child">
                    <c:set var="node" value="${child}" scope="request" />
                    <jsp:include page="commentItem.jsp" />
                </c:forEach>

                <c:set var="node" value="${backupNode}" scope="request" /> 
            </div>
        </c:if>
    </div>
</div>
<c:set var="depthLevel" value="${depthLevel - 1}" scope="request" />