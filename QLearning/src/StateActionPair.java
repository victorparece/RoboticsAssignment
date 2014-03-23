import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: vic
 * Date: 3/1/14
 * Time: 11:00 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Class defining a State-Action pair
 */
public class StateActionPair
{
    private Point location;
    private Robot.Direction direction;

    /**
     * Constructs a new state action pair
     * @param location
     * @param direction
     */
    public StateActionPair(Point location, Robot.Direction direction)
    {
        this.location = location;
        this.direction = direction;
    }

    /**
     * Compares this object with otherStateActionPairs for equivalence
     * @param otherStateActionPair
     * @return Returns true if the State-Action Pairs are equal, false otherwise
     */
    public boolean equals(Object otherStateActionPair)
    {
        if (otherStateActionPair == null) return false;
        if (otherStateActionPair instanceof StateActionPair)
        {
            //Return true if the locations are equal and the directions are the same
            if (this.location.equals(((StateActionPair)otherStateActionPair).location)
                    && this.direction == ((StateActionPair)otherStateActionPair).direction)
                return true;
        }

        return false;
    }

    /**
     * Hash code for StateActionPair object
     * @return Returns a hash code based on the location of the StateActionPair
     */
    public int hashCode()
    {
        return (location != null) ? location.hashCode() : 0;
    }

    /**
     * Returns a string representation of the StateActionPair
     * @return Returns a string representation of the StateActionPair
     */
    public String toString()
    {
        return "(" + (int)location.getX() + "," + (int)location.getY() + ")-" + direction;
    }

    /**
     * Returns the location of the state action pair
     */
    public Point GetLocation()
    {
        return location;
    }
}
