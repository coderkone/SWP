package control;

import config.DBContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/test/db")
public class TestDBController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html><head><title>DB Test</title><style>body{font-family:Arial;margin:20px;}</style></head><body>");
        out.println("<h2>Database Connectivity Test</h2>");
        
        try {
            out.println("<p>1. Creating DBContext...</p>");
            DBContext db = new DBContext();
            out.println("<p style='color:green;'>✓ DBContext created</p>");
            
            out.println("<p>2. Getting connection...</p>");
            Connection con = db.getConnection();
            out.println("<p style='color:green;'>✓ Connection: " + (con != null ? "OK" : "NULL") + "</p>");
            
            if (con != null) {
                out.println("<p>3. Creating statement...</p>");
                Statement st = con.createStatement();
                out.println("<p style='color:green;'>✓ Statement created</p>");
                
                out.println("<p>4. Querying Questions count...</p>");
                ResultSet rs = st.executeQuery("SELECT COUNT(*) as cnt FROM Questions");
                if (rs.next()) {
                    int count = rs.getInt("cnt");
                    out.println("<p style='color:green;'>✓ Questions in DB: <strong>" + count + "</strong></p>");
                    
                    if (count == 0) {
                        out.println("<p style='color:red;'><strong>⚠ No sample data!</strong><br/>Insert test data using the provided SQL script.</p>");
                    } else {
                        out.println("<p><a href='/DevQuery/question/detail?id=1'>Try Question #1</a></p>");
                    }
                }
                con.close();
            }
            
        } catch (Exception e) {
            out.println("<p style='color:red;'><strong>✗ ERROR</strong></p>");
            out.println("<p><strong>" + e.getClass().getSimpleName() + ":</strong> " + e.getMessage() + "</p>");
            out.println("<pre style='background:#f5f5f5;padding:10px;border:1px solid #ccc;'>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
        
        out.println("</body></html>");
    }
}
