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

        <jsp:include page="../Common/header.jsp" />

        <div class="container">
            <div class="left-sidebar">
                <jsp:include page="../Common/sidebar.jsp" />
            </div>

            <div class="main-content">
                <% 
                    QuestionDTO question = (QuestionDTO) request.getAttribute("question");
                    Object sessionPrincipal = session.getAttribute("user");
                    Long currentUserId = null;
                    String currentUserRole = null;
                    int currentUserReputation = 0;
                    if (sessionPrincipal instanceof UserDTO) {
                        currentUserId = ((UserDTO) sessionPrincipal).getUserId();
                        currentUserRole = ((UserDTO) sessionPrincipal).getRole();
                        currentUserReputation = ((UserDTO) sessionPrincipal).getReputation();
                    } else if (sessionPrincipal instanceof User) {
                        currentUserId = ((User) sessionPrincipal).getUserId();
                        currentUserRole = ((User) sessionPrincipal).getRole();
                        currentUserReputation = ((User) sessionPrincipal).getReputation();
                    }
                    boolean isLoggedIn = (sessionPrincipal instanceof UserDTO) || (sessionPrincipal instanceof User);
                    SimpleDateFormat editDateFormat = new SimpleDateFormat("MMM d, yyyy 'at' HH:mm");
                    int minBountyReputation = request.getAttribute("minBountyReputation") instanceof Integer ? (Integer) request.getAttribute("minBountyReputation") : 100;
                    int bountyDurationDays = request.getAttribute("bountyDurationDays") instanceof Integer ? (Integer) request.getAttribute("bountyDurationDays") : 7;
                    if (question != null) {
                    boolean isQuestionClosed = question.isIsClosed();
                    boolean hasAnyBounty = question.getBountyAmount() > 0;
                    boolean hasActiveBounty = hasAnyBounty && question.getBountyExpiresAt() != null && question.getBountyExpiresAt().after(new java.util.Date());
                    boolean bountyBlockedByAcceptedAnswer = question.getAcceptedAnswerId() != null;
                    boolean bountyOwner = currentUserId != null && currentUserId == question.getUserId();
                    boolean canOpenBountyModal = !isQuestionClosed && bountyOwner && !hasActiveBounty && !bountyBlockedByAcceptedAnswer;
                    boolean canAddBounty = canOpenBountyModal && currentUserReputation >= minBountyReputation;
                %>

                <% if ("success".equals(request.getParameter("flag"))) { %>
                <div class="flag-notice success" role="status" aria-live="polite">
                    Your report has been submitted. Thank you for helping keep the community safe.
                </div>
                <% } %>
                <% if (request.getParameter("flagError") != null) { %>
                <div class="flag-notice error" role="alert">
                    <%= request.getParameter("flagError") %>
                </div>
                <% } %>
                <% if ("success".equals(request.getParameter("close"))) { %>
                <div class="flag-notice success" role="status" aria-live="polite">
                    This question has been closed by a high-reputation user.
                </div>
                <% } %>
                <% if (request.getParameter("closeError") != null) { %>
                <div class="flag-notice error" role="alert">
                    <%= request.getParameter("closeError") %>
                </div>
                <% } %>
                <% if ("success".equals(request.getParameter("bounty"))) { %>
                <div class="flag-notice success" role="status" aria-live="polite">
                    Bounty has been added successfully. The spent reputation for this bounty is not refundable.
                </div>
                <% } %>
                <% if (request.getParameter("bountyError") != null) { %>
                <div class="flag-notice error" role="alert">
                    <%= request.getParameter("bountyError") %>
                </div>
                <% } %>
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
                        <% if (!isQuestionClosed) { %>
                        <button type="button" id="question-upvote" class="vote-btn upvote-btn<%= upvoteClass %>" title="Upvote" 
                                data-question-id="<%= qid %>" data-vote-type="upvote" onclick="handleVoteClick(event, this)">
                            <i class="fa-solid fa-arrow-up"></i>
                        </button>
                        <% } %>
                        <div class="vote-count"><%= question.getScore() %></div>
                        <% if (!isQuestionClosed) { %>
                        <button type="button" id="question-downvote" class="vote-btn downvote-btn<%= downvoteClass %>" title="Downvote" 
                                data-question-id="<%= qid %>" data-vote-type="downvote" onclick="handleVoteClick(event, this)">
                            <i class="fa-solid fa-arrow-down"></i>
                        </button>
                        <button type="button" id="bookmark-btn" class="vote-btn<%= bookmarkClass %>" title="<%= isBookmarked ? "Remove bookmark" : "Bookmark" %>" 
                                data-question-id="<%= qid %>" onclick="handleBookmarkClick(event, this)">
                            <i class="fa-solid fa-bookmark"></i>
                        </button>
                        <% } %>
                    </div>

                    <div class="question-content">
                        <div class="question-title-row">
                            <div>
                                <div class="question-title"><%= question.getTitle() %></div>
                                <% if (question.isIsClosed()) { %>
                                <span class="closed-badge"><i class="fa-solid fa-lock"></i> Closed</span>
                                <% } %>
                            </div>
                            <div class="post-actions-group">
                                <% if (bountyOwner) { %>
                                <button type="button"
                                        class="action-btn<%= canAddBounty ? "" : " disabled" %>"
                                        <%= canAddBounty ? "onclick=\"openBountyModal('" + question.getQuestionId() + "')\"" : "disabled title=\"Bounty requires owner access, enough reputation, no accepted answer, and no active bounty\"" %>>
                                    <i class="fa-solid fa-coins"></i> Add Bounty
                                </button>
                                <% } %>
                                <div class="share-wrapper">
                                    <% if (!isQuestionClosed) { %>
                                    <button type="button" class="action-btn" onclick="toggleSharePopup(event)">
                                        <i class="fa-solid fa-share-nodes"></i> Share
                                    </button>
                                    <div id="share-popup" class="share-popup">
                                        <input type="text" id="share-link-input" class="share-input" readonly>
                                        <button type="button" class="copy-link-btn" onclick="copyQuestionLink()">Copy link</button>
                                        <span id="copy-link-status" class="copy-link-status"></span>
                                    </div>
                                    <% } %>
                                </div>
                                <% if (!isQuestionClosed) { %>
                                <a class="action-btn" href="${pageContext.request.contextPath}/question/<%= question.getQuestionId() %>/revisions">
                                    <i class="fa-solid fa-clock-rotate-left"></i> Revisions
                                </a>
                                <% } %>
                                <% if (!isQuestionClosed && isLoggedIn && (currentUserId == null || currentUserId != question.getUserId())) { %>
                                <button type="button"
                                        class="action-btn"
                                        onclick="openFlagModal('question', '<%= question.getQuestionId() %>', '<%= question.getQuestionId() %>', '')">
                                    <i class="fa-solid fa-flag"></i> Flag
                                </button>
                                <% } %>
                                <% if (!isQuestionClosed && isLoggedIn && currentUserReputation >= 3000 && !question.isIsClosed()) { %>
                                <button type="button"
                                        class="action-btn action-btn-warning"
                                        onclick="openCloseModal('<%= question.getQuestionId() %>')">
                                    <i class="fa-solid fa-lock"></i> Close Question
                                </button>
                                <% } %>
                                <% if (!isQuestionClosed && currentUserId != null && (currentUserId == question.getUserId() || currentUserReputation >= 3000)) { %>                                <a class="action-btn" href="${pageContext.request.contextPath}/post/edit?type=question&id=<%= question.getQuestionId() %>">
                                    <i class="fa-solid fa-pen"></i> Edit
                                </a>
                                <% } %>
                                <% if (!isQuestionClosed && currentUserId != null
                                        && (currentUserId == question.getUserId()
                                        || (currentUserRole != null && currentUserRole.equalsIgnoreCase("admin")))) { %>
                                <form method="post"
                                      action="${pageContext.request.contextPath}/question/delete?id=<%= question.getQuestionId() %>"
                                      class="inline-action-form"
                                      onsubmit="return confirmDeleteQuestion();">
                                    <button type="submit" class="action-btn action-btn-danger">
                                        <i class="fa-solid fa-trash"></i> Delete
                                    </button>
                                </form>
                                <% } %>
                            </div>
                        </div>

                        <div class="question-body">
                            <% if (hasAnyBounty) { %>
                            <div class="bounty-panel<%= hasActiveBounty ? "" : " expired" %>">
                                <div class="bounty-summary">
                                    <span class="bounty-badge"><i class="fa-solid fa-coins"></i> +<%= question.getBountyAmount() %> bounty</span>
                                    <div class="bounty-meta">
                                        <% if (hasActiveBounty) { %>
                                        <span class="bounty-countdown" data-bounty-expiry="<%= question.getBountyExpiresAt().getTime() %>"></span>
                                        <span class="bounty-hint">Expires at <%= editDateFormat.format(question.getBountyExpiresAt()) %></span>
                                        <% } else { %>
                                        <span class="bounty-countdown">Expired</span>
                                        <span class="bounty-hint">Last bounty ended at <%= question.getBountyExpiresAt() != null ? editDateFormat.format(question.getBountyExpiresAt()) : "unknown time" %></span>
                                        <% } %>
                                    </div>
                                </div>
                                <% if (bountyOwner && !canAddBounty) { %>
                                <div class="bounty-hint">
                                    <% if (currentUserReputation < minBountyReputation) { %>
                                    Need at least <%= minBountyReputation %> reputation to start a bounty.
                                    <% } else if (bountyBlockedByAcceptedAnswer) { %>
                                    Bounty is disabled because this question already has an accepted answer.
                                    <% } else if (hasActiveBounty) { %>
                                    You already have an active bounty on this question.
                                    <% } %>
                                </div>
                                <% } %>
                            </div>
                            <% } %>
                            <%= question.getBody() %>
                        </div>

                        <% if (question.isIsClosed()) { %>
                        <div class="closed-question-message">
                            <strong>This question has been closed by a high-reputation user.</strong>
                            <% if (question.getClosedReason() != null && !question.getClosedReason().isEmpty()) { %>
                            <div class="closed-question-reason">Reason: <%= question.getClosedReason() %></div>
                            <% } %>
                        </div>
                        <% } %>

                        <% if (question.getCodeSnippet() != null && !question.getCodeSnippet().isEmpty()) { %>
                        <div class="code-block">
                            <code><%= question.getCodeSnippet().replace("<", "&lt;").replace(">", "&gt;") %></code>
                        </div>
                        <% } %>

                        <% if (question.getTags() != null && !question.getTags().isEmpty()) { %>
                        <div class="tags-list">
                            <% for (String tag : question.getTags()) { %>
                            <a href="${pageContext.request.contextPath}/home?tag=<%= tag %>" class="tag-badge"><%= tag %></a>                            <% } %>
                        </div>
                        <% } %>

                        <div class="post-footer-row">
                            <div class="question-meta question-meta-main">
                                <div>
                                    <strong>asked</strong> <%= question.getCreatedAt() %>
                                    <% if (question.getUpdatedAt() != null && question.getCreatedAt() != null
                                    && question.getUpdatedAt().after(question.getCreatedAt())) { %>
                                    <span style="margin-left: 10px; color: #6a737c;">
                                        edited <%= editDateFormat.format(question.getUpdatedAt()) %>
                                    </span>
                                    <% } %>
                                </div>
                                <div>
                                    <strong>viewed</strong> <%= question.getViewCount() %> times
                                </div>
                            </div>

                            <div class="user-card user-card-compact">
                                <div class="user-avatar">
                                    <i class="fa-solid fa-user"></i>
                                </div>
                                <div class="user-info">
                                    <a href="${pageContext.request.contextPath}/profile?id=<%= question.getUserId() %>" class="user-name"><%= question.getAuthorName() %></a>                                    <div class="user-meta">asked at <%= question.getCreatedAt() %></div>
                                    <div class="user-meta">reputation: <%= question.getAuthorReputation() %></div>
                                </div>
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
                            <% if (isLoggedIn && !isQuestionClosed) { %>
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
                                                onclick="document.getElementById('add-question-comment-form').style.display = 'none'; document.getElementById('add-question-comment-btn').style.display = 'block';">Cancel</button>
                                    </div>
                                </form>
                            </div>
                            <button type="button" id="add-question-comment-btn" class="reply-btn" style="font-size: 13px; color: #0a95ff; background: none; border: none; cursor: pointer; padding: 0;"
                                    onclick="document.getElementById('add-question-comment-form').style.display = 'block'; this.style.display = 'none';">
                                Add a comment
                            </button>
                            <% } else if (!isLoggedIn) { %>
                            <a href="${pageContext.request.contextPath}/auth/login" style="font-size: 13px; color: #0a95ff; text-decoration: none;">
                                Sign in to add a comment
                            </a>
                            <% } else { %>
                            <span style="font-size: 13px; color: #6a737c;">Comments are disabled because this question is closed</span>
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
                          String sort = (String) request.getAttribute("sort");
                        String answerFilterQuery = (String) request.getAttribute("answerFilterQuery");
                        if (sort == null || sort.trim().isEmpty()) sort = "score_desc";
                        if (answerFilterQuery == null) answerFilterQuery = "";
                        if (answerVotes == null) answerVotes = new java.util.HashMap<>();
                        if (isQuestionOwner == null) isQuestionOwner = false;
                    %>

                    <form method="get" action="${pageContext.request.contextPath}/question/detail" class="answers-filter-form">
                        <input type="hidden" name="id" value="<%= question.getQuestionId() %>">
                        <div class="answers-filter-row">
                            <label for="answer-sort">Sort by</label>
                            <select id="answer-sort" name="sort">
                                <option value="score_desc" <%= "score_desc".equals(sort) ? "selected" : "" %>>Highest score</option>
                                <option value="score_asc" <%= "score_asc".equals(sort) ? "selected" : "" %>>Lowest score</option>
                                <option value="newest" <%= "newest".equals(sort) ? "selected" : "" %>>Newest</option>
                                <option value="oldest" <%= "oldest".equals(sort) ? "selected" : "" %>>Oldest</option>
                            </select>
                            <button type="submit" class="btn">Apply</button>
                        </div>
                    </form>

                    <%
                    
                    if (answers != null && !answers.isEmpty()) {
                    for (Object answerObj : answers) {
                    dto.AnswerDTO answer = (dto.AnswerDTO) answerObj;
                    String answerUserVote = answerVotes.get(answer.getAnswerId());
                    String answerUpvoteClass = "upvote".equals(answerUserVote) ? " voted-up" : "";
                    String answerDownvoteClass = "downvote".equals(answerUserVote) ? " voted-down" : "";
                    boolean accepted = answer.isIsAccepted();
                    boolean bountySelfAcceptBlocked = hasActiveBounty && currentUserId != null && currentUserId == answer.getUserId();
                    %>
                    <div class="answer-box<%= accepted ? " accepted" : "" %>" id="answer-<%= answer.getAnswerId() %>">
                        <div class="vote-box">
                            <% if (!isQuestionClosed) { %>
                            <button type="button" id="answer-upvote-<%= answer.getAnswerId() %>" class="vote-btn upvote-btn<%= answerUpvoteClass %>" title="Upvote" 
                                    data-answer-id="<%= answer.getAnswerId() %>" data-vote-type="upvote" onclick="handleVoteClick(event, this)">
                                <i class="fa-solid fa-arrow-up"></i>
                            </button>
                            <% } %>
                            <div class="vote-count"><%= answer.getScore() %></div>

                            <% if (!isQuestionClosed) { %>
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
                            <% } %>
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

                            <div class="post-footer-row answer-footer-row">
                                <div class="answer-meta-block">
                                    <div class="answer-meta-info">
                                        <strong>Answered</strong> <%= answer.getCreatedAt() %>
                                        <% if (answer.getUpdatedAt() != null && answer.getCreatedAt() != null
                                        && answer.getUpdatedAt().after(answer.getCreatedAt())) { %>
                                        <span style="color: #6a737c; margin-left: 10px;">
                                            Edited <%= editDateFormat.format(answer.getUpdatedAt()) %>
                                        </span>
                                        <% } %>
                                    </div>

                                    <div class="answer-meta-actions">
                                        <% if (!isQuestionClosed && currentUserId != null && currentUserId == answer.getUserId()) { %>
                                        <a class="action-btn" href="${pageContext.request.contextPath}/post/edit?type=answer&id=<%= answer.getAnswerId() %>">
                                            <i class="fa-solid fa-pen"></i> Edit
                                        </a>
                                        <% } %>
                                        <% if (!isQuestionClosed) { %>
                                        <a class="action-btn" href="${pageContext.request.contextPath}/answer/<%= answer.getAnswerId() %>/revisions">
                                            <i class="fa-solid fa-clock-rotate-left"></i> Revisions
                                        </a>
                                        <% } %>
                                        <% if (!isQuestionClosed && isLoggedIn && (currentUserId == null || currentUserId != answer.getUserId())) { %>
                                        <button type="button"
                                                class="action-btn"
                                                onclick="openFlagModal('answer', '<%= answer.getAnswerId() %>', '<%= question.getQuestionId() %>', '<%= answer.getAnswerId() %>')">
                                            <i class="fa-solid fa-flag"></i> Flag
                                        </button>
                                        <% } %>
                                        <% if (!isQuestionClosed && isQuestionOwner && !bountySelfAcceptBlocked) { %>
                                        <button type="button" class="accept-btn<%= accepted ? " accepted" : "" %>" 
                                                data-question-id="<%= question.getQuestionId() %>" data-answer-id="<%= answer.getAnswerId() %>"
                                                onclick="handleAcceptClick(event, this)" title="<%= accepted ? "Unaccept" : "Accept" %>">
                                            <i class="fa-solid fa-check"></i> <%= accepted ? "Accepted" : "Accept" %>
                                        </button>
                                        <% } else if (!isQuestionClosed && isQuestionOwner && bountySelfAcceptBlocked) { %>
                                        <span style="color: #8a6d3b; font-weight: 500;"><i class="fa-solid fa-ban"></i> Cannot accept your own answer while bounty is active</span>
                                        <% } else if (accepted) { %>
                                        <span style="color: #2e7d32; font-weight: 500;"><i class="fa-solid fa-check-circle"></i> Accepted</span>
                                        <% } %>
                                    </div>
                                </div>

                                <div class="user-card user-card-compact">
                                    <div class="user-avatar">
                                        <i class="fa-solid fa-user"></i>
                                    </div>
                                    <div class="user-info">
                                        <a href="${pageContext.request.contextPath}/profile?id=<%= answer.getUserId() %>" class="user-name"><%= answer.getAuthorName() %></a>                                        <div class="user-meta">answered at <%= answer.getCreatedAt() %></div>
                                        <div class="user-meta">reputation: <%= answer.getAuthorReputation() %></div>
                                    </div>
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
                                <% if (isLoggedIn && !isQuestionClosed) { %>
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
                                                    onclick="document.getElementById('comment-form-<%= answer.getAnswerId() %>').style.display = 'none'; document.getElementById('reply-btn-<%= answer.getAnswerId() %>').style.display = 'block';">Cancel</button>
                                        </div>
                                    </form>
                                </div>
                                <button type="button" class="reply-btn" style="font-size: 13px; color: #0a95ff; background: none; border: none; cursor: pointer; padding: 0;"
                                        id="reply-btn-<%= answer.getAnswerId() %>"
                                        onclick="document.getElementById('comment-form-<%= answer.getAnswerId() %>').style.display = 'block'; this.style.display = 'none';">
                                    Add a comment
                                </button>
                                <% } else if (!isLoggedIn) { %>
                                <a href="${pageContext.request.contextPath}/auth/login" style="font-size: 13px; color: #0a95ff; text-decoration: none;">
                                    Sign in to add a comment
                                </a>
                                <% } else { %>
                                <span style="font-size: 13px; color: #6a737c;">Comments are disabled because this question is closed</span>
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
                            <a href="<%= detailPath %>?id=<%= question.getQuestionId() %>&page=<%= answerCurrentPage - 1 %><%= answerFilterQuery %>#answers-section" aria-label="Previous page">&laquo;</a>                            <% } else { %>
                            <span class="disabled" aria-disabled="true">&laquo;</span>
                            <% } %>

                            <% for (int pageIndex = 1; pageIndex <= answerTotalPages; pageIndex++) { %>
                            <a href="<%= detailPath %>?id=<%= question.getQuestionId() %>&page=<%= pageIndex %><%= answerFilterQuery %>#answers-section"                               class="<%= answerCurrentPage == pageIndex ? "active" : "" %>"><%= pageIndex %></a>
                            <% } %>

                            <% if (answerCurrentPage < answerTotalPages) { %>
                            <a href="<%= detailPath %>?id=<%= question.getQuestionId() %>&page=<%= answerCurrentPage + 1 %><%= answerFilterQuery %>#answers-section" aria-label="Next page">&raquo;</a>                            <% } else { %>
                            <span class="disabled" aria-disabled="true">&raquo;</span>
                            <% } %>
                        </div>
                    </div>
                    <% } %>
                </div>

                <!-- Add Answer Section -->
                <% if (!isQuestionClosed) { %>
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
                <div class="add-answer-section">
                    <div class="section-header">This question is closed</div>
                    <div style="font-size: 14px; color: #6a737c;">All interaction features are disabled. You can only view the question and existing answers.</div>
                </div>
                <% } %>

                <% } %>

                <% if (question == null) { %>
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
                    List<dto.QuestionDTO> popularQuestions = (List<dto.QuestionDTO>) request.getAttribute("popularQuestions");
                    if (relatedQuestions == null) {
                        relatedQuestions = new java.util.ArrayList<>();
                    }
                    if (popularQuestions == null) {
                        popularQuestions = new java.util.ArrayList<>();
                    }
            
                    // Split related questions into linked and related
                    int mid = (relatedQuestions.size() + 1) / 2;
                    List<dto.QuestionDTO> linkedQuestions = relatedQuestions.subList(0, Math.min(mid, relatedQuestions.size()));
                    List<dto.QuestionDTO> relatedOnlyQuestions = relatedQuestions.subList(Math.min(mid, relatedQuestions.size()), relatedQuestions.size());
                %>

                <% if (!popularQuestions.isEmpty()) { %>
                <div class="card">
                    <div class="card-title"><i class="fa-solid fa-chart-line"></i> Popular Questions</div>
                    <div class="linked-box">
                        <% for (dto.QuestionDTO q : popularQuestions) { %>
                        <a href="${pageContext.request.contextPath}/question/detail?id=<%= q.getQuestionId() %>" class="linked-link"><%= q.getTitle() %></a>
                        <% } %>
                    </div>
                </div>
                <% } %>

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

        <div id="flag-modal" class="simple-modal" aria-hidden="true">
            <div class="simple-modal-dialog flag-modal-dialog" role="dialog" aria-modal="true" aria-labelledby="flag-modal-title">
                <div class="simple-modal-header">
                    <h3 id="flag-modal-title">Report Content</h3>
                    <button type="button" class="simple-modal-close" onclick="closeFlagModal()" aria-label="Close">
                        <i class="fa-solid fa-xmark"></i>
                    </button>
                </div>
                <div class="simple-modal-body flag-modal-body">
                    <form method="post" action="${pageContext.request.contextPath}/flag/submit" class="flag-form">
                        <input type="hidden" id="flag-post-type" name="postType">
                        <input type="hidden" id="flag-post-id" name="postId">
                        <input type="hidden" id="flag-question-id" name="questionId" value="<%= question != null ? question.getQuestionId() : 0 %>">
                        <input type="hidden" id="flag-answer-id" name="answerId">

                        <div class="flag-field">
                            <label for="flag-reason" class="form-label">Reason</label>
                            <select id="flag-reason" name="reason" class="form-input" required>
                                <option value="">Select a reason</option>
                                <option value="Spam">Spam</option>
                                <option value="Harassment or abusive language">Harassment or abusive language</option>
                                <option value="Misleading content">Misleading content</option>
                                <option value="Other">Other</option>
                            </select>
                        </div>

                        <div class="flag-field">
                            <label for="flag-note" class="form-label">Note (optional)</label>
                            <textarea id="flag-note"
                                      name="note"
                                      class="form-input"
                                      maxlength="500"
                                      placeholder="Add a short note (optional)..."></textarea>
                        </div>

                        <div class="flag-actions">
                            <button type="submit" class="btn">Submit Report</button>
                            <button type="button" class="btn btn-secondary" onclick="closeFlagModal()">Cancel</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div id="close-modal" class="simple-modal" aria-hidden="true">
            <div class="simple-modal-dialog flag-modal-dialog" role="dialog" aria-modal="true" aria-labelledby="close-modal-title">
                <div class="simple-modal-header">
                    <h3 id="close-modal-title">Close Question</h3>
                    <button type="button" class="simple-modal-close" onclick="closeCloseModal()" aria-label="Close">
                        <i class="fa-solid fa-xmark"></i>
                    </button>
                </div>
                <div class="simple-modal-body flag-modal-body">
                    <form method="post" action="${pageContext.request.contextPath}/question/close" class="flag-form">
                        <input type="hidden" id="close-question-id" name="questionId" value="<%= question != null ? question.getQuestionId() : 0 %>">

                        <div class="flag-field">
                            <label for="close-reason" class="form-label">Reason</label>
                            <select id="close-reason" name="closeReason" class="form-input" required>
                                <option value="">Select a reason</option>
                                <option value="Duplicate question">Duplicate question</option>
                                <option value="Needs more details">Needs more details</option>
                                <option value="Off-topic">Off-topic</option>
                                <option value="Opinion-based">Opinion-based</option>
                            </select>
                        </div>

                        <div class="flag-actions">
                            <button type="submit" class="btn">Close Question</button>
                            <button type="button" class="btn btn-secondary" onclick="closeCloseModal()">Cancel</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div id="bounty-modal" class="simple-modal" aria-hidden="true">
            <div class="simple-modal-dialog flag-modal-dialog" role="dialog" aria-modal="true" aria-labelledby="bounty-modal-title">
                <div class="simple-modal-header">
                    <h3 id="bounty-modal-title">Add Bounty</h3>
                    <button type="button" class="simple-modal-close" onclick="closeBountyModal()" aria-label="Close">
                        <i class="fa-solid fa-xmark"></i>
                    </button>
                </div>
                <div class="simple-modal-body flag-modal-body">
                    <form method="post" action="${pageContext.request.contextPath}/question/bounty" class="flag-form">
                        <input type="hidden" id="bounty-question-id" name="questionId" value="<%= question != null ? question.getQuestionId() : 0 %>">

                        <div class="bounty-form-copy">
                            Start a bounty to attract more answers. The bounty lasts <strong><%= bountyDurationDays %> days</strong> and the spent reputation will not be refunded.
                        </div>

                        <div class="flag-field">
                            <label for="bounty-amount" class="form-label">Bounty amount</label>
                            <select id="bounty-amount" name="amount" class="form-input" required>
                                <option value="50">50 reputation</option>
                                <option value="100">100 reputation</option>
                                <option value="150">150 reputation</option>
                                <option value="200">200 reputation</option>
                                <option value="250">250 reputation</option>
                                <option value="500">500 reputation</option>
                            </select>
                        </div>

                        <ul class="bounty-rule-list">
                            <li>Only the question owner can add a bounty.</li>
                            <li>You need at least <%= minBountyReputation %> reputation before starting one.</li>
                            <li>Bounty cannot be added when the question already has an accepted answer.</li>
                            <li>Only one active bounty is allowed on a question at a time.</li>
                        </ul>

                        <div class="flag-actions">
                            <button type="submit" class="btn">Start Bounty</button>
                            <button type="button" class="btn btn-secondary" onclick="closeBountyModal()">Cancel</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <jsp:include page="../Common/footer.jsp" />

        <%@ include file="partials/question-detail-scripts.jspf" %>
    </body>
</html>
