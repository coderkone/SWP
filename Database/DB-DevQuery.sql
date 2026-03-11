USE [master]
GO

CREATE DATABASE [devquery]
GO

USE [devquery]
GO

-- 1. USERS (Giữ nguyên, bỏ check role)
CREATE TABLE [dbo].[Users](
	[user_id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[username] [nvarchar](50) NOT NULL UNIQUE,
	[email] [varchar](120) NOT NULL UNIQUE,
	[password_hash] [varchar](255) NOT NULL,
	[role] [varchar](20) DEFAULT 'member', -- Xử lý validate trong Java
	[created_at] [datetime] DEFAULT GETDATE(),
	[updated_at] [datetime] DEFAULT GETDATE(),
	[Reputation] [int] DEFAULT 0,
	[provider] [varchar](20) DEFAULT 'local',
	[provider_id] [varchar](150) NULL
)
GO

-- 2. USER_PROFILE
CREATE TABLE [dbo].[User_Profile](
	[profile_id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[user_id] [bigint] NOT NULL UNIQUE,
	[bio] [nvarchar](max) NULL,
	[avatar_url] [varchar](255) NULL,
	[location] [nvarchar](100) NULL,
	[website] [varchar](255) NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
)
GO

-- 3. QUESTIONS (Đã xóa accepted_answer_id để tránh vòng lặp)
CREATE TABLE [dbo].[Questions](
	[question_id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[user_id] [bigint] NOT NULL,
	[title] [nvarchar](255) NOT NULL,
	[body] [nvarchar](max) NOT NULL,
	[code_snippet] [nvarchar](max) NULL,
	-- [accepted_answer_id] ĐÃ XÓA
	[view_count] [int] DEFAULT 0,
	[is_closed] [bit] DEFAULT 0,
	[closed_reason] [nvarchar](255) NULL,
	[created_at] [datetime] DEFAULT GETDATE(),
	[updated_at] [datetime] DEFAULT GETDATE(),
	[Score] [int] DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
)
GO

-- 4. ANSWERS (Thêm is_accepted)
CREATE TABLE [dbo].[Answers](
	[answer_id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[question_id] [bigint] NOT NULL,
	[user_id] [bigint] NOT NULL,
	[body] [nvarchar](max) NOT NULL,
	[code_snippet] [nvarchar](max) NULL,
	[is_edited] [bit] DEFAULT 0,
    [is_accepted] [bit] DEFAULT 0, -- THÊM DÒNG NÀY
	[created_at] [datetime] DEFAULT GETDATE(),
	[updated_at] [datetime] DEFAULT GETDATE(),
	[Score] [int] DEFAULT 0,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
)
GO

-- 5. COMMENTS (Giữ nguyên nhưng bỏ CHECK logic phức tạp)
CREATE TABLE [dbo].[Comments](
	[comment_id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[user_id] [bigint] NOT NULL,
	[question_id] [bigint] NULL,
	[answer_id] [bigint] NULL,
	[body] [nvarchar](max) NOT NULL,
	[created_at] [datetime] DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE,
    FOREIGN KEY (answer_id) REFERENCES Answers(answer_id)
)
GO

-- 6. VOTES (Bỏ CHECK vote_type)
CREATE TABLE [dbo].[Votes](
	[vote_id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[user_id] [bigint] NOT NULL,
	[question_id] [bigint] NULL,
	[answer_id] [bigint] NULL,
	[vote_type] [varchar](10) NOT NULL, -- Java tự check "up" hoặc "down"
	[created_at] [datetime] DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE,
    FOREIGN KEY (answer_id) REFERENCES Answers(answer_id)
)
GO

-- 7. TAGS
CREATE TABLE [dbo].[Tags](
	[tag_id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[tag_name] [varchar](50) NOT NULL UNIQUE,
	[description] [nvarchar](max) NULL,
	[IsActive] [bit] DEFAULT 1
)
GO

-- 8. QUESTION_TAGS
CREATE TABLE [dbo].[Question_Tags](
	[question_id] [bigint] NOT NULL,
	[tag_id] [bigint] NOT NULL,
    PRIMARY KEY (question_id, tag_id),
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES Tags(tag_id) ON DELETE CASCADE
)
GO

-- COLLECTIONS
CREATE TABLE [dbo].[Collections](
	[collection_id] [int] IDENTITY(1,1) PRIMARY KEY,
	[user_id] [bigint] NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[CreatedAt] [datetime2](7) DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
)
GO

-- BOOKMARKS
CREATE TABLE [dbo].[Bookmarks](
	[user_id] [bigint] NOT NULL,
	[question_id] [bigint] NOT NULL,
	[created_at] [datetime] DEFAULT GETDATE(),
	[collection_id] [int] NULL,
    PRIMARY KEY (user_id, question_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE,
    FOREIGN KEY (collection_id) REFERENCES Collections(collection_id)
)
GO

-- BADGES
CREATE TABLE [dbo].[Badges](
	[badge_id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[name] [nvarchar](50) NOT NULL UNIQUE,
	[type] [varchar](10) NOT NULL, -- Java check: gold/silver/bronze
	[description] [nvarchar](max) NULL
)
GO

-- USER_BADGES
CREATE TABLE [dbo].[User_Badges](
	[user_id] [bigint] NOT NULL,
	[badge_id] [bigint] NOT NULL,
	[created_at] [datetime] DEFAULT GETDATE(),
    PRIMARY KEY (user_id, badge_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (badge_id) REFERENCES Badges(badge_id) ON DELETE CASCADE
)
GO

-- QUESTION_VIEWS
CREATE TABLE [dbo].[Question_Views](
	[id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[question_id] [bigint] NOT NULL,
	[viewer_ip] [varchar](50) NULL,
	[viewed_at] [datetime] DEFAULT GETDATE(),
	[user_id] [bigint] NULL,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
)
GO

-- REPUTATION_HISTORY
CREATE TABLE [dbo].[Reputation_History](
	[rep_id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[user_id] [bigint] NOT NULL,
	[action_type] [varchar](30) NOT NULL,
	[value] [int] NOT NULL,
	[created_at] [datetime] DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
)
GO

-- SYSTEM_RULES
CREATE TABLE [dbo].[System_Rules](
	[rule_id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[title] [nvarchar](255) NOT NULL,
	[content] [nvarchar](max) NOT NULL,
	[created_at] [datetime] DEFAULT GETDATE(),
	[updated_at] [datetime] DEFAULT GETDATE(),
	[created_by] [bigint] NOT NULL,
	[updated_by] [bigint] NULL,
    FOREIGN KEY (created_by) REFERENCES Users(user_id)
)
GO

-- NOTIFICATIONS
CREATE TABLE [dbo].[Notifications](
	[notification_id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[user_id] [bigint] NOT NULL,
	[type] [varchar](20) NOT NULL,
	[content] [nvarchar](max) NOT NULL,
	[is_read] [bit] DEFAULT 0,
	[created_at] [datetime] DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
)
GO

-- REPORTS
CREATE TABLE [dbo].[Reports](
	[report_id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[reporter_id] [bigint] NOT NULL,
	[target_type] [varchar](20) NOT NULL,
	[target_id] [bigint] NOT NULL,
	[reason] [nvarchar](max) NOT NULL,
	[status] [varchar](20) DEFAULT 'open',
	[created_at] [datetime] DEFAULT GETDATE(),
    FOREIGN KEY (reporter_id) REFERENCES Users(user_id)
)
GO

-- MODERATOR_ACTIONS
CREATE TABLE [dbo].[Moderator_Actions](
	[action_id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[moderator_id] [bigint] NOT NULL,
	[action_type] [varchar](30) NOT NULL,
	[target_type] [varchar](20) NOT NULL,
	[target_id] [bigint] NOT NULL,
	[description] [nvarchar](max) NULL,
	[created_at] [datetime] DEFAULT GETDATE(),
    FOREIGN KEY (moderator_id) REFERENCES Users(user_id)
)
GO

-- FOLLOW (Tag & User)
CREATE TABLE [dbo].[TagFollow](
	[id] [bigint] IDENTITY(1,1) PRIMARY KEY,
	[user_id] [bigint] NOT NULL,
	[tag_id] [bigint] NOT NULL,
	[followed_at] [datetime] DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (tag_id) REFERENCES Tags(tag_id)
)
GO

CREATE TABLE [dbo].[UserFollow](
	[follower_id] [bigint] NOT NULL,
	[following_id] [bigint] NOT NULL,
	[followed_at] [datetime2](7) DEFAULT GETDATE(),
    PRIMARY KEY (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES Users(user_id),
    FOREIGN KEY (following_id) REFERENCES Users(user_id)
)
GO

ALTER TABLE [dbo].[Badges] 
ADD [required_reputation] [int] DEFAULT 0;
GO

CREATE TABLE [dbo].[Privileges](
	[privilege_id] [int] IDENTITY(1,1) PRIMARY KEY,
	[name] [nvarchar](100) NOT NULL,
	[description] [nvarchar](max) NOT NULL,
	[required_reputation] [int] NOT NULL
)
GO

-- 1. BLOGS TABLE (Stores articles posted by the Admin)
CREATE TABLE [dbo].[Blogs] (
    [blog_id] INT IDENTITY(1,1) PRIMARY KEY,
    [title] NVARCHAR(MAX) NOT NULL,
    [content] NVARCHAR(MAX) NOT NULL,
    [thumbnail_url] VARCHAR(500) NULL,
    -- Links to the Admin who posted it
    [author_id] BIGINT NOT NULL,
    -- Timestamps and Stats
    [created_at] DATETIME DEFAULT GETDATE(),
    [updated_at] DATETIME DEFAULT GETDATE(),
    [view_count] INT DEFAULT 0, -- Required for "Most Viewed" sorting
    [comment_count] INT DEFAULT 0,
    [status] INT DEFAULT 1,
    FOREIGN KEY ([author_id]) REFERENCES [dbo].[Users]([user_id])
);
GO
-- 2. BLOG COMMENTS TABLE (Stores user discussions and replies)
CREATE TABLE [dbo].[BlogComments] (
    [comment_id] INT IDENTITY(1,1) PRIMARY KEY,
    -- Which blog post this comment belongs to
    [blog_id] INT NOT NULL,
    -- Which user wrote this comment
    [user_id] BIGINT NOT NULL,
    -- Nested Comment Logic: NULL means it's a root comment. 
    -- An ID means it's a reply to another comment.
    [parent_id] INT NULL,
    [content] NVARCHAR(MAX) NOT NULL,
    [created_at] DATETIME DEFAULT GETDATE(),  
    -- If a blog is deleted, its comments are automatically deleted
    FOREIGN KEY ([blog_id]) REFERENCES [dbo].[Blogs]([blog_id]) ON DELETE CASCADE,
    FOREIGN KEY ([user_id]) REFERENCES [dbo].[Users]([user_id]),
    -- Self-referencing Foreign Key for the Parent/Child reply system
    FOREIGN KEY ([parent_id]) REFERENCES [dbo].[BlogComments]([comment_id])
);
GO

