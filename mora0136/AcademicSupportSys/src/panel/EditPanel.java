package panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditPanel extends JPanel {
    CardLayout cardLayout;
    JPanel cardPane;

    EditPanel(JPanel pane){
        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();
        JButton back = new JButton("back");
        back.addActionListener(this::actionPerformed);
        add(back);
    }
    public void actionPerformed(ActionEvent e){
        cardLayout.show(cardPane, "Home");
    }
}