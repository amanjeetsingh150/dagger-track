# ⏰ Dagger Track
A gradle plugin that automatically adds clock tracking for your components and subcomponents.

For example, for `HomeFragment` doing calling inject method you will see following output in your logcat at runtime:

```
D/DaggerTrack: Total time of com.droidsingh.daggertrack.ui.HomeFragment: 10420ms
D/DaggerTrack: Total On CPU time of com.droidsingh.daggertrack.ui.HomeFragment: 4230ms
D/DaggerTrack: Total Off CPU time of com.droidsingh.daggertrack.ui.HomeFragment: 6190ms
```
DaggerTrack automatically filters the components and their subcomponents and adds clock tracking to `.class` files so that you can see logs in logcat at runtime whenever `inject` is called. 


## Usecase

DaggerTrack will tell you following type of time for each of your component and subcomponent injection:

1. **Total time**: This is the total wall clock time took by the component or subcomponent injection.
2. **On CPU time**: CPU time is the time the inject method took working on cpu.
3. **Off CPU time**: Off CPU time is the time that inject method took when it was not running on the CPU which means it was doing some I/O work or maybe blocked on some other resource.

Above mentioned times helps you to narrow down exactly where you can optimize in your dagger graph.

By default dagger-track will calculate these time for the thread where you will be calling your inject method of dagger, usually the main thread.

### How these type of times help?

Whenever you want to optimize the rendering time of your screen in a critical flow for example cold start, you would have following usecases:

1. You would want to know total time spent on doing dependency injection 
2. You would want to know the total time spent for dependency injection on the basis of components and subcomponents. These can be for Application, activity or fragments classes.
3. By looking at each total time values you can priortize what components you want to optimize first
4. Now you would think how off CPU and on CPU time are helping. So, after you select a component to optimize you would take help of these two values to narrow down on the point where to optimize?
	1. A **large on CPU time** value tells you that main thread is doing more work CPU maybe in form of initializing objects or any calculation which can be off loaded to a background thread
	2. A **large off CPU time** value tells you that your main thread is not consuming CPU and is either involved in waiting for any resource or disk I/O.
 
 
## Internal Working

1. The plugin registers a gradle transform which helps you to perform transformations on java/kotlin bytecode.
2. The transform filters out all the components and their subcomponents in project
3. [Javassist](https://www.javassist.org/) exposes API to perform operations easily on java byte code. 
4. Through Javassist API `insertBefore` and `insertAfter` all the relevant logs are embed to byte code.
5. `dagger-track-clock` module has the clock APIs to get wall clock time and CPU time at any instant.
6. To calculate CPU time at any instant we are using `getrusage` linux API provided in `#include <sys/resource.h> ` header by default in linux systems. For usage see the man page [here](https://man7.org/linux/man-pages/man2/getrusage.2.html). 


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
