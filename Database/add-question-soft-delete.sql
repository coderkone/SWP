USE [devquery];
GO

/*
Add soft delete columns to Questions.
Idempotent script: safe to run multiple times.
*/

IF COL_LENGTH('dbo.Questions', 'is_deleted') IS NULL
BEGIN
    ALTER TABLE dbo.Questions
    ADD is_deleted BIT NOT NULL CONSTRAINT DF_Questions_is_deleted DEFAULT (0);
END
GO

IF COL_LENGTH('dbo.Questions', 'deleted_at') IS NULL
BEGIN
    ALTER TABLE dbo.Questions
    ADD deleted_at DATETIME NULL;
END
GO

IF COL_LENGTH('dbo.Questions', 'deleted_by') IS NULL
BEGIN
    ALTER TABLE dbo.Questions
    ADD deleted_by BIGINT NULL;
END
GO

IF COL_LENGTH('dbo.Questions', 'deleted_by') IS NOT NULL
AND NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'FK_Questions_deleted_by_Users'
)
BEGIN
    ALTER TABLE dbo.Questions
    ADD CONSTRAINT FK_Questions_deleted_by_Users
        FOREIGN KEY (deleted_by) REFERENCES dbo.Users(user_id);
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_Questions_is_deleted'
      AND object_id = OBJECT_ID('dbo.Questions')
)
BEGIN
    CREATE INDEX IX_Questions_is_deleted ON dbo.Questions(is_deleted);
END
GO
