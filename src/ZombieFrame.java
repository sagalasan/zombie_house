import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
  public ZombieFrame()
  {

    this.setLayout(new BorderLayout());
    //GridBagConstraints c = new GridBagConstraints();


    button = new JButton(" Start Game! ");
    button.setPreferredSize(new Dimension(this.getWidth(), 100));

    exit = new JButton("Exit");
    exit.setPreferredSize(new Dimension(this.getWidth(), 100));


    welcomeText = new JLabel("Please Don't Eat My Brain!",SwingConstants.CENTER);
    welcomeText.setPreferredSize(new Dimension(this.getWidth(), 200));

    Font font = new Font("Courier", Font.PLAIN,24);

    //set font for JLabel
    welcomeText.setFont(font);

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
