import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePanel extends JPanel implements ActionListener {
    CardLayout cardLayout;
    JPanel cardPane;
    JTextArea testMessage;
    JButton upload, edit, history, setting, contacts;
    ButtonPanel home = new ButtonPanel();
    HomePanel(JPanel pane){
        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();

        JTextArea testMessage = new JTextArea("This is the Home Panel");
        upload = new JButton("Upload");
        edit = new JButton("Edit");
        history = new JButton("History");
        setting = new JButton("Settings");
        contacts = new JButton("Contacts");

        upload.addActionListener(this);
        edit.addActionListener(this);
        history.addActionListener(this);
        setting.addActionListener(this);
        contacts.addActionListener(this);

        add(testMessage);
        add(upload);
        add(edit);
        add(history);
        add(setting);
        add(contacts);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == upload){
            cardLayout.show(cardPane, "Upload");
        }else if(e.getSource() == edit){
            cardLayout.show(cardPane, "Edit");
        }else if(e.getSource() == history){
            cardLayout.show(cardPane, "History");
        }else if(e.getSource() == setting){
            cardLayout.show(cardPane, "Setting");
        }else if(e.getSource() == contacts){
            cardLayout.show(cardPane, "Contacts");
        }else{
            System.out.print("error action not recognised");
        }
    }
}

//Since all 5 buttons will have same structure, this class will create an instance of them.
//Could also just do a method to manually create and return the panel data.
class ButtonPanel{

}
