package panel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class AssDriver {
    public static void main(String[] args) throws IOException, SQLException {
        JFrame frame = new JFrame("Academic Support System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CardLayout cardLayout = new CardLayout();
        JPanel cardPane = new JPanel();
        cardPane.setLayout(cardLayout);

        HomePanel home = new HomePanel(cardPane);
        UploadPanel upload = new UploadPanel(cardPane);
        EditPanel edit = new EditPanel(cardPane);
        HistoryPanel history = new HistoryPanel(cardPane);
        SettingPanel setting = new SettingPanel(cardPane);
        ContactsPanel contacts = new ContactsPanel(cardPane);

        cardPane.add(home, "Home");
        cardPane.add(upload, "Upload");
        cardPane.add(edit, "Edit");
        cardPane.add(history, "History");
        cardPane.add(setting, "Settings");
        cardPane.add(contacts, "Contacts");
        frame.add(cardPane);
        //Systems with high DPI and more then 100% scaling will not utilise the proper minimum size()
        //target system is 1920x1080 displays, a typical academic workstation
        frame.setMinimumSize(new Dimension(622, 350)); // minimum size 16:9
        frame.setSize(1280, 720); //Default to 16:9 HD
        frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
        frame.setVisible(true);
    }
}
