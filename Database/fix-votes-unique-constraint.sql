-- Fix UNIQUE constraint on Votes table.
-- Problem: Old constraint on (user_id, question_id) or (user_id, answer_id) causes (user_id, NULL) duplicate
-- when voting on multiple questions (answer_id=NULL) or answers (question_id=NULL).
-- Solution: UNIQUE(user_id, question_id, answer_id) allows one vote per user per question AND per answer.

USE [devquery]
GO

-- Drop ALL unique constraints on Votes (handles any constraint name)
DECLARE @sql NVARCHAR(MAX) = '';
SELECT @sql = @sql + 'ALTER TABLE [dbo].[Votes] DROP CONSTRAINT [' + name + '];'
FROM sys.key_constraints
WHERE parent_object_id = OBJECT_ID('dbo.Votes')
  AND type = 'UQ';

IF LEN(@sql) > 0
    EXEC sp_executesql @sql;
GO

-- Add correct constraint: one vote per user per question OR per answer
IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE parent_object_id = OBJECT_ID('dbo.Votes') AND name = 'UQ_Votes_user_question_answer')
BEGIN
    ALTER TABLE [dbo].[Votes] ADD CONSTRAINT [UQ_Votes_user_question_answer] UNIQUE ([user_id], [question_id], [answer_id]);
END
GO
