import java.awt.*;
import java.util.Comparator;

/**
 * Created by vic on 3/9/14.
 */
public class ExplorationPoint
{
    private Point location;
    private Point previousLocation; //Value may be null (Start location)
    private double fValue; //Estimated cost from start to goal through this point
    private double gValue; //Cost from start along best known path
    private double hValue; //Heuristic estimate of distance from point to goal

    /**
     * Represets a point to be explored
     */
    public ExplorationPoint(Point location, Point previousLocation, double gValue, double hValue)
    {
        this.location = location;
        this.previousLocation = previousLocation;
        this.fValue = gValue + hValue;
        this.gValue = gValue;
        this.hValue = hValue;
    }

   @Override
   public boolean equals(Object a)
   {
       if (a == null)
           return false;
       if (a instanceof ExplorationPoint)
           return ((ExplorationPoint)a).GetPoint().equals(location);
       else
           return false;
   }

    /**
     * Gets the f-value of the ExplorationPoint
     * @return
     */
    public double GetFValue()
    {
        return fValue;
    }

    /**
     * Gets the g-value of the ExplorationPoint
     * @return
     */
    public double GetGValue()
    {
        return gValue;
    }

    public double GetHValue()
    {
        return hValue;
    }

    public Point GetPoint()
    {
        return location;
    }

    public Point GetPreviousPoint()
    {
        return previousLocation;
    }
}
