# Luồng Gọi Hàm (Calling Flow) - Tài Liệu Chi Tiết

## 1. Vote Câu Hỏi (Vote Question)

### Luồng Đơn Giản:
```
UI Form (JavaScript Click)
    ↓
VoteController.doPost("/vote/submit")
    ├─ Check Session & Authentication
    ├─ Get Parameters: questionId, voteType ("upvote"/"downvote")
    ├─ Validate Input
    └─ VoteDAO.addVote(userId, questionId, answerId=null, voteType)
        ├─ Check if user already voted
        ├─ If exists: UPDATE Votes table
        │   └─ VoteDAO.updateQuestionScore(questionId)
        └─ If not exists: INSERT new vote
            └─ VoteDAO.updateQuestionScore(questionId)
                └─ UPDATE Questions.Score = (upvote count - downvote count)

Return JSON Response: {success: true, votes: score}
```

### Chi Tiết Hàm:
- **VoteController.doPost()** - Xử lý request HTTP POST từ client
- **VoteDAO.addVote()** - Kiểm tra và thêm/cập nhật vote vào DB
- **VoteDAO.updateQuestionScore()** - Tính toán lại điểm vote của câu hỏi

---

## 2. Trả Lời Câu Hỏi (Answer Question)

### Luồng Đơn Giản:
```
UI Form (Submit Button)
    ↓
AnswerController.doPost("/answer/create")
    ├─ Check Session & Authentication
    ├─ Get Parameters: questionId, answerBody
    ├─ Validate Input
    └─ AnswerDAO.createAnswer(questionId, userId, answerBody, codeSnippet)
        └─ INSERT INTO Answers table
            └─ Return answerId (tự sinh từ DB)

Return: Redirect to question detail page with success message
```

### Chi Tiết Hàm:
- **AnswerController.doPost()** - Xử lý request tạo câu trả lời
- **AnswerDAO.createAnswer()** - Thêm câu trả lời vào DB
  - Fields: question_id, user_id, body, code_snippet, is_edited=0, is_accepted=0, Score=0

---

## 3. Vote Câu Trả Lời (Vote Answer)

### Luồng Đơn Giản:
```
UI Form (JavaScript Click)
    ↓
VoteController.doPost("/vote/submit")
    ├─ Check Session & Authentication
    ├─ Get Parameters: answerId, voteType ("upvote"/"downvote")
    ├─ Validate Input
    └─ VoteDAO.addVote(userId, questionId=null, answerId, voteType)
        ├─ Check if user already voted
        ├─ If exists: UPDATE Votes table
        │   └─ VoteDAO.updateAnswerScore(answerId)
        └─ If not exists: INSERT new vote
            └─ VoteDAO.updateAnswerScore(answerId)
                └─ UPDATE Answers.Score = (upvote count - downvote count)

Return JSON Response: {success: true, votes: score}
```

### Chi Tiết Hàm:
- **VoteController.doPost()** - Xử lý request HTTP POST từ client
- **VoteDAO.addVote()** - Kiểm tra và thêm/cập nhật vote vào DB
- **VoteDAO.updateAnswerScore()** - Tính toán lại điểm vote của câu trả lời

---

## 4. Chấp Nhận / Bỏ Chấp Nhận Câu Trả Lời (Accept/Unaccept Answer)

### Luồng Đơn Giản:
```
UI Form (Accept Button - chỉ chủ sở hữu câu hỏi)
    ↓
AcceptAnswerController.doPost("/answer/accept")
    ├─ Check Session & Authentication
    ├─ Get Parameters: questionId, answerId
    ├─ Validate: User là chủ sở hữu câu hỏi
    ├─ QuestionDAO.getQuestionById(questionId)
    │   └─ Kiểm tra question.userId == user.userId
    ├─ AnswerDAO.getAnswerById(answerId)
    │   └─ Kiểm tra answer có thuộc question này không
    └─ QuestionDAO.toggleAcceptAnswer(questionId, answerId, userId)
        ├─ Get Question: getQuestionById(questionId)
        ├─ Check current accepted_answer_id
        └─ If current exists & == answerId: unaccept (set to null)
           Else: accept this answer (set accepted_answer_id = answerId)
            └─ QuestionDAO.setAcceptedAnswer(questionId, answerId)
                └─ UPDATE Questions.accepted_answer_id = ?

Return JSON Response: {success: true, accepted: true/false}
```

### Chi Tiết Hàm:
- **AcceptAnswerController.doPost()** - Xử lý request chấp nhận/từ chối câu trả lời
- **QuestionDAO.getQuestionById()** - Lấy thông tin câu hỏi
- **AnswerDAO.getAnswerById()** - Lấy thông tin câu trả lời
- **QuestionDAO.toggleAcceptAnswer()** - Toggle trạng thái chấp nhận (Accept/Unaccept)
- **QuestionDAO.setAcceptedAnswer()** - Cập nhật accepted_answer_id trong DB

---

## 5. Bình Luận (Comment)

### Luồng Đơn Giản:
```
UI Form (Submit Comment Button)
    ↓
CommentController.doPost("/comment/add")
    ├─ Check Session & Authentication (user != null)
    ├─ Get Parameters: answerId hoặc questionId, commentBody
    ├─ Validate Input:
    │   ├─ commentBody không rỗng (1-1000 characters)
    │   ├─ IDs hợp lệ
    │   └─ Answer/Question tồn tại
    │
    ├─ If answerId exists:
    │   ├─ CommentDAO.answerExists(answerId)
    │   └─ CommentDAO.insertAnswerComment(userId, answerId, commentBody)
    │       └─ INSERT INTO Comments (user_id, answer_id, body, created_at)
    │
    └─ If questionId exists:
        ├─ CommentDAO.questionExists(questionId)
        └─ CommentDAO.insertQuestionComment(userId, questionId, commentBody)
            └─ INSERT INTO Comments (user_id, question_id, body, created_at)

Return: Redirect back to question detail page with anchor to answer/comment
```

### Chi Tiết Hàm:
- **CommentController.doPost()** - Xử lý request thêm bình luận
- **CommentDAO.insertAnswerComment()** - Thêm bình luận cho câu trả lời
- **CommentDAO.insertQuestionComment()** - Thêm bình luận cho câu hỏi
- **CommentDAO.answerExists()** - Kiểm tra câu trả lời có tồn tại
- **CommentDAO.questionExists()** - Kiểm tra câu hỏi có tồn tại

---

## Sơ Đồ Tương Tác (Interaction Flow)

```
┌─────────────────────────────────────────────────────────────────┐
│                         WEB BROWSER                              │
│  (Vote / Answer / Accept / Comment Form)                        │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                  POST Request (JSON/Form)
                           │
        ┌──────────────────┼──────────────────┬──────────┐
        │                  │                  │          │
    Vote URL         Answer URL        Accept URL   Comment URL
    /vote/submit     /answer/create    /answer/accept /comment/add
        │                  │                  │          │
        ↓                  ↓                  ↓          ↓
┌──────────────┐  ┌──────────────┐  ┌─────────────┐  ┌──────────────┐
│VoteController│  │AnswerCtrl    │  │AcceptAnswer │  │CommentCtrl   │
│              │  │              │  │             │  │              │
│• Auth Check │  │• Auth Check  │  │• Auth Check │  │• Auth Check  │
│• Validate   │  │• Validate    │  │• Validate   │  │• Validate    │
│• Get Params │  │• Get Params  │  │• Get Params │  │• Get Params  │
└──────┬───────┘  └──────┬───────┘  └──────┬──────┘  └──────┬───────┘
       │                 │                 │               │
       ↓                 ↓                 ↓               ↓
┌───────────────────────────────────────────────────────────────────┐
│                           DAO Layer                               │
│                   (Database Access Objects)                       │
│                                                                   │
│  VoteDAO          AnswerDAO      QuestionDAO      CommentDAO     │
│  • addVote()      • createAnswer()  • getQuestionById()           │
│  • getVoteScore() • getAnswerById() • toggleAcceptAnswer()        │
│  • updateScore()                    • setAcceptedAnswer()        │
└──────────────────────┬──────────────────────────────────────────┘
                       │
                       ↓
           ┌──────────────────────┐
           │    SQL Database      │
           │   (SQL Server/MSSQL) │
           │                      │
           │  Votes               │
           │  Answers             │
           │  Questions           │
           │  Comments            │
           └──────────────────────┘
```

---

## Bảng Tóm Tắt

| Tính Năng | Controller | DAO Method | DB Tables | Response |
|-----------|-----------|-----------|----------|----------|
| Vote Q/A | VoteController | VoteDAO.addVote() | Votes, Questions/Answers | JSON (success, score) |
| Answer | AnswerController | AnswerDAO.createAnswer() | Answers | Redirect + Message |
| Accept Answer | AcceptAnswerController | QuestionDAO.toggleAcceptAnswer() | Questions | JSON (success, accepted) |
| Comment | CommentController | CommentDAO.insert*Comment() | Comments | Redirect + Anchor |

---

## Quy Trình Chi Tiết Từng Bước

### Vote Câu Hỏi / Câu Trả Lời:
1. User click Vote button (UI)
2. JavaScript gửi POST request đến `/vote/submit` với `{questionId/answerId, voteType}`
3. VoteController xác thực user từ session
4. Validate parameters (IDs hợp lệ, voteType là "upvote" hoặc "downvote")
5. VoteDAO.addVote() kiểm tra nếu user đã vote:
   - Nếu đã vote: UPDATE vote_type cũ thành mới
   - Nếu chưa vote: INSERT row mới
6. Gọi updateQuestionScore() hoặc updateAnswerScore()
   - Tính: COUNT(upvote) - COUNT(downvote)
   - UPDATE Questions/Answers table
7. Trả về JSON response với điểm số mới

### Trả Lời Câu Hỏi:
1. User điền nội dung và click "Post Answer"
2. Form submit POST đến `/answer/create`
3. AnswerController xác thực user
4. Validate: questionId hợp lệ, answerBody không rỗng
5. AnswerDAO.createAnswer() INSERT row mới vào Answers table
6. DB trả về answerId (auto-generated)
7. Redirect về question detail page

### Chấp Nhận Câu Trả Lời:
1. Chủ sở hữu câu hỏi click accept button
2. POST request đến `/answer/accept` với `{questionId, answerId}`
3. AcceptAnswerController xác thực user
4. Verify: User là chủ sở hữu câu hỏi
5. Verify: Answer thuộc Question này
6. QuestionDAO.toggleAcceptAnswer():
   - Lấy current accepted_answer_id
   - Nếu đã = answerId: set thành NULL (unaccept)
   - Nếu khác: set thành answerId (accept)
7. Trả JSON: `{success: true, accepted: true/false}`

### Bình Luận:
1. User điền bình luận, click "Add Comment"
2. Form submit POST đến `/comment/add`
3. CommentController xác thực user
4. Validate: commentBody (1-1000 chars), IDs hợp lệ
5. Kiểm tra answer/question tồn tại
6. CommentDAO.insertAnswerComment() hoặc insertQuestionComment()
   - INSERT vào Comments table
7. Redirect về question detail page
8. Page tải lại và hiện bình luận mới
