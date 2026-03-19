-- Add nested-reply support for comments
-- parent_comment_id: NULL = top-level comment, NOT NULL = reply to another comment
-- This script is idempotent and auto-detects schema/table name (Comments or Comment).

SET NOCOUNT ON;

DECLARE @schemaName sysname;
DECLARE @tableName sysname;
DECLARE @fullTable nvarchar(300);
DECLARE @sql nvarchar(max);
DECLARE @objectId int;

SELECT TOP 1
	@schemaName = s.name,
	@tableName = t.name
FROM sys.tables t
JOIN sys.schemas s ON s.schema_id = t.schema_id
WHERE t.name IN ('Comments', 'Comment')
ORDER BY CASE WHEN t.name = 'Comments' THEN 0 ELSE 1 END;

IF @schemaName IS NULL
BEGIN
	THROW 50001, 'Cannot find table Comments/Comment in current database. Verify you are using the correct database and have permissions.', 1;
END;

SET @fullTable = QUOTENAME(@schemaName) + '.' + QUOTENAME(@tableName);
SET @objectId = OBJECT_ID(@schemaName + '.' + @tableName);

-- 1) Add column if missing
IF COL_LENGTH(@schemaName + '.' + @tableName, 'parent_comment_id') IS NULL
BEGIN
	SET @sql = N'ALTER TABLE ' + @fullTable + N' ADD [parent_comment_id] BIGINT NULL;';
	EXEC sp_executesql @sql;
END;

-- 2) Add self-reference FK on parent_comment_id if missing
IF COL_LENGTH(@schemaName + '.' + @tableName, 'parent_comment_id') IS NOT NULL
AND NOT EXISTS (
	SELECT 1
	FROM sys.foreign_key_columns fkc
	JOIN sys.columns pc
		ON pc.object_id = fkc.parent_object_id
	   AND pc.column_id = fkc.parent_column_id
	WHERE fkc.parent_object_id = @objectId
	  AND fkc.referenced_object_id = @objectId
	  AND pc.name = 'parent_comment_id'
)
BEGIN
	SET @sql = N'ALTER TABLE ' + @fullTable
		+ N' ADD CONSTRAINT [FK_Comments_ParentComment]'
		+ N' FOREIGN KEY ([parent_comment_id]) REFERENCES ' + @fullTable + N'([comment_id]);';
	EXEC sp_executesql @sql;
END;

-- 3) Add index if missing
IF NOT EXISTS (
	SELECT 1
	FROM sys.indexes i
	WHERE i.object_id = @objectId
	  AND i.name = 'IX_Comments_ParentComment'
)
BEGIN
	SET @sql = N'CREATE INDEX [IX_Comments_ParentComment] ON ' + @fullTable + N'([parent_comment_id]);';
	EXEC sp_executesql @sql;
END;

PRINT 'Nested comment migration applied on ' + @fullTable + '.';
