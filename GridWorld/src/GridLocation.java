/**
 * Created with IntelliJ IDEA.
 * User: vic
 * Date: 2/28/14
 * Time: 1:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class GridLocation
{
    private int size;
    private boolean isWalkable;

    /**
     * Constructs a new GridLocation object
     * @param size
     * @param isWalkable
     */
    GridLocation(int size, boolean isWalkable)
    {
        this.size = size;
        this.isWalkable = isWalkable;
    }

    /**
     * Constructs a new GridLocation object that is not walkable
     * @param size
     */
    GridLocation(int size)
    {
        this.size = size;
        this.isWalkable = false;
    }

    /**
     * Returns true if the location is walkable, false otherwise
     * @return
     */
    public boolean IsWalkable()
    {
        return isWalkable;
    }

    /**
     * Returns the size of one side of the grid location
     * @return
     */
    public int Size()
    {
        return size;
    }

    /**
     * Sets the walkablility of the grid location
     * @param isWalkable
     */
    public void SetWalkability(boolean isWalkable)
    {
        this.isWalkable = isWalkable;
    }
}
