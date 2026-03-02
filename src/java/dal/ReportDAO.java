package dal;

import config.DBContext;
import dto.ReportDTO;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    private final DBContext db = new DBContext();

    // Lay tong so reports
    public int getReportCount() {
        String sql = "SELECT COUNT(*) FROM Reports";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    // Dem reports theo status
    public int getReportCountByStatus(String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Reports");
        if ("open".equals(status)) {
            sql.append(" WHERE status = 'open'");
        } else if ("resolved".equals(status)) {
            sql.append(" WHERE status = 'resolved'");
        }

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Dem reports theo filter (status + date range)
    public int getReportCountFiltered(String status, Date fromDate, Date toDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Reports WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (fromDate != null) {
            sql.append(" AND CAST(created_at AS DATE) >= ?");
            params.add(fromDate);
        }
        if (toDate != null) {
            sql.append(" AND CAST(created_at AS DATE) <= ?");
            params.add(toDate);
        }

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    
    // Lay danh sach reports voi pagination
    public List<ReportDTO> getAllReports(int page, int pageSize) {
        List<ReportDTO> reports = new ArrayList<>();
        String sql = "SELECT r.report_id, r.reporter_id, r.target_type, r.target_id, " +
                     "r.reason, r.status, r.created_at, u.username as reporter_name " +
                     "FROM Reports r " +
                     "JOIN Users u ON r.reporter_id = u.user_id " +
                     "ORDER BY r.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportDTO report = mapResultSetToReportDTO(rs);
                    reports.add(report);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reports;
    }


    // Lay reports theo status voi pagination
    public List<ReportDTO> getReportsByStatus(String status, int page, int pageSize) {
        List<ReportDTO> reports = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT r.report_id, r.reporter_id, r.target_type, r.target_id, " +
            "r.reason, r.status, r.created_at, u.username as reporter_name " +
            "FROM Reports r " +
            "JOIN Users u ON r.reporter_id = u.user_id"
        );

        if ("open".equals(status)) {
            sql.append(" WHERE r.status = 'open'");
        } else if ("resolved".equals(status)) {
            sql.append(" WHERE r.status = 'resolved'");
        }

        sql.append(" ORDER BY r.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportDTO report = mapResultSetToReportDTO(rs);
                    reports.add(report);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reports;
    }

    // Lay reports voi filter (status + date range) va pagination
    public List<ReportDTO> getReportsFiltered(String status, Date fromDate, Date toDate, int page, int pageSize) {
        List<ReportDTO> reports = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT r.report_id, r.reporter_id, r.target_type, r.target_id, " +
            "r.reason, r.status, r.created_at, u.username as reporter_name " +
            "FROM Reports r " +
            "JOIN Users u ON r.reporter_id = u.user_id WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isEmpty()) {
            sql.append(" AND r.status = ?");
            params.add(status);
        }
        if (fromDate != null) {
            sql.append(" AND CAST(r.created_at AS DATE) >= ?");
            params.add(fromDate);
        }
        if (toDate != null) {
            sql.append(" AND CAST(r.created_at AS DATE) <= ?");
            params.add(toDate);
        }

        sql.append(" ORDER BY r.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object param : params) {
                ps.setObject(idx++, param);
            }
            ps.setInt(idx++, (page - 1) * pageSize);
            ps.setInt(idx, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportDTO report = mapResultSetToReportDTO(rs);
                    reports.add(report);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reports;
    }

    // Lay report theo ID voi full detail
    public ReportDTO getReportById(long reportId) {
        String sql = "SELECT r.report_id, r.reporter_id, r.target_type, r.target_id, " +
                     "r.reason, r.status, r.created_at, " +
                     "u.username as reporter_name, u.email as reporter_email " +
                     "FROM Reports r " +
                     "JOIN Users u ON r.reporter_id = u.user_id " +
                     "WHERE r.report_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, reportId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ReportDTO report = mapResultSetToReportDTO(rs);
                    report.setReporterEmail(rs.getString("reporter_email"));

                    // Load target content
                    loadTargetContent(report);
                    return report;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cap nhat status report
    public boolean updateReportStatus(long reportId, String status) {
        String sql = "UPDATE Reports SET status = ? WHERE report_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, reportId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Soft delete question (dong cau hoi)
    public boolean closeQuestion(long questionId, String reason) {
        String sql = "UPDATE Questions SET is_closed = 1, closed_reason = ? WHERE question_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reason);
            ps.setLong(2, questionId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Soft delete answer (thay body bang thong bao)
    public boolean hideAnswer(long answerId) {
        String sql = "UPDATE Answers SET body = N'[Nội dung đã bị ẩn do vi phạm quy định cộng đồng]' WHERE answer_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, answerId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Soft delete comment (thay body bang thong bao)
    public boolean hideComment(long commentId) {
        String sql = "UPDATE Comments SET body = N'[Nội dung đã bị ẩn do vi phạm quy định cộng đồng]' WHERE comment_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, commentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Load target content dua vao type
    private void loadTargetContent(ReportDTO report) {
        String targetType = report.getTargetType();
        long targetId = report.getTargetId();

        try (Connection con = db.getConnection()) {
            if ("question".equals(targetType)) {
                loadQuestionContent(con, report, targetId);
            } else if ("answer".equals(targetType)) {
                loadAnswerContent(con, report, targetId);
            } else if ("comment".equals(targetType)) {
                loadCommentContent(con, report, targetId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadQuestionContent(Connection con, ReportDTO report, long questionId) throws Exception {
        String sql = "SELECT q.question_id, q.title, q.body, q.user_id, u.username " +
                     "FROM Questions q " +
                     "JOIN Users u ON q.user_id = u.user_id " +
                     "WHERE q.question_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report.setQuestionId(rs.getLong("question_id"));
                    report.setTargetTitle(rs.getString("title"));
                    report.setTargetBody(rs.getString("body"));
                    report.setTargetAuthorId(rs.getLong("user_id"));
                    report.setTargetAuthorName(rs.getString("username"));
                }
            }
        }
    }

    private void loadAnswerContent(Connection con, ReportDTO report, long answerId) throws Exception {
        String sql = "SELECT a.answer_id, a.question_id, a.body, a.user_id, u.username, q.title as question_title " +
                     "FROM Answers a " +
                     "JOIN Users u ON a.user_id = u.user_id " +
                     "JOIN Questions q ON a.question_id = q.question_id " +
                     "WHERE a.answer_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, answerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report.setQuestionId(rs.getLong("question_id"));
                    report.setTargetTitle(rs.getString("question_title"));
                    report.setTargetBody(rs.getString("body"));
                    report.setTargetAuthorId(rs.getLong("user_id"));
                    report.setTargetAuthorName(rs.getString("username"));
                }
            }
        }
    }

    private void loadCommentContent(Connection con, ReportDTO report, long commentId) throws Exception {
        String sql = "SELECT c.comment_id, c.body, c.user_id, c.question_id, c.answer_id, u.username " +
                     "FROM Comments c " +
                     "JOIN Users u ON c.user_id = u.user_id " +
                     "WHERE c.comment_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, commentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report.setTargetBody(rs.getString("body"));
                    report.setTargetAuthorId(rs.getLong("user_id"));
                    report.setTargetAuthorName(rs.getString("username"));

                    // Get question_id from comment
                    long qId = rs.getLong("question_id");
                    if (qId > 0) {
                        report.setQuestionId(qId);
                    } else {
                        // Comment on answer, get question_id from answer
                        long aId = rs.getLong("answer_id");
                        if (aId > 0) {
                            report.setQuestionId(getQuestionIdFromAnswer(con, aId));
                        }
                    }
                }
            }
        }
    }

    private long getQuestionIdFromAnswer(Connection con, long answerId) throws Exception {
        String sql = "SELECT question_id FROM Answers WHERE answer_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, answerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("question_id");
                }
            }
        }
        return 0;
    }

    // Helper method to map ResultSet to ReportDTO
    private ReportDTO mapResultSetToReportDTO(ResultSet rs) throws Exception {
        ReportDTO report = new ReportDTO();
        report.setReportId(rs.getLong("report_id"));
        report.setReporterId(rs.getLong("reporter_id"));
        report.setTargetType(rs.getString("target_type"));
        report.setTargetId(rs.getLong("target_id"));
        report.setReason(rs.getString("reason"));
        report.setStatus(rs.getString("status"));
        report.setCreatedAt(rs.getTimestamp("created_at"));
        report.setReporterName(rs.getString("reporter_name"));
        return report;
    }
}
