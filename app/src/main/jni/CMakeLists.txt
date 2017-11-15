cmake_minimum_required(VERSION 3.4.1)

include_directories(${CMAKE_SOURCE_DIR}/bzip2)

add_library(
    bspatch
    SHARED
    bspatch.c )
