package dal;

import config.DBContext;
import dto.RevisionDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EditHistoryDAO extends DBContext {

    public List<RevisionDTO> getRevisionsByPost(String postType, long postId) throws Exception {
        List<RevisionDTO> revisions = new ArrayList<>();
        String sql = "SELECT h.history_id, h.post_type, h.post_id, h.title, h.body, h.code_snippet, h.tags, "
                + "h.editor_id, h.edited_at, u.username "
                + "FROM Post_Edit_History h "
                + "JOIN Users u ON h.editor_id = u.user_id "
                + "WHERE h.post_type = ? AND h.post_id = ? "
                + "ORDER BY h.edited_at DESC, h.history_id DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, postType);
            ps.setLong(2, postId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    revisions.add(mapRevision(rs));
                }
            }
        }

        return revisions;
    }

    public RevisionDTO getRevisionById(long historyId) throws Exception {
        String sql = "SELECT h.history_id, h.post_type, h.post_id, h.title, h.body, h.code_snippet, h.tags, "
                + "h.editor_id, h.edited_at, u.username "
                + "FROM Post_Edit_History h "
                + "JOIN Users u ON h.editor_id = u.user_id "
                + "WHERE h.history_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, historyId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRevision(rs);
                }
            }
        }

        return null;
    }

    private RevisionDTO mapRevision(ResultSet rs) throws Exception {
        RevisionDTO dto = new RevisionDTO();
        dto.setHistoryId(rs.getLong("history_id"));
        dto.setPostType(rs.getString("post_type"));
        dto.setPostId(rs.getLong("post_id"));
        dto.setTitle(rs.getString("title"));
        dto.setBody(rs.getString("body"));
        dto.setCodeSnippet(rs.getString("code_snippet"));
        dto.setTags(rs.getString("tags"));
        dto.setEditorId(rs.getLong("editor_id"));
        dto.setEditorName(rs.getString("username"));
        dto.setEditedAt(rs.getTimestamp("edited_at"));
        return dto;
    }
}
