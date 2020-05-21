package template;

import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * A class to store simple information about a template statement.
 */

public class Template {
    int templateID;
    String statement;

    public Template(ResultSet rs){
        try {
            templateID = rs.getInt("templateID");
            statement = rs.getString("statement");
        }catch(SQLException e){
            System.out.println(e+" Error creating Template");
        }
    }

    public Template(int id, String text){
        templateID = id;
        statement = text;
    }

    public int getTemplateID(){
        return templateID;
    }

    public String getStatement(){
        return statement;
    }

    public String toString(){
        return statement;
    }
}
