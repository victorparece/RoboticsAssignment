import java.awt.*;

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

    /**
     * Main class for using Q-Learning
     * @param args
     */
    public static void main(String[] args)
    {
        GridWorld gridWorld = new GridWorld(args[0]);
        Point goalLocation = new Point(7, 1);
        double[] Q = new double[TOTAL_TIMESTEPS];

        //Loop for episodes
        for (int episode = 0; episode < TOTAL_EPISODES; episode++)
        {
            //TODO: Episodes should have the robot start in different locations
            Robot robot = new Robot(gridWorld, new Point(1, 1));
            for(int timeStep = 0; timeStep < TOTAL_TIMESTEPS; timeStep++)
            {
                //The robot is in the learning period, take exploratory policy
                if (timeStep < LEARNING_PERIOD)
                {

                }

                //The robot has reached the goal location
                if (robot.GetLocation().equals(goalLocation))
                {
                    System.out.println("The robot has reached the goal location.  Episode: " + episode + "  Time Step: " + timeStep);
                    break;
                }
            }
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
}
