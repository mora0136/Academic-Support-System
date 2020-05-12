package log;

import contacts.ContactDB;
import upload.UploadDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Log {
    int logID;
    LocalDate date;
    String type;
    String action;
    Object data; //Either Upload or Contact
//    String description;

    public Object getData() {
        return data;
    }

    public void setData(Object at) {
        this.data = at;
    }

    //Deprecated
//    Log(int logID, LocalDate time, String type, String action, String description){
//        this.logID = logID;
//        this.type = type;
//        this.date = time;
//        this.action = type;
//        this.description = description;
//    }

    public Log(ResultSet rs) throws SQLException {
        this.logID = rs.getInt("log_ID");
        this.date = rs.getDate("date").toLocalDate();
        this.type = rs.getString("type");
        this.action = rs.getString("action");

        //Find associated data for its particular type
        if(this.type.equals("Upload")){
            int uploadID = rs.getInt("associate_ID");
//            actionType = new Upload(uploadID, "Temp");
            data = UploadDB.getUpload(uploadID);
//            description = data.toString();
        }else {
            int contactID = rs.getInt("associate_ID");
            ContactDB db = new ContactDB();
            data = db.getContactDetails(contactID);
//            description = data.toString();
//            System.out.println("Description");
        }
    }
    public int getLogID() {
        return logID;
    }

    public void setLogID(int logID) {
        this.logID = logID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }

    public String toString(){
        return String.valueOf(logID);
    }
}
