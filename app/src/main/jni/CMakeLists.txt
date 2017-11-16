cmake_minimum_required(VERSION 3.4.1)


add_library(imported-lib SHARED IMPORTED)
set_target_properties( # Specifies the target library.
                       imported-lib

                       # Specifies the parameter you want to define.
                       PROPERTIES IMPORTED_LOCATION

                       # Provides the path to the library you want to import.
                       imported-lib/src/${ANDROID_ABI}/libimported-lib.so )
include_directories( bzip2/ )
add_library(bspatch SHARED bspatch.c )