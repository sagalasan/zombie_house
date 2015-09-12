import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by Jalen on 9/10/2015.
 * will hold the player/zombie objects/arrays, etc
 */
public class GameControl {

  static Player userPlayer;
  static Zombie zombie1;
  ZombiePanel reference;
  //adjust this to repaint faster
  int speed = 1000;

  Timer timer = new Timer(speed, new ActionListener()
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      reference.repaint();
      //while player is nearby
      zombie1.getNextCoordsToFollowPlayer();

    }
  });

  public GameControl(ZombiePanel panel)
  {
    reference = panel;
    userPlayer = new Player(5,5);
    zombie1 = new Zombie(15,10);
    zombie1.chasePlayer();
    timer.start();
  }

  public Player getUserPlayer()
  {
    return userPlayer;
  }


  //astar probably will go here.  Zombie arrays and players can be referenced from here

}
