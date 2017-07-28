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
    public static final String NAMESPACE = "CenterGearTrajectories";


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
        Waypoint[] rightGearPoints = new Waypoint[] {
                new Waypoint(0, 0, 0),
                // new Waypoint(6, 0, Pathfinder.d2r(60)),
                new Waypoint(8, 2.5, Pathfinder.d2r(60))
        };


        // Base line distance = 7'9 3/4"
        final double BASE_LINE_DISTANCE_FEET = 7.0 + (9.75 / 12);
        // Robot length = 35 1/2"
        final double ROBOT_LENGTH_FEET = 35.5 / 12;
        final double GEAR_DRIVE_DISTANCE_FEET = BASE_LINE_DISTANCE_FEET - (ROBOT_LENGTH_FEET / 2.0);

        Waypoint[] gearPoints = new Waypoint[] {
                new Waypoint(0, 0, 0),
                new Waypoint(GEAR_DRIVE_DISTANCE_FEET, 0, 0)
        };

        // Waypoint[] points = rightGearPoints;
        Waypoint[] points = gearPoints;
        Trajectory trajectory = Pathfinder.generate(points, config);

        final double WHEEL_BASE_INCHES = 30.0;
        final double WHEEL_BASE_FEET = WHEEL_BASE_INCHES / 12.0;
        TankModifier modifier = new TankModifier(trajectory).modify(WHEEL_BASE_FEET);

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

    public static final double WHEEL_DIAMETER_INCHES = 4.0;
    public static final double WHEEL_DIAMETER_FEET = WHEEL_DIAMETER_INCHES / 12.0;  // 1/3
    public static final double FEET_TO_ROTATION = 1.0 / (WHEEL_DIAMETER_FEET * Math.PI);  // 0.9549
    public static final double FPS_TO_RPM = FEET_TO_ROTATION * 60;  // 57.9

    public static void WriteToStruct(File out, Trajectory leftTrajectory, Trajectory rightTrajectory)
    {
      WriteToStruct(out, leftTrajectory.segments, rightTrajectory.segments, FEET_TO_ROTATION, FPS_TO_RPM);
    }

    public static void WriteToStruct(File file,
                                     Trajectory.Segment[] leftSegments,
                                     Trajectory.Segment[] rightSegments,
                                     double positionScale,
                                     double velocityScale)
    {
      // todo: check leftSegments.length == rightSegments.length
      try {
        PrintWriter out = new PrintWriter(file);

        out.printf("#include \"ChassisMotionProfileCommand.h\"\n");
        out.printf("namespace %s\n{\n\n", NAMESPACE);

        // Default linkage is extern for non-const and static for const.
        // Change the linkage for const by specifing extern.
        out.printf("extern const unsigned int kTrajectoryLength = %d;\n\n", leftSegments.length);
        out.println("extern const ChassisMotionProfileCommand::TrajectoryPoint leftTrajectories[] = {");
        PrintSegments(out, leftSegments, positionScale, velocityScale );

        out.println("extern const ChassisMotionProfileCommand::TrajectoryPoint rightTrajectories[] = {");
        PrintSegments(out, rightSegments, positionScale, velocityScale);
        out.printf("\n} // namespace %s\n", NAMESPACE);

        out.flush();
        out.close();
      } catch(FileNotFoundException ex) {
        System.err.println("Bummer! An exception.");
      }
    }

    public static void PrintSegments(PrintWriter out,
                                     Trajectory.Segment[] segments,
                                     double positionScale,
                                     double velocityScale) {
      out.println("  // { position (R), velocity (RPM) },");
      boolean first = true;
      for (int index = 0; index < segments.length; index++) {
        if (! first ) {
          out.println(",");
        } else {
          first = false;
        }
        out.printf("  { %g, %g }",
          segments[index].position * positionScale,
          segments[index].velocity * velocityScale);
      }
      out.println("\n};");
    }
}
