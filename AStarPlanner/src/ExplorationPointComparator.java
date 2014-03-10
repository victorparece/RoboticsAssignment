import java.util.Comparator;

/**
 * Created by vic on 3/9/14.
 */
public class ExplorationPointComparator implements Comparator<ExplorationPoint>
{
    @Override
    public int compare(ExplorationPoint x, ExplorationPoint y)
    {
        if (x.GetFValue() < y.GetFValue())
            return -1;
        if (x.GetFValue() > y.GetFValue())
            return 1;
        else
            return 0;
    }
}
