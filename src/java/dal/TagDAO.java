/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;
import dto.TagDTO;
import config.DBContext;
>>>>>>> Mai
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
<<<<<<< HEAD

public class TagDAO {

    private final DBContext db = new DBContext();

    // Lấy tổng số tags
    public int getTagCount() {
        String sql = "SELECT COUNT(*) FROM Tags";
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

    // Lấy danh sách tags với pagination + questionCount + followerCount
    public List<TagDTO> getAllTags(int page, int pageSize) {
        List<TagDTO> tags = new ArrayList<>();
        String sql = "SELECT t.tag_id, t.tag_name, t.description, t.IsActive, " +
                     "(SELECT COUNT(*) FROM Question_Tags qt WHERE qt.tag_id = t.tag_id) as questionCount, " +
                     "(SELECT COUNT(*) FROM TagFollow tf WHERE tf.tag_id = t.tag_id) as followerCount " +
                     "FROM Tags t ORDER BY t.tag_name ASC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TagDTO tag = mapResultSetToTagDTO(rs);
                    tags.add(tag);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tags;
    }

    // Lấy tất cả tags active (cho dropdown merge)
    public List<TagDTO> getAllActiveTags() {
        List<TagDTO> tags = new ArrayList<>();
        String sql = "SELECT tag_id, tag_name, description, IsActive, " +
                     "(SELECT COUNT(*) FROM Question_Tags qt WHERE qt.tag_id = t.tag_id) as questionCount, " +
                     "0 as followerCount " +
                     "FROM Tags t WHERE IsActive = 1 ORDER BY tag_name ASC";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TagDTO tag = mapResultSetToTagDTO(rs);
                tags.add(tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tags;
    }

    // Lấy tag theo ID
    public TagDTO getTagById(long tagId) {
        String sql = "SELECT t.tag_id, t.tag_name, t.description, t.IsActive, " +
                     "(SELECT COUNT(*) FROM Question_Tags WHERE tag_id = t.tag_id) as questionCount, " +
                     "(SELECT COUNT(*) FROM TagFollow WHERE tag_id = t.tag_id) as followerCount " +
                     "FROM Tags t WHERE t.tag_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, tagId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTagDTO(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tạo tag mới
    public boolean createTag(String tagName, String description) {
        String sql = "INSERT INTO Tags(tag_name, description, IsActive) VALUES (?, ?, 1)";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tagName.trim());
            ps.setString(2, description != null ? description.trim() : null);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update tag
    public boolean updateTag(long tagId, String tagName, String description, boolean isActive) {
        String sql = "UPDATE Tags SET tag_name = ?, description = ?, IsActive = ? WHERE tag_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tagName.trim());
            ps.setString(2, description != null ? description.trim() : null);
            ps.setBoolean(3, isActive);
            ps.setLong(4, tagId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Toggle status (Active <-> Inactive)
    public boolean toggleTagStatus(long tagId) {
        String sql = "UPDATE Tags SET IsActive = CASE WHEN IsActive = 1 THEN 0 ELSE 1 END WHERE tag_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, tagId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đếm tags với filter status
    public int getTagCountByStatus(String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Tags");
        if ("active".equals(status)) {
            sql.append(" WHERE IsActive = 1");
        } else if ("inactive".equals(status)) {
            sql.append(" WHERE IsActive = 0");
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

    // Lấy tags với pagination + filter status
    public List<TagDTO> getTagsByStatus(String status, int page, int pageSize) {
        List<TagDTO> tags = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT t.tag_id, t.tag_name, t.description, t.IsActive, " +
            "(SELECT COUNT(*) FROM Question_Tags qt WHERE qt.tag_id = t.tag_id) as questionCount, " +
            "(SELECT COUNT(*) FROM TagFollow tf WHERE tf.tag_id = t.tag_id) as followerCount " +
            "FROM Tags t"
        );

        if ("active".equals(status)) {
            sql.append(" WHERE t.IsActive = 1");
        } else if ("inactive".equals(status)) {
            sql.append(" WHERE t.IsActive = 0");
        }

        sql.append(" ORDER BY t.tag_name ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TagDTO tag = mapResultSetToTagDTO(rs);
                    tags.add(tag);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tags;
    }

    // Merge tags: chuyển tất cả questions và followers từ source sang target, rồi xóa source
    public boolean mergeTags(long sourceTagId, long targetTagId) {
        if (sourceTagId == targetTagId) {
            return false;
        }

        Connection con = null;
        try {
            con = db.getConnection();
            con.setAutoCommit(false);

            // 1. Update Question_Tags: source → target (skip duplicates)
            String updateQT = "UPDATE Question_Tags SET tag_id = ? " +
                              "WHERE tag_id = ? AND question_id NOT IN " +
                              "(SELECT question_id FROM Question_Tags WHERE tag_id = ?)";
            try (PreparedStatement ps = con.prepareStatement(updateQT)) {
                ps.setLong(1, targetTagId);
                ps.setLong(2, sourceTagId);
                ps.setLong(3, targetTagId);
                ps.executeUpdate();
            }

            // 2. Delete remaining Question_Tags with sourceId (duplicates)
            String deleteQT = "DELETE FROM Question_Tags WHERE tag_id = ?";
            try (PreparedStatement ps = con.prepareStatement(deleteQT)) {
                ps.setLong(1, sourceTagId);
                ps.executeUpdate();
            }

            // 3. Update TagFollow: source → target (skip duplicates)
            String updateTF = "UPDATE TagFollow SET tag_id = ? " +
                              "WHERE tag_id = ? AND user_id NOT IN " +
                              "(SELECT user_id FROM TagFollow WHERE tag_id = ?)";
            try (PreparedStatement ps = con.prepareStatement(updateTF)) {
                ps.setLong(1, targetTagId);
                ps.setLong(2, sourceTagId);
                ps.setLong(3, targetTagId);
                ps.executeUpdate();
            }

            // 4. Delete remaining TagFollow with sourceId
            String deleteTF = "DELETE FROM TagFollow WHERE tag_id = ?";
            try (PreparedStatement ps = con.prepareStatement(deleteTF)) {
                ps.setLong(1, sourceTagId);
                ps.executeUpdate();
            }

            // 5. Delete source tag
            String deleteTag = "DELETE FROM Tags WHERE tag_id = ?";
            try (PreparedStatement ps = con.prepareStatement(deleteTag)) {
                ps.setLong(1, sourceTagId);
                ps.executeUpdate();
            }

            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Search tags
    public List<TagDTO> searchTags(String keyword, int limit) {
        List<TagDTO> tags = new ArrayList<>();
        String sql = "SELECT TOP (?) t.tag_id, t.tag_name, t.description, t.IsActive, " +
                     "(SELECT COUNT(*) FROM Question_Tags qt WHERE qt.tag_id = t.tag_id) as questionCount, " +
                     "(SELECT COUNT(*) FROM TagFollow tf WHERE tf.tag_id = t.tag_id) as followerCount " +
                     "FROM Tags t WHERE t.tag_name LIKE ? OR t.description LIKE ? " +
                     "ORDER BY t.tag_name";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TagDTO tag = mapResultSetToTagDTO(rs);
                    tags.add(tag);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tags;
    }

    // Check tag name exists
    public boolean tagNameExists(String tagName) {
        String sql = "SELECT COUNT(*) FROM Tags WHERE tag_name = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tagName.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
=======
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
>>>>>>> Mai
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
<<<<<<< HEAD

    // Check tag name exists excluding current tag (for edit validation)
    public boolean tagNameExistsExcluding(String tagName, long excludeId) {
        String sql = "SELECT COUNT(*) FROM Tags WHERE tag_name = ? AND tag_id != ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tagName.trim());
            ps.setLong(2, excludeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to map ResultSet to TagDTO
    private TagDTO mapResultSetToTagDTO(ResultSet rs) throws Exception {
        TagDTO tag = new TagDTO();
        tag.setTagId(rs.getLong("tag_id"));
        tag.setTagName(rs.getString("tag_name"));
        tag.setDescription(rs.getString("description"));
        tag.setActive(rs.getBoolean("IsActive"));
        tag.setQuestionCount(rs.getInt("questionCount"));
        tag.setFollowerCount(rs.getInt("followerCount"));
        return tag;
    }
=======
    
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
    
       
>>>>>>> Mai
}
