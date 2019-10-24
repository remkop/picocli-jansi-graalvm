# jansi-substratevm
Helper library for using Jansi in GraalVM native images.

## Background

GraalVM native images now offer experimental support for Windows,
so it is now possible to build applications in Java and compile them to a native Windows executable.

Many command line applications use Jansi to enable ANSI escape codes to show colors in the `cmd.exe` console or PowerShell console.

Unfortunately, the Jansi library (as of version 1.18) by itself is not sufficient to show show colors in the console when running as a GraalVM native image in Windows.

This library enables the use of ANSI escape codes in GraalVM native image applications running on Windows.

## Usage

The `AnsiConsole` class can be used as a drop-in replacement of the standard Jansi `org.fusesource.jansi.AnsiConsole` class to enable the Jansi ANSI support.


```java
import picocli.jansi.substratevm.AnsiConsole;

public class MyApp {

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        doCoolStuff();
        AnsiConsole.systemUninstall();
    }
    // ...
}
```

If you require other Jansi functionality than `AnsiConsole`,
call the `Workaround.enableLibraryLoad()` method before invoking the Jansi code. For example:

```java
import org.fusesource.jansi.internal.WindowsSupport;
import picocli.jansi.substratevm.Workaround;

public class OtherApp {

    static {
        Workaround.enableLibraryLoad();
    }

    public static void main(String[] args) {
        int width = WindowsSupport.getWindowsTerminalWidth();
        doCoolStuff(width);
    }
    // ...
}
```

See the [Jansi README](https://github.com/fusesource/jansi) for more details on what Jansi can do.


## Details

When generating a native image, we need two configuration files for Jansi:

* [JNI](https://github.com/oracle/graal/blob/master/substratevm/JNI.md) - Jansi uses JNI, and all classes, methods, and fields that should be accessible via JNI must be specified during native image generation in a configuration file
* [resources](https://github.com/oracle/graal/blob/master/substratevm/RESOURCES.md) - to get a single executable we need to bundle the jansi.dll in the native image. We need some configuration to ensure the jansi.dll is included as a resource.

By including these configuration files in our JAR file, developers can simply put this JAR in the classpath when creating a native image; no command line options are necessary.

Also, there is a problem extracting the jansi.dll from the native image.
The `org.fusesource.hawtjni.runtime.Library` (in jansi 1.18) uses non-standard
system properties to determine the bitMode of the platform,
and these system properties are not available in SubstrateVM (the Graal native image JVM).

As a result, the native libraries embedded in the Jansi JAR under `/META-INF/native/*64/*`
cannot be extracted from the native image even when they are included as a resource.

The `Workaround` class provides a workaround for this.

## JNI Configuration Generator

The `jni-config.json` file contains JNI configuration for all classes, methods and fields in `org.fusesource.jansi.internal.CLibrary` and `org.fusesource.jansi.internal.Kernel32`.

The following command can be used to regenerate it:

```
java -cp picocli-4.0.4.jar;jansi-1.18.jar;picocli-codegen-4.0.5-SNAPSHOT.jar ^
  picocli.codegen.aot.graalvm.JniConfigGenerator ^
  org.fusesource.jansi.internal.CLibrary ^
  org.fusesource.jansi.internal.Kernel32 ^
  -o=.\jni-config.json
```
