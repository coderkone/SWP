-- Insert test user
INSERT INTO Users (username, email, password_hash, role, Reputation, provider)
VALUES ('testuser', 'test@example.com', 'hashedpassword123', 'member', 50, 'local');

DECLARE @UserId BIGINT = SCOPE_IDENTITY();

INSERT INTO Questions (user_id, title, body, code_snippet, view_count, is_closed, created_at, updated_at, Score)
VALUES (@UserId, 'How to learn Java?', 'I am new to programming and want to learn Java. What resources would you recommend?', NULL, 10, 0, GETDATE(), GETDATE(), 5);

DECLARE @QuestionId BIGINT = SCOPE_IDENTITY();

INSERT INTO Answers (question_id, user_id, body, code_snippet, is_edited, is_accepted, created_at, updated_at, Score)
VALUES (@QuestionId, @UserId, 'Start with Oracle Java tutorials and practice with LeetCode problems.', NULL, 0, 0, GETDATE(), GETDATE(), 3);

SELECT 'Sample data inserted successfully' AS Status;
