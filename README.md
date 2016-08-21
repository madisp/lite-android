Lite Android Gradle Plugin
==========================

[![Build Status](https://travis-ci.org/madisp/lite-android.svg?branch=master)](https://travis-ci.org/madisp/lite-android)

A lightweight unofficial Gradle plugin aiming to simplify building Android libraries with the Java plugin applied.
Only emitting jars is supported, no support for resources.

**Work in progress.**

What works?
-----------

* Putting the `android.jar` on the bootclasspath, compileOnly and testCompileOnly configs
  * This ensures build failures if you use something like `java.nio.file.Path` without resorting to things like [animalsniffer](https://github.com/xvik/gradle-animalsniffer-plugin)
* Automatically registers the maven repositories in the SDK

What doesn't?
-------------

* Resolving aars
* IDEA module still inherits the JDK library from the parent

Design Constraints
------------------

* DSL as similar to the Android plugin as possible
* Emit libraries only with code (.jar)
  * Consider adding basic .aar support for emitting proguard configs in the future?
* No reliance on Android classes because of classpath issues (shading could help but makes the build unnecessarily heavy)
* Must be able to consume .aar libraries
* The Android and Google m2 repos must be available
* No support for buildtypes or flavours