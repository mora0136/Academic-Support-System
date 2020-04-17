import javax.swing.*;
import java.awt.event.ActionEvent;

public class EditPanel extends JPanel {
    EditPanel(){
        JTextArea testMessage = new JTextArea("This is the Edit Panel");
        JButton back = new JButton("back");
        back.addActionListener(this::actionPerformed);
        add(testMessage);
        add(back);
    }
    public void actionPerformed(ActionEvent e){

    }
}
