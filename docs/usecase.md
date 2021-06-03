DaggerTrack will tell you following type of time for each of your component and subcomponent injection:

1. **Total time**: This is the total wall clock time took by the component or subcomponent injection.
2. **On CPU time**: CPU time is the time the inject method took working on cpu.
3. **Off CPU time**: Off CPU time is the time that inject method took when it was not running on the CPU which means it was doing some I/O work or maybe blocked on some other resource.

Above mentioned times helps you to narrow down exactly where you can optimize in your dagger graph.

By default dagger-track will calculate these time for the thread where you will be calling your inject method of dagger, usually the main thread.

### How these type of times help?

Whenever you want to optimize the rendering time of your screen in a critical flow for example cold start, you would have following usecases:

* You would want to know total time spent on doing dependency injection
* You would want to know the total time spent for dependency injection on the basis of components and subcomponents. These can be for Application, activity or fragments classes.
* By looking at each total time values you can priortize what components you want to optimize first
* Now you would think how off CPU and on CPU time are helping. So, after you select a component to optimize you would take help of these two values to narrow down on the point where to optimize?<br>
* A **large on CPU time** value tells you that main thread is doing more work CPU maybe in form of initializing objects or any calculation which can be off loaded to a background thread
* A **large off CPU time** value tells you that your main thread is not consuming CPU and is either involved in waiting for any resource or disk I/O.