import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Jalen on 9/9/2015.
 */
public class Player extends Entity
{
  double stamina = PLAYER_DEFAULT_STAMINA;
  boolean running = false;
  boolean moving = false;
  private boolean canMove = true;
  int runSoundSpeed = 400;
  int walkSoundSpeed = 800;
  String playerFootstepsFileName = "sound_files/player_footsteps.wav";
  int fireTrapInventory = 0;

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
        playRunningSound.stop();
        playWalkingSound.start();
        setSpeed(PLAYER_DEFAULT_SPEED);
      }
      //only subtract if moving as well
      else if (moving)
      {
        //player is running,
        //check if its on top of a firetrap.  if so explode
        //possible large use of processing power

        playRunningSound.start();
        playWalkingSound.stop();
        //change speed of sound timer
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


  Timer playWalkingSound = new Timer(walkSoundSpeed, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      playSound(playerFootstepsFileName);
    }
  });

  Timer playRunningSound = new Timer(runSoundSpeed, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      playSound(playerFootstepsFileName);
    }
  });

  public boolean canMove()
  {
    return canMove;
  }
  public void setCanMove(boolean b)
  {
    canMove = b;
  }
  public void playSound(String fileName)
  {
    try {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(fileName).getAbsoluteFile());
      Clip clip = AudioSystem.getClip();
      clip.open(audioInputStream);
      clip.start();
    } catch(Exception ex) {
      System.out.println("Error with playing sound.");
      ex.printStackTrace();
    }
  }

  public void stopWalkingSound()
  {
    playWalkingSound.stop();
  }
  public void startWalkingSound()
  {
    playWalkingSound.start();
  }
  public void stopRunningSound()
{
  playRunningSound.stop();
}

  public boolean hasFiretraps()
  {
    if (fireTrapInventory > 0)
    {
      return true;
    }
    return false;
  }
  public void takeFiretrap()
  {
    fireTrapInventory += 1;
  }
  public void useFiretrap()
  {
    fireTrapInventory -= 1;
  }
  public void addSpeed()
  {

    running = true;
    setSpeed(PLAYER_RUN_SPEED);
    //if im running, begin regen?
    staminaRegen.stop();
    staminaRun.start();
    //System.out.println("keeps starting timer");


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