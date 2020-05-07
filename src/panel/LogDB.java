package panel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LogDB {
    //return of zero means a successful log was created

    LogDB(){
        //open db, save connection conn
    }

    //Logging upload interactions
    static int logSavedUpload(){
        return 0;
    }

    static int logUploadedUpload(){
        return 0;
    }
    static int logDeletedUpload(){
        return 0;
    }

    //Logging contact interactions
    static int logSavedContact(){
        return 0;
    }

    static int logDeletedContact(){
        return 0;
    }

    static Connection connect() {
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
