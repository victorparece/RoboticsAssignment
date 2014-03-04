import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: vic
 * Date: 2/28/14
 * Time: 11:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class QLearning
{
    //TODO: Determine appropriate values for these variables
    private static int TOTAL_EPISODES = 200;
    private static double LEARNING_RATE = .1;
    private static double DISCOUNT_FACTOR = .5;
    private static int TOTAL_TIMESTEPS = 150;
    private static int LEARNING_PERIOD = 50;
    private static int GOAL_STATE_REWARD = 100;
    private static int IMMEDIATE_REWARD = 1;

    private static boolean INCLUDE_DEBUG_INFO = false;

    /**
     * Main class for using Q-Learning
     * @param args
     */
    public static void main(String[] args)
    {
        Random random = new Random();
        GridWorld gridWorld = new GridWorld(args[0]);
        Point goalLocation = new Point(7, 1);

        System.out.println("---- World Representation ----");
        PrintRepresentation(gridWorld, null, goalLocation);

        Map<StateActionPair, Double> QTable = new HashMap<StateActionPair, Double>();

        //Add StateActionPair to Q-Table for each combination of GridWorld location and direction
        ArrayList<Point> gridWorldLocations = gridWorld.GetAllLocations();
        for(Point point : gridWorldLocations)
        {
            //Only walkable points will have StateActionPairs. Actions may be added that attempt
            //to go into a non-walkable location, if this action occurs it will result in a NOP.
            if (gridWorld.IsWalkable(point))
            {
                QTable.put(new StateActionPair(point, Robot.Direction.UP), 0.0);
                QTable.put(new StateActionPair(point, Robot.Direction.DOWN), 0.0);
                QTable.put(new StateActionPair(point, Robot.Direction.LEFT), 0.0);
                QTable.put(new StateActionPair(point, Robot.Direction.RIGHT), 0.0);
            }
        }

        //Loop for episodes
        for (int episode = 0; episode < TOTAL_EPISODES; episode++)
        {
            Robot robot = new Robot(gridWorld);
            StateActionPair previousStateActionPair = null;

            if (INCLUDE_DEBUG_INFO)
            {
                System.out.println("---- Start of Episode: " + episode + " ----");
                PrintRepresentation(gridWorld, robot, goalLocation);
            }

            for(int timeStep = 0; timeStep < TOTAL_TIMESTEPS; timeStep++)
            {
                /*
                //The robot is in the learning period, take exploratory policy
                if (timeStep < LEARNING_PERIOD)
                {

                }
                */

                //Get current location of robot
                Point robotLocation = robot.GetLocation();

                //Get all available actions
                ArrayList<Robot.Direction> walkableDirections = robot.GetWalkableDirections();

                //Choose random action
                Robot.Direction currentAction = walkableDirections.get(random.nextInt(walkableDirections.size()));

                //Take action
                robot.Move(currentAction, Robot.WorldType.DETERMINISTIC);

                if (INCLUDE_DEBUG_INFO)
                {
                    System.out.println("---- Time Step:" + timeStep + " ----");
                    PrintRepresentation(gridWorld, robot, goalLocation);
                }

                //TODO: If this is stochastic, a check needs to be done here to make sure the previousStateActionPair is correct

                previousStateActionPair = new StateActionPair(robotLocation, currentAction);

                //Update the Q-Table
                if (previousStateActionPair != null)
                {
                    //The robot has reached the goal location
                    if (robot.GetLocation().equals(goalLocation))
                    {
                        QTable.put(previousStateActionPair, (double)GOAL_STATE_REWARD);
                        break;
                    }
                    //The robot has not reached the goal state
                    else
                    {
                        double max = 0;

                        //Find maximum reachable StateActionPair
                        for (Robot.Direction direction : robot.GetWalkableDirections())
                        {
                            StateActionPair stateActionPair = new StateActionPair(robot.GetLocation(), direction);
                            max = Math.max(max, QTable.get(stateActionPair));
                        }

                        double oldValue = QTable.get(previousStateActionPair);
                        double learnedValue = IMMEDIATE_REWARD + DISCOUNT_FACTOR * max;

                        QTable.put(previousStateActionPair, oldValue + LEARNING_RATE * (learnedValue - oldValue));
                    }
                }
            }
        }

        System.out.println("---- Final Q-Table ----");
        for (StateActionPair stateActionPair: QTable.keySet())
        {
            System.out.println(stateActionPair.toString() + " = " + QTable.get(stateActionPair));
        }

        /*
        //Learning complete, try to reach goal location from 2 random starts
        for (int trial = 0; trial < 2; trial++)
        {
            //TODO: This should be random start
            Robot robot = new Robot(gridWorld, new Point(1, 1));

            //The robot has reached the goal location
            if (robot.GetLocation().equals(goalLocation))
            {
                System.out.println("The robot has reached the goal location.  Episode: " + episode + "  Time Step: " + timeStep);
                break;
            }
        }
        */
    }

    /**
     * Print the representation of the world, including the robot location and goal location
     * @param gridWorld
     * @param robot nullable
     * @param goal
     */
    public static void PrintRepresentation(GridWorld gridWorld, Robot robot, Point goal)
    {
        char[][] representation = gridWorld.GetRepresentation();

        if (robot != null)
        {
            Point robotLocation = robot.GetLocation();

            if (robotLocation.equals(goal))
            {
                representation[(int)robotLocation.getY()][(int)robotLocation.getX()] = 'X';
            }
            else
            {
                representation[(int)robotLocation.getY()][(int)robotLocation.getX()] = 'R';
                representation[(int)goal.getY()][(int)goal.getX()] = 'G';
            }
        }
        else
        {
            representation[(int)goal.getY()][(int)goal.getX()] = 'G';
        }

        for (int y = 0; y < representation.length; y++)
        {
            for (int x = 0; x < representation[y].length; x++)
                System.out.print(representation[y][x]);
            System.out.println();
        }
    }
}
