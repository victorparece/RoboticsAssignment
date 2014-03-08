import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

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

    private Point location;

    /**
     * Directions that may be travelled by the robot
     */
    public static enum Direction
    {
        UP, DOWN, LEFT, RIGHT
    }

    /**
     * Defines if robot movement is Deterministic or Stochastic
     */
    public static enum WorldType
    {
        DETERMINISTIC, STOCHASTIC
    }

    /**
     * Constructs a Robot object, which can be moved around in the GridWorld.
     * Random start location.
     * @param gridWorld
     */
    public Robot(GridWorld gridWorld)
    {
        Random random = new Random();
        this.gridWorld = gridWorld;
        Point location;

        //Find a random walkable start location
        do {
            int x = random.nextInt(gridWorld.GetWidth());
            int y = random.nextInt(gridWorld.GetHeight());
            location = new Point(x, y);
        } while (!gridWorld.IsWalkable(location));

        this.location = location;
    }

    /**
     * Constructs a Robot object, which can be moved around in the GridWorld.
     * Start location is defined by parameters (must be walkable).
     * @param gridWorld
     * @param location
     */
    public Robot(GridWorld gridWorld, Point location)
    {
        this.gridWorld = gridWorld;
        this.location = location;

        if (!gridWorld.IsWalkable(location))
            System.out.println("Start location " + location.toString() + " is not walkable.");
    }

    /**
     * Moves the robot up.
     * @return Returns true if the location moved to is walkable.
     */
    public boolean Move(Direction direction, WorldType worldType)
    {
        //The world type is Stochastic, therefore there is some probability the requested action
        //will be taken or another action will be taken instead
        if (worldType == WorldType.STOCHASTIC)
        {
            Random random = new Random();
            double n = random.nextDouble();
            //15% chance to take a random action
            if (n < 0.15)
                direction = GetRandomWalkableDirection();
            //15% chance to remain in place
            else if (n < 0.30)
                return true;
            //70% change to take requested action
        }

        //Compute the new location of the robot based on the current location and move direction
        Point newLocation = GetNewLocation(gridWorld, location, direction);

        //Move the robot in the selected direction as long as this direction is walkable
        if (!gridWorld.IsWalkable(newLocation))
            return false;
        else
            location = newLocation;

        return true;
    }

    /**
     * Computes the new location of the robot based on the current location, move direction, and grid world
     * @param gridWorld
     * @param currentLocation
     * @param direction
     * @return Returns the new location of the robot after the move
     */
    public static Point GetNewLocation(GridWorld gridWorld, Point currentLocation, Direction direction)
    {
        Point newLocation;

        switch (direction)
        {
            case UP:
                newLocation = new Point((int)currentLocation.getX(), (int)currentLocation.getY()-1);
                break;
            case DOWN:
                newLocation = new Point((int)currentLocation.getX(), (int)currentLocation.getY()+1);
                break;
            case LEFT:
                newLocation = new Point((int)currentLocation.getX()-1, (int)currentLocation.getY());
                break;
            case RIGHT:
                newLocation = new Point((int)currentLocation.getX()+1, (int)currentLocation.getY());
                break;
            //This should never happen
            default:
                newLocation = currentLocation;
        }

        //If the location is not walkable, the new location is the current location
        if (!gridWorld.IsWalkable(newLocation))
            return currentLocation;

        return newLocation;
    }

    /**
     * Gets a random Direction that is walkable from the current location
     * @return Returns a walkable Direction selected at random
     */
    private Direction GetRandomWalkableDirection()
    {
        Random random = new Random();

        //Get all possible walkable directions
        ArrayList<Direction> walkableDirections = GetWalkableDirections();

        return walkableDirections.get(random.nextInt(walkableDirections.size()));
    }

    /**
     * Get the current location of the robot in the GridWorld
     * @return Returns the current location of the robot
     */
    public Point GetLocation()
    {
        return location;
    }

    /**
     * Gets all walkable directions adjacent to the current location
     * @return Returns a list of adjacent walkable directions
     */
    public ArrayList<Direction> GetWalkableDirections()
    {
        return GetWalkableDirections(gridWorld, location);
    }

    /**
     * Gets all walkable directions adjacent to the current location
     * @return Returns a list of adjacent walkable directions
     */
    public static ArrayList<Direction> GetWalkableDirections(GridWorld gridWorld, Point location)
    {
        ArrayList<Direction> walkableDirections = new ArrayList<Direction>();

        if (gridWorld.IsWalkable(new Point((int)location.getX()+1, (int)location.getY())))
            walkableDirections.add(Direction.RIGHT);

        if (gridWorld.IsWalkable(new Point((int)location.getX()-1, (int)location.getY())))
            walkableDirections.add(Direction.LEFT);

        if (gridWorld.IsWalkable(new Point((int)location.getX(), (int)location.getY()-1)))
            walkableDirections.add(Direction.UP);

        if (gridWorld.IsWalkable(new Point((int)location.getX(), (int)location.getY()+1)))
            walkableDirections.add(Direction.DOWN);

        return walkableDirections;
    }
}
