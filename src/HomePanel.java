import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HomePanel extends JPanel {
    CardLayout cardLayout;
    JPanel cardPane;
    HomePanel(JPanel pane){
        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();

        JTextArea testMessage = new JTextArea("This is the Home Panel");
        JButton upload = new JButton("Upload");
        JButton edit = new JButton("Edit");
        JButton history = new JButton("History");
        JButton setting = new JButton("Settings");
        JButton contacts = new JButton("Contacts");

        upload.addActionListener(this::actionPerformed);
//        edit.addActionListener(this::actionPerformed);
//        history.addActionListener(this::actionPerformed);
//        setting.addActionListener(this::actionPerformed);
//        contacts.addActionListener(this::actionPerformed);

        add(testMessage);
        add(upload);
        add(edit);
        add(history);
        add(setting);
        add(contacts);
    }

    public void actionPerformed(ActionEvent e){
        cardLayout.show(cardPane, "Upload");
    }
}
