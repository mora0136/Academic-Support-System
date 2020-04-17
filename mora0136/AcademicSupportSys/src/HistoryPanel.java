import javax.swing.*;
import java.awt.event.ActionEvent;

public class HistoryPanel extends JPanel{
    HistoryPanel(){
        JTextArea testMessage = new JTextArea("This is the History Panel");
        JButton back = new JButton("back");
        back.addActionListener(this::actionPerformed);
        add(testMessage);
        add(back);
    }

    public void actionPerformed(ActionEvent e){

    }
}
