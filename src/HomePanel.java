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
        buttonGroupTop = new JPanel();
        buttonGroupBottom = new JPanel();

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        buttonGroupTop.setLayout(gridBag);
        buttonGroupBottom.setLayout(gridBag);
        setLayout(gridBag);

        c.insets = new Insets(20, 80, 20, 80);

        upload = createButton("resources/upload.png", "Upload");
        gridBag.setConstraints(upload, c);
        buttonGroupTop.add(upload);

        edit = createButton("resources/edit.png", "Edit");
        gridBag.setConstraints(edit, c);
        buttonGroupTop.add(edit);

        c.gridwidth = GridBagConstraints.REMAINDER;
        history = createButton("resources/history.png", "History");
        gridBag.setConstraints(history, c);
        buttonGroupTop.add(history);

        c.gridwidth = GridBagConstraints.RELATIVE;
        contacts = createButton("resources/contacts.png", "Contacts");
        gridBag.setConstraints(contacts, c);
        buttonGroupBottom.add(contacts);

        c.gridwidth = GridBagConstraints.REMAINDER;
        setting = createButton("resources/setting.png", "Setting");
        gridBag.setConstraints(setting, c);
        buttonGroupBottom.add(setting);

        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(buttonGroupTop, c);
        add(buttonGroupTop);

        c.gridwidth= GridBagConstraints.REMAINDER;
        gridBag.setConstraints(buttonGroupBottom,c);
        add(buttonGroupBottom);
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
            btn.setMargin(new Insets(0, 50, 0, 50));
        }catch(IOException io){
            System.out.println(io);
            //popUp Message?
            //DialogBox?
            //Info Bottom left corner like internet browser?
        }
        return btn;
    }
}