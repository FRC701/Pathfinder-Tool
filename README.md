# Pathfinder Tool

The Pathfinder Tool can generate Tank Drive trajectories with using the Pathfinder library.

Right now, this is very cobbled together.

1. build using `./gradlew fatJar`
2. run using
   `java -jar build/libs/Pathfinder-Tool-all.jar` 
   
   See the `pathfinder-tool` script.

   for Windows
   `java -jar build\libs\Pathfinder-Tool-all.jar`
   
   Need a script for Windows.

The newest versions of Java seem to be able to load JNI libraries from a single jar file. 
The resulting jar file appears to run completely on it's own with no external paths.

The libraries that are checked in with this project were extracted from libraries at

```
https://imjac.in/dev/maven/jaci/
https://imjac.in/dev/maven/jaci/pathfinder/Pathfinder-JNI/2019.2.19/Pathfinder-JNI-2019.2.19-osxx86-64.jar
https://imjac.in/dev/maven/jaci/pathfinder/Pathfinder-CoreJNI/2019.2.19/Pathfinder-CoreJNI-2019.2.19-osxx86.jar
https://imjac.in/dev/maven/jaci/jniloader/JNILoader/1.0.1/JNILoader-1.0.1.jar
https://imjac.in/dev/maven/jaci/pathfinder/Pathfinder-Java/2019.2.19/Pathfinder-Java-2019.2.19.jar
```
There are POM files available here, but I don't know how to integrate with Gradle.


# To Do

* Rewrite the tool in C++. A standalone tool should be possible in C++ using the Pathfinder static library. 
  As it stands now, the Java tool requires a native library which means the tool cannot be standalone.

# Dependencies

The dependencies can be downloaded from the links above. The latest java files are included with this project.


