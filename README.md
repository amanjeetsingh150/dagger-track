# ⏰  Dagger Track
A gradle plugin that automatically adds clock tracking for your components and subcomponents.

## Features
DaggerTrack will tell you following type of time for each of your component and subcomponent injection:

1. **Total time**: This is the total wall clock time took by the component or subcomponent injection.
2. **On CPU time**: CPU time is the time the inject method took working on cpu.
3. **Off CPU time**: Off CPU time is the time that inject method took when it was not running on the CPU which means it was 
doing some I/O work or maybe blocked on some other resource.

## Getting Started
For guide and usage please visit [Project Website](https://amanjeetsingh150.github.io/dagger-track/).

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
