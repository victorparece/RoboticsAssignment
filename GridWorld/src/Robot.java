/**
 * Created with IntelliJ IDEA.
 * User: vic
 * Date: 2/28/14
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class Robot
{
    private GridWorld gridWorld;

    private int locationY;
    private int locationX;

    /**
     * Constructs a Robot object, which can be moved around in the GridWorld.
     * Random start location.
     * @param gridWorld
     */
    public Robot(GridWorld gridWorld)
    {
        this.gridWorld = gridWorld;
    }

    /**
     * Constructs a Robot object, which can be moved around in the GridWorld.
     * Start location is defined by parameters (must be walkable).
     * @param gridWorld
     * @param locationY
     * @param locationY
     */
    public Robot(GridWorld gridWorld, int locationY, int locationX)
    {
        this.gridWorld = gridWorld;
        this.locationY = locationY;
        this.locationX = locationX;

        if (!gridWorld.IsWalkable(locationY, locationX))
            System.out.println("Start location X:" + locationX + " Y:" + locationY + " is not walkable.");
    }

    /**
     * Moves the robot up.
     * @return Move success status.
     */
    public boolean MoveUp()
    {
        if (!gridWorld.IsWalkable(locationY+1, locationX))
            return false;

        locationY++;
        return true;
    }

    /**
     * Moves the robot down.
     * @return Move success status.
     */
    public boolean MoveDown()
    {
        if (!gridWorld.IsWalkable(locationY-1, locationX))
            return false;

        locationY--;
        return true;
    }

    /**
     * Moves the robot right.
     * @return Move success status.
     */
    public boolean MoveRight()
    {
        if (!gridWorld.IsWalkable(locationY, locationX+1))
            return false;

        locationX++;
        return true;
    }

    /**
     * Moves the robot left.
     * @return Move success status.
     */
    public boolean MoveLeft()
    {
        if (!gridWorld.IsWalkable(locationY, locationX-1))
            return false;

        locationX--;
        return true;
    }
}
