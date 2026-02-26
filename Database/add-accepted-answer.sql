-- Add accepted_answer_id to Questions (Option A - recommended)
-- When accepted answer is deleted, ON DELETE SET NULL clears it automatically.

USE [devquery]
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Questions') AND name = 'accepted_answer_id')
BEGIN
    ALTER TABLE [dbo].[Questions] ADD [accepted_answer_id] [bigint] NULL;
    ALTER TABLE [dbo].[Questions] ADD CONSTRAINT [FK_Questions_AcceptedAnswer] 
        FOREIGN KEY ([accepted_answer_id]) REFERENCES [dbo].[Answers]([answer_id]) ON DELETE SET NULL;
END
GO
