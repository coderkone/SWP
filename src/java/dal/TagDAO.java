/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;
import model.Tag;
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
    public List<Tag> getAllTags(){
        List<Tag> list = new ArrayList<>();
        String sql = "SELECT tag_id, tag_name, description, IsActive " +
                     "FROM Tags WHERE IsActive = 1 " +
                     "ORDER BY tag_name ASC";
        try{
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Tag tag = new Tag();
                tag.setTagId(rs.getInt("tag_id"));
                tag.setTagName(rs.getString("tag_name"));
                tag.setDescription(rs.getString("description"));
                tag.setIsActive(rs.getBoolean("IsActive"));
                list.add(tag);
            }
            
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error" + e.getMessage());
        }
        return list;
    }
    public List<Tag> searchTags(String keyword){
        List<Tag> list = new ArrayList<>();
        String sql = "SELECT tag_id, tag_name, description, IsActive " + 
                     "FROM Tags " +
                     "WHERE IsActive = 1 " +
                     "And tag_name LIKE ? " +
                     "ORDER BY tag_name ASC";
        try{
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, "%" + keyword + "%");
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Tag tag = new Tag();
                tag.setTagId(rs.getInt("tag_id"));
                tag.setTagName(rs.getString("tag_name"));
                tag.setDescription(rs.getString("description"));
                tag.setIsActive(rs.getBoolean("IsActive"));
                list.add(tag);
            }
            
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error" + e.getMessage());
        }
        return list;
        
    }
    public List<Tag> sortByName(){
        List<Tag> list = new ArrayList<>();
        String sql = "SELECT tag_id, tag_name, description, IsActive " + 
                     "FROM Tags " +
                     "WHERE IsActive = 1 " +
                     "ORDER BY tag_name ASC";
        try{
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Tag tag = new Tag();
                tag.setTagId(rs.getInt("tag_id"));
                tag.setTagName(rs.getString("tag_name"));
                tag.setDescription(rs.getString("description"));
                tag.setIsActive(rs.getBoolean("IsActive"));
                list.add(tag);
            }
            
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
        }
        return list;
            
    }
    public List<Tag> sortByNewest(){
        List<Tag> list = new ArrayList<>();
        String sql = "SELECT tag_id, tag_name, description, IsActive " + 
                     "FROM Tags " +
                     "WHERE IsActive = 1 " +
                     "ORDER BY tag_id DESC";
        try{
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Tag tag = new Tag();
                tag.setTagId(rs.getInt("tag_id"));
                tag.setTagName(rs.getString("tag_name"));
                tag.setDescription(rs.getString("description"));
                tag.setIsActive(rs.getBoolean("IsActive"));
                list.add(tag);
            }
            
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
        }
        return list;
    }
    public List<Tag> sortByPopular(){
        List<Tag> list = new ArrayList<>();
        String sql = "SELECT t.tag_id, t.tag_name, t.description, t.IsActive, "
               + "COUNT(qt.question_id) AS question_count "
               + "FROM Tags t "                         
               + "LEFT JOIN Question_Tags qt "
               + "ON t.tag_id = qt.tag_id "
               + "WHERE t.IsActive = 1 "                
               + "GROUP BY t.tag_id, t.tag_name, t.description, t.IsActive "
               + "ORDER BY question_count DESC, t.tag_name ASC";
        try{
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Tag tag = new Tag();
                tag.setTagId(rs.getInt("tag_id"));
                tag.setTagName(rs.getString("tag_name"));
                tag.setDescription(rs.getString("description"));
                tag.setIsActive(rs.getBoolean("IsActive"));
                list.add(tag);
            }
            
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
        }
        return list;
    }
}
