import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class Tank {

    public static final double SAMPLE_INTERVAL_SECONDS = 0.05;
    public static final double MAX_VELOCITY = 1.7;    // units must be consistant feet/second?
    public static final double MAX_ACCELERATION = 2.0; // units must be consistent feet/second/second?
    public static final double MAX_JERK = 60.0;       // units must be consistent acceleration/second

    public static void main(String[] args) {
        Trajectory.Config config
          = new Trajectory.Config(
              Trajectory.FitMethod.HERMITE_QUINTIC, // HERMITE_CUBIC
              Trajectory.Config.SAMPLES_HIGH,
              SAMPLE_INTERVAL_SECONDS,
              MAX_VELOCITY,
              MAX_ACCELERATION,
              MAX_JERK);

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

        out = new File("trajectory.cpp");
        WriteToStruct(out, left, right);
        System.out.println("Done!");
    }

    public static void WriteToStruct(File out, Trajectory leftTrajectory, Trajectory rightTrajectory)
    {
      WriteToStruct(out, leftTrajectory.segments, rightTrajectory.segments);
    }

    public static void WriteToStruct(File file, Trajectory.Segment[] leftSegments, Trajectory.Segment[] rightSegments)
    {
      // todo: check leftSegments.length == rightSegments.length
      try {
        PrintWriter out = new PrintWriter(file);

        out.printf("static const unsigned int kTrajectoryLength = %d\n\n", leftSegments.length);
        out.println("const ChassisMotionProfileCommand::TrajectoryPoint leftTrajectories[] = {");
        PrintSegments(out, leftSegments);

        out.println("const ChassisMotionProfileCommand::TrajectoryPoint rightTrajectories[] = {");
        PrintSegments(out, rightSegments);

        out.flush();
        out.close();
      } catch(FileNotFoundException ex) {
        System.err.println("Bummer! An exception.");
      }
    }

    public static void PrintSegments(PrintWriter out, Trajectory.Segment[] segments) {
      out.println("  // { position, velocity },");
      boolean first = true;
      for (int index = 0; index < segments.length; index++) {
        if (! first ) {
          out.println(",");
        } else {
          first = false;
        }
        out.printf("  { %g, %g }",
          segments[index].position, segments[index].velocity);
      }
      out.println("\n};");
    }
}
