import javax.swing.*;
import java.awt.event.ActionEvent;

public class ContactsPanel extends JPanel{
    ContactsPanel(){
        JTextArea testMessage = new JTextArea("This is the Contact Panel");
        JButton back = new JButton("back");
        back.addActionListener(this::actionPerformed);
        add(testMessage);
        add(back);
    }
    public void actionPerformed(ActionEvent e){

    }
}
