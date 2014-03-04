import java.awt.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: vic
 * Date: 2/28/14
 * Time: 1:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class GridWorld
{

    private GridLocation[][] gridWorld;
    private int worldWidth;
    private int worldHeight;

    private static int gridSize = 10;

    //TODO: GridWorld should be surrounded by unwalkable space

    /**
     * Constructs a new GridWorld object from a text file containing a block of 1's and 0's.
     * 1's are walkable areas and 0's are non-walkable areas.
     * @param inputFile
     */
    public GridWorld(String inputFile)
    {
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            int worldWidth = 0, worldHeight = 0;

            //Read representation of world from file
            while (line != null)
            {
                sb.append(line);
                sb.append(System.lineSeparator());

                //Update world size
                if (line.length() > worldWidth)
                    worldWidth = line.length();
                worldHeight++;

                line = br.readLine();
            }

            this.worldHeight = worldHeight;
            this.worldWidth = worldWidth;
            char[] gridWorldString = sb.toString().toCharArray();

            gridWorld = new GridLocation[worldHeight][worldWidth];

            //Initialize the GridWorld to a multidimensional array of unwalkable locations
            for(int i = 0; i < worldHeight; i++)
                for(int j = 0; j < worldWidth; j++)
                    gridWorld[i][j] = new GridLocation(gridSize);

            //Update the GridWorld with the walkable locations
            int yLocation = 0;
            for (int i = 0; i < gridWorldString.length; i++)
            {
                //If the end of line is reached, move to next yLocation
                if (gridWorldString[i] == '\n')
                    yLocation++;
                //If the character is '1', the location is walkable
                else if (gridWorldString[i] == '1')
                    gridWorld[yLocation][i%(worldWidth+1)].SetWalkability(true);
            }

            /*
            //Print the representation of gridWorld
            for (int i = 0; i < gridWorld.length; i++)
            {
                for (int j = 0; j < gridWorld[i].length; j++)
                    System.out.print(gridWorld[i][j].toString());
                System.out.println();
            }*/

        } catch (IOException e)
        {
            System.out.println("IOException occurred while reading " + inputFile + ".");
            System.exit(1);
        }
    }

    /**
     * Indicates if the location in the GridWorld is Walkable
     * @param location
     * @return Returns true if the location is walkable
     */
    public boolean IsWalkable(Point location)
    {
        return gridWorld[(int)location.getY()][(int)location.getX()].IsWalkable();
    }

    /**
     * Get the GridWorld width
     * @return Returns the width of the GridWorld
     */
    public int GetWidth()
    {
        return this.worldWidth;
    }

    /**
     * Get the GridWorld height
     * @return Returns the height of the GridWorld
     */
    public int GetHeight()
    {
        return this.worldHeight;
    }

    /**
     * Get the coordinates of all locations in the GridWorld
     * @return Returns a list of all locations in the GridWorld
     */
    public ArrayList<Point> GetAllLocations()
    {
        ArrayList<Point> allLocations = new ArrayList<Point>();
        for (int y = 0; y < gridWorld.length; y++)
            for (int x = 0; x < gridWorld[y].length; x++)
                allLocations.add(new Point(x, y));

        return allLocations;
    }

    /**
     * Gets a printable representation of the GridWorld
     */
    public char[][] GetRepresentation()
    {
        char[][] representation = new char[worldHeight][worldWidth];

        for (int i = 0; i < gridWorld.length; i++)
            for (int j = 0; j < gridWorld[i].length; j++)
                if (gridWorld[i][j].IsWalkable())
                    representation[i][j] = '1';
                else
                    representation[i][j] = '0';

        return representation;
    }
}
