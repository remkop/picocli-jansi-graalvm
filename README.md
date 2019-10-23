# jansi-substratevm
Helper library for using Jansi in GraalVM native images.

## Background

GraalVM native images now offer experimental support for Windows,
so it is now possible to build applications in Java and compile them to a native Windows executable.

Many command line applications use Jansi to enable ANSI escape codes to show colors in the `cmd.exe` console or PowerShell console.

Unfortunately, the Jansi library (as of version 1.18) by itself is not sufficient to show show colors in the console when running as a GraalVM native image in Windows.

This library enables the use of ANSI escape codes in GraalVM native image applications running on Windows.

## Usage

The `NativeJansi` class can be used instead of the standard Jansi `AnsiConsole` class to enable the Jansi ANSI support.


```java
import picocli.jansi.substratevm.windows64.NativeJansi;

public class MyApp {
    public static void main(String... args) {
        NativeJansi.systemInstall(); // instead of AnsiConsole.systemInstall()
        
        printColoredOutputToConsole();

        NativeJansi.systemUninstall(); // instead of AnsiConsole.systemUninstall()
    }
    
    // ...
}
```

See the [Jansi README](https://github.com/fusesource/jansi) for more details.


## Details

When generating a native image, we need two configuration files for Jansi:

* [JNI](https://github.com/oracle/graal/blob/master/substratevm/JNI.md) - Jansi uses JNI, and all classes, methods, and fields that should be accessible via JNI must be specified during native image generation in a configuration file
* [resources](https://github.com/oracle/graal/blob/master/substratevm/RESOURCES.md) - to get a single executable we need to bundle the jansi.dll in the native image. We need some configuration to ensure the jansi.dll is included as a resource.

By including these configuration files in our JAR file, developers can simply put this JAR in the classpath when creating a native image; no command line options are necessary.

Also, there is a problem extracting the jansi.dll from the native image.
The `org.fusesource.hawtjni.runtime.Library` (in jansi 1.18) uses non-standard
system properties to determine the bitMode of the platform,
and these system properties are not available in SubstrateVM (the Graal native image JVM).

As a result, the `jansi.dll` cannot be extracted from the native image even when it is included as a resource.

The `NativeJansi` class provides a workaround for this.

