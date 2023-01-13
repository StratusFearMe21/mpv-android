#!/bin/bash -e

. ../../include/path.sh

cp ../../scripts/ffmpeg_meson.build meson.tmp

if cmp -s meson.{tmp,build}; then
	rm "meson.tmp"
else
	mv meson.{tmp,build}
fi

cp ../../scripts/ffmpeg_meson_options.txt meson_options.tmp

if cmp -s meson_options.{tmp,text}; then
	rm "meson_options.tmp"
else
	mv meson_options.{tmp,txt}
fi

build=_build$ndk_suffix

if [ "$1" == "build" ]; then
	true
elif [ "$1" == "clean" ]; then
	rm -rf $build
	exit 0
else
	exit 255
fi

cpu=armv7-a
[[ "$ndk_triple" == "aarch64"* ]] && cpu=armv8-a
[[ "$ndk_triple" == "x86_64"* ]] && cpu=generic
[[ "$ndk_triple" == "i686"* ]] && cpu="i686 --disable-asm"

cpuflags=
[[ "$ndk_triple" == "arm"* ]] && export CFLAGS="$cpuflags -mfpu=neon -mcpu=cortex-a8"

meson $build --cross-file "$prefix_dir"/crossfile.txt \
	--default-library shared \
	-Db_lto=true \
	-Dandroid_abi=$android_abi \
	-Ddav1d:enable_tests=false \
	-Ddav1d:stack_alignment=16 \
	-Dffmpeg:mbedtls=enabled \
	-Dffmpeg:libdav1d=enabled \
	-Dffmpeg:vulkan=disabled \
	-Dffmpeg:cli=disabled \
	-Dffmpeg:tests=disabled \
	-Dffmpeg:indevs=disabled \
	-Dffmpeg:gpl=enabled \
	-Dffmpeg:version3=enabled \
	-Dffmpeg:outdevs=disabled \
	-Dffmpeg:muxers=disabled \
	-Dffmpeg:encoders=disabled \
	-Dffmpeg:mjpeg_encoder=enabled \
	-Dffmpeg:png_encoder=enabled

ninja -C $build -j$cores
DESTDIR="$prefix_dir" ninja -C $build install
