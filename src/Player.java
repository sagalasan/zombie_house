import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Jalen on 9/9/2015.
 */
public class Player extends Entity
{
  double stamina = PLAYER_DEFAULT_STAMINA;
  public Player(int x, int y)
  {
    super("Player", x, y);
    setSpeed(Constants.PLAYER_DEFAULT_SPEED);
  }

  public void addSpeed()
  {
    setSpeed(PLAYER_RUN_SPEED);
    Timer staminaTimer = new Timer(1000, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        stamina -= 1;
        if (stamina == 0)
        {
          setSpeed(PLAYER_DEFAULT_SPEED);

        }
      }
    });
    staminaTimer.start();
    //setSpeed(PLAYER_RUN_SPEED);

  }

  public void setSpeedZero()
  {

  }

}