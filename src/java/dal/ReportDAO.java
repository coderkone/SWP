package dal;

import config.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Report;

public class ReportDAO {

    private final DBContext db = new DBContext();

    static {
        // Self-healing: Check if 'note' column exists
        DBContext db = new DBContext();
        try (Connection con = db.getConnection()) {
            String checkSql = "IF COL_LENGTH('dbo.Reports', 'note') IS NULL " +
                              "ALTER TABLE [dbo].[Reports] ADD [note] [nvarchar](500) NULL";
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("ReportDAO self-healing error: " + e.getMessage());
        }
    }

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

    public java.util.List<Report> getAllReports(int page, int pageSize) throws Exception {
        java.util.List<Report> list = new java.util.ArrayList<>();
        String sql = "SELECT r.report_id, r.reporter_id, r.target_type, r.target_id, r.reason, r.note, r.status, r.created_at, u.username " +
                     "FROM Reports r LEFT JOIN Users u ON r.reporter_id = u.user_id " +
                     "ORDER BY r.created_at DESC";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                int start = (page - 1) * pageSize;
                int count = 0;
                while (rs.next()) {
                    if (count >= start && count < start + pageSize) {
                        Report r = new Report();
                        r.setReportId(rs.getLong("report_id"));
                        r.setReporterId(rs.getLong("reporter_id"));
                        r.setTargetType(rs.getString("target_type"));
                        r.setTargetId(rs.getLong("target_id"));
                        r.setReason(rs.getString("reason"));
                        r.setNote(rs.getString("note"));
                        r.setStatus(rs.getString("status"));
                        r.setCreatedAt(rs.getTimestamp("created_at"));
                        r.setReporterUsername(rs.getString("username"));
                        list.add(r);
                    }
                    count++;
                }
            }
        }
        return list;
    }

    public int getReportCount() throws Exception {
        String sql = "SELECT COUNT(*) FROM Reports";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public boolean updateReportStatus(long reportId, String status) throws Exception {
        String sql = "UPDATE Reports SET status = ? WHERE report_id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, reportId);
            return ps.executeUpdate() > 0;
        }
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
