# Pathfinder Tool

The Pathfinder Tool can generate Tank Drive trajectories with using the Pathfinder library.

Right now, this is very cobbled together.

1. Pathfinder must be in a parallel directory and built ahead of time.
2. build using `./gradlew build`
3. run using 
   `java -cp ../Pathfinder/Pathfinder-Java/build/classes/main/:build/classes/main/ -Djava.library.path=../Pathfinder/Pathfinder-Java/build/libs/pathfinderjava/shared/platform/Mac/x86_64/ Tank`
    (Yes, it's that ugly)

You will need to change the java.library.path variable to match your platform.



