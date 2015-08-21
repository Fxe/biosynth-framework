[![Build Status](https://travis-ci.org/Fxe/biosynth-framework.svg?branch=master)](https://travis-ci.org/Fxe/biosynth-framework)
[![Software License](https://img.shields.io/badge/license-GPLv3-blue.svg)](LICENSE)

## Biosynth Framework

## Documentation
None. Yet !

## Issue Tracking
None. Yet !

## Building from Source
The Spring Framework uses a [Gradle][]-based build system. In the instructions
below, [`./gradlew`][] is invoked from the root of the source tree and serves as
a cross-platform, self-contained bootstrap mechanism for the build.

### Prerequisites

[Git][] and [JDK 8 update 20 or later][JDK8 build]

Be sure that your `JAVA_HOME` environment variable points to the `jdk1.8.0` folder
extracted from the JDK download.

### Check out sources
`git clone git@github.com:biosynth-framework.git`

### Install all spring-\* jars into your local Maven cache
`./gradlew install`

### Compile and test; build all jars, distribution zips, and docs
`./gradlew build`

... and discover more commands with `./gradlew tasks`. See also the [Gradle build and release FAQ][].

[Gradle]: http://gradle.org
[`./gradlew`]: http://vimeo.com/34436402
[Git]: http://help.github.com/set-up-git-redirect
[JDK8 build]: http://www.oracle.com/technetwork/java/javase/downloads
[Gradle build and release FAQ]: https://github.com/spring-projects/spring-framework/wiki/Gradle-build-and-release-FAQ