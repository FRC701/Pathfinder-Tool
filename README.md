# Pathfinder Tool

The Pathfinder Tool can generate Tank Drive trajectories with using the Pathfinder library.

Right now, this is very cobbled together.

1. Pathfinder must be in a parallel directory and built ahead of time.
2. build using `./gradlew build`
3. run using
   `java -cp "../Pathfinder/Pathfinder-Java/build/libs/Pathfinder-Java-1.8.jar:build/libs/Pathfinder-Tool.jar" -Djava.library.path=../Pathfinder/Pathfinder-Java/build/libs/pathfinderjava/shared/any64/ Tank`
    (Yes, it's that ugly)

   for Windows
   `java -cp ..\Pathfinder\Pathfinder-Java\build\libs\Pathfinder-Java-1.8.jar;build\libs\Pathfinder-Tool.jar -Djava.library.path=..\Pathfinder\Pathfinder-Java\build\libs\pathfinderjava\shared\any64\ Tank`

You will need to change the java.library.path variable to match your platform.

## To Do

1. Update the Manifest file so that the program can be run from the JAR file.
