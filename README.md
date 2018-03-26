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

# To Do

* Rewrite the tool in C++. A standalone tool should be possible in C++ using the Pathfinder static library. As it stands now, the Java tool requires a native library which means the tool cannot be standalone.

# Dependencies

The Pathfinder project must be in a parallel directory and must also be built. Building Pathfinder takes several steps. As of this writing the steps are:

1. Install Visual Studio Community 2017 
2. From the Visual Studio Installer install
  * Desktop development with C++
  * Windows 10 SDK (10.0.14393.0) [This SDK is shown in the summary section on the right.]
3. Install the Java JDK
  * Currently Java 8
  * Follow the instructions. In particular, update the PATH environment variable. 
     * Control Panel -> System -> Advanced -> Environment Variables
     * Add the JDK path to the path environment variable
4. Checkout the [Pathfinder project](https://github.com/JacisNonsense/Pathfinder)
5. Build using `gradlew build`

Now change to the Pathfinder-Tool directory and build as described above.

