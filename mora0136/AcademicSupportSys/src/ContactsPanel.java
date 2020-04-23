import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class ContactsPanel extends JPanel {
    CardLayout cardLayout;
    JPanel cardPane, left, right, leftTop, leftMiddle, rightTop, rightBottom, searchPanel;
    Image backImg, searchImg;
    JButton back, search, addNew;

    ContactsPanel(JPanel pane) throws IOException {
        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();

        left = new JPanel();
        right = new JPanel();
        leftTop = new JPanel();
        leftMiddle = new JPanel();
        rightTop = new JPanel();
        rightBottom = new JPanel();

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        //Set the frame to 1x2 grid(left and right Panels)
        setLayout(new GridLayout(1, 2));

//        JButton back = new JButton("back");
        backImg = ImageIO.read(new File("resources/back.png"));
//        backImg = backImg.getScaledInstance(50, -1, Image.SCALE_DEFAULT);
//        back.setIcon(new ImageIcon(backImg));
//        back.setVerticalTextPosition(SwingConstants.BOTTOM);
//        back.setHorizontalTextPosition(SwingConstants.CENTER);
//        back.setFont(new Font("Arial", Font.PLAIN, 16));
//        back.setFocusPainted(false);
        back = createButton(backImg, "Back", 50);

        JTextField searchField = new JTextField("Search...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 32));
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new FocusListener() {
            //Note that whatever was in the box previously is erased
            public void focusGained(FocusEvent e) {
                searchField.setText("");
                searchField.setForeground(Color.BLACK);
            }
            public void focusLost(FocusEvent e) {
                if(searchField.getText().isEmpty()){
                    searchField.setText("Search...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        JButton search = new JButton("Search");
        searchImg = ImageIO.read(new File("resources/search.png"));
        searchImg = searchImg.getScaledInstance(40, -1, Image.SCALE_DEFAULT);
        search.setIcon(new ImageIcon(searchImg));
        search.setVerticalTextPosition(SwingConstants.BOTTOM);
        search.setHorizontalTextPosition(SwingConstants.CENTER);
        search.setFont(new Font("Arial", Font.PLAIN, 16));
        search.setFocusPainted(false);
//        search = createButton(searchImg, "Search", 50);
        search.setMnemonic(KeyEvent.VK_ENTER);
        //Action Event needed, either make exclusive to search or could auto do it as entering text

        JButton addNew = new JButton("Add New");
        searchImg = ImageIO.read(new File("resources/add.png"));
        searchImg = searchImg.getScaledInstance(40, -1, Image.SCALE_DEFAULT);
        addNew.setIcon(new ImageIcon(searchImg));
//        search.setVerticalTextPosition(SwingConstants.BOTTOM);
//        search.setHorizontalTextPosition(SwingConstants.CENTER);
        addNew.setFont(new Font("Arial", Font.PLAIN, 16));
        addNew.setFocusPainted(false);

        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");
        back.addActionListener(this::actionPerformed);

        left.setLayout(new BorderLayout());
        right.setLayout(new BorderLayout());
        leftTop.setLayout(gridBag);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        gridBag.setConstraints(back, c);
        leftTop.add(back);
        c.insets = new Insets(10, 10, 10, 0);
        c.weightx = 25;
        gridBag.setConstraints(searchField, c);
        leftTop.add(searchField);
        c.insets = new Insets(10, 0, 10, 10);
        c.weightx = 2;
        gridBag.setConstraints(search, c);
        leftTop.add(search);
        leftTop.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));


        rightTop.add(new JTextArea("temp"));
        rightBottom.setLayout(new GridLayout(1, 2));
        rightBottom.add(edit);
        rightBottom.add(delete);

        left.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        right.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        left.add(leftTop, BorderLayout.NORTH);
        left.add(leftMiddle, BorderLayout.CENTER);
        left.add(addNew, BorderLayout.SOUTH);

        right.add(rightTop, BorderLayout.NORTH);
        right.add(rightBottom, BorderLayout.SOUTH);

        add(left);
        add(right);

        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                GridBagConstraints c2 = new GridBagConstraints();
                int font = 16;
                if(windowWidth <= 600 || windowHeight <= 400){
                    font = 0;
                }
                if(windowWidth < 800){
                    int width = (int)(windowWidth*0.0625);
//                    c2.insets = new Insets()
                    resizeIcon(back, backImg, width, width, font, gridBag, c2);
                    resizeIcon(search, searchImg, width, width, font, gridBag, c2);
                    searchField.setFont(new Font("Arial", Font.PLAIN, Integer.max(font*2, 16)));
                }else{
                    resizeIcon(back, backImg, 50, 50, font, gridBag, c2);
                    resizeIcon(search, searchImg, 50, 50, font, gridBag, c2);
                    searchField.setFont(new Font("Arial", Font.PLAIN, Integer.max(font*2, 16)));
                }
            }
        });
    }
    private void resizeIcon(JButton btn,Image img, int width, int height, int font, GridBagLayout gridBag, GridBagConstraints c){
        img = img.getScaledInstance(Integer.min(width, height), -1, Image.SCALE_DEFAULT);
        btn.setIcon(new ImageIcon(img));
//        btn.setMargin(new Insets(0, (int)(width*0.25), 0, (int)(width*0.25)));
        btn.setFont(new Font("Arial", Font.PLAIN, font));
        gridBag.setConstraints(btn,c);
    }

    private JButton createButton(Image img, String buttonLabel, int width){
        JButton btn = new JButton(buttonLabel);
        //width and height represent pixels, setting either to -1 will size by aspect ratio
        img = img.getScaledInstance(width, -1, Image.SCALE_DEFAULT);
        btn.setIcon(new ImageIcon(img));

        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        return btn;
    }

    public void actionPerformed(ActionEvent e){
        cardLayout.show(cardPane, "Home");
    }

}