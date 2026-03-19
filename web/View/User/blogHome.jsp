<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>DevQuery Blog - Knowledge & Technology</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

        <style>
            body {
                background-color: #f8f9fa;
                padding-top: 70px; /* Căn lề cho Header cố định */
            }

            /* Tiêu đề trang */
            .page-header {
                background: linear-gradient(135deg, #0a95ff 0%, #0074cc 100%);
                color: white;
                padding: 40px 0;
                margin-bottom: 40px;
                border-radius: 8px;
                box-shadow: 0 4px 15px rgba(10, 149, 255, 0.2);
            }

            /* Style cho Card Bài viết */
            .blog-card {
                border: none;
                border-radius: 10px;
                transition: all 0.3s ease;
                background-color: #fff;
                box-shadow: 0 2px 5px rgba(0,0,0,0.05);
                overflow: hidden;
            }
            .blog-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 10px 20px rgba(0,0,0,0.1);
            }

            /* Ảnh Thumbnail */
            .blog-thumbnail {
                height: 200px;
                object-fit: cover;
                width: 100%;
                border-bottom: 1px solid #eee;
            }

            /* Tiêu đề bài viết */
            .blog-title {
                font-size: 1.25rem;
                font-weight: bold;
                line-height: 1.4;
                margin-bottom: 10px;
            }
            .blog-title a {
                color: #232629;
                text-decoration: none;
                transition: color 0.2s;
            }
            .blog-title a:hover {
                color: #0a95ff;
            }

            /* Cắt ngắn nội dung (Excerpt) thành 3 dòng */
            .blog-excerpt {
                color: #6a737c;
                font-size: 0.95rem;
                display: -webkit-box;
                -webkit-line-clamp: 3;
                -webkit-box-orient: vertical;
                overflow: hidden;
                margin-bottom: 15px;
            }

            /* Meta info (Tác giả, Ngày tháng) */
            .blog-meta {
                font-size: 0.85rem;
                color: #838c95;
                display: flex;
                align-items: center;
                justify-content: space-between;
            }
            .author-avatar {
                width: 24px;
                height: 24px;
                border-radius: 50%;
                margin-right: 8px;
                object-fit: cover;
            }
        </style>
    </head>
    <body>

        <jsp:include page="../Common/blogHeader.jsp" />

        <div class="container mb-5">

            <div class="page-header text-center">
                <h1 class="fw-bold"><i class="fa-solid fa-laptop-code me-2"></i>DevQuery Blog</h1>
                <p class="lead mb-0 opacity-75">Sharing knowledge, experience, and the latest technology news.</p>
            </div>

            <div class="d-flex justify-content-between align-items-center mb-4">
                <h4 class="fw-bold mb-0 text-dark">Blog Posts</h4>

                <form action="${pageContext.request.contextPath}/blog" method="get" class="d-flex gap-2">
                    <select name="sort" class="form-select form-select-sm shadow-none" style="width: 150px; border-color: #ced4da;" onchange="this.form.submit()">
                        <option value="default" ${currentSort == 'default' ? 'selected' : ''}>Default</option>
                        <option value="newest" ${currentSort == 'newest' ? 'selected' : ''}>Newest</option>
                        <option value="most_viewed" ${currentSort == 'most_viewed' ? 'selected' : ''}>Most Viewed</option>
                        <option value="oldest" ${currentSort == 'oldest' ? 'selected' : ''}>Oldest</option>
                    </select>

                    <div class="input-group input-group-sm" style="width: 250px;">
                        <input type="text" name="search" class="form-control shadow-none border-end-0" placeholder="Searching..." value="${searchParam}">
                        <button class="btn btn-primary shadow-none" type="submit" style="background-color: #0a95ff; border: none;">
                            <i class="fa-solid fa-magnifying-glass"></i>
                        </button>
                    </div>
                </form>
            </div>

            <div class="row">
                <c:choose>
                    <c:when test="${empty blogList}">
                        <div class="col-12 text-center py-5">
                            <i class="fa-solid fa-newspaper fa-4x text-muted opacity-25 mb-3"></i>
                            <h5 class="text-muted">There are no blog posts available at the moment.</h5>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${blogList}" var="blog">
                            <div class="col-md-4 mb-4">
                                <div class="blog-card h-100 d-flex flex-column">
                                    <a href="${pageContext.request.contextPath}/blog/detail?id=${blog.blogId}">
                                        <img src="${not empty blog.thumbnailUrl ? blog.thumbnailUrl : 'https://placehold.co/600x400/eeeeee/999999?text=DevQuery+Blog'}" 
                                             onerror="this.onerror=null; this.src='https://placehold.co/600x400/eeeeee/999999?text=DevQuery+Blog';" 
                                             class="blog-thumbnail" alt="Thumbnail">
                                    </a>

                                    <div class="card-body d-flex flex-column flex-grow-1 p-3">
                                        <h2 class="blog-title">
                                            <a href="${pageContext.request.contextPath}/blog/detail?id=${blog.blogId}">
                                                ${blog.title}
                                            </a>
                                        </h2>

                                        <div class="blog-excerpt">
                                            ${blog.content}
                                        </div>

                                        <div class="mt-auto pt-3 border-top blog-meta">
                                            <div class="d-flex align-items-center">
                                                <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" class="author-avatar" alt="Admin">
                                                <span class="fw-bold text-dark">${blog.authorName}</span>
                                            </div>
                                            <div>
                                                <i class="fa-regular fa-clock me-1"></i> <fmt:formatDate value="${blog.createdAt}" pattern="dd/MM/yyyy" />
                                            </div>
                                        </div>

                                        <div class="text-end mt-2">
                                            <span class="badge bg-light text-secondary border">
                                                <i class="fa-regular fa-comments me-1"></i> ${blog.commentCount}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>

            <c:if test="${totalPages > 1}">
                <nav aria-label="Blog page navigation" class="mt-4 mb-5">
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link shadow-none" href="?page=${currentPage - 1}&search=${param.search}">
                                <i class="fa-solid fa-chevron-left"></i>
                            </a>
                        </li>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link shadow-none" href="?page=${i}&search=${param.search}">${i}</a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link shadow-none" href="?page=${currentPage + 1}&search=${param.search}">
                                <i class="fa-solid fa-chevron-right"></i>
                            </a>
                        </li>
                    </ul>
                </nav>
            </c:if>

        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>