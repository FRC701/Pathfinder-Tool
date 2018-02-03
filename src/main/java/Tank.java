import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class Tank {

    // Settings for Mjolnir
    // Distances are in Feet
    // Velocity is in feet/sec
    // Accelleration is in feet/sec/sec
    // Jerk is in feet/sec/sec/sec

    public static final double INCHES_PER_FOOT = 12;
    public static final double SECONDS_PER_MINUTE = 60;
    public static final double SAMPLE_INTERVAL_SECONDS = 0.01;
    public static final double ROBOT_MAX_VELOCITY_FPS = 13.5;
    public static final double MAX_VELOCITY_FPS = ROBOT_MAX_VELOCITY_FPS * 0.75;     // units must be consistant feet/second?
    public static final double MAX_ACCELERATION_FPSPS = MAX_VELOCITY_FPS * 1.0; // units must be consistent feet/second/second?
    public static final double MAX_JERK_FPSPSPS = 60.0;        // units must be consistent acceleration/second
    public static final String NAMESPACE = "AutoRunTrajectories";


    public static void main(String[] args) {
        Trajectory.Config config
          = new Trajectory.Config(
              Trajectory.FitMethod.HERMITE_QUINTIC, // HERMITE_CUBIC
              Trajectory.Config.SAMPLES_HIGH,
              SAMPLE_INTERVAL_SECONDS,
              MAX_VELOCITY_FPS,
              MAX_ACCELERATION_FPSPS,
              MAX_JERK_FPSPSPS);

        final double AUTO_LINE_DISTANCE_INCHES = 120.0;
        final double AUTO_LINE_DISTANCE_FEET = AUTO_LINE_DISTANCE_INCHES / INCHES_PER_FOOT;
        // Robot length = 35 1/2"
        final double BUMPER_THICKNESS_INCHES = 0.75 + 2.5;
        final double ROBOT_LENGTH_FEET = (32.5 + (BUMPER_THICKNESS_INCHES * 2)) / INCHES_PER_FOOT;
        // Put the robot half way across the line.
        final double AUTO_RUN_DISTANCE_FEET = AUTO_LINE_DISTANCE_FEET - (ROBOT_LENGTH_FEET / 2.0);

        Waypoint[] gearPoints = new Waypoint[] {
                new Waypoint(0, 0, 0),
                new Waypoint(AUTO_RUN_DISTANCE_FEET, 0, 0)
        };

        // Waypoint[] points = rightGearPoints;
        Waypoint[] points = gearPoints;
        Trajectory trajectory = Pathfinder.generate(points, config);

        final double WHEEL_BASE_INCHES = 27.5;
        final double WHEEL_BASE_FEET = WHEEL_BASE_INCHES / INCHES_PER_FOOT;
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
    public static final double WHEEL_DIAMETER_FEET = WHEEL_DIAMETER_INCHES / INCHES_PER_FOOT;  // 1/3
    public static final double FEET_PER_ROTATION = 1.0 / (WHEEL_DIAMETER_FEET * Math.PI);  // 0.9549
    public static final double FPS_TO_RPM = FEET_PER_ROTATION * SECONDS_PER_MINUTE;  // 57.9

    public static void WriteToStruct(File out, Trajectory leftTrajectory, Trajectory rightTrajectory)
    {
      WriteToStruct(out, leftTrajectory.segments, rightTrajectory.segments, FEET_PER_ROTATION, FPS_TO_RPM);
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

        out.printf("#include \"MotionProfile.h\"\n");
        out.printf("namespace %s\n{\n\n", NAMESPACE);

        // Default linkage is extern for non-const and static for const.
        // Change the linkage for const by specifing extern.
        out.printf("extern const unsigned int kTrajectoryLength = %d;\n\n", leftSegments.length);
        out.println("extern const robovikes::TrajectoryPoint leftTrajectories[] = {");
        PrintSegments(out, leftSegments, positionScale, velocityScale );

        out.println("extern const robovikes::TrajectoryPoint rightTrajectories[] = {");
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
