package dal;

import config.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ModeratorActionDAO {

    private final DBContext db = new DBContext();

    // Log moderator action
    public boolean createAction(long moderatorId, String actionType, String targetType, long targetId, String description) {
        String sql = "INSERT INTO Moderator_Actions (moderator_id, action_type, target_type, target_id, description, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, GETDATE())";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, moderatorId);
            ps.setString(2, actionType);
            ps.setString(3, targetType);
            ps.setLong(4, targetId);
            ps.setString(5, description);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
