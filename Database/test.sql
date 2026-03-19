USE [devquery]
GO

-- ========================================================
-- BƠM DỮ LIỆU ẢO CHO TÀI KHOẢN CỦA BẠN (USER_ID = 18)
-- ========================================================

-- 1. Cập nhật điểm uy tín (Reputation) cho ngầu (Ví dụ: 1550 điểm)
UPDATE [dbo].[Users] 
SET [Reputation] = 1550 
WHERE [user_id] = 18;

-- 2. Bơm 2 Câu hỏi do chính bạn (ID = 18) đặt ra
-- Câu 1 có 1250 view, Câu 2 có 340 view -> Tổng Reached sẽ là 1590
INSERT INTO [dbo].[Questions] ([user_id], [title], [body], [view_count], [Score]) 
VALUES 
(18, N'Làm sao để code trang Profile chuẩn MVC?', N'Mình đang làm UI cho trang Profile của DevQuery mà chưa biết thiết kế DAO sao cho chuẩn.', 1250, 15),
(18, N'Lỗi gạch đỏ chữ Connection trong Java', N'Mọi người cho mình hỏi fix lỗi này như thế nào với?', 340, 5);

-- 3. Bơm 3 Câu trả lời do bạn (ID = 18) đi giải đáp cho người khác
-- Giả sử bạn vào trả lời cho các câu hỏi có question_id = 1, 2, 3 đang có sẵn trong DB
INSERT INTO [dbo].[Answers] ([question_id], [user_id], [body], [is_accepted], [Score]) 
VALUES 
(1, 18, N'Lỗi NullPointerException này là do biến chưa được khởi tạo. Bạn check lại kỹ nhé.', 1, 10),
(2, 18, N'Bạn thử dùng JDBC chuẩn bằng hàm getConnection() xem sao.', 0, 2),
(3, 18, N'Lỗi vô hạn loop này thường do quên truyền dependency array vào useEffect trong React.', 1, 25);

-- 4. Cấp phát 5 Danh hiệu (Badges) cho bạn
-- Theo record cũ: ID 3 là Vàng, ID 2 & 4 là Bạc, ID 1 & 5 là Đồng
INSERT INTO [dbo].[User_Badges] ([user_id], [badge_id]) 
VALUES 
(18, 3), -- Tặng 1 huy hiệu Vàng (Famous Question)
(18, 2), -- Tặng 1 huy hiệu Bạc (Good Answer)
(18, 4), -- Tặng 1 huy hiệu Bạc (Bug Hunter)
(18, 1), -- Tặng 1 huy hiệu Đồng (First Question)
(18, 5); -- Tặng 1 huy hiệu Đồng (Helper)

<<<<<<< HEAD
=======
GO

INSERT INTO [dbo].[Questions] ([user_id], [title], [body], [created_at], [Score]) 
VALUES (18, N'Java OOP là gì?', N'Nội dung test', '2025-11-10 10:00:00', 20);
DECLARE @Q1 BIGINT = SCOPE_IDENTITY();

INSERT INTO [dbo].[Questions] ([user_id], [title], [body], [created_at], [Score]) 
VALUES (18, N'Hỏi về React Hook', N'Nội dung test', '2026-01-05 09:00:00', 30);
DECLARE @Q2 BIGINT = SCOPE_IDENTITY();

INSERT INTO [dbo].[Answers] ([question_id], [user_id], [body], [created_at], [Score]) 
VALUES (1, 18, N'Test Answer tháng 12', '2025-12-15 14:00:00', 15);

INSERT INTO [dbo].[Answers] ([question_id], [user_id], [body], [created_at], [Score]) 
VALUES (2, 18, N'Test Answer tháng 2', '2026-02-20 16:00:00', 5);

-- B. Gắn Tags cho các câu hỏi của User 18 để test Biểu đồ 2 (Cột ngang: Tags)
-- 1. Gắn tag cho 2 câu hỏi quá khứ vừa tạo ở trên
INSERT INTO [dbo].[Question_Tags] ([question_id], [tag_id]) VALUES 
(@Q1, 1), -- 1 là id của tag 'java' (Điểm +20)
(@Q2, 4), -- 4 là id của tag 'reactjs' (Điểm +30)
(@Q2, 5); -- 5 là id của tag 'javascript' (Điểm +30)

-- 2. Gắn thêm tag cho 2 câu hỏi cũ bạn đã tạo ở phần trên của file test.sql
-- Tìm ID của các câu hỏi cũ thông qua Title và gắn tag tương ứng
INSERT INTO [dbo].[Question_Tags] ([question_id], [tag_id])
SELECT question_id, 1 FROM [dbo].[Questions] WHERE user_id = 18 AND title LIKE N'%Java%';

INSERT INTO [dbo].[Question_Tags] ([question_id], [tag_id])
SELECT question_id, 2 FROM [dbo].[Questions] WHERE user_id = 18 AND title LIKE N'%MVC%'; -- tag 2 là spring-boot
>>>>>>> sub
GO