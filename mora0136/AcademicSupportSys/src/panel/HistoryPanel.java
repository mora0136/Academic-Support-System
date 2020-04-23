package panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HistoryPanel extends JPanel {
    CardLayout cardLayout;
    JPanel cardPane;

    HistoryPanel(JPanel pane){
        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();
        JTextArea testMessage = new JTextArea("This is the History Panel");
        JButton back = new JButton("back");
        back.addActionListener(this::actionPerformed);
        add(testMessage);
        add(back);
    }
    public void actionPerformed(ActionEvent e){
        cardLayout.show(cardPane, "Home");
    }
}