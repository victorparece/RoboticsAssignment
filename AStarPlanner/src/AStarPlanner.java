import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by vic on 3/9/14.
 */
public class AStarPlanner
{
    /**
     * Main class for using A* Planner
     * @param args
     */
    public static void main(String[] args)
    {
        Random random = new Random();
        GridWorld gridWorld = new GridWorld(args[0]);
        Point goalLocation = new Point(7, 1);

        for (int trial = 0; trial < 2; trial++)
        {
            try
            {
                PrintWriter pw = new PrintWriter(new File("A*Planner-Trial" + trial + ".txt"), "UTF-8");

                //Robot has random start location
                Robot robot = new Robot(gridWorld);

                Comparator<ExplorationPoint> comparator = new ExplorationPointComparator();
                PriorityQueue<ExplorationPoint> openQueue = new PriorityQueue<ExplorationPoint>(10, comparator);
                ArrayList<ExplorationPoint> closedSet = new ArrayList<ExplorationPoint>();

                //Add start location to open set
                openQueue.add(new ExplorationPoint(robot.GetLocation(), null, 0.0, 0.0));

                //System.out.println("---- World Representation ----");
                //PrintRepresentation(gridWorld, goalLocation, robot, openQueue, closedSet);
                pw.write("---- World Representation ----" + System.lineSeparator());
                PrintRepresentationToFile(pw, gridWorld, goalLocation, robot, openQueue, closedSet);

                int counter = 0;

                //Loop while the open queue start is not the goal point and the open queue is not empty
                while (!openQueue.peek().GetPoint().equals(goalLocation) && !openQueue.isEmpty())
                {
                    //Remove current from open queue
                    ExplorationPoint currentExplorationPoint = openQueue.remove();

                    //Add current to closed set
                    closedSet.add(currentExplorationPoint);

                    //Get all walkable points from current Exploration Point
                    ArrayList<Robot.Direction> walkableDirections = Robot.GetWalkableDirections(gridWorld, currentExplorationPoint.GetPoint());

                    //Loop for all neighbor points
                    for (Robot.Direction direction : walkableDirections)
                    {
                        Point location = Robot.GetNewLocation(gridWorld, currentExplorationPoint.GetPoint(), direction);
                        double newGValue = currentExplorationPoint.GetGValue() + 1;
                        double hValue = location.distance(goalLocation);
                        boolean alreadyInOpenQueue = false;
                        boolean alreadyInClosedSet = false;

                        //Check if neighbor is in open queue and new g-value is less than current g-value
                        for (ExplorationPoint ep : openQueue)
                        {
                            if (location.equals(ep.GetPoint()) && newGValue < ep.GetGValue())
                            {
                                //The neighbor is better than what is in the open queue, replace it
                                openQueue.remove(ep);
                                break;
                            }
                            else if (location.equals(ep.GetPoint()))
                            {
                                //A better option is already in the open queue
                                alreadyInOpenQueue = true;
                                break;
                            }
                        }

                        //Check if neighbor is in closed and new g-value is less than current g-value
                        for (ExplorationPoint ep : closedSet)
                        {
                            if (location.equals(ep.GetPoint()) && newGValue < ep.GetGValue())
                            {
                                //The neighbor is better than a point in the closed set, reopen
                                closedSet.remove(ep);
                                break;
                            }
                            else if (location.equals((ep.GetPoint())))
                            {
                                //A better option is already in the closed set
                                alreadyInClosedSet = true;
                                break;
                            }
                        }

                        if (!alreadyInOpenQueue && !alreadyInClosedSet)
                        {
                            //Add neighbor to open queue
                            openQueue.add(new ExplorationPoint(location, currentExplorationPoint.GetPoint(), newGValue, hValue));
                        }
                    }

                    //System.out.println("---- Iteration:" + counter++ + " ----");
                    //PrintRepresentation(gridWorld, goalLocation, robot, openQueue, closedSet);
                    pw.write("---- Iteration:" + counter++ + " ----" + System.lineSeparator());
                    PrintRepresentationToFile(pw, gridWorld, goalLocation, robot, openQueue, closedSet);
                }

                //The goal location has been found, reconstruct path
                if (openQueue.peek().GetPoint().equals(goalLocation))
                {
                    //System.out.println("---- Final Solution with Path ----");
                    //PrintRepresentation(gridWorld, goalLocation, robot, openQueue, closedSet);
                    pw.write("---- Final Solution with Path ----" + System.lineSeparator());
                    PrintRepresentationToFile(pw, gridWorld, goalLocation, robot, openQueue, closedSet);
                }

                pw.close();
            }
            catch (Exception e) {}
        }
    }

    /**
     * Print the representation of the world, including open and closed exploration points
     * @param gridWorld
     * @param openQueue
     * @param closedSet
     */
    public static void PrintRepresentation(GridWorld gridWorld, Point goal, Robot robot, PriorityQueue<ExplorationPoint> openQueue, ArrayList<ExplorationPoint> closedSet)
    {
        char[][] representation = GetRepresentation(gridWorld, goal, robot, openQueue, closedSet);
        //Print representation
        for (int y = 0; y < representation.length; y++)
        {
            for (int x = 0; x < representation[y].length; x++)
                System.out.print(representation[y][x]);
            System.out.println();
        }
    }

    /**
     * Print the representation of the world, including open and closed exploration points
     * @param gridWorld
     * @param openQueue
     * @param closedSet
     */
    public static void PrintRepresentationToFile(PrintWriter pw, GridWorld gridWorld, Point goal, Robot robot, PriorityQueue<ExplorationPoint> openQueue, ArrayList<ExplorationPoint> closedSet)
    {
        char[][] representation = GetRepresentation(gridWorld, goal, robot, openQueue, closedSet);
        //Print representation
        for (int y = 0; y < representation.length; y++)
        {
            for (int x = 0; x < representation[y].length; x++)
                pw.write(representation[y][x]);
            pw.write(System.lineSeparator());
        }
    }

    /**
     * Get the representation of the world, including open and closed exploration point
     * @param gridWorld
     * @param goal
     * @param robot
     * @param openQueue
     * @param closedSet
     * @return
     */
    public static char[][] GetRepresentation(GridWorld gridWorld, Point goal, Robot robot, PriorityQueue<ExplorationPoint> openQueue, ArrayList<ExplorationPoint> closedSet)
    {
        char[][] representation = gridWorld.GetRepresentation();
        ExplorationPoint goalExplorationPoint = null;

        representation[(int)goal.getY()][(int)goal.getX()] = 'G';

        //Open Queue
        for (ExplorationPoint ep : openQueue)
            if (goal.equals(ep.GetPoint()))
                goalExplorationPoint = ep;
            else
                representation[(int)ep.GetPoint().getY()][(int)ep.GetPoint().getX()] = 'o';

        //Closed Queue
        for (ExplorationPoint ep : closedSet)
            representation[(int)ep.GetPoint().getY()][(int)ep.GetPoint().getX()] = 'x';

        //The goal was found, print the path to the goal
        if (goalExplorationPoint != null)
        {
            ExplorationPoint currentExplorationPoint = goalExplorationPoint;
            while (currentExplorationPoint.GetPreviousPoint() != null)
            {
                representation[(int)currentExplorationPoint.GetPoint().getY()][(int)currentExplorationPoint.GetPoint().getX()] = '.';

                //Find next exploration point by point
                for (ExplorationPoint ep : closedSet)
                {
                    if (currentExplorationPoint.GetPreviousPoint().equals(ep.GetPoint()))
                    {
                        currentExplorationPoint = ep;
                        break;
                    }
                }
            }

            //Set the goal location
            representation[(int)goalExplorationPoint.GetPoint().getY()][(int)goalExplorationPoint.GetPoint().getX()] = 'X';
        }

        //Set the start location
        representation[(int)robot.GetLocation().getY()][(int)robot.GetLocation().getX()] = 'R';

        return representation;
    }
}
