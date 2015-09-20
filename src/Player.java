import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Jalen on 9/9/2015.
 */
public class Player extends Entity
{
  double stamina = PLAYER_DEFAULT_STAMINA;
  boolean running = false;
  boolean moving = false;
  public Player(int x, int y)
  {
    super("Player", x, y);
    setSpeed(Constants.PLAYER_DEFAULT_SPEED);
  }

  Timer staminaRun = new Timer(10, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      //System.out.println("stamina is "+stamina);
      if (stamina <= 0)
      {
        setSpeed(PLAYER_DEFAULT_SPEED);
      }
      //only subtract if moving as well
      else if (moving)
      {
        stamina -= .01;
      }
    }
  });

  Timer staminaRegen = new Timer(1000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      if (stamina <= PLAYER_DEFAULT_STAMINA)
      {
        stamina += PLAYER_REGEN_STAMINA;
      }
    }
  });
  public void addSpeed()
  {

    running = true;
    setSpeed(PLAYER_RUN_SPEED);
    //if im running, begin regen?
    staminaRegen.stop();
    staminaRun.start();
    //System.out.println("keeps starting timer");
    //setSpeed(PLAYER_RUN_SPEED);

  }
  public void regenStamina()
  {
    running = false;
    setSpeed(PLAYER_DEFAULT_SPEED);
    staminaRun.stop();
    staminaRegen.start();
  }

  public void setSpeedZero()
  {

  }

}