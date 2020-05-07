package panel;

public class Log {
    int logID;
    String time;
    String type;
    String description;

    Log(int logID, String time, String type, String description){
        this.logID = logID;
        this.time = time;
        this.type = type;
        this.description = description;
    }
    public int getLogID() {
        return logID;
    }

    public void setLogID(int logID) {
        this.logID = logID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString(){
        return String.valueOf(logID);
    }
}
