IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Questions' AND COLUMN_NAME = 'bounty_amount')
BEGIN
    ALTER TABLE [dbo].[Questions]
    ADD [bounty_amount] [int] NOT NULL CONSTRAINT [DF_Questions_bounty_amount] DEFAULT 0;
END
GO

IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Questions' AND COLUMN_NAME = 'bounty_awarder_id')
BEGIN
    ALTER TABLE [dbo].[Questions]
    ADD [bounty_awarder_id] [bigint] NULL;
END
GO

IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Questions' AND COLUMN_NAME = 'bounty_started_at')
BEGIN
    ALTER TABLE [dbo].[Questions]
    ADD [bounty_started_at] [datetime] NULL;
END
GO

IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Questions' AND COLUMN_NAME = 'bounty_expires_at')
BEGIN
    ALTER TABLE [dbo].[Questions]
    ADD [bounty_expires_at] [datetime] NULL;
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_Questions_Bounty_Active' AND object_id = OBJECT_ID('dbo.Questions'))
BEGIN
    CREATE INDEX [IX_Questions_Bounty_Active]
        ON [dbo].[Questions]([bounty_expires_at], [bounty_amount])
        WHERE [bounty_amount] > 0;
END
GO