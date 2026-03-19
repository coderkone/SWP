-- DevQuery already has [dbo].[Reports] in DB-DevQuery.sql.
-- This script only aligns existing Reports schema with the Flag UI requirements.

USE [devquery]
GO

IF COL_LENGTH('dbo.Reports', 'note') IS NULL
BEGIN
    ALTER TABLE [dbo].[Reports]
    ADD [note] [nvarchar](500) NULL;
END
GO

IF COL_LENGTH('dbo.Reports', 'comment') IS NOT NULL
   AND COL_LENGTH('dbo.Reports', 'note') IS NULL
BEGIN
    EXEC sp_rename 'dbo.Reports.comment', 'note', 'COLUMN';
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.check_constraints
    WHERE name = 'CHK_Reports_TargetType'
)
BEGIN
    ALTER TABLE [dbo].[Reports]
    ADD CONSTRAINT [CHK_Reports_TargetType]
    CHECK ([target_type] IN ('question', 'answer'));
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_Reports_Target'
      AND object_id = OBJECT_ID('dbo.Reports')
)
BEGIN
    CREATE INDEX [IX_Reports_Target]
    ON [dbo].[Reports]([target_type], [target_id]);
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_Reports_Reporter'
      AND object_id = OBJECT_ID('dbo.Reports')
)
BEGIN
    CREATE INDEX [IX_Reports_Reporter]
    ON [dbo].[Reports]([reporter_id]);
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_Reports_CreatedAt'
      AND object_id = OBJECT_ID('dbo.Reports')
)
BEGIN
    CREATE INDEX [IX_Reports_CreatedAt]
    ON [dbo].[Reports]([created_at]);
END
GO
