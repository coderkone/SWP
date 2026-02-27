USE [devquery]
GO

-- =============================================
-- 1. TẠO DỮ LIỆU USERS (~15 users)
-- Password: "123456789" (SHA256 hash)
-- =============================================
INSERT INTO [dbo].[Users] ([username], [email], [password_hash], [role], [Reputation]) VALUES
('admin_master', 'admin@devquery.com', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'admin', 1000),
('hoang_coder', 'hoang@gmail.com', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'member', 150),
('thuy_java', 'thuy.nguyen@fpt.vn', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'moderator', 500),
('tuan_sql', 'tuanit@yahoo.com', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'member', 45),
('david_lee', 'david.lee@us.com', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'member', 10),
('alice_wonder', 'alice@gmail.com', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'member', 230),
('bob_builder', 'bob.code@outlook.com', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'member', 5),
('charlie_react', 'charlie@fe.com', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'member', 80),
('nam_spring', 'nam.java@viettel.com', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'member', 300),
('lan_tester', 'lan.qa@cmc.vn', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'member', 20),
('hung_mobile', 'hung.android@samsung.com', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'member', 110),
('mai_design', 'mai.uiux@vng.com.vn', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'member', 60),
('newbie_coder', 'student1@fpt.edu.vn', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'member', 0),
('pro_backend', 'senior@nashtech.com', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'member', 890),
('bot_auto', 'bot@devquery.system', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c979c0fde7dae', 'member', 0);
GO
select * from Users
-- =============================================
-- 2. TẠO USER PROFILE (15 profiles)
-- =============================================
INSERT INTO [dbo].[User_Profile] ([user_id], [location], [bio], [website]) VALUES
(1, N'Hà Nội', N'Administrator of System', 'https://devquery.com'),
(2, N'TP.HCM', N'Fullstack Developer yêu thích Spring Boot', 'https://hoangcoder.me'),
(3, N'Đà Nẵng', N'Senior Java Developer', NULL),
(4, N'Hà Nội', N'Data Analyst & SQL Expert', NULL),
(5, N'New York', N'Freelancer', 'https://davidcode.io'),
(6, N'California', N'ReactJS Enthusiast', NULL),
(7, N'London', N'Learning to code', NULL),
(8, N'Singapore', N'Frontend Ninja', 'https://charlie.js'),
(9, N'Hà Nội', N'Backend Architect', NULL),
(10, N'TP.HCM', N'Automation Tester', NULL),
(11, N'Bắc Ninh', N'Android & Kotlin', NULL),
(12, N'Hà Nội', N'Thích màu hồng và code HTML', NULL),
(13, N'Cần Thơ', N'Sinh viên năm 2', NULL),
(14, N'Remote', N'10 năm kinh nghiệm System Design', 'https://architect.io'),
(15, N'Server', N'I am a robot', NULL);
GO

-- =============================================
-- 3. TẠO TAGS (~12 tags)
-- =============================================
INSERT INTO [dbo].[Tags] ([tag_name], [description]) VALUES
('java', N'Ngôn ngữ lập trình hướng đối tượng phổ biến.'),
('spring-boot', N'Framework Java giúp xây dựng ứng dụng nhanh chóng.'),
('sql-server', N'Hệ quản trị cơ sở dữ liệu của Microsoft.'),
('reactjs', N'Thư viện JavaScript để xây dựng giao diện người dùng.'),
('javascript', N'Ngôn ngữ kịch bản cho Web.'),
('html-css', N'Cấu trúc và định kiểu cho trang web.'),
('python', N'Ngôn ngữ lập trình đa năng.'),
('docker', N'Nền tảng container hóa ứng dụng.'),
('android', N'Hệ điều hành di động của Google.'),
('git', N'Hệ thống quản lý phiên bản phân tán.'),
('algorithm', N'Các thuật toán và cấu trúc dữ liệu.'),
('c#', N'Ngôn ngữ lập trình của Microsoft.');
GO

-- =============================================
-- 4. TẠO BADGES (5 badges)
-- =============================================
INSERT INTO [dbo].[Badges] ([name], [type], [description]) VALUES
('First Question', 'bronze', N'Đặt câu hỏi đầu tiên.'),
('Good Answer', 'silver', N'Câu trả lời đạt 10 vote.'),
('Famous Question', 'gold', N'Câu hỏi có 1000 lượt xem.'),
('Bug Hunter', 'silver', N'Tìm ra lỗi bảo mật.'),
('Helper', 'bronze', N'Trả lời 5 câu hỏi.');
GO

-- =============================================
-- 5. TẠO CÂU HỎI - QUESTIONS (~20 câu hỏi)
-- =============================================
INSERT INTO [dbo].[Questions] ([user_id], [title], [body], [code_snippet], [view_count], [Score]) VALUES
(13, N'Lỗi NullPointerException trong Java là gì?', N'Mình mới học Java và hay gặp lỗi này khi chạy chương trình. Ai giải thích giúp mình với?', N'String s = null; System.out.println(s.length());', 105, 5),
(2, N'Làm sao để kết nối SQL Server với Spring Boot?', N'Mình cấu hình file application.properties nhưng vẫn báo lỗi connection refused.', N'spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=test', 340, 12),
(6, N'React useEffect chạy vô hạn loop', N'Tại sao component của mình bị render lại liên tục?', N'useEffect(() => { setCount(count + 1); });', 560, 8),
(12, N'Canh giữa div trong CSS', N'Cách nào nhanh nhất để căn giữa 1 thẻ div trong thẻ cha?', N'.parent { ? }', 1200, 25),
(4, N'Sự khác nhau giữa INNER JOIN và LEFT JOIN', N'Mình vẫn chưa phân biệt rõ 2 cái này khi query dữ liệu.', NULL, 89, 3),
(7, N'Hỏi về lộ trình học Frontend năm 2026', N'Nên học React hay Vue hay Angular ạ?', NULL, 450, 10),
(11, N'Android Studio bị chậm trên máy RAM 8GB', N'Có cách nào tối ưu IDE không mọi người?', NULL, 210, 2),
(9, N'Microservices pattern saga là gì?', N'Mình đang tìm hiểu về distributed transaction.', NULL, 67, 15),
(2, N'Cách fix lỗi CORS khi gọi API', N'Browser chặn request từ frontend gọi sang backend khác port.', NULL, 800, 20),
(13, N'Biến static trong Java dùng để làm gì?', N'Tại sao hàm main phải là static?', NULL, 90, 1),
(5, N'Python list comprehension example', N'How to filter even numbers?', N'[x for x in range(10)]', 150, 6),
(3, N'Interface vs Abstract Class', N'Khi nào nên dùng cái nào trong thiết kế hệ thống?', NULL, 300, 18),
(10, N'Selenium không tìm thấy element', N'Mình dùng xpath nhưng lúc chạy được lúc không.', NULL, 120, 4),
(14, N'Tối ưu câu truy vấn SQL 1 triệu bản ghi', N'Dùng index như thế nào cho hiệu quả?', N'SELECT * FROM LargeTable WHERE status = 1', 900, 30),
(8, N'Redux Toolkit vs Context API', N'Dự án nhỏ thì nên dùng cái nào?', NULL, 400, 9),
(13, N'Làm sao push code lên Github?', N'Mình bị lỗi conflict khi pull về.', NULL, 50, 0),
(2, N'Deploy Spring Boot lên Docker', N'Xin file Dockerfile mẫu ạ.', NULL, 230, 7),
(4, N'Stored Procedure trong SQL', N'Có nên dùng logic trong DB không?', NULL, 180, 5),
(6, N'NextJS 14 Server Actions', N'Tính năng này có thay thế được API Route không?', NULL, 350, 11),
(9, N'Kafka vs RabbitMQ', N'So sánh ưu nhược điểm của 2 message queue này.', NULL, 500, 22);
GO

-- =============================================
-- 6. GẮN TAG CHO CÂU HỎI - QUESTION_TAGS (~30 bản ghi)
-- =============================================
INSERT INTO [dbo].[Question_Tags] ([question_id], [tag_id]) VALUES
(1, 1), (2, 2), (2, 3), (3, 4), (3, 5),
(4, 6), (5, 3), (6, 4), (6, 5), (6, 6),
(7, 9), (7, 1), (8, 1), (8, 2), (9, 4), (9, 2),
(10, 1), (11, 7), (12, 1), (13, 1),
(14, 3), (15, 4), (16, 10), (17, 2), (17, 8),
(18, 3), (19, 4), (19, 5), (20, 1);
GO

-- =============================================
-- 7. TẠO CÂU TRẢ LỜI - ANSWERS (~30 câu trả lời)
-- =============================================
INSERT INTO [dbo].[Answers] ([question_id], [user_id], [body], [code_snippet], [is_accepted], [Score]) VALUES
(1, 3, N'Bạn đang gọi phương thức trên một đối tượng null. Hãy kiểm tra s != null trước.', N'if (s != null) { ... }', 1, 10),
(1, 14, N'Đây là lỗi phổ biến, bạn nên dùng Optional trong Java 8 trở lên.', NULL, 0, 2),
(2, 9, N'Bạn kiểm tra xem SQL Server đã bật TCP/IP trong Configuration Manager chưa nhé.', NULL, 1, 15),
(3, 8, N'Bạn cần thêm dependency array vào useEffect.', N'useEffect(() => { ... }, []); // Thêm mảng rỗng', 1, 20),
(4, 12, N'Dùng Flexbox là nhanh nhất.', N'display: flex; justify-content: center; align-items: center;', 1, 50),
(4, 7, N'Dùng margin: 0 auto; nếu có width.', NULL, 0, 5),
(5, 4, N'INNER JOIN chỉ lấy phần chung, LEFT JOIN lấy hết bảng bên trái.', NULL, 1, 8),
(9, 14, N'Cấu hình @CrossOrigin ở Controller Spring Boot hoặc cài Proxy ở React.', NULL, 1, 12),
(14, 1, N'Đánh Index vào cột status, và tránh Select *.', NULL, 1, 25),
(16, 2, N'Dùng git stash để lưu code tạm, rồi pull, sau đó git stash pop.', NULL, 0, 3),
(16, 3, N'Giải quyết conflict bằng tay trong VS Code rồi commit lại.', NULL, 1, 4),
(10, 3, N'Static thuộc về lớp chứ không thuộc về đối tượng (instance).', NULL, 1, 6),
(12, 14, N'Interface cho hành động (Can do), Abstract cho bản chất (Is a).', NULL, 1, 15),
(7, 11, N'Nâng RAM lên 16GB đi bạn, 8GB không đủ đâu :D', NULL, 0, 10),
(17, 9, N'Tạo file Dockerfile như sau:', N'FROM openjdk:17-jdk-alpine\nCOPY target/*.jar app.jar\nENTRYPOINT ["java","-jar","/app.jar"]', 1, 9),
(6, 8, N'React vẫn đang là vua, job nhiều, nên học React trước.', NULL, 0, 12),
(8, 9, N'Saga pattern dùng để quản lý transaction qua nhiều service bằng chuỗi các local transaction.', NULL, 1, 7),
(20, 14, N'Kafka cho throughput cao (streaming), RabbitMQ cho complex routing.', NULL, 1, 11),
(19, 6, N'Server Actions rất mạnh, nhưng API Route vẫn cần cho Webhook hoặc Mobile app gọi vào.', NULL, 0, 5),
(11, 2, N'Dùng list comprehension nhanh hơn vòng lặp for thông thường.', NULL, 1, 3);
GO

-- =============================================
-- 8. TẠO COMMENTS (~25 comments)
-- =============================================
INSERT INTO [dbo].[Comments] ([user_id], [question_id], [answer_id], [body]) VALUES
(13, 1, 1, N'Cảm ơn bạn, mình sửa được rồi!'),
(2, 2, NULL, N'Bạn chụp ảnh lỗi lên được không?'),
(3, 2, 3, N'Chuẩn luôn, mình hay quên bật cái TCP/IP này.'),
(12, 4, 5, N'Cách này giờ là standard rồi.'),
(7, 4, NULL, N'CSS khó quá :('),
(5, 7, NULL, N'Mua máy mới là giải pháp tốt nhất :))'),
(13, 16, NULL, N'Mình mới học Git, sợ mất code quá.'),
(14, 14, 9, N'Select * hại performance lắm.'),
(8, 6, NULL, N'Học chắc JS trước khi học Framework nhé.'),
(11, 7, 14, N'Đúng là Android Studio ngốn RAM kinh khủng.'),
(1, 1, NULL, N'Nhớ format code khi đăng bài nhé bạn.'),
(9, 20, 18, N'Bài so sánh rất chi tiết.'),
(6, 3, 4, N'Quên cái dependency array là treo trình duyệt luôn.'),
(10, 13, NULL, N'Mình dùng Java + Selenium cũng hay bị.'),
(4, 5, 7, N'Giải thích ngắn gọn dễ hiểu.'),
(13, 10, NULL, N'Thầy mình bắt dùng mà chưa hiểu lắm.'),
(2, 17, NULL, N'Docker hay nhưng hơi khó cấu hình ban đầu.'),
(3, 12, 13, N'Vote +1 cho câu trả lời chất lượng.'),
(14, 9, NULL, N'CORS là nỗi đau của mọi dev frontend.'),
(5, 11, 20, N'Python cú pháp gọn thật.');
GO

-- =============================================
-- 9. TẠO VOTES (~30 votes)
-- =============================================
-- Giả lập vote cho câu hỏi và câu trả lời
INSERT INTO [dbo].[Votes] ([user_id], [question_id], [answer_id], [vote_type]) VALUES
(2, 1, NULL, 'up'), (3, 1, NULL, 'up'), (4, 1, 1, 'up'), (5, 1, 2, 'down'),
(6, 2, NULL, 'up'), (7, 2, 3, 'up'), (1, 2, 3, 'up'),
(8, 3, NULL, 'up'), (9, 3, 4, 'up'), (10, 3, 4, 'up'),
(11, 4, NULL, 'up'), (14, 4, 5, 'up'), (1, 4, 5, 'up'), (2, 4, 5, 'up'),
(3, 14, NULL, 'up'), (4, 14, 9, 'up'), (5, 14, 9, 'up'),
(6, 16, NULL, 'down'), (7, 16, 11, 'up'),
(8, 17, NULL, 'up'), (9, 17, 15, 'up'),
(10, 20, NULL, 'up'), (11, 20, 18, 'up'),
(12, 12, NULL, 'up'), (13, 12, 13, 'up'),
(14, 9, NULL, 'up'), (1, 9, 8, 'up'),
(2, 6, NULL, 'up'), (3, 6, 16, 'up'),
(4, 7, NULL, 'down'), (5, 7, 14, 'up');
GO

-- =============================================
-- 10. TẠO BỘ SƯU TẬP & BOOKMARKS
-- =============================================
INSERT INTO [dbo].[Collections] ([user_id], [Name]) VALUES
(2, N'Lỗi Java thường gặp'),
(6, N'React Best Practices'),
(14, N'System Design Interview');

INSERT INTO [dbo].[Bookmarks] ([user_id], [question_id], [collection_id]) VALUES
(2, 1, 1),
(6, 3, 2),
(14, 20, 3),
(14, 8, 3),
(9, 14, NULL),
(13, 16, NULL),
(3, 2, NULL),
(8, 6, NULL);
GO

-- =============================================
-- 11. CẤP BADGE CHO USER (USER_BADGES)
-- =============================================
INSERT INTO [dbo].[User_Badges] ([user_id], [badge_id]) VALUES
(13, 1), -- Newbie gets First Question
(12, 2), -- Mai gets Good Answer (CSS question answer)
(14, 2),
(4, 1),
(14, 5), -- Pro gets Helper
(3, 5);
GO

-- =============================================
-- 12. SYSTEM RULES & NOTIFICATIONS
-- =============================================
INSERT INTO [dbo].[System_Rules] ([title], [content], [created_by]) VALUES
(N'Quy định đặt câu hỏi', N'Câu hỏi phải rõ ràng, có code minh họa, không spam.', 1),
(N'Văn hóa ứng xử', N'Tôn trọng người khác, không chửi bới, toxic.', 1);

INSERT INTO [dbo].[Notifications] ([user_id], [type], [content], [is_read]) VALUES
(13, 'answer', N'Thuy_java đã trả lời câu hỏi của bạn.', 0),
(2, 'comment', N'Có bình luận mới trong bài viết SQL của bạn.', 1),
(12, 'badge', N'Chúc mừng! Bạn nhận được huy hiệu Good Answer.', 0),
(14, 'system', N'Bảo trì hệ thống vào 12h đêm nay.', 0);
GO
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
