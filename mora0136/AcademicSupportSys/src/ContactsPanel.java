import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ContactsPanel extends JPanel {
    CardLayout cardLayout;
    JPanel cardPane, left, right, leftTop, leftMiddle, rightTop, rightBottom;

    ContactsPanel(JPanel pane){
        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();

        left = new JPanel();
        right = new JPanel();
        leftTop = new JPanel();
        leftMiddle = new JPanel();
        rightTop = new JPanel();
        rightBottom = new JPanel();

        //Set the frame to 1x2 grid(left and right Panels)
        setLayout(new GridLayout(1, 2));

        JTextArea testMessage = new JTextArea("This is the Contacts Panel");
        JButton back = new JButton("back");
        JButton temp = new JButton("testing btn");
        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");
        back.addActionListener(this::actionPerformed);

        left.setLayout(new BorderLayout());
        right.setLayout(new BorderLayout());
        leftTop.add(back);
        leftMiddle.add(testMessage);

        rightTop.add(new JTextArea("temp"));
        rightBottom.setLayout(new GridLayout(1, 2));
        rightBottom.add(edit);
        rightBottom.add(delete);

        left.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        right.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        left.add(leftTop, BorderLayout.NORTH);
        left.add(leftMiddle, BorderLayout.CENTER);
        left.add(temp, BorderLayout.SOUTH);

        right.add(rightTop, BorderLayout.NORTH);
        right.add(rightBottom, BorderLayout.SOUTH);

        add(left);
        add(right);

        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){

            }
        });
    }
    public void actionPerformed(ActionEvent e){
        cardLayout.show(cardPane, "Home");
    }
}