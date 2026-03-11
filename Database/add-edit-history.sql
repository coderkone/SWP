USE [devquery]
GO

IF OBJECT_ID('dbo.Post_Edit_History', 'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[Post_Edit_History](
        [history_id] [bigint] IDENTITY(1,1) PRIMARY KEY,
        [post_type] [varchar](20) NOT NULL,
        [post_id] [bigint] NOT NULL,
        [title] [nvarchar](255) NULL,
        [body] [nvarchar](max) NOT NULL,
        [code_snippet] [nvarchar](max) NULL,
        [tags] [nvarchar](1000) NULL,
        [editor_id] [bigint] NOT NULL,
        [edited_at] [datetime] NOT NULL DEFAULT GETDATE(),
        FOREIGN KEY (editor_id) REFERENCES Users(user_id)
    )
END
ELSE
BEGIN
    IF COL_LENGTH('dbo.Post_Edit_History', 'edited_content') IS NULL
        ALTER TABLE [dbo].[Post_Edit_History] ADD [edited_content] [nvarchar](max) NULL;

    IF COL_LENGTH('dbo.Post_Edit_History', 'title') IS NULL
        ALTER TABLE [dbo].[Post_Edit_History] ADD [title] [nvarchar](255) NULL;

    IF COL_LENGTH('dbo.Post_Edit_History', 'body') IS NULL
        ALTER TABLE [dbo].[Post_Edit_History] ADD [body] [nvarchar](max) NULL;

    IF COL_LENGTH('dbo.Post_Edit_History', 'code_snippet') IS NULL
        ALTER TABLE [dbo].[Post_Edit_History] ADD [code_snippet] [nvarchar](max) NULL;

    IF COL_LENGTH('dbo.Post_Edit_History', 'tags') IS NULL
        ALTER TABLE [dbo].[Post_Edit_History] ADD [tags] [nvarchar](1000) NULL;

    UPDATE [dbo].[Post_Edit_History]
    SET [edited_content] = ISNULL([edited_content], '')
    WHERE [edited_content] IS NULL;
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_Post_Edit_History_Post'
      AND object_id = OBJECT_ID('dbo.Post_Edit_History')
)
BEGIN
    CREATE INDEX IX_Post_Edit_History_Post
    ON [dbo].[Post_Edit_History]([post_type], [post_id], [edited_at] DESC)
END
GO
