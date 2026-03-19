-- Add Answer Bookmarks Table
CREATE TABLE [dbo].[Answer_Bookmarks](
	[user_id] [bigint] NOT NULL,
	[answer_id] [bigint] NOT NULL,
	[created_at] [datetime] DEFAULT GETDATE(),
    PRIMARY KEY (user_id, answer_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (answer_id) REFERENCES Answers(answer_id) ON DELETE CASCADE
)
GO
