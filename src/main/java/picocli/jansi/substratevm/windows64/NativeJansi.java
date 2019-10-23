package picocli.jansi.substratevm.windows64;

import org.fusesource.jansi.AnsiConsole;

/**
 * Enables the use of ANSI escape codes in GraalVM native image applications running on Windows.
 * <p>
 * This class provides a workaround for a problem in Jansi's
 * <code>org.fusesource.hawtjni.runtime.Library</code>
 * that prevents the <code>/META-INF/native/windows64/jansi.dll</code> from being loaded
 * when running in a GraalVM native image on Windows.
 * </p>
 * <p>
 * Usage: call the {@link #systemInstall()} and {@link #systemUninstall()} methods defined
 * in this class
 * instead of calling <code>org.fusesource.jansi.AnsiConsole.systemInstall()</code>
 * and  <code>org.fusesource.jansi.AnsiConsole.systemUninstall()</code>.
 * </p>
 */
public class NativeJansi {

    /**
     * Install <code>AnsiConsole.out</code> to <code>System.out</code> and
     * <code>AnsiConsole.err</code> to <code>System.err</code>.
     *
     * @see #systemUninstall()
     */
    synchronized public static void systemInstall() {
        // workaround for https://github.com/fusesource/jansi/issues/162
        String arch = System.getProperty("os.arch");
        String vm = System.getProperty("java.vm.name");
        if (arch.endsWith("64") && "Substrate VM".equals(vm)) {
            System.setProperty("sun.arch.data.model", "64");
        }
        AnsiConsole.systemInstall();
    }

    /**
     * undo a previous {@link #systemInstall()}.  If {@link #systemInstall()} was called
     * multiple times, {@link #systemUninstall()} must be called the same number of times before
     * it is actually uninstalled.
     */
    synchronized public static void systemUninstall() {
        AnsiConsole.systemUninstall();
    }
}
