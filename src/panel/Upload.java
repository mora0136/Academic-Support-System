package panel;

public class Upload {
    int uploadID;
    String title;

    public Upload(int uploadID, String title){
        this.uploadID = uploadID;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUploadID() {
        return uploadID;
    }

    public void setUploadID(int uploadID) {
        this.uploadID = uploadID;
    }

    public String toString(){
        return title;
    }
}
