# Pathfinder Tool

The Pathfinder Tool can generate Tank Drive trajectories with using the Pathfinder library.

Right now, this is very cobbled together.

1. Pathfinder must be in a parallel directory and built ahead of time.
2. build using `./gradlew fatJar`
3. run using
   `java -Djava.library.path=../Pathfinder/Pathfinder-Java/build/libs/pathfinderjava/shared/any64/ -jar build/libs/Pathfinder-Tool-all.jar`

   for Windows
   `java -Djava.library.path=..\Pathfinder\Pathfinder-Java\build\libs\pathfinderjava\shared\any64\ -jar build\libs\Pathfinder-Tool-all.jar `

You will need to change the java.library.path variable to match your platform.

