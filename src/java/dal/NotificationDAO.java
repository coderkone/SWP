/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;
import config.DBContext;
import model.Notification;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
public class NotificationDAO {
    private final DBContext db = new DBContext();
    public int getUnreadCount(long userId) throws Exception{
        String sql = "SELECT COUNT(*) FROM Notifications WHERE user_id = ? AND is_read = 0";
        try (Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setLong(1, userId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    public List<Notification> getNotification(long userId ,int limit) throws Exception{
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT TOP (?) notification_id, user_id, type, content, is_read, created_at "+ "FROM Notifications WHERE user_id = ? ORDER BY created_at DESC"; 
        try (Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, limit);
            ps.setLong(2, userId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Notification n = new Notification(
                    rs.getLong("notification_id"),
                    rs.getLong("user_id"),
                    rs.getString("type"),
                    rs.getString("content"),
                    rs.getBoolean("is_read"),
                    rs.getTimestamp("created_at")
                    );
                    list.add(n);
                }
            }
        }
        return list;
    }
    public void markAsRead (long notificationId , long userId) throws Exception{
        String sql = "UPDATE Notifications SET is_read = 1 WHERE notification_id = ? AND user_id = ?";
        try (Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setLong(1, notificationId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }
    public void markAllRead(long userId) throws Exception{
        String sql = "UPDATE Notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0";
        try (Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }
}
