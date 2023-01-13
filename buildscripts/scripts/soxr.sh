#!/bin/bash -e

. ../../include/path.sh

build=_build$ndk_suffix

if [ "$1" == "build" ]; then
	true
elif [ "$1" == "clean" ]; then
	rm -rf $build
	exit 0
else
	exit 255
fi

mkdir -p $build
cd $build

cmake .. -GNinja \
	-DCMAKE_BUILD_TYPE=Release \
	-DCMAKE_TOOLCHAIN_FILE=$DIR/sdk/android-ndk-r25b/build/cmake/android.toolchain.cmake \
	-DANDROID_ABI=$android_abi \
	-DANDROID_PLATFORM=android-21 \
	-DCMAKE_INSTALL_PREFIX="$prefix_dir" \
	-DBUILD_SHARED_LIBS:bool=off \
	-DWITH_OPENMP:bool=off \
	-DBUILD_TESTS:bool=off

ninja -j$cores
ninja install
