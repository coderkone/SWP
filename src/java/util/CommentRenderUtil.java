package util;

import dto.CommentDTO;
import java.util.List;
import java.util.Map;

public class CommentRenderUtil {

    private CommentRenderUtil() {
    }

    public static String renderAnswerCommentThread(
            Map<Long, List<CommentDTO>> commentTree,
            List<CommentDTO> comments,
            long answerId,
            long questionId,
            boolean isLoggedIn,
            String contextPath,
            int level) {

        StringBuilder html = new StringBuilder();
        appendThread(html, commentTree, comments, answerId, questionId, isLoggedIn, contextPath, level);
        return html.toString();
    }

    private static void appendThread(
            StringBuilder html,
            Map<Long, List<CommentDTO>> commentTree,
            List<CommentDTO> comments,
            long answerId,
            long questionId,
            boolean isLoggedIn,
            String contextPath,
            int level) {

        if (comments == null || comments.isEmpty()) {
            return;
        }

        int marginLeft = Math.min(level, 8) * 24;
        for (CommentDTO comment : comments) {
            String replyFormId = "reply-form-comment-" + comment.getCommentId();
            String replyBtnId = "reply-btn-comment-" + comment.getCommentId();

            html.append("<div class=\"comment-item\" style=\"margin-bottom: 12px; margin-left: ")
                .append(marginLeft)
                .append("px; font-size: 13px;\">");

            html.append("<div style=\"color: #6a737c; margin-bottom: 4px;\">")
                .append("<span style=\"color: #0a95ff; font-weight: 500;\">")
                .append(escapeHtml(comment.getAuthorName()))
                .append("</span>")
                .append("<span style=\"margin-left: 8px;\">")
                .append(escapeHtml(String.valueOf(comment.getCreatedAt())))
                .append("</span>")
                .append("</div>");

            html.append("<div style=\"color: #3b4045;\">")
                .append(escapeHtml(comment.getBody()))
                .append("</div>");

            if (isLoggedIn) {
                html.append("<div id=\"").append(replyFormId).append("\" style=\"display: none; margin-top: 8px;\">")
                    .append("<form method=\"post\" action=\"").append(contextPath).append("/comment/add\">")
                    .append("<input type=\"hidden\" name=\"answerId\" value=\"").append(answerId).append("\">")
                    .append("<input type=\"hidden\" name=\"questionId\" value=\"").append(questionId).append("\">")
                    .append("<input type=\"hidden\" name=\"parentCommentId\" value=\"").append(comment.getCommentId()).append("\">")
                    .append("<textarea name=\"commentBody\" class=\"form-input\" placeholder=\"Write a reply...\" style=\"width: 100%; min-height: 56px; font-size: 13px; padding: 8px; margin-top: 6px;\" required></textarea>")
                    .append("<div style=\"display: flex; gap: 8px; margin-top: 8px;\">")
                    .append("<button type=\"submit\" class=\"btn\" style=\"padding: 6px 12px; font-size: 12px;\">Reply</button>")
                    .append("<button type=\"button\" class=\"btn\" style=\"padding: 6px 12px; font-size: 12px; background: #f1f2f3; color: #3b4045;\" onclick=\"hideReplyForm(")
                    .append(comment.getCommentId())
                    .append(")\">Cancel</button>")
                    .append("</div>")
                    .append("</form>")
                    .append("</div>");

                html.append("<button type=\"button\" class=\"reply-btn\" id=\"")
                    .append(replyBtnId)
                    .append("\" style=\"margin-top: 6px; font-size: 12px; color: #0a95ff; background: none; border: none; cursor: pointer; padding: 0;\" onclick=\"showReplyForm(")
                    .append(comment.getCommentId())
                    .append(")\">Reply</button>");
            } else {
                html.append("<div style=\"margin-top: 6px;\"><a href=\"")
                    .append(contextPath)
                    .append("/auth/login\" style=\"font-size: 12px; color: #0a95ff; text-decoration: none;\">Sign in to reply</a></div>");
            }

            List<CommentDTO> children = commentTree.get(comment.getCommentId());
            appendThread(html, commentTree, children, answerId, questionId, isLoggedIn, contextPath, level + 1);
            html.append("</div>");
        }
    }

    private static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
