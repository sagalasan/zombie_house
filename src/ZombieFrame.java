import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Jalen on 9/9/2015.
 */
public class ZombieFrame extends JFrame{
  //have mouse listeners keyboard listeners
  //frame
  ZombiePanel panel;
  JButton button;
  JButton exit;
  JLabel welcomeText;
  ImageIcon buttonBackground;
  ImageIcon headerBackground;



  public ZombieFrame()
  {
    this.setLayout(new BorderLayout());

    //this.setContentPane(new JLabel(new ImageIcon("tile_images/background.png")));
    JLabel background = new JLabel(new ImageIcon(("tile_images/background.png")));
    background.setLayout( new BorderLayout());
    this.setContentPane( background );
    background.setOpaque(false);

    buttonBackground = new ImageIcon("tile_images/game_start.gif");
    headerBackground = new ImageIcon("tile_images/header.gif");


    button = new JButton("");
    button.setIcon(buttonBackground);
    button.setPreferredSize(new Dimension(this.getWidth(), 100));
    button.setContentAreaFilled(false);
    button.setBorder(null);

    exit = new JButton("Exit");
    exit.setPreferredSize(new Dimension(this.getWidth(), 200));
    exit.setContentAreaFilled(false);
    exit.setBorder(null);

    welcomeText = new JLabel("", SwingConstants.CENTER);
    welcomeText.setPreferredSize(new Dimension(this.getWidth(), 200));
    welcomeText.setIcon(headerBackground);

    Font font = new Font("", Font.PLAIN,32);

    //set font for JLabel
    exit.setFont(font);

    //Add action listener to button
    this.add(welcomeText, BorderLayout.NORTH);
    this.add(button, BorderLayout.CENTER);

    this.add(exit, BorderLayout.PAGE_END);

    button.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        //Execute when button is pressed
        System.out.println("You clicked the button");
        panel = new ZombiePanel();
        add(panel);
        panel.setPreferredSize(new Dimension(400, 400));

        //if frame is packed elsewhere and key pressed does not work
        //make sure to panel.requestFocus();
        panel.requestFocus();
        button.setVisible(false);
        welcomeText.setVisible(false);
        exit.setVisible(false);
        sendRefernece();

      }
    });

    exit.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });


    this.setVisible(true);
    this.pack();



  }

  public void sendRefernece()
  {
    panel.setReference(this);
  }

}
