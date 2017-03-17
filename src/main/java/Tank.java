import java.io.File;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class Tank {

    public static void main(String[] args) {
        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.05, 1.7, 2.0, 60.0);

        // Waypoints are x, y, heading (in radians) where x is the direction in front of the robot
        Waypoint[] points = new Waypoint[] {
                new Waypoint(0, 0, 0),
                // new Waypoint(6, 0, Pathfinder.d2r(60)),
                new Waypoint(8, 2.5, Pathfinder.d2r(60))
        };

        Trajectory trajectory = Pathfinder.generate(points, config);

        // Wheelbase Width = 0.5m
        TankModifier modifier = new TankModifier(trajectory).modify(0.5);

        // Do something with the new Trajectories...
        Trajectory left = modifier.getLeftTrajectory();
        Trajectory right = modifier.getRightTrajectory();

        File out = new File("left.csv");
        Pathfinder.writeToCSV(out, left);

        out = new File("right.csv");
        Pathfinder.writeToCSV(out, right);

        System.out.println("Done!");
    }

}
