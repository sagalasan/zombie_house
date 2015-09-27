import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Jalen on 9/9/2015.
 */
public class Player extends Entity
{
  private double stamina = PLAYER_DEFAULT_STAMINA;
  private boolean running = false;
  private boolean moving = false;
  private boolean canMove = true;
  private int runSoundSpeed = 400;
  private int walkSoundSpeed = 800;

  private boolean movePlayerUp = false;
  private boolean movePlayerDown = false;
  private boolean movePlayerRight = false;
  private boolean movePlayerLeft = false;

  private String playerFootstepsFileName = "sound_files/player_footsteps.wav";
  private int fireTrapInventory = 0;


  private String playerSpriteSheet = "character_images/player_sprite_sheet.png";
  //private int animationWidth = ANIMATION_FORWARD_WIDTH;
  //private int animationHeight = ANIMATION_FORWARD_HEIGHT;
  public Player(int x, int y)
  {
    super("Player", x, y);
    setSpeed(PLAYER_DEFAULT_SPEED);
    setSpriteSheet(playerSpriteSheet);
    resetCurrentFrame();
  }

  Timer staminaRun = new Timer(10, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {

      if (stamina <= 0 && running)
      {
        running = false;
        playRunningSound.stop();
        if (!playWalkingSound.isRunning())
        {
          startWalkingSound();
        }
        setSpeed(PLAYER_DEFAULT_SPEED);
      }
      //only subtract if moving as well
      else if (moving)
      {
        if (stamina > 0)
        {
          stamina -= .01;
        }

      }
    }
  });

  Timer staminaRegen = new Timer(1000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      if (stamina <= PLAYER_DEFAULT_STAMINA)
      {
       // System.out.println(stamina);
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

  public void addSpeed()
  {
    running = true;
    setSpeed(PLAYER_RUN_SPEED);
    stopWalkingSound();
    playRunningSound.start();
    staminaRegen.stop();
    staminaRun.start();
  }
  public void regenStamina()
  {
    running = false;
    setSpeed(PLAYER_DEFAULT_SPEED);
    staminaRun.stop();
    playRunningSound.stop();
    playWalkingSound.setInitialDelay(0);
    playWalkingSound.start();
    staminaRegen.start();
  }

  public void move()
  {
    super.move(movePlayerUp, movePlayerDown, movePlayerRight, movePlayerLeft);
  }

  public void setMovePlayerUp(boolean b)
  {
    movePlayerUp = b;
  }
  public void setMovePlayerDown(boolean b)
  {
    movePlayerDown = b;
  }
  public void setMovePlayerRight(boolean b)
  {
    movePlayerRight = b;
  }
  public void setMovePlayerLeft(boolean b)
  {
    movePlayerLeft = b;
  }

  public boolean getMovePlayerUp()
  {
    return movePlayerUp;
  }
  public boolean getMovePlayerDown()
  {
    return movePlayerDown;
  }
  public boolean getMovePlayerRight()
  {
    return movePlayerRight;
  }
  public boolean getMovePlayerLeft()
  {
    return movePlayerLeft;
  }

  public double getStamina()
  {
    return stamina;
  }
  public boolean canMove()
  {
    return canMove;
  }

  public void setCanMove(boolean b)
  {
    canMove = b;
  }
  public boolean isRunning()
  {
    return running;
  }
  public void setMoving(boolean b)
  {
    moving = b;
  }
  public boolean isMoving()
  {
    return moving;
  }

  public void setAnimationDirectionBasedOnMovementBooleans()
  {
    if (movePlayerLeft)
    {
      setAnimationDirection(ANIMATION_LEFT_WALKING);
    }
    else if (movePlayerRight)
    {
      setAnimationDirection(ANIMATION_RIGHT_WALKING);
    }
    else if (movePlayerUp)
    {
      setAnimationDirection(ANIMATION_TOP_WALKING);
    }
    else if (movePlayerDown)
    {
      setAnimationDirection(ANIMATION_DOWN_WALKING);
    }
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
    //playWalkingSound.setInitialDelay(0);
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

  public int getTotalFiretraps()
  {
    return fireTrapInventory;
  }
  public void setTotalFiretraps(int n)
  {
    fireTrapInventory = n;
  }



  public void setSpeedZero()
  {

  }

}