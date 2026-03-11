<%@ page import="dto.QuestionDTO" %>
<%@ page import="dto.AnswerDTO" %>
<%@ page import="dto.UserDTO" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList,java.util.HashMap" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="util.CommentRenderUtil" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Câu hỏi - DevQuery</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Quill Rich Text Editor -->
    <link href="https://cdn.quilljs.com/1.3.6/quill.snow.css" rel="stylesheet">
    <%@ include file="partials/question-detail-styles.jspf" %>
</head>
<body>

<header>
    <div class="header-left">
        <button class="menu-btn" onclick="toggleMenu()">
            <i class="fa-solid fa-bars"></i>
        </button>
        <a href="${pageContext.request.contextPath}/home" class="logo">
            <i class="fa-brands fa-stack-overflow"></i>
            <span>Dev<b>Query</b></span>
        </a>
    </div>

    <div class="search-box">
        <form method="get" action="${pageContext.request.contextPath}/home">
            <input type="text" name="q" class="search-input" placeholder="Search...">
        </form>
    </div>

    <div class="header-right">
        <a href="<%=request.getContextPath()%>/logout">Log out</a>
    </div>
</header>

<div class="sidebar" id="sidebar">
    <ul class="nav-list">
        <li><a href="${pageContext.request.contextPath}/home" class="nav-link"><i class="fa-solid fa-house"></i> Home</a></li>
        <li><a href="#" class="nav-link"><i class="fa-solid fa-earth-americas"></i> Questions</a></li>
        <li><a href="${pageContext.request.contextPath}/ask" class="nav-link"><i class="fa-solid fa-pen"></i> Ask</a></li>
        <li><a href="${pageContext.request.contextPath}/tags" class="nav-link"><i class="fa-solid fa-tags"></i> Tags</a></li>
        <li><a href="#" class="nav-link"><i class="fa-solid fa-bookmark"></i> Saves</a></li>
    </ul>
</div>

<div class="container">
    <div class="main-content">
        <% 
            QuestionDTO question = (QuestionDTO) request.getAttribute("question");
            Object sessionPrincipal = session.getAttribute("user");
            Long currentUserId = null;
            String currentUserRole = null;
            if (sessionPrincipal instanceof UserDTO) {
                currentUserId = ((UserDTO) sessionPrincipal).getUserId();
                currentUserRole = ((UserDTO) sessionPrincipal).getRole();
            } else if (sessionPrincipal instanceof User) {
                currentUserId = ((User) sessionPrincipal).getUserId();
                currentUserRole = ((User) sessionPrincipal).getRole();
            }
            SimpleDateFormat editDateFormat = new SimpleDateFormat("MMM d, yyyy 'at' HH:mm");
            if (question != null) {
        %>

        <!-- Question -->
        <div class="question-box">
            <div class="vote-box">
                <%
                    String questionUserVote = (String) request.getAttribute("questionUserVote");
                    String upvoteClass = "upvote".equals(questionUserVote) ? " voted-up" : "";
                    String downvoteClass = "downvote".equals(questionUserVote) ? " voted-down" : "";
                    Boolean isBookmarked = (Boolean) request.getAttribute("isBookmarked");
                    if (isBookmarked == null) isBookmarked = false;
                    String bookmarkClass = isBookmarked ? " bookmarked" : "";
                    long qid = question.getQuestionId();
                %>
                <button type="button" id="question-upvote" class="vote-btn upvote-btn<%= upvoteClass %>" title="Upvote" 
                        data-question-id="<%= qid %>" data-vote-type="upvote" onclick="handleVoteClick(event, this)">
                    <i class="fa-solid fa-arrow-up"></i>
                </button>
                <div class="vote-count"><%= question.getScore() %></div>
                <button type="button" id="question-downvote" class="vote-btn downvote-btn<%= downvoteClass %>" title="Downvote" 
                        data-question-id="<%= qid %>" data-vote-type="downvote" onclick="handleVoteClick(event, this)">
                    <i class="fa-solid fa-arrow-down"></i>
                </button>
                <button type="button" id="bookmark-btn" class="vote-btn<%= bookmarkClass %>" title="<%= isBookmarked ? "Remove bookmark" : "Bookmark" %>" 
                        data-question-id="<%= qid %>" onclick="handleBookmarkClick(event, this)">
                    <i class="fa-solid fa-bookmark"></i>
                </button>
            </div>

            <div class="question-content">
                <div class="question-title-row">
                    <div class="question-title"><%= question.getTitle() %></div>
                    <div class="share-wrapper">
                        <button type="button" class="share-btn" onclick="toggleSharePopup(event)">
                            <i class="fa-solid fa-share-nodes"></i> Share
                        </button>
                        <div id="share-popup" class="share-popup">
                            <input type="text" id="share-link-input" class="share-input" readonly>
                            <button type="button" class="copy-link-btn" onclick="copyQuestionLink()">Copy link</button>
                            <span id="copy-link-status" class="copy-link-status"></span>
                        </div>
                    </div>
                </div>

                <div class="question-body">
                    <%= question.getBody() %>
                </div>

                <% if (question.getCodeSnippet() != null && !question.getCodeSnippet().isEmpty()) { %>
                <div class="code-block">
                    <code><%= question.getCodeSnippet().replace("<", "&lt;").replace(">", "&gt;") %></code>
                </div>
                <% } %>

                <% if (question.getTags() != null && !question.getTags().isEmpty()) { %>
                <div class="tags-list">
                    <% for (String tag : question.getTags()) { %>
                    <a href="#" class="tag-badge"><%= tag %></a>
                    <% } %>
                </div>
                <% } %>

                <div class="question-meta">
                    <div>
                        <strong>asked</strong> <%= question.getCreatedAt() %>
                        <% if (question.getUpdatedAt() != null && question.getCreatedAt() != null
                                && question.getUpdatedAt().after(question.getCreatedAt())) { %>
                        <span style="margin-left: 10px; color: #6a737c;">
                            edited <%= editDateFormat.format(question.getUpdatedAt()) %>
                        </span>
                        <% } %>
                    </div>
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <strong>viewed</strong> <%= question.getViewCount() %> times
                        <a class="edit-link" href="${pageContext.request.contextPath}/question/<%= question.getQuestionId() %>/revisions">Revisions</a>
                        <% if (currentUserId != null && currentUserId == question.getUserId()) { %>
                        <a class="edit-link" href="${pageContext.request.contextPath}/post/edit?type=question&id=<%= question.getQuestionId() %>">Edit</a>
                        <% } %>
                        <% if (currentUserId != null
                                && (currentUserId == question.getUserId()
                                || (currentUserRole != null && currentUserRole.equalsIgnoreCase("admin")))) { %>
                        <form method="post"
                              action="${pageContext.request.contextPath}/question/delete?id=<%= question.getQuestionId() %>"
                              style="display: inline;"
                              onsubmit="return confirmDeleteQuestion();">
                            <button type="submit"
                                    class="edit-link"
                                    style="background: none; border: none; padding: 0; cursor: pointer; color: #d93025;">
                                Delete
                            </button>
                        </form>
                        <% } %>
                    </div>
                </div>

                <div class="user-card">
                    <div class="user-avatar">
                        <i class="fa-solid fa-user"></i>
                    </div>
                    <div class="user-info">
                        <a href="#" class="user-name"><%= question.getAuthorName() %></a>
                        <div class="user-meta">Member since today • reputation: 1</div>
                    </div>
                </div>

                <!-- Question Comments Section -->
                <% 
                    java.util.List<dto.CommentDTO> questionComments = 
                        (java.util.List<dto.CommentDTO>) request.getAttribute("questionComments");
                    if (questionComments == null) questionComments = new java.util.ArrayList<>();
                    
                    // Determine if should be collapsed by default
                    boolean questionCommentsCollapsed = questionComments.size() > 3;
                %>
                
                <% if (!questionComments.isEmpty()) { %>
                <div class="question-comments" style="margin-top: 20px; padding-top: 15px; border-top: 1px solid #e3e6e8;">
                    <!-- Toggle Button -->
                    <button type="button" id="question-comments-toggle-btn" 
                            style="font-size: 13px; color: #0a95ff; background: none; border: none; cursor: pointer; padding: 0; margin-bottom: 12px;"
                            onclick="toggleQuestionComments()">
                        <span id="question-comments-toggle-text">
                            <%= questionCommentsCollapsed ? 
                                "Show comments (" + questionComments.size() + ")" : 
                                "Hide comments (" + questionComments.size() + ")" %>
                        </span>
                    </button>
                    
                    <!-- Comments Container -->
                    <div id="question-comments-container" 
                         style="<%= questionCommentsCollapsed ? "display: none;" : "display: block;" %>">
                        <% for (dto.CommentDTO comment : questionComments) { %>
                        <div class="comment-item" style="margin-bottom: 12px; font-size: 13px;">
                            <div style="color: #6a737c; margin-bottom: 4px;">
                                <span style="color: #0a95ff; font-weight: 500;"><%= comment.getAuthorName() %></span>
                                <span style="margin-left: 8px;"><%= comment.getCreatedAt() %></span>
                            </div>
                            <div style="color: #3b4045;"><%= comment.getBody() %></div>
                        </div>
                        <% } %>
                    </div>
                </div>
                <% } %>

                <!-- Add Comment Section -->
                <div style="margin-top: 15px; padding-top: 10px; border-top: 1px solid #e3e6e8;">
                    <%
                        Object principal = session.getAttribute("user");
                        boolean isLoggedIn = (principal instanceof UserDTO) || (principal instanceof User);
                    %>
                    <% if (isLoggedIn) { %>
                    <div id="add-question-comment-form" style="display: none; margin-top: 12px;">
                        <form method="post" action="${pageContext.request.contextPath}/comment/add">
                            <input type="hidden" name="questionId" value="<%= question.getQuestionId() %>">
                            <div style="display: flex; gap: 8px;">
                                <textarea name="commentBody" class="form-input" placeholder="Add a comment..." 
                                        style="flex: 1; min-height: 60px; font-size: 13px; padding: 8px;" required></textarea>
                            </div>
                            <div style="display: flex; gap: 8px; margin-top: 8px;">
                                <button type="submit" class="btn" style="padding: 6px 12px; font-size: 13px;">Add Comment</button>
                                <button type="button" class="btn" style="padding: 6px 12px; font-size: 13px; background: #f1f2f3; color: #3b4045;" 
                                        onclick="document.getElementById('add-question-comment-form').style.display='none'; document.getElementById('add-question-comment-btn').style.display='block';">Cancel</button>
                            </div>
                        </form>
                    </div>
                    <button type="button" id="add-question-comment-btn" class="reply-btn" style="font-size: 13px; color: #0a95ff; background: none; border: none; cursor: pointer; padding: 0;"
                            onclick="document.getElementById('add-question-comment-form').style.display='block'; this.style.display='none';">
                        Add a comment
                    </button>
                    <% } else { %>
                    <a href="${pageContext.request.contextPath}/auth/login" style="font-size: 13px; color: #0a95ff; text-decoration: none;">
                        Sign in to add a comment
                    </a>
                    <% } %>
                </div>
            </div>
        </div>

        <!-- Answers Section -->
        <div class="answers-section" id="answers-section">
            <div class="section-header">Answers</div>

            <%
                List answers = (java.util.List) request.getAttribute("answers");
                Map<Long, String> answerVotes = (java.util.Map<Long, String>) request.getAttribute("answerVotes");
                Boolean isQuestionOwner = (Boolean) request.getAttribute("isQuestionOwner");
            Integer answerCurrentPage = (Integer) request.getAttribute("answerCurrentPage");
            Integer answerTotalPages = (Integer) request.getAttribute("answerTotalPages");
            Integer answerTotalCount = (Integer) request.getAttribute("answerTotalCount");
                Integer answerPageSize = (Integer) request.getAttribute("answerPageSize");
            if (answerCurrentPage == null || answerCurrentPage < 1) answerCurrentPage = 1;
            if (answerTotalPages == null || answerTotalPages < 1) answerTotalPages = 1;
            if (answerTotalCount == null || answerTotalCount < 0) answerTotalCount = 0;
                if (answerPageSize == null || answerPageSize < 1) answerPageSize = 5;
                if (answerVotes == null) answerVotes = new java.util.HashMap<>();
                if (isQuestionOwner == null) isQuestionOwner = false;

                if (answers != null && !answers.isEmpty()) {
                    for (Object answerObj : answers) {
                        dto.AnswerDTO answer = (dto.AnswerDTO) answerObj;
                        String answerUserVote = answerVotes.get(answer.getAnswerId());
                        String answerUpvoteClass = "upvote".equals(answerUserVote) ? " voted-up" : "";
                        String answerDownvoteClass = "downvote".equals(answerUserVote) ? " voted-down" : "";
                        boolean accepted = answer.isIsAccepted();
            %>
            <div class="answer-box<%= accepted ? " accepted" : "" %>" id="answer-<%= answer.getAnswerId() %>">
                <div class="vote-box">
                    <button type="button" id="answer-upvote-<%= answer.getAnswerId() %>" class="vote-btn upvote-btn<%= answerUpvoteClass %>" title="Upvote" 
                            data-answer-id="<%= answer.getAnswerId() %>" data-vote-type="upvote" onclick="handleVoteClick(event, this)">
                        <i class="fa-solid fa-arrow-up"></i>
                    </button>
                    <div class="vote-count"><%= answer.getScore() %></div>
                    <button type="button" id="answer-downvote-<%= answer.getAnswerId() %>" class="vote-btn downvote-btn<%= answerDownvoteClass %>" title="Downvote" 
                            data-answer-id="<%= answer.getAnswerId() %>" data-vote-type="downvote" onclick="handleVoteClick(event, this)">
                        <i class="fa-solid fa-arrow-down"></i>
                    </button>
                    <%
                        Map<Long, Boolean> answerBookmarks = (java.util.Map<Long, Boolean>) request.getAttribute("answerBookmarks");
                        if (answerBookmarks == null) answerBookmarks = new java.util.HashMap<>();
                        Boolean answerIsBookmarked = answerBookmarks.get(answer.getAnswerId());
                        if (answerIsBookmarked == null) answerIsBookmarked = false;
                        String answerBookmarkClass = answerIsBookmarked ? " bookmarked" : "";
                    %>
                    <button type="button" id="answer-bookmark-<%= answer.getAnswerId() %>" class="vote-btn<%= answerBookmarkClass %>" title="<%= answerIsBookmarked ? "Remove bookmark" : "Bookmark" %>" 
                            data-answer-id="<%= answer.getAnswerId() %>" onclick="handleAnswerBookmarkClick(event, this)">
                        <i class="fa-solid fa-bookmark"></i>
                    </button>
                </div>

                <div class="answer-content">
                    <div class="answer-body">
                        <%= answer.getBody() %>
                    </div>

                    <% if (answer.getCodeSnippet() != null && !answer.getCodeSnippet().isEmpty()) { %>
                    <div class="code-block">
                        <code><%= answer.getCodeSnippet().replace("<", "&lt;").replace(">", "&gt;") %></code>
                    </div>
                    <% } %>

                    <div class="question-meta" style="display: flex; align-items: center; gap: 12px; flex-wrap: wrap;">
                        <div>
                            <strong>answered</strong> <%= answer.getCreatedAt() %>
                            <% if (answer.getUpdatedAt() != null && answer.getCreatedAt() != null
                                    && answer.getUpdatedAt().after(answer.getCreatedAt())) { %>
                            <span style="color: #6a737c; margin-left: 10px;">
                                edited <%= editDateFormat.format(answer.getUpdatedAt()) %>
                            </span>
                            <% } %>
                        </div>
                        <% if (currentUserId != null && currentUserId == answer.getUserId()) { %>
                        <a class="edit-link" href="${pageContext.request.contextPath}/post/edit?type=answer&id=<%= answer.getAnswerId() %>">Edit</a>
                        <% } %>
                        <a class="edit-link" href="${pageContext.request.contextPath}/answer/<%= answer.getAnswerId() %>/revisions">Revisions</a>
                        <% if (isQuestionOwner) { %>
                        <button type="button" class="accept-btn<%= accepted ? " accepted" : "" %>" 
                                data-question-id="<%= question.getQuestionId() %>" data-answer-id="<%= answer.getAnswerId() %>"
                                onclick="handleAcceptClick(event, this)" title="<%= accepted ? "Unaccept" : "Accept" %>">
                            <i class="fa-solid fa-check"></i> <%= accepted ? "Accepted" : "Accept" %>
                        </button>
                        <% } else if (accepted) { %>
                        <span style="color: #2e7d32; font-weight: 500;"><i class="fa-solid fa-check-circle"></i> Accepted</span>
                        <% } %>
                    </div>

                    <div class="user-card">
                        <div class="user-avatar">
                            <i class="fa-solid fa-user"></i>
                        </div>
                        <div class="user-info">
                            <a href="#" class="user-name"><%= answer.getAuthorName() %></a>
                            <div class="user-meta">Member since today • reputation: 1</div>
                        </div>
                    </div>

                    <!-- Comments Section -->
                    <% 
                        Map answerComments = 
                            (Map) request.getAttribute("answerComments");
                        Map answerCommentTrees =
                            (java.util.Map) request.getAttribute("answerCommentTrees");
                        if (answerComments == null) answerComments = new java.util.HashMap<>();
                        if (answerCommentTrees == null) answerCommentTrees = new java.util.HashMap<>();
                        List comments = (java.util.List) answerComments.get(answer.getAnswerId());
                        Map commentTree = (java.util.Map) answerCommentTrees.get(answer.getAnswerId());
                        if (comments == null) comments = new java.util.ArrayList<>();
                        if (commentTree == null) commentTree = new java.util.HashMap<>();
                        
                        // Determine if should be collapsed by default
                        boolean answerCommentsCollapsed = comments.size() > 3;
                    %>
                    
                    <% if (!comments.isEmpty()) { %>
                    <div class="comments-section" style="margin-top: 15px; padding-top: 10px; border-top: 1px solid #e3e6e8;">
                        <!-- Toggle Button -->
                        <button type="button" id="answer-comments-toggle-btn-<%= answer.getAnswerId() %>" 
                                style="font-size: 13px; color: #0a95ff; background: none; border: none; cursor: pointer; padding: 0; margin-bottom: 10px;"
                                onclick="toggleAnswerComments(<%= answer.getAnswerId() %>)">
                            <span id="answer-comments-toggle-text-<%= answer.getAnswerId() %>">
                                <%= answerCommentsCollapsed ? 
                                    "Show comments (" + comments.size() + ")" : 
                                    "Hide comments (" + comments.size() + ")" %>
                            </span>
                        </button>
                        
                        <!-- Comments Container -->
                        <div id="answer-comments-container-<%= answer.getAnswerId() %>" 
                             style="<%= answerCommentsCollapsed ? "display: none;" : "display: block;" %>">
                            <% List rootComments = (List) commentTree.get(null);
                                out.print(CommentRenderUtil.renderAnswerCommentThread(
                                    commentTree,
                                    rootComments,
                                        answer.getAnswerId(),
                                        question.getQuestionId(),
                                        isLoggedIn,
                                        request.getContextPath(),
                                        0));
                            %>
                        </div>
                    </div>
                    <% } %>
                    
                    <!-- Reply Button and Form -->
                    <div style="margin-top: 12px; padding-top: 10px; border-top: 1px solid #e3e6e8;">
                        <% if (isLoggedIn) { %>
                        <div id="comment-form-<%= answer.getAnswerId() %>" style="display: none; margin-top: 12px;">
                            <form method="post" action="${pageContext.request.contextPath}/comment/add">
                                <input type="hidden" name="answerId" value="<%= answer.getAnswerId() %>">
                                <input type="hidden" name="questionId" value="<%= question.getQuestionId() %>">
                                <div style="display: flex; gap: 8px;">
                                    <textarea name="commentBody" class="form-input" placeholder="Add a comment..." 
                                            style="flex: 1; min-height: 60px; font-size: 13px; padding: 8px;" required></textarea>
                                </div>
                                <div style="display: flex; gap: 8px; margin-top: 8px;">
                                    <button type="submit" class="btn" style="padding: 6px 12px; font-size: 13px;">Add Comment</button>
                                    <button type="button" class="btn" style="padding: 6px 12px; font-size: 13px; background: #f1f2f3; color: #3b4045;" 
                                            onclick="document.getElementById('comment-form-<%= answer.getAnswerId() %>').style.display='none'; document.getElementById('reply-btn-<%= answer.getAnswerId() %>').style.display='block';">Cancel</button>
                                </div>
                            </form>
                        </div>
                        <button type="button" class="reply-btn" style="font-size: 13px; color: #0a95ff; background: none; border: none; cursor: pointer; padding: 0;"
                                id="reply-btn-<%= answer.getAnswerId() %>"
                                onclick="document.getElementById('comment-form-<%= answer.getAnswerId() %>').style.display='block'; this.style.display='none';">
                            Add a comment
                        </button>
                        <% } else { %>
                        <a href="${pageContext.request.contextPath}/auth/login" style="font-size: 13px; color: #0a95ff; text-decoration: none;">
                            Sign in to add a comment
                        </a>
                        <% } %>
                    </div>
                </div>
            </div>
            <%
                    }
                } else {
            %>
            <div class="answer-box">
                <div class="answer-content">
                    <div class="empty-state">
                        <i class="fa-solid fa-lightbulb" style="font-size: 32px; margin-bottom: 10px;"></i>
                        <div>Chưa có câu trả lời nào</div>
                    </div>
                </div>
            </div>
            <%
                }
            %>

            <% if (answerTotalPages > 1) {
                int startAnswerIndex = ((answerCurrentPage - 1) * answerPageSize) + 1;
                int endAnswerIndex = Math.min(answerCurrentPage * answerPageSize, answerTotalCount);
                String detailPath = (String) request.getAttribute("answerPaginationPath");
                if (detailPath == null || detailPath.trim().isEmpty()) {
                    detailPath = request.getContextPath() + "/question/detail";
                }
            %>
            <div class="answers-pagination">
                <div class="answers-pagination-info">
                    Showing <%= startAnswerIndex %>-<%= endAnswerIndex %> of <%= answerTotalCount %> answers
                </div>
                <div class="answers-pagination-links">
                    <% if (answerCurrentPage > 1) { %>
                    <a href="<%= detailPath %>?id=<%= question.getQuestionId() %>&page=<%= answerCurrentPage - 1 %>#answers-section" aria-label="Previous page">&laquo;</a>
                    <% } else { %>
                    <span class="disabled" aria-disabled="true">&laquo;</span>
                    <% } %>

                    <% for (int pageIndex = 1; pageIndex <= answerTotalPages; pageIndex++) { %>
                    <a href="<%= detailPath %>?id=<%= question.getQuestionId() %>&page=<%= pageIndex %>#answers-section"
                       class="<%= answerCurrentPage == pageIndex ? "active" : "" %>"><%= pageIndex %></a>
                    <% } %>

                    <% if (answerCurrentPage < answerTotalPages) { %>
                    <a href="<%= detailPath %>?id=<%= question.getQuestionId() %>&page=<%= answerCurrentPage + 1 %>#answers-section" aria-label="Next page">&raquo;</a>
                    <% } else { %>
                    <span class="disabled" aria-disabled="true">&raquo;</span>
                    <% } %>
                </div>
            </div>
            <% } %>
        </div>

        <!-- Add Answer Section -->
        <div class="add-answer-section">
            <div class="section-header">Your Answer</div>

            <form method="post" action="${pageContext.request.contextPath}/answer/create" onsubmit="return handleAnswerFormSubmit(event)">
                <input type="hidden" name="questionId" value="<%= question.getQuestionId() %>">
                <input type="hidden" name="answerBody" id="answer-body-hidden">

                <div class="form-group">
                    <label class="form-label">Answer</label>
                    <div id="answer-editor"></div>
                </div>

                <button type="submit" class="btn">Post Answer</button>
            </form>
        </div>

        <% } else { %>
        <div class="empty-state">
            <i class="fa-solid fa-circle-xmark" style="font-size: 48px;"></i>
            <div style="margin-top: 10px;">
                <% if (request.getAttribute("error") != null) { %>
                    <%= request.getAttribute("error") %>
                <% } else { %>
                    Câu hỏi không tồn tại
                <% } %>
            </div>
        </div>
        <% } %>
    </div>

    <!-- Right Sidebar -->
    <div class="sidebar-right">
        <% 
            List<dto.QuestionDTO> relatedQuestions = (List<dto.QuestionDTO>) request.getAttribute("relatedQuestions");
            if (relatedQuestions == null) {
                relatedQuestions = new java.util.ArrayList<>();
            }
            
            // Split related questions into linked and related
            int mid = (relatedQuestions.size() + 1) / 2;
            List<dto.QuestionDTO> linkedQuestions = relatedQuestions.subList(0, Math.min(mid, relatedQuestions.size()));
            List<dto.QuestionDTO> relatedOnlyQuestions = relatedQuestions.subList(Math.min(mid, relatedQuestions.size()), relatedQuestions.size());
        %>
        
        <% if (!linkedQuestions.isEmpty()) { %>
        <div class="card">
            <div class="card-title"><i class="fa-solid fa-link"></i> Linked</div>
            <div class="linked-box">
                <% for (dto.QuestionDTO q : linkedQuestions) { %>
                <a href="${pageContext.request.contextPath}/question/detail?id=<%= q.getQuestionId() %>" class="linked-link"><%= q.getTitle() %></a>
                <% } %>
            </div>
        </div>
        <% } %>

        <% if (!relatedOnlyQuestions.isEmpty()) { %>
        <div class="card">
            <div class="card-title"><i class="fa-solid fa-fire"></i> Related</div>
            <div class="linked-box">
                <% for (dto.QuestionDTO q : relatedOnlyQuestions) { %>
                <a href="${pageContext.request.contextPath}/question/detail?id=<%= q.getQuestionId() %>" class="linked-link"><%= q.getTitle() %></a>
                <% } %>
            </div>
        </div>
        <% } %>
    </div>
</div>

<!-- Footer -->
<%@ include file="../Common/footer.jsp" %>

<%@ include file="partials/question-detail-scripts.jspf" %>
</body>
</html>
