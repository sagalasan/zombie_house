import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 * Created by Jalen on 9/9/2015.
 */
public class ZombieFrame extends JFrame implements KeyListener{
  //have mouse listeners keyboard listeners
  //frame
  ZombiePanel panel;
  JButton button;
  JButton exit;
  JLabel welcomeText;
  ImageIcon buttonBackground;
  ImageIcon headerBackground;
  boolean panelIsOpen = false;

  JSpinner playerSight = new JSpinner();
  JSpinner playerSpeed = new JSpinner();
  JSpinner playerRunSpeed = new JSpinner();
  JSpinner playerHearing = new JSpinner();
  JSpinner playerStamina = new JSpinner();
  JSpinner playerStaminaRegen = new JSpinner();
  JSpinner zombieSpawnRate = new JSpinner();
  JSpinner zombieSpeed = new JSpinner();
  JSpinner zombieDecisionRate = new JSpinner();
  JSpinner zombieSmell = new JSpinner();
  JSpinner firetrapSpawnRate = new JSpinner();


  Object[] message = {
          "Player sight:", playerSight,
          "Player speed:", playerSpeed,
          "Player run speed:", playerRunSpeed,
          "Player hearing:", playerHearing,
          "Player stamina:", playerStamina,
          "Player stamina regen:", playerStaminaRegen,
          "Zombie spawn rate:", zombieSpawnRate,
          "Zombie speed:", zombieSpeed,
          "Zombie decision rate:", zombieDecisionRate,
          "Zombie smell", zombieSmell,
          "Firetrap spawn rate", firetrapSpawnRate,
  };

  public ZombieFrame()
  {
    this.setLayout(new BorderLayout());
    //this.setContentPane(new JLabel(new ImageIcon("tile_images/background.png")));
    JLabel background = new JLabel(new ImageIcon(("tile_images/background.png")));
    background.setLayout(new BorderLayout());
    this.setContentPane( background );
    background.setOpaque(false);
    addKeyListener(this);
    setFocusable(true);
    buttonBackground = new ImageIcon("tile_images/game_start.gif");
    headerBackground = new ImageIcon("tile_images/header.gif");

    welcomeText = new JLabel("", SwingConstants.CENTER);
    welcomeText.setPreferredSize(new Dimension(this.getWidth(), 200));
    welcomeText.setIcon(headerBackground);

    button = new JButton("");
    button.setIcon(buttonBackground);
    button.setPreferredSize(new Dimension(this.getWidth(), 100));
    button.setContentAreaFilled(false);
    button.setBorder(null);

    exit = new JButton("Exit");
    exit.setPreferredSize(new Dimension(this.getWidth(), 200));
    exit.setContentAreaFilled(false);
    exit.setBorder(null);

    Font font = new Font("", Font.BOLD,32);

    //set font for JLabel
    exit.setFont(font);
    exit.setForeground(new Color(225, 64, 0));

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
        setFocusable(false);



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

  @Override
  public void keyReleased(KeyEvent e) {
    if(e.getKeyCode()== KeyEvent.VK_I)
    {
        int option = JOptionPane.showConfirmDialog(this, message, "Enter all your values", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION)
        {
          //set constants
          panel.gameController.constants.setPlayerSight((int)playerSight.getValue());
          panel.gameController.constants.setPlayerDefaultSpeed((int)playerSpeed.getValue());
          panel.gameController.constants.setPlayerRunSpeed((int)playerRunSpeed.getValue());
          panel.gameController.constants.setPlayerHearing((int)playerHearing.getValue());
          panel.gameController.constants.setPlayerStamina((int)playerStamina.getValue());
          panel.gameController.constants.setPlayerStaminaRegen((int)playerStaminaRegen.getValue());
          panel.gameController.constants.setZombieSpawnRate((int)zombieSpawnRate.getValue());
          panel.gameController.constants.setZombieDefaultSpeed((int)zombieSpeed.getValue());
          panel.gameController.constants.setZombieDecisionRate((int)zombieDecisionRate.getValue());
          panel.gameController.constants.setFireTrapSpawnRate((int)zombieSmell.getValue());

      }

    }
  }

  public void setPanelIsOpen(boolean b)
  {

    panelIsOpen = b;
  }
  public boolean getPanelIsOpen()
  {
    return panelIsOpen;
  }

  public void sendRefernece()
  {
    panel.setReference(this);
  }

  @Override
  public void keyTyped(KeyEvent e) {

  }

  @Override
  public void keyPressed(KeyEvent e) {

  }


}
