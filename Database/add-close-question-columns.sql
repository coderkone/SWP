-- Close Question feature: add close metadata columns to Questions table (idempotent)

IF NOT EXISTS (
    SELECT 1
    FROM sys.columns
    WHERE object_id = OBJECT_ID('dbo.Questions')
      AND name = 'is_closed'
)
BEGIN
    ALTER TABLE dbo.Questions
    ADD is_closed bit NOT NULL CONSTRAINT DF_Questions_is_closed DEFAULT (0);
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.columns
    WHERE object_id = OBJECT_ID('dbo.Questions')
      AND name = 'closed_by'
)
BEGIN
    ALTER TABLE dbo.Questions
    ADD closed_by bigint NULL;
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.columns
    WHERE object_id = OBJECT_ID('dbo.Questions')
      AND name = 'closed_reason'
)
BEGIN
    ALTER TABLE dbo.Questions
    ADD closed_reason nvarchar(255) NULL;
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.columns
    WHERE object_id = OBJECT_ID('dbo.Questions')
      AND name = 'closed_at'
)
BEGIN
    ALTER TABLE dbo.Questions
    ADD closed_at datetime NULL;
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'FK_Questions_ClosedBy_Users'
)
BEGIN
    ALTER TABLE dbo.Questions
    ADD CONSTRAINT FK_Questions_ClosedBy_Users
        FOREIGN KEY (closed_by) REFERENCES dbo.Users(user_id);
END
GO
