package picocli.jansi.graalvm;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Enables the use of ANSI escape codes in GraalVM native image applications,
 * especially, but not limited to, running on Windows.
 * <p>
 * Each method in this class calls {@link Workaround#enableLibraryLoad()}
 * before delegating to the corresponding method in the
 * {@code org.fusesource.jansi.AnsiConsole} class.
 * This works around a problem in Jansi's
 * <code>org.fusesource.hawtjni.runtime.Library</code>
 * that prevents the native libraries embedded in the Jansi JAR in
 * <code>/META-INF/native/*</code> from being loaded
 * when running in a GraalVM native image.
 * </p>
 * <p>
 * Usage: use this class as a drop-in replacement for
 * <code>org.fusesource.jansi.AnsiConsole</code>.
 * </p>
 */
public class AnsiConsole implements Closeable {

    private AnsiConsole() {
    }

    /**
     * Install <code>AnsiConsole.out</code> to <code>System.out</code> and
     * <code>AnsiConsole.err</code> to <code>System.err</code>.
     *
     * @see #systemUninstall()
     */
    synchronized public static void systemInstall() {
        Workaround.enableLibraryLoad();
        org.fusesource.jansi.AnsiConsole.systemInstall();
    }

    /**
     * undo a previous {@link #systemInstall()}.  If {@link #systemInstall()} was called
     * multiple times, {@link #systemUninstall()} must be called the same number of times before
     * it is actually uninstalled.
     */
    synchronized public static void systemUninstall() {
        Workaround.enableLibraryLoad();
        org.fusesource.jansi.AnsiConsole.systemUninstall();
    }

    @Deprecated
    public static OutputStream wrapOutputStream(final OutputStream stream) {
        Workaround.enableLibraryLoad();
        return org.fusesource.jansi.AnsiConsole.wrapOutputStream(stream);
    }

    public static PrintStream wrapSystemOut(final PrintStream ps) {
        Workaround.enableLibraryLoad();
        return org.fusesource.jansi.AnsiConsole.wrapSystemOut(ps);
    }

    @Deprecated
    public static OutputStream wrapErrorOutputStream(final OutputStream stream) {
        Workaround.enableLibraryLoad();
        return org.fusesource.jansi.AnsiConsole.wrapErrorOutputStream(stream);
    }

    public static PrintStream wrapSystemErr(final PrintStream ps) {
        Workaround.enableLibraryLoad();
        return org.fusesource.jansi.AnsiConsole.wrapSystemErr(ps);
    }

    @Deprecated
    public static OutputStream wrapOutputStream(final OutputStream stream, int fileno) {
        Workaround.enableLibraryLoad();
        return org.fusesource.jansi.AnsiConsole.wrapOutputStream(stream, fileno);
    }

    /**
     * Wrap PrintStream applying rules in following order:<ul>
     * <li>if <code>jansi.passthrough</code> is <code>true</code>, don't wrap but just passthrough (console is
     * expected to natively support ANSI escape codes),</li>
     * <li>if <code>jansi.strip</code> is <code>true</code>, just strip ANSI escape codes inconditionally,</li>
     * <li>if OS is Windows and terminal is not Cygwin or Mingw, wrap as WindowsAnsiPrintStream to process ANSI escape codes,</li>
     * <li>if file descriptor is a terminal (see <code>isatty(int)</code>) or <code>jansi.force</code> is <code>true</code>,
     * just passthrough,</li>
     * <li>else strip ANSI escape codes (not a terminal).</li>
     * </ul>
     *
     * @param ps original PrintStream to wrap
     * @param fileno file descriptor
     * @return wrapped PrintStream depending on OS and system properties
     */
    public static PrintStream wrapPrintStream(final PrintStream ps, int fileno) {
        Workaround.enableLibraryLoad();
        return org.fusesource.jansi.AnsiConsole.wrapPrintStream(ps, fileno);
    }

    /**
     * If the standard out natively supports ANSI escape codes, then this just
     * returns System.out, otherwise it will provide an ANSI aware PrintStream
     * which strips out the ANSI escape sequences or which implement the escape
     * sequences.
     *
     * @return a PrintStream which is ANSI aware.
     * @see #wrapPrintStream(PrintStream, int)
     */
    public static PrintStream out() {
        Workaround.enableLibraryLoad();
        return org.fusesource.jansi.AnsiConsole.out();
    }

    /**
     * If the standard out natively supports ANSI escape codes, then this just
     * returns System.err, otherwise it will provide an ANSI aware PrintStream
     * which strips out the ANSI escape sequences or which implement the escape
     * sequences.
     *
     * @return a PrintStream which is ANSI aware.
     * @see #wrapPrintStream(PrintStream, int)
     */
    public static PrintStream err() {
        Workaround.enableLibraryLoad();
        return org.fusesource.jansi.AnsiConsole.err();
    }

    /**
     * Calls {@link #systemInstall()} if running on a Windows OS, otherwise does nothing.
     * @return a {@link Closeable} instance
     */
    public static AnsiConsole windowsInstall() {
        boolean windows = System.getProperty("os.name").toLowerCase().startsWith("win");
        if (windows) { systemInstall(); }
        return new AnsiConsole();
    }

    /**
     * Calls {@link #systemUninstall()} if running on a Windows OS, otherwise does nothing.
     */
    @Override
    public void close() {
        boolean windows = System.getProperty("os.name").toLowerCase().startsWith("win");
        if (windows) { systemUninstall(); }
    }
}
