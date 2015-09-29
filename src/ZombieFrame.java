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
  private ImageIcon buttonBackground;
  private ImageIcon headerBackground;

  //values for playerStaminaRegen
  private double min1 = 0.000;
  private double value1 = 0.2;
  private double max1 = 1.000;
  private double stepSize1 = 0.05;
  private SpinnerNumberModel modelPlayerStaminaRegen = new SpinnerNumberModel(value1, min1, max1, stepSize1);
  //Player speed
  private double min2 = 0.000;
  private double value2 = 1.0;
  private double max2 = 10.00;
  private double stepSize2 = 0.5;
  SpinnerNumberModel modelPlayerSpeed = new SpinnerNumberModel(value2, min2, max2, stepSize2);
  //Player run speed
  private double min4 = 0.000;
  private double value4 = 2.0;
  private double max4 = 12.00;
  private double stepSize4 = 0.25;
  SpinnerNumberModel modelPlayerRunSpeed = new SpinnerNumberModel(value4, min4, max4, stepSize4);
  //Player hearing
  private double min5 = 0.000;
  private double value5 = 2.0;
  private double max5 = 20.00;
  private double stepSize5 = 1.0;
  SpinnerNumberModel modelPlayerHearing = new SpinnerNumberModel(value5, min5, max5, stepSize5);
  //Player stamina
  private double min6 = 0.000;
  private double value6 = 10.0;
  private double max6 = 20.00;
  private double stepSize6 = 1.0;
  SpinnerNumberModel modelPlayerStamina = new SpinnerNumberModel(value6, min6, max6, stepSize6);
  //Zombie Speed
  private double min7 = 0.000;
  private double value7 = .50;
  private double max7 = 20.00;
  private double stepSize7 = 0.5;
  SpinnerNumberModel modelZombieSpeed = new SpinnerNumberModel(value7, min7, max7, stepSize7);
  //Zombie Spawn Rate
  private double min8 = 0.000;
  private double value8 = 0.010;
  private double max8 = 1.00;
  private double stepSize8 = 0.01;
  SpinnerNumberModel modelZombieSpawnRate = new SpinnerNumberModel(value8, min8, max8, stepSize8);
  //Firetrap spawn rate
  private double min9 = 0.000;
  private double value9 = 0.010;
  private double max9 = 1.00;
  private double stepSize9 = 0.01;
  SpinnerNumberModel modelFiretrapSpawnRate= new SpinnerNumberModel(value9, min9, max9, stepSize9);
  //spinners with int values
  private JSpinner playerSight = new JSpinner();
  private JSpinner zombieDecisionRate = new JSpinner();
  private JSpinner zombieSmell = new JSpinner();

  //spinners with double values
  private JSpinner playerSpeed = new JSpinner(modelPlayerSpeed);
  private JSpinner playerRunSpeed = new JSpinner(modelPlayerRunSpeed);
  private JSpinner playerHearing = new JSpinner(modelPlayerHearing);
  private JSpinner playerStamina = new JSpinner(modelPlayerStamina);
  private JSpinner playerStaminaRegen = new JSpinner(modelPlayerStaminaRegen);
  private JSpinner zombieSpawnRate = new JSpinner(modelZombieSpawnRate);
  private JSpinner zombieSpeed = new JSpinner(modelZombieSpeed);
  private JSpinner firetrapSpawnRate = new JSpinner(modelFiretrapSpawnRate);

  private int pSight = 5;
  private double pSpeed = 1;
  private double pRunSpeed = 2;
  private double pHearing = 10;
  private double pStamina = 5;
  private double pStaminaRegen = .20;
  private double zSpawnRate = 0.010;
  private double zSpeed = 0.5;
  private int zDecisionRate = 2000;
  private int zSmell = 7;
  private double firetrapSpawn = 0.010;



  //message for pup up spinners
  Object[] message = {
          "Player sight:", playerSight,
          "Player speed:", playerSpeed,
          "Player run speed:", playerRunSpeed,
          "Player hearing:", playerHearing,
          "Player stamina:", playerStamina,
          "Player stamina regen:", playerStaminaRegen,
          "Zombie spawn rate:", zombieSpawnRate,
          "Zombie speed:", zombieSpeed,
          "Zombie decision rate (seconds):", zombieDecisionRate,
          "Zombie smell", zombieSmell,
          "Firetrap spawn rate", firetrapSpawnRate,
  };

  public ZombieFrame()
  {
    playerSight.setValue(new Integer(5));
    zombieDecisionRate.setValue(new Integer(2));
    zombieSmell.setValue(new Integer(7));


    this.setLayout(new BorderLayout());
    JLabel background = new JLabel(new ImageIcon(getClass().getResource(("tile_images/background.png"))));
    //JLabel background = new JLabel(new ImageIcon(("tile_images/background.png")));
    background.setLayout(new BorderLayout());
    this.setContentPane(background);
    background.setOpaque(false);
    addKeyListener(this);
    setFocusable(true);

    //buttonBackground = new ImageIcon("tile_images/game_start.gif");
    //headerBackground = new ImageIcon("tile_images/header.gif");
    buttonBackground = new ImageIcon(getClass().getResource("tile_images/game_start.gif"));
    headerBackground = new ImageIcon(getClass().getResource("tile_images/header.gif"));

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
        sendReference();
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
      changeDefaultValues();
    }
  }


  public void changeDefaultValues()
  {
    int option = JOptionPane.showConfirmDialog(this, message, "Enter all your values", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION)
    {
      //set constants
      pSight = (int) playerSight.getValue();
      pSpeed = (double) playerSpeed.getValue();
      pRunSpeed = (double) playerRunSpeed.getValue();
      pHearing = (double) playerHearing.getValue();
      pStamina = (double) playerStamina.getValue();
      pStaminaRegen = (double) playerStaminaRegen.getValue();
      zSpawnRate = (double) zombieSpawnRate.getValue();
      zSpeed = (double) zombieSpeed.getValue();
      zDecisionRate = (int) zombieDecisionRate.getValue()*1000;
      zSmell = (int) zombieSmell.getValue();
      firetrapSpawn = (double) firetrapSpawnRate.getValue();
    }
  }


  public void setConstants()
  {
    panel.gameController.setPlayerSight(pSight);
    panel.gameController.setPlayerDefaultSpeed(pSpeed);
    panel.gameController.setPlayerRunSpeed(pRunSpeed);
    panel.gameController.setPlayerHearing(pHearing);
    panel.gameController.setPlayerStamina(pStamina);
    panel.gameController.setPlayerStaminaRegen(pStaminaRegen);
    panel.gameController.setZombieSpawnRate(zSpawnRate);
    panel.gameController.setZombieDefaultSpeed(zSpeed);
    panel.gameController.setZombieDecisionRate(zDecisionRate);
    panel.gameController.setZombieSmell(zSmell);
    panel.gameController.setFiretrapSpawnRate(firetrapSpawn);
  }


  public void sendReference()
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
