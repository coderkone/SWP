/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;
import dto.TagDTO;
import config.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Asus
 */
public class TagDAO extends DBContext {
    
    private boolean isFollowed(long userId, long tagId) {
        String sql = "SELECT COUNT(*) FROM TagFollow "
                   + "WHERE user_id = ? AND tag_id = ?";
        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, userId);
            st.setLong(2, tagId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<TagDTO> getAllTags(long userId , String keyword , String sort){
        List<TagDTO> list = new ArrayList<>();
        StringBuilder sqlDynamic = new StringBuilder(
            "SELECT t.tag_id, t.tag_name, t.description, t.IsActive, " +
            "CASE WHEN tf.user_id IS NOT NULL THEN 1 ELSE 0 END AS is_followed, " +
            "COUNT(qt.question_id) AS question_count " +
            "FROM Tags t " +
            "LEFT JOIN TagFollow tf ON t.tag_id = tf.tag_id AND tf.user_id = ? " +
            "LEFT JOIN Question_Tags qt ON t.tag_id = qt.tag_id " +
            "WHERE t.IsActive = 1 "
        );
        if(keyword != null && !keyword.trim().isEmpty()){
            sqlDynamic.append("AND t.tag_name LIKE ? ");  
        }
        sqlDynamic.append("GROUP BY t.tag_id, t.tag_name, t.description, t.IsActive, tf.user_id ");
        sqlDynamic.append("ORDER BY is_followed DESC ");
        if("popular".equals(sort)){
            sqlDynamic.append(", question_count DESC");
        }else if ("newest".equals(sort)){
            sqlDynamic.append(", t.tag_id DESC");
        }else{
            sqlDynamic.append(", t.tag_name ASC");
        }
                     
        try{
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sqlDynamic.toString());
            int paramIndex = 1;
            st.setLong(paramIndex++, userId);
            if(keyword != null && !keyword.trim().isEmpty()){
                st.setString(paramIndex++, "%" + keyword.trim() + "%");
            }
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                TagDTO tag = new TagDTO();
                tag.setTagId(rs.getLong("tag_id"));
                tag.setTagName(rs.getString("tag_name"));
                tag.setDescription(rs.getString("description"));
                tag.setIsActive(rs.getBoolean("IsActive"));
                tag.setIsFollowed(rs.getInt("is_followed") == 1);
                list.add(tag);
            }
            
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error" + e.getMessage());
        }
        return list;
    }
    
    public void followTag(long userId, long tagId){
        if (isFollowed(userId, tagId)){
            return;
        }
        String sql = "INSERT INTO TagFollow (user_id, tag_id) VALUES (?, ?)";
        try{
            Connection con = getConnection();
            PreparedStatement st = con.prepareStatement(sql);
            st.setLong(1, userId);
            st.setLong(2, tagId);
            st.executeUpdate();
            
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error" + e.getMessage());
        }
    }
    
    public void unfollowTag(long userId, long tagId){
        String sql = "DELETE FROM TagFollow WHERE user_id = ? AND tag_id = ?";
        try{
            Connection con = getConnection();
            PreparedStatement st = con.prepareStatement(sql);
            st.setLong(1, userId);
            st.setLong(2, tagId);
            st.executeUpdate();
            
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error" + e.getMessage());
        }
    }
    
       
}
