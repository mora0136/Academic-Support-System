import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Flow;

public class HomePanel extends JPanel implements ActionListener {
    CardLayout cardLayout;
    JPanel cardPane;
    JTextArea testMessage;
    JButton upload, edit, history, setting, contacts;
    JPanel buttonGroup, buttonGroupTop, buttonGroupBottom;
    HomePanel(JPanel pane) throws IOException {
        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();
        buttonGroup = new JPanel();
        buttonGroupTop = new JPanel();
        buttonGroupBottom = new JPanel();
//        setLayout(new GridLayout(2, 1));
//        buttonGroup.setLayout(new FlowLayout(FlowLayout.RIGHT));

        upload = createButton("resources/upload.png", "Upload");
        buttonGroupTop.add(upload);

        edit = createButton("resources/edit.png", "Edit");
        buttonGroupTop.add(edit);

        history = createButton("resources/history.png", "History");
        buttonGroupTop.add(history);

        contacts = createButton("resources/contacts.png", "Contacts");
        buttonGroupBottom.add(contacts);

        setting = createButton("resources/setting.png", "Setting");
        buttonGroupBottom.add(setting);
        buttonGroupTop.setLayout(new BoxLayout(buttonGroupTop, BoxLayout.X_AXIS));
        buttonGroupBottom.setLayout(new BoxLayout(buttonGroupBottom, BoxLayout.X_AXIS));
        buttonGroup.add(buttonGroupTop);
        buttonGroup.add(buttonGroupBottom);
        buttonGroup.setLayout(new BoxLayout(buttonGroup, BoxLayout.Y_AXIS));
        add(buttonGroup);
//        setLayout(new FlowLayout(FlowLayout.CENTER));
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

    private JButton createButton(String fileName, String buttonName){
        JPanel button;
        JButton btn = new JButton(buttonName);
        Image img;
        try {
            img = ImageIO.read(new File(fileName));
            //width and height represent pixels, setting either to -1 will size by aspect ratio
            img = img.getScaledInstance(200, -1, Image.SCALE_DEFAULT);

            btn.setIcon(new ImageIcon(img));
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.addActionListener(this);
            btn.setFont(new Font("Arial", Font.PLAIN, 32));
            btn.setBackground(null);
            btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        }catch(IOException io){
            System.out.println(io);
            //popUp Message?
            //DialogBox?
            //Info Bottom left corner like internet browser?
        }
        return btn;
    }
}