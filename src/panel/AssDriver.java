package panel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class AssDriver {
    public static void main(String[] args) throws IOException {
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
        cardPane.add(setting, "Setting");
        cardPane.add(contacts, "Contacts");
        frame.add(cardPane);
        cardLayout.show(cardPane, "Upload"); //Testing
        frame.setMinimumSize(new Dimension(350, 350));
        frame.setSize(1000, 700);
        frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
        frame.setVisible(true);
    }
}
