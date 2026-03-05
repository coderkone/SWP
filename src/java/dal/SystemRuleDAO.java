/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import model.SystemRule;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import config.DBContext;

public class SystemRuleDAO extends DBContext {
    public List<SystemRule> getAllRules(){
        List<SystemRule> list = new ArrayList<>();
        String sql = "SELECT * FROM System_Rules ORDER BY created_at DESC";
        try{
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                SystemRule sr = new SystemRule();
                sr.setRuleId(rs.getLong("rule_id"));
                sr.setTitle(rs.getString("title"));
                sr.setContent(rs.getString("content"));
                sr.setCreatedAt(rs.getTimestamp("created_at"));
                sr.setUpdatedAt(rs.getTimestamp("updated_at"));
                sr.setCreatedBy(rs.getLong("created_by"));
                
                long updatedBy = rs.getLong("updated_by");
                if(rs.wasNull()){
                    sr.setUpdatedBy(null);
                }else{
                    sr.setUpdatedBy(updatedBy);
                }
                list.add(sr);
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error"+e.getMessage());
        }
        return list;
    }
}
