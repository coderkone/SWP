package dal;

import config.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Report;

public class ReportDAO {

    private final DBContext db = new DBContext();

    public long insertReport(Report report) throws Exception {
        String sql = "INSERT INTO Reports (reporter_id, target_type, target_id, reason, note, status, created_at) "
                + "VALUES (?, ?, ?, ?, ?, 'open', GETDATE())";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, report.getReporterId());
            ps.setString(2, report.getTargetType());
            ps.setLong(3, report.getTargetId());
            ps.setString(4, report.getReason());
            ps.setString(5, report.getNote());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        return -1;
    }

    public boolean isOwnerOfPost(long userId, String postType, long postId) throws Exception {
        String sql;
        if ("question".equals(postType)) {
            sql = "SELECT 1 FROM Questions WHERE question_id = ? AND user_id = ?";
        } else if ("answer".equals(postType)) {
            sql = "SELECT 1 FROM Answers WHERE answer_id = ? AND user_id = ?";
        } else {
            return false;
        }

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, postId);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
