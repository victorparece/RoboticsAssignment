import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: vic
 * Date: 2/28/14
 * Time: 11:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class QLearning
{
    private static Robot.WorldType worldType = Robot.WorldType.STOCHASTIC;
    private static ExplorationPolicy explorationPolicy = ExplorationPolicy.GreedyWithExplorationBonus;

    //TODO: Determine appropriate values for these variables
    private static int TOTAL_EPISODES = 200;
    private static double LEARNING_RATE = .1;
    private static double DISCOUNT_FACTOR = 1;//.5;
    private static int TOTAL_TIMESTEPS = 150;
    private static int LEARNING_PERIOD = 50;
    private static int GOAL_STATE_REWARD = 100;
    private static int IMMEDIATE_REWARD = 1;

    private static boolean INCLUDE_DEBUG_INFO = false;

    /**
     * Enumeration of possible Exploration Policies
     */
    private static enum ExplorationPolicy
    {
        GreedyWithExplorationBonus,
        Random,
        Greedy
    }

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
        Map<Point, Integer> ExplorationTable = new HashMap<Point, Integer>();

        //Add StateActionPair to Q-Table and Exploration-Table for each valid combination of
        //GridWorld locations and directions
        ArrayList<Point> gridWorldLocations = gridWorld.GetAllLocations();
        for(Point point : gridWorldLocations)
        {
            //Only walkable points
            if (gridWorld.IsWalkable(point))
            {
                //Add new state to exploration table
                ExplorationTable.put(point, 0);

                //Only available actions for the given point
                ArrayList<Robot.Direction> walkableDirections = Robot.GetWalkableDirections(gridWorld, point);

                //Create state action pair for each point/action combination
                for (Robot.Direction direction : walkableDirections)
                    QTable.put(new StateActionPair(point, direction), 0.0);
            }
        }

        //Loop for episodes
        for (int episode = 0; episode < TOTAL_EPISODES; episode++)
        {
            //Robot has random start location
            Robot robot = new Robot(gridWorld);
            StateActionPair previousStateActionPair = null;

            if (INCLUDE_DEBUG_INFO)
            {
                System.out.println("---- Start of Episode: " + episode + " ----");
                PrintRepresentation(gridWorld, robot, goalLocation);
            }

            //Loop for time steps
            for(int timeStep = 0; timeStep < TOTAL_TIMESTEPS; timeStep++)
            {
                //Get current location of robot
                Point robotLocation = robot.GetLocation();

                //Update the visit count for the current State
                ExplorationTable.put(robotLocation, ExplorationTable.get(robotLocation)+1);

                //Get all available actions
                ArrayList<Robot.Direction> walkableDirections = robot.GetWalkableDirections();

                //Selects the next action using the given policy
                Robot.Direction currentAction = SelectAction(explorationPolicy, QTable, ExplorationTable, gridWorld, robot);

                //Take action
                robot.Move(currentAction, worldType);

                //The world is stochastic, make sure the new state matches the requested state
                if (worldType == Robot.WorldType.STOCHASTIC)
                {
                    //The new robot location is not equal to the requested robot location
                    if (!robot.GetLocation().equals(Robot.GetNewLocation(gridWorld, robotLocation, currentAction)))
                    {
                        //Find the actual direction the robot moved
                        for (Robot.Direction direction : walkableDirections)
                            if (robot.GetLocation().equals(Robot.GetNewLocation(gridWorld, robotLocation, direction)))
                                currentAction = direction;
                    }
                }

                previousStateActionPair = new StateActionPair(robotLocation, currentAction);

                if (INCLUDE_DEBUG_INFO)
                {
                    System.out.println("---- Time Step:" + timeStep + " ----");
                    PrintRepresentation(gridWorld, robot, goalLocation);
                }

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

        //Print the heat map to file
        try
        {
            PrintWriter pwHeatmap = new PrintWriter(new File("QLearning-HeatMap.txt"), "UTF-8");
            pwHeatmap.write("---- Heat Map ----" + System.lineSeparator());
            PrintHeatMapToFile(pwHeatmap, QTable, gridWorld);
            pwHeatmap.close();
        } catch (Exception e){}

        //Learning complete, try to reach goal location from 2 random starts
        for (int trial = 0; trial < 2; trial++)
        {
            Robot robot = new Robot(gridWorld, new Point(1, 7));
            int counter = 0;

            try
            {
                PrintWriter pwOutput = new PrintWriter(new File("QLearning-Trial" + trial + ".txt"), "UTF-8");

                //Loop until goal location is reached
                while (!robot.GetLocation().equals(goalLocation))
                {
                    pwOutput.write("---- Iteration:" + counter++ + " ----" + System.lineSeparator());
                    PrintRepresentationToFile(pwOutput, gridWorld, robot, goalLocation);

                    //Get next action
                    Robot.Direction direction = SelectAction(ExplorationPolicy.Greedy, QTable, ExplorationTable, gridWorld, robot);

                    //Move the robot
                    robot.Move(direction, Robot.WorldType.DETERMINISTIC);
                }

                pwOutput.write("---- Iteration:" + counter++ + " ----" + System.lineSeparator());
                PrintRepresentationToFile(pwOutput, gridWorld, robot, goalLocation);

                pwOutput.close();
            }
            catch (Exception e){}
        }
    }

    /**
     * Select the next action using the given policy
     * @param ep
     * @param QTable
     * @param ExplorationTable
     * @param gridWorld
     * @param robot
     * @return Returns an action
     */
    private static Robot.Direction SelectAction(ExplorationPolicy ep, Map<StateActionPair, Double> QTable,
        Map<Point, Integer> ExplorationTable, GridWorld gridWorld, Robot robot)
    {
        Random random = new Random();
        //Get all available actions
        ArrayList<Robot.Direction> walkableDirections = robot.GetWalkableDirections();

        //Select action using selected Exploration Policy
        Robot.Direction currentAction = null;
        switch (ep)
        {
            case GreedyWithExplorationBonus:
                //Find maximum number of times a State has been visited
                int maxVisitCount = Collections.max(ExplorationTable.values()) + 1;
                double maxQTableValue = Collections.max(QTable.values()) + 1;

                double maxValue = -1;
                for (Robot.Direction direction : walkableDirections)
                {
                    StateActionPair stateActionPair = new StateActionPair(robot.GetLocation(), direction);
                    double qValue = QTable.get(stateActionPair)/maxQTableValue;
                    double explorationValue = 1 - (double)ExplorationTable.get(Robot.GetNewLocation(gridWorld, robot.GetLocation(), direction))/(double)maxVisitCount;
                    double score = qValue + explorationValue;

                    //System.out.println(stateActionPair + " Exploration: " + explorationValue + " QValue: " + qValue);

                    if (score > maxValue)
                    {
                        currentAction = direction;
                        maxValue = score;
                    }
                }
                break;
            case Random:
                currentAction = walkableDirections.get(random.nextInt(walkableDirections.size()));
                break;
            case Greedy:
                double maxValue2 = -1;
                for (Robot.Direction direction : walkableDirections)
                {
                    StateActionPair stateActionPair = new StateActionPair(robot.GetLocation(), direction);
                    double score = QTable.get(stateActionPair);

                    if (score > maxValue2)
                    {
                        currentAction = direction;
                        maxValue2 = score;
                    }
                }
                break;
            default:
                System.out.println("Exploration Policy not implemented.");
                System.exit(0);
        }

        return currentAction;
    }

    /**
     * Print the representation of the world, including the robot location and goal location
     * @param gridWorld
     * @param robot nullable
     * @param goal
     */
    public static void PrintRepresentation(GridWorld gridWorld, Robot robot, Point goal)
    {
        char[][] representation = GetRepresentation(gridWorld, robot, goal);

        for (int y = 0; y < representation.length; y++)
        {
            for (int x = 0; x < representation[y].length; x++)
                System.out.print(representation[y][x]);
            System.out.println();
        }
    }

    /**
     * Print the representation of the world, including the robot location and goal location
     * @param pw
     * @param gridWorld
     * @param robot nullable
     * @param goal
     */
    public static void PrintRepresentationToFile(PrintWriter pw, GridWorld gridWorld, Robot robot, Point goal)
    {
        char[][] representation = GetRepresentation(gridWorld, robot, goal);

        for (int y = 0; y < representation.length; y++)
        {
            for (int x = 0; x < representation[y].length; x++)
                pw.write(representation[y][x]);
            pw.write(System.lineSeparator());
        }
    }

    /**
     * Gets the representation of the world, including the robot location and goal location
     * @param gridWorld
     * @param robot
     * @param goal
     */
    public static char[][] GetRepresentation(GridWorld gridWorld, Robot robot, Point goal)
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

        return representation;
    }

    public static void PrintHeatMapToFile(PrintWriter pw, Map<StateActionPair, Double> QTable, GridWorld gridWorld)
    {
        Map <Point, Double> HeatMap = new HashMap<Point, Double>();
        //Sum values for each point
        for (Map.Entry<StateActionPair, Double> entryQTable : QTable.entrySet())
        {
            double sum = 0;

            if (HeatMap.containsKey(entryQTable.getKey().GetLocation()))
                sum += HeatMap.get(entryQTable.getKey().GetLocation()) + entryQTable.getValue();
            else
                sum += entryQTable.getValue();

            HeatMap.put(entryQTable.getKey().GetLocation(), sum);
        }
        //Find average value for point
        for (Map.Entry<Point, Double> entry : HeatMap.entrySet())
        {
            ArrayList<Robot.Direction> walkableDirections = Robot.GetWalkableDirections(gridWorld, entry.getKey());
            HeatMap.put(entry.getKey(), entry.getValue()/walkableDirections.size());
            System.out.println("(" + (int)entry.getKey().getX() + "," + (int)entry.getKey().getY() + ") = " + entry.getValue());
        }
        //Print formatted Heat Map to screen
        char[][] temp = gridWorld.GetRepresentation();
        for (int i = 0; i < temp.length; i++)
        {
            for (int j = 0; j < temp[0].length; j++)
            {
                if (HeatMap.containsKey(new Point(j, i)))
                    pw.write(String.format("%.1f", HeatMap.get(new Point(j, i))) + " ");
                else
                    pw.write("0.0 ");
            }
            pw.write(System.lineSeparator());
        }
    }
}
