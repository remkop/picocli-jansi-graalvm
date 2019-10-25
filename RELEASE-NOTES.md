# <a name="1.0.0"></a> 1.0.0 - Initial release

Provide a workaround for issues that prevent Jansi from being used in GraalVM native images:

* `jni-config.json` with JNI configuration for the Jansi `org.fusesource.jansi.internal.CLibrary` and `org.fusesource.jansi.internal.Kernel32` classes.
* `resource-config.json` configuration file that ensures the `/META-INF/native/windows64/jansi.dll` file is included as a resource in the native image.
* `picocli.jansi.graalvm.Workaround` class that works around an issue in the `org.fusesource.hawtjni.runtime.Library` (in jansi 1.18) class that prevents  `/META-INF/native/windows64/jansi.dll` from being extracted from the native image even when it is included as a resource.
* `picocli.jansi.graalvm.AnsiConsole` class that enables the above workaround and can be used as a drop-in replacement for `org.fusesource.jansi.AnsiConsole`.

