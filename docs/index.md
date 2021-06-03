# ⏰ Dagger Track
A gradle plugin that automatically adds clock tracking for your components and subcomponents.

For example, for `HomeFragment` calling inject method you will see following output in your logcat at runtime:

```
D/DaggerTrack: Total time of me.amanjeet.daggertrack.ui.HomeFragment: 10420ms
D/DaggerTrack: Total On CPU time of me.amanjeet.daggertrack.ui.HomeFragment: 4230ms
D/DaggerTrack: Total Off CPU time of me.amanjeet.daggertrack.ui.HomeFragment: 6190ms
```
DaggerTrack automatically filters the components and their subcomponents and adds tracking logs to `.class` files so that you can see them in logcat at runtime whenever `inject` is called.

## Installation Guide

Dagger Track is distributed via maven central. Dagger Track requires Gradle 6.0 or higher.

You can find the snapshots of development version in following sonatype. Include the following in your `build.gradle`.

```
buildscript {
	repositories {
	 	mavenCentral()
    	google()
		maven {
            url 'https://s01.oss.sonatype.org/content/repositories/snapshots/'
       }
	}
	dependencies {
		classpath "me.amanjeet.daggertrack:dagger-track:1.0.0-SNAPSHOT"
	}
}
```

In your app level `build.gradle` apply the plugin:

If you use plugin DSL:

```
plugins {
	id 'com.android.application' <---- Only works with android application & library 
	id 'me.amanjeet.daggertrack'
}
```

or if you are using older versions of Gradle:

```
apply plugin: 'com.android.application' <---- Only works with android application & library 
apply plugin: 'me.amanjeet.daggertrack'
```
Note that DaggeTrack must be applied after the Android gradle plugin.


You need to tell dagger track which variant it should run on:

```
daggerTrack {
    applyFor = ["debug"]
}
```
Integrate the `dagger-track-clocks` library in your app `build.gradle`, necessary for providing the different clocks during logging:

```
implementation 'me.amanjeet.daggertrack:dagger-track-clocks:1.0.0-SNAPSHOT'
```

Sync your project and voila ✅ you are ready for tracking.
 
## Internal Working

1. The plugin registers a gradle transform which helps you to perform transformations on java/kotlin bytecode.
2. The transform filters out all the components and their subcomponents in project.
3. [Javassist](https://www.javassist.org/) exposes fluent APIs to perform operations easily on java byte code.
4. Through Javassist `insertBefore` and `insertAfter` API on a [CtMethod](https://www.javassist.org/html/javassist/CtMethod.html) all the relevant logs are embedded to byte code.
5. `dagger-track-clock` module has the clock APIs necessary to get wall clock time and CPU time at any instant.
6. To calculate CPU time at any instant we are using `getrusage` linux API provided in `#include <sys/resource.h> ` header by default in all linux systems. For usage see the man page [here](https://man7.org/linux/man-pages/man2/getrusage.2.html).

## License

    Copyright 2020 Amanjeet Singh

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
