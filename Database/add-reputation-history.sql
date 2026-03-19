-- Reputation history table for profile timeline (e.g. +10 reputation (Answer upvoted))
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Reputation_History' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE [dbo].[Reputation_History](
        [history_id] [bigint] IDENTITY(1,1) NOT NULL,
        [user_id] [bigint] NOT NULL,
        [delta] [int] NOT NULL,
        [reason] [nvarchar](255) NULL,
        [event_type] [varchar](50) NULL,
        [related_post_type] [varchar](20) NULL,
        [related_post_id] [bigint] NULL,
        [actor_user_id] [bigint] NULL,
        [created_at] [datetime] NOT NULL CONSTRAINT [DF_Reputation_History_created_at] DEFAULT (GETDATE()),
        CONSTRAINT [PK_Reputation_History] PRIMARY KEY CLUSTERED ([history_id] ASC),
        CONSTRAINT [FK_Reputation_History_user] FOREIGN KEY ([user_id]) REFERENCES [dbo].[Users]([user_id]),
        CONSTRAINT [FK_Reputation_History_actor] FOREIGN KEY ([actor_user_id]) REFERENCES [dbo].[Users]([user_id])
    );
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_Reputation_History_User_Created' AND object_id = OBJECT_ID('dbo.Reputation_History'))
BEGIN
    CREATE INDEX [IX_Reputation_History_User_Created]
        ON [dbo].[Reputation_History]([user_id], [created_at] DESC);
END
GO
