import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditPanel extends JPanel {
    CardLayout cardLayout;
    JPanel cardPane;

    EditPanel(JPanel pane){
        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();
        JTextArea testMessage = new JTextArea("This is the Edit Panel");
        JButton back = new JButton("back");
        back.addActionListener(this::actionPerformed);
        add(testMessage);
        add(back);
    }
    public void actionPerformed(ActionEvent e){
        cardLayout.show(cardPane, "Home");
    }
}