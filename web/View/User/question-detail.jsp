<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dto.QuestionDTO" %>
<%@ page import="dto.AnswerDTO" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Câu hỏi - DevQuery</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Liberation Sans", sans-serif;
        }

        body {
            background-color: #f1f2f3;
            color: #3b4045;
        }

        /* Header */
        header {
            background-color: white;
            height: 53px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            box-shadow: 0 1px 2px rgba(0,0,0,0.05);
            border-top: 3px solid #f48024;
            position: fixed;
            top: 0;
            width: 100%;
            z-index: 1000;
            padding: 0 10px;
        }

        .header-left {
            display: flex;
            align-items: center;
        }

        .menu-btn {
            background: none;
            border: none;
            cursor: pointer;
            padding: 0 15px;
            font-size: 18px;
            color: #525960;
            transition: color 0.2s;
        }

        .menu-btn:hover {
            color: #232629;
        }

        .logo {
            display: flex;
            align-items: center;
            margin-left: 5px;
            text-decoration: none;
            color: black;
        }

        .logo i {
            color: #f48024;
            font-size: 24px;
            margin-right: 5px;
        }

        .logo span {
            font-size: 18px;
            font-weight: 400;
        }

        .logo span b {
            font-weight: 700;
        }

        .search-box {
            flex: 1;
            max-width: 600px;
            margin: 0 20px;
        }

        .search-input {
            width: 100%;
            padding: 8px 12px;
            font-size: 13px;
            border: 1px solid #c8ccd0;
            border-radius: 4px;
            background-color: #f8f9fa;
        }

        .search-input:focus {
            background-color: white;
            border-color: #0a95ff;
            outline: none;
        }

        .header-right {
            padding-right: 15px;
        }

        .header-right a {
            text-decoration: none;
            color: #525960;
            padding: 8px 12px;
            border-radius: 1000px;
        }

        .header-right a:hover {
            background-color: #e3e6e8;
        }

        /* Sidebar */
        .sidebar {
            position: fixed;
            top: 53px;
            left: -240px;
            width: 240px;
            height: calc(100vh - 53px);
            background-color: white;
            box-shadow: 1px 0 3px rgba(0,0,0,0.05);
            transition: left 0.3s ease;
            padding-top: 20px;
            overflow-y: auto;
            border-right: 1px solid #e3e6e8;
            z-index: 999;
        }

        .sidebar.active {
            left: 0;
        }

        .nav-list {
            list-style: none;
            padding: 0;
        }

        .nav-link {
            display: flex;
            align-items: center;
            padding: 10px 15px;
            color: #525960;
            text-decoration: none;
            font-size: 14px;
            transition: all 0.2s;
        }

        .nav-link:hover {
            color: #232629;
            background-color: #f1f2f3;
            border-right: 3px solid #f48024;
        }

        .nav-link i {
            width: 20px;
            text-align: center;
            margin-right: 10px;
        }

        /* Main Container */
        .container {
            max-width: 1264px;
            margin: 53px auto 0;
            padding: 24px 16px;
            display: flex;
            gap: 24px;
            transition: margin-left 0.3s ease;
        }

        body.sidebar-open .container {
            margin-left: 240px;
        }

        .main-content {
            flex: 1;
        }

        .sidebar-right {
            width: 300px;
        }

        /* Question Section */
        .question-box {
            background: white;
            border: 1px solid #d6d9dc;
            border-radius: 6px;
            padding: 24px;
            margin-bottom: 24px;
            display: flex;
            gap: 16px;
        }

        .vote-box {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 8px;
            min-width: 60px;
        }

        .vote-btn {
            width: 36px;
            height: 36px;
            border: 1px solid #c8ccd0;
            background: white;
            border-radius: 3px;
            cursor: pointer;
            font-size: 18px;
            color: #525960;
            transition: all 0.2s;
        }

        .vote-btn:hover {
            background-color: #e3e6e8;
        }

        .vote-btn.voted-up {
            background-color: #fff3cd;
            border-color: #f59e0b;
            color: #f59e0b;
        }

        .vote-btn.voted-down {
            background-color: #fee2e2;
            border-color: #ef4444;
            color: #ef4444;
        }

        .vote-count {
            font-size: 18px;
            font-weight: bold;
            color: #232629;
        }

        .question-content {
            flex: 1;
        }

        .question-title {
            font-size: 26px;
            font-weight: bold;
            color: #232629;
            margin-bottom: 16px;
            line-height: 1.3;
        }

        .question-body {
            font-size: 14px;
            line-height: 1.6;
            color: #3b4045;
            margin-bottom: 16px;
        }

        .code-block {
            background: #f5f5f5;
            border: 1px solid #d6d9dc;
            border-radius: 4px;
            padding: 12px;
            overflow-x: auto;
            margin: 10px 0;
            font-family: Consolas, monospace;
            font-size: 13px;
        }

        .tags-list {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            margin-bottom: 16px;
        }

        .tag-badge {
            background: #e1ecf4;
            border: 1px solid #bcd0e2;
            color: #3b4045;
            padding: 6px 8px;
            border-radius: 3px;
            font-size: 12px;
            text-decoration: none;
            cursor: pointer;
            transition: all 0.2s;
        }

        .tag-badge:hover {
            background-color: #d0e1f7;
        }

        .question-meta {
            padding-top: 16px;
            border-top: 1px solid #e2e3e4;
            font-size: 12px;
            color: #6a737c;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .user-card {
            display: flex;
            align-items: center;
            gap: 12px;
            background: #f8f9fa;
            padding: 12px;
            border-radius: 4px;
            border-left: 3px solid #0a95ff;
            margin-top: 16px;
        }

        .user-avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 16px;
        }

        .user-info {
            display: flex;
            flex-direction: column;
        }

        .user-name {
            font-size: 12px;
            color: #0a95ff;
            font-weight: bold;
            text-decoration: none;
        }

        .user-meta {
            font-size: 11px;
            color: #6a737c;
        }

        /* Answers Section */
        .answers-section {
            margin-top: 32px;
        }

        .section-header {
            font-size: 20px;
            font-weight: bold;
            color: #232629;
            margin-bottom: 16px;
            padding-bottom: 12px;
            border-bottom: 1px solid #d6d9dc;
        }

        .answer-box {
            background: white;
            border: 1px solid #d6d9dc;
            border-radius: 6px;
            padding: 24px;
            margin-bottom: 24px;
            display: flex;
            gap: 16px;
        }

        .answer-content {
            flex: 1;
        }

        .answer-body {
            font-size: 14px;
            line-height: 1.6;
            color: #3b4045;
            margin-bottom: 16px;
        }

        /* Add Answer Form */
        .add-answer-section {
            background: white;
            border: 1px solid #d6d9dc;
            border-radius: 6px;
            padding: 24px;
            margin-top: 32px;
        }

        .form-group {
            margin-bottom: 16px;
        }

        .form-label {
            font-size: 14px;
            font-weight: bold;
            margin-bottom: 8px;
            display: block;
        }

        .form-input {
            width: 100%;
            padding: 10px;
            border: 1px solid #c8ccd0;
            border-radius: 4px;
            font-size: 13px;
            font-family: inherit;
        }

        .form-input:focus {
            border-color: #0a95ff;
            outline: none;
            box-shadow: 0 0 0 3px rgba(10,149,255,0.15);
        }

        textarea.form-input {
            resize: vertical;
            min-height: 120px;
            font-family: Consolas, monospace;
        }

        .btn {
            padding: 10px 16px;
            background: #0a95ff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 500;
            transition: all 0.2s;
        }

        .btn:hover {
            background: #0074cc;
        }

        /* Sidebar Cards */
        .card {
            background: white;
            border: 1px solid #d6d9dc;
            border-radius: 6px;
            padding: 16px;
            margin-bottom: 16px;
        }

        .card-title {
            font-size: 13px;
            font-weight: bold;
            color: #3b4045;
            margin-bottom: 12px;
        }

        .linked-box {
            border-left: 3px solid #0a95ff;
            padding-left: 12px;
        }

        .linked-link {
            display: block;
            font-size: 13px;
            color: #0a95ff;
            text-decoration: none;
            margin-bottom: 8px;
            line-height: 1.4;
        }

        .linked-link:hover {
            color: #0074cc;
        }

        .empty-state {
            text-align: center;
            padding: 40px 20px;
            color: #6a737c;
        }
    </style>
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
            if (question != null) {
        %>

        <!-- Question -->
        <div class="question-box">
            <div class="vote-box">
                <%
                    String questionUserVote = (String) request.getAttribute("questionUserVote");
                    String upvoteClass = "upvote".equals(questionUserVote) ? " voted-up" : "";
                    String downvoteClass = "downvote".equals(questionUserVote) ? " voted-down" : "";
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
                <button class="vote-btn" title="Star">
                    <i class="fa-solid fa-star"></i>
                </button>
            </div>

            <div class="question-content">
                <div class="question-title"><%= question.getTitle() %></div>

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
                    </div>
                    <div>
                        <strong>viewed</strong> <%= question.getViewCount() %> times
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
            </div>
        </div>

        <!-- Answers Section -->
        <div class="answers-section">
            <div class="section-header">Answers</div>

            <%
                java.util.List answers = (java.util.List) request.getAttribute("answers");
                java.util.Map<Long, String> answerVotes = (java.util.Map<Long, String>) request.getAttribute("answerVotes");
                if (answerVotes == null) {
                    answerVotes = new java.util.HashMap<>();
                }
                
                if (answers != null && !answers.isEmpty()) {
                    for (Object answerObj : answers) {
                        dto.AnswerDTO answer = (dto.AnswerDTO) answerObj;
                        String answerUserVote = answerVotes.get(answer.getAnswerId());
                        String answerUpvoteClass = "upvote".equals(answerUserVote) ? " voted-up" : "";
                        String answerDownvoteClass = "downvote".equals(answerUserVote) ? " voted-down" : "";
            %>
            <div class="answer-box">
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
                    <button class="vote-btn" title="Star">
                        <i class="fa-solid fa-star"></i>
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

                    <div class="question-meta">
                        <div>
                            <strong>answered</strong> <%= answer.getCreatedAt() %>
                            <% if (answer.isIsEdited()) { %>
                            <span style="color: #6a737c;"> (edited)</span>
                            <% } %>
                        </div>
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
        </div>

        <!-- Add Answer Section -->
        <div class="add-answer-section">
            <div class="section-header">Your Answer</div>

            <form method="post" action="${pageContext.request.contextPath}/answer/create">
                <input type="hidden" name="questionId" value="<%= question.getQuestionId() %>">

                <div class="form-group">
                    <label class="form-label">Answer</label>
                    <textarea name="answerBody" class="form-input" placeholder="Viết câu trả lời của bạn..." required></textarea>
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

<script>
    function toggleMenu() {
        var sidebar = document.getElementById('sidebar');
        sidebar.classList.toggle('active');
        document.body.classList.toggle('sidebar-open');
    }

    function handleVoteClick(ev, button) {
        if (ev) ev.preventDefault();
        // Extract data from button attributes
        const questionId = button.getAttribute('data-question-id') || null;
        const answerId = button.getAttribute('data-answer-id') || null;
        const voteType = button.getAttribute('data-vote-type');
        
        if (!voteType) {
            alert('Invalid vote type');
            return;
        }
        
        submitVote(questionId, answerId, voteType);
    }

    function submitVote(questionId, answerId, voteType) {
        // Use URLSearchParams (application/x-www-form-urlencoded) so servlet getParameter() works.
        // FormData sends multipart/form-data which requires @MultipartConfig to parse.
        const params = new URLSearchParams();
        if (questionId !== null && questionId !== undefined && questionId !== '' && !isNaN(questionId)) {
            params.append('questionId', questionId);
        }
        if (answerId !== null && answerId !== undefined && answerId !== '' && !isNaN(answerId)) {
            params.append('answerId', answerId);
        }
        if (voteType) {
            params.append('voteType', voteType);
        }

        fetch('${pageContext.request.contextPath}/vote/submit', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: params
        })
        .then(response => response.text().then(text => ({ ok: response.ok, status: response.status, text: text })))
        .then(({ ok, status, text }) => {
            if (status === 401) {
                alert('Vui lòng đăng nhập để vote');
                window.location.href = '${pageContext.request.contextPath}/auth/login';
                return;
            }
            let data;
            try { data = JSON.parse(text); } catch (e) { alert('Lỗi phản hồi từ server'); return; }
            if (data && data.success) {
                const isQuestion = questionId != null && questionId !== '' && !isNaN(questionId);
                let upvoteBtn, downvoteBtn, voteCountEl;
                if (isQuestion) {
                    upvoteBtn = document.getElementById('question-upvote');
                    downvoteBtn = document.getElementById('question-downvote');
                    voteCountEl = upvoteBtn && upvoteBtn.parentElement ? upvoteBtn.parentElement.querySelector('.vote-count') : null;
                } else {
                    const btn = document.querySelector('button[data-answer-id="' + answerId + '"]');
                    if (btn) {
                        const box = btn.closest('.vote-box');
                        upvoteBtn = box ? box.querySelector('.upvote-btn') : null;
                        downvoteBtn = box ? box.querySelector('.downvote-btn') : null;
                        voteCountEl = box ? box.querySelector('.vote-count') : null;
                    }
                }
                if (upvoteBtn && downvoteBtn) {
                    if (voteType === 'upvote') {
                        upvoteBtn.classList.add('voted-up');
                        downvoteBtn.classList.remove('voted-down');
                    } else if (voteType === 'downvote') {
                        downvoteBtn.classList.add('voted-down');
                        upvoteBtn.classList.remove('voted-up');
                    }
                }
                if (voteCountEl) voteCountEl.textContent = data.score;
            } else if (data && data.error) {
                alert('Lỗi: ' + data.error);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Có lỗi xảy ra khi vote');
        });
    }
</script>
</body>
</html>
