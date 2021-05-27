cmake_minimum_required(VERSION 3.4.1)

add_library(cpu_time SHARED cpu_time.cpp)
target_link_libraries(cpu_time)
