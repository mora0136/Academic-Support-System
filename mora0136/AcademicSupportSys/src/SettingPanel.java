import javax.swing.*;
import java.awt.event.ActionEvent;

public class SettingPanel extends JPanel{
    SettingPanel(){
        JTextArea testMessage = new JTextArea("This is the Setting Panel");
        JButton back = new JButton("back");
        back.addActionListener(this::actionPerformed);
        add(testMessage);
        add(back);
    }
    public void actionPerformed(ActionEvent e){

    }
}
