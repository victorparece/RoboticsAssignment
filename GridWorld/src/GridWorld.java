import java.awt.*;
import java.io.*;

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
                //If the character is not '0', the location is walkable
                else if (gridWorldString[i] != '0')
                    gridWorld[yLocation][i].SetWalkability(true);
            }

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
}
