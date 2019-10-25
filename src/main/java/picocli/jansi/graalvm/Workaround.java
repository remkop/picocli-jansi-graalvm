package picocli.jansi.graalvm;

/**
 * Enables the use of Jansi native library code in GraalVM native image applications.
 * <p>
 * This class provides a workaround for a problem in Jansi's
 * <code>org.fusesource.hawtjni.runtime.Library</code>
 * that prevents the native libraries embedded in the Jansi JAR in
 * <code>/META-INF/native/*</code> from being loaded
 * when running in a GraalVM native image.
 * </p>
 * <p>
 * Usage: call {@code Workaround.enableLibraryLoad()} before calling any Jansi code.
 * For example:
 * </p>
 * <pre>
 * import org.fusesource.jansi.internal.WindowsSupport;
 * import picocli.jansi.substratevm.Workaround;
 *
 * public class OtherApp {
 *
 *     static {
 *         Workaround.enableLibraryLoad();
 *     }
 *
 *     public static void main(String[] args) {
 *         int width = WindowsSupport.getWindowsTerminalWidth();
 *         doCoolStuff(width);
 *     }
 *     // ...
 * }
 * </pre>
 */
public class Workaround {
    private Workaround() {}

    // workaround for https://github.com/fusesource/jansi/issues/162
    public static void enableLibraryLoad() {
        String arch = System.getProperty("os.arch");
        String vm = System.getProperty("java.vm.name");
        if (arch.endsWith("64") && "Substrate VM".equals(vm)) {
            System.setProperty("sun.arch.data.model", "64");
        }
    }
}
