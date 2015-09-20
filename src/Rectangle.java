import java.util.Random;

/**
 * Created by laurencemirabal on 9/14/15.
 */
public class Rectangle {
    //room size is compensating for spacing. So subtract 1 from each  outer wall which will
    //become a wall tile. We will then be left with 8x8 min room size
    private static int MIN_ROOM_SIZE = 10;
    private static Random rnd = new Random();

    int x, y, width, height;
    Rectangle leftChild;
    Rectangle rightChild;
    Rectangle room;
    Rectangle hall;

    public Rectangle(int x, int y, int height, int width)
    {
        this.x = x;
        this.y = y;
        this.width = width-2;//subtract 2 to avoid collisions
        this.height = height-2;//same
    }

    public boolean split()
    {
        //if area already split then do nothing
        if( leftChild != null )
        {
            return false;
        }
        //generate either true or false for the split direction
        boolean horizontal = rnd.nextBoolean();

        //if horizontal is true we precede with height, otherwise we precede with width
        //and check that the room size will fit
        int max = (horizontal ? height : width ) - MIN_ROOM_SIZE;

        if( max <= MIN_ROOM_SIZE )
        {
            return false;
        }
        int split = rnd.nextInt( max ); // generate split point

        //check that the split size can accommodate the min room size
        if(split < MIN_ROOM_SIZE)
        {
            split = MIN_ROOM_SIZE;
        }
        //if horizontal is true then we split horizontally and make the child areas
        if(horizontal)
        {
            leftChild = new Rectangle( x, y, split, width );
            rightChild = new Rectangle( x+split, y, height-split, width );
        }
        //if horizontal is not true then we split vertically
        else
        {
            leftChild = new Rectangle( x, y, height, split );
            rightChild = new Rectangle( x, y+split, height, width-split );
        }

        //we split
        return true;
    }

    public void createRoom()
    {
        //if leftChild is not null we have a documented area so there should already
        //be a room in that location
        if(leftChild != null)
        {
            leftChild.createRoom();
            rightChild.createRoom();
        }
        //if there is no room than create one based on the leaf nodes available space
        //and also with the constraints set.
        else
        {   //if height - MIN_ROOM_SIZE <= 0, we only have enough room for the min room
            //size. Otherwise we pick a number within the areas size + min room size.
            //this also determines half the height to be joined with the leaf
            int roomTop = (height - MIN_ROOM_SIZE <= 0) ? 0 : rnd.nextInt( height - MIN_ROOM_SIZE);
            //this variable is the same as the one above but is for determining room width
            int roomLeft =  (width - MIN_ROOM_SIZE <= 0) ? 0 : rnd.nextInt( width - MIN_ROOM_SIZE);
            //now we set those constraints into what will become the dimensions of the room
            int roomHeight = Math.max(rnd.nextInt( height - roomTop ), MIN_ROOM_SIZE );
            int roomWidth = Math.max(rnd.nextInt( width - roomLeft ), MIN_ROOM_SIZE );
            //create the room object
            room = new Rectangle( x + roomTop, y+roomLeft, roomHeight, roomWidth);
        }
    }

}
