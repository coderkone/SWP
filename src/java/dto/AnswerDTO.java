package dto;

import model.Answer;
import java.sql.Timestamp;

public class AnswerDTO extends Answer {
    private String authorName;
    private String authorAvatar;
    private int voteCount;

    public AnswerDTO() {
    }

    public AnswerDTO(long answerId, long questionId, long userId, String body, String codeSnippet, 
                     boolean isEdited, boolean isAccepted, Timestamp createdAt, Timestamp updatedAt, 
                     int score, String authorName, String authorAvatar, int voteCount) {
        super(answerId, questionId, userId, body, codeSnippet, isEdited, isAccepted, createdAt, updatedAt, score);
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.voteCount = voteCount;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
