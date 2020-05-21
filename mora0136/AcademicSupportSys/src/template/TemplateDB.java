package template;

import javax.swing.*;
import java.sql.*;

/*
 * Much like LogDB and UploadDB, this is a simple talker between the GUI and database for template statements.
 */
public final class TemplateDB {

    private TemplateDB(){

    }
    public static DefaultListModel<Template> getTemplates(){
        DefaultListModel<Template> list = new DefaultListModel<>();
        String sql = "SELECT * FROM template WHERE isDeleted = false";

        try (Connection conn = connect()) {
            PreparedStatement pstmtUpload = conn.prepareStatement(sql);
            ResultSet rs = pstmtUpload.executeQuery();
            while(rs.next()){
                list.addElement(new Template(rs));
            }

        }catch(SQLException e){
            System.out.println(e+" Error getting");
        }
        return list;
    }

    public static void addTemplate(String statement){
        String sql = "INSERT INTO template(statement) VALUES (?)";

        try (Connection conn = connect()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, statement);
            pstmt.executeUpdate();

        }catch(SQLException e){
            System.out.println(e+" Error adding");
        }
    }

    public static void editTemplate(int templateID, String statement){
        String sql = "UPDATE template SET statement = ? WHERE templateID = ?";

        try (Connection conn = connect()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, statement);
            pstmt.setInt(2, templateID);
            pstmt.executeUpdate();

        }catch(SQLException e){
            System.out.println(e+" Error editing");
        }
    }

    public static void deleteTemplate(Template t){
        String sql = "UPDATE template SET isDeleted = true WHERE templateID = ?";

        try (Connection conn = connect()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, t.getTemplateID());
            pstmt.executeUpdate();

        }catch(SQLException e){
            System.out.println(e+" Error deleting");
        }
    }

    private static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:data/database.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
