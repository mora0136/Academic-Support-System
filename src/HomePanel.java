import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;

public class HomePanel extends JPanel implements ActionListener {
    JPanel cardPane, buttonGroupTop, buttonGroupBottom;
    CardLayout cardLayout;
    //Instance variables of image and button allow for resizing to change dimenions as per window size
    Image uploadImg, editImg, historyImg, settingImg, contactsImg;
    JButton upload, edit, history, setting, contacts;
    HomePanel(JPanel pane){
        buttonGroupTop = new JPanel();
        buttonGroupBottom = new JPanel();
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();

        c.fill = GridBagConstraints.BOTH;
        buttonGroupTop.setLayout(gridBag);
        buttonGroupBottom.setLayout(gridBag);
        setLayout(gridBag);

        //A padding of size dimensions surrounds each component that is added with to gridBag with this constraint
        c.insets = new Insets(20, 50, 20, 50);

        try {
            uploadImg = ImageIO.read(new File("resources/upload.png"));
            editImg = ImageIO.read(new File("resources/edit.png"));
            historyImg = ImageIO.read(new File("resources/history.png"));
            contactsImg = ImageIO.read(new File("resources/contacts.png"));
            settingImg = ImageIO.read(new File("resources/setting.png"));
        }catch(IOException io){
            System.out.println(io);
        }

        //note that buttons are assigned and to JButton variable and then added to JPanel
        buttonGroupTop.add(upload = createButton(uploadImg, "Upload", gridBag, c));
        buttonGroupTop.add(edit = createButton(editImg, "Edit", gridBag, c));
        buttonGroupTop.add(history = createButton(historyImg, "History", gridBag, c));
        buttonGroupBottom.add(contacts = createButton(contactsImg, "Contacts", gridBag, c));
        buttonGroupBottom.add(setting = createButton(settingImg, "Setting", gridBag, c));

        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(buttonGroupTop, c);
        add(buttonGroupTop);
        c.gridwidth= GridBagConstraints.REMAINDER;
        gridBag.setConstraints(buttonGroupBottom,c);
        add(buttonGroupBottom);


        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                System.out.println("x="+getWidth()+", y="+getHeight());
                GridBagConstraints c2 = new GridBagConstraints();
                int font = 32;
                if(getWidth() <= 300 || getHeight() <= 300){
                    font = 0;
                }else if(getWidth() <= 600 || getHeight() <= 600){
                    font = 16;
                }
                if(getWidth()<1200 && getHeight()<600){
                    int height = (int)(getHeight()*0.333);
                    int width = (int)(getWidth()*0.16);
                    c2.insets = new Insets((int)(getHeight()*0.0333), (int)(getWidth()*0.0416), (int)(getHeight()*0.0333), (int)(getWidth()*0.0416));
                    resizeIcon(upload, uploadImg, width, height, font, gridBag, c2);
                    resizeIcon(edit, editImg, width, height, font, gridBag, c2);
                    resizeIcon(history, historyImg, width, height, font, gridBag, c2);
                    resizeIcon(contacts, contactsImg, width, height, font, gridBag, c2);
                    resizeIcon(setting, settingImg, width, height, font, gridBag, c2);

                }else if(getWidth() < 1200){
                    int width = (int)(getWidth()*0.16);
                    c2.insets = new Insets(20, (int)(getWidth()*0.0416), 20, (int)(getWidth()*0.0416));
                    resizeIcon(upload, uploadImg, width, width, font, gridBag, c2);
                    resizeIcon(edit, editImg, width, width, font, gridBag, c2);
                    resizeIcon(history, historyImg, width, width, font, gridBag, c2);
                    resizeIcon(contacts, contactsImg, width, width, font, gridBag, c2);
                    resizeIcon(setting, settingImg, width, width, font, gridBag, c2);
                }else if(getHeight() < 600){
                    int height = (int)(getHeight()*0.333);
//                    int font = 16;//(int)(16/(600/getHeight()));
                    c2.insets = new Insets((int)(getHeight()*0.0333), 50, (int)(getHeight()*0.0333), 50);
                    resizeIcon(upload, uploadImg, height, height, font, gridBag, c2);
                    resizeIcon(edit, editImg, height, height, font, gridBag, c2);
                    resizeIcon(history, historyImg, height, height, font, gridBag, c2);
                    resizeIcon(contacts, contactsImg, height, height, font, gridBag, c2);
                    resizeIcon(setting, settingImg, height, height, font, gridBag, c2);
                }else{
                    c2.insets = new Insets(20, 50, 20, 50);
                    resizeIcon(upload, uploadImg, 200, 200, font, gridBag, c2);
                    resizeIcon(edit, editImg, 200, 200, font, gridBag, c2);
                    resizeIcon(history, historyImg, 200, 200, font, gridBag, c2);
                    resizeIcon(contacts, contactsImg, 200, 200, font, gridBag, c2);
                    resizeIcon(setting, settingImg, 200, 200, font, gridBag, c2);
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e){
        switch(e.getActionCommand()){
            case "Upload":
                cardLayout.show(cardPane, "Upload");
            case "Edit":
                cardLayout.show(cardPane, "Edit");
            case "History":
                cardLayout.show(cardPane, "History");
            case "Contacts":
                cardLayout.show(cardPane, "Contacts");
            case "Setting":
                cardLayout.show(cardPane, "Setting");
            default:
                System.out.print("error action not recognised");
        }
    }

    private void resizeIcon(JButton btn,Image img, int width, int height, int font, GridBagLayout gridBag, GridBagConstraints c){
        img = img.getScaledInstance(Integer.min(width, height), -1, Image.SCALE_DEFAULT);
        btn.setIcon(new ImageIcon(img));
        btn.setMargin(new Insets(0, (int)(width*0.25), 0, (int)(width*0.25)));
        btn.setFont(new Font("Arial", Font.PLAIN, font));
        gridBag.setConstraints(btn,c);
    }

    private JButton createButton(Image img, String buttonLabel, GridBagLayout gridBag, GridBagConstraints c){
        JButton btn = new JButton(buttonLabel);
        //width and height represent pixels, setting either to -1 will size by aspect ratio
        img = img.getScaledInstance(200, -1, Image.SCALE_DEFAULT);
        btn.setIcon(new ImageIcon(img));

        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setFont(new Font("Arial", Font.PLAIN, 32));
        btn.setMargin(new Insets(0, 50, 0, 50));
        btn.setFocusPainted(false);

        btn.addActionListener(this);
        gridBag.setConstraints(btn, c);
        return btn;
    }
}