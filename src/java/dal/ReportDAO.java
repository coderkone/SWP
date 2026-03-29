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
            String checkSql = "IF COL_LENGTH('dbo.Reports', 'note') IS NULL "
                    + "ALTER TABLE [dbo].[Reports] ADD [note] [nvarchar](500) NULL";
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

        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

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

    public java.util.List<Report> getFilteredReports(String status, String fromDate, String toDate, int page, int pageSize) throws Exception {
        java.util.List<Report> list = new java.util.ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT r.*, u.username as reporter_name "
                + "FROM Reports r LEFT JOIN Users u ON r.reporter_id = u.user_id WHERE 1=1 ");

        if (status != null && !status.isEmpty()) {
            sql.append(" AND r.status = ? ");
        }
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND r.created_at >= ? ");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND r.created_at <= ? ");
        }

        sql.append(" ORDER BY r.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int paramIdx = 1;
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIdx++, status);
            }
            if (fromDate != null && !fromDate.isEmpty()) {
                ps.setString(paramIdx++, fromDate + " 00:00:00");
            }
            if (toDate != null && !toDate.isEmpty()) {
                ps.setString(paramIdx++, toDate + " 23:59:59");
            }
            ps.setInt(paramIdx++, (page - 1) * pageSize);
            ps.setInt(paramIdx, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Report r = new Report();
                    r.setReportId(rs.getLong("report_id"));
                    r.setReporterId(rs.getLong("reporter_id"));
                    r.setTargetType(rs.getString("target_type"));
                    r.setTargetId(rs.getLong("target_id"));
                    r.setReason(rs.getString("reason"));
                    r.setNote(rs.getString("note"));
                    r.setStatus(rs.getString("status"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setReporterName(rs.getString("reporter_name"));
                    list.add(r);
                }
            }
        }
        return list;
    }

    public int getFilteredReportCount(String status, String fromDate, String toDate) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Reports WHERE 1=1 ");
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ? ");
        }
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND created_at >= ? ");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND created_at <= ? ");
        }

        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int paramIdx = 1;
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIdx++, status);
            }
            if (fromDate != null && !fromDate.isEmpty()) {
                ps.setString(paramIdx++, fromDate + " 00:00:00");
            }
            if (toDate != null && !toDate.isEmpty()) {
                ps.setString(paramIdx++, toDate + " 23:59:59");
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public Report getReportById(long reportId) throws Exception {
        String sql = "SELECT r.*, u.username as reporter_name, u.email as reporter_email "
                + "FROM Reports r "
                + "LEFT JOIN Users u ON r.reporter_id = u.user_id "
                + "WHERE r.report_id = ?";

        Report report = null;
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, reportId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report = new Report();
                    report.setReportId(rs.getLong("report_id"));
                    report.setReporterId(rs.getLong("reporter_id"));
                    report.setTargetType(rs.getString("target_type"));
                    report.setTargetId(rs.getLong("target_id"));
                    report.setReason(rs.getString("reason"));
                    report.setNote(rs.getString("note"));
                    report.setStatus(rs.getString("status"));
                    report.setCreatedAt(rs.getTimestamp("created_at"));
                    report.setReporterName(rs.getString("reporter_name"));
                    report.setReporterEmail(rs.getString("reporter_email"));

                    // Fetch target content
                    fetchTargetInfo(report, con);
                }
            }
        }
        return report;
    }

    private void fetchTargetInfo(Report report, Connection con) throws Exception {
        String targetType = report.getTargetType();
        long targetId = report.getTargetId();

        if ("question".equals(targetType)) {
            String sql = "SELECT q.title, q.body, u.username as author_name, q.question_id "
                    + "FROM Questions q JOIN Users u ON q.user_id = u.user_id WHERE q.question_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, targetId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        report.setTargetTitle(rs.getString("title"));
                        report.setTargetBody(rs.getString("body"));
                        report.setTargetAuthorName(rs.getString("author_name"));
                        report.setQuestionId(rs.getLong("question_id"));
                    }
                }
            }
        } else if ("answer".equals(targetType)) {
            String sql = "SELECT a.body, u.username as author_name, a.question_id "
                    + "FROM Answers a JOIN Users u ON a.user_id = u.user_id WHERE a.answer_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, targetId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        report.setTargetBody(rs.getString("body"));
                        report.setTargetAuthorName(rs.getString("author_name"));
                        report.setQuestionId(rs.getLong("question_id"));
                        report.setTargetTitle("Answer to Question #" + report.getQuestionId());
                    }
                }
            }
        } else if ("comment".equals(targetType)) {
            String sql = "SELECT c.body, u.username as author_name, c.question_id, c.answer_id "
                    + "FROM Comments c JOIN Users u ON c.user_id = u.user_id WHERE c.comment_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, targetId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        report.setTargetBody(rs.getString("body"));
                        report.setTargetAuthorName(rs.getString("author_name"));
                        long qid = rs.getLong("question_id");
                        if (rs.wasNull()) {
                            // If it's a comment on an answer, we might need another join or just show answer_id
                            report.setQuestionId(0); // Optional: find question_id via answer_id
                        } else {
                            report.setQuestionId(qid);
                        }
                    }
                }
            }
        }
    }

    public boolean updateReportStatus(long reportId, String status, String note) throws Exception {
        String sql = "UPDATE Reports SET status = ?, note = ? WHERE report_id = ?";
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, note);
            ps.setLong(3, reportId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateReportStatus(long reportId, String status) throws Exception {
        return updateReportStatus(reportId, status, null);
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

        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, postId);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public java.util.List<Report> getRecentReports(int limit) throws Exception {
        java.util.List<Report> list = new java.util.ArrayList<>();
        String sql = "SELECT TOP (?) r.*, u.username as reporter_name "
                + "FROM Reports r LEFT JOIN Users u ON r.reporter_id = u.user_id "
                + "ORDER BY r.created_at DESC";

        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Report r = new Report();
                    r.setReportId(rs.getLong("report_id"));
                    r.setReporterId(rs.getLong("reporter_id"));
                    r.setTargetType(rs.getString("target_type"));
                    r.setTargetId(rs.getLong("target_id"));
                    r.setReason(rs.getString("reason"));
                    r.setNote(rs.getString("note"));
                    r.setStatus(rs.getString("status"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setReporterName(rs.getString("reporter_name"));
                    list.add(r);
                }
            }
        }
        return list;
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
}
