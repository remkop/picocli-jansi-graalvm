package my.pkg;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class MyAppTest {

    static final String executable = "build/graal/myapp" + extension();

    private static String extension() {
        return System.getProperty("os.name").toLowerCase().startsWith("win") ? ".exe" : "";
    }

    @Test
    public void testUsageHelp() throws IOException, InterruptedException {
        Process process = new ProcessBuilder(executable, "--help").start();

        String exected = String.format("" +
                "Usage: myapp [-hV] [--user=<user>]%n" +
                "Example native CLI app with colors%n" +
                "  -h, --help          Show this help message and exit.%n" +
                "      --user=<user>   User name%n" +
                "  -V, --version       Print version information and exit.%n");
        assertEquals(exected, getStdOut(process));
        assertEquals("", getStdErr(process));
        process.waitFor(3, TimeUnit.SECONDS);
        assertEquals(0, process.exitValue());
    }

    @Test
    public void testVersionInfo() throws IOException, InterruptedException {
        Process process = new ProcessBuilder(executable, "--version").start();

        String exected = String.format("myapp 1.0%n"); // JVM: 1.8.0_222 (Oracle Corporation Substrate VM GraalVM dev)

        assertEquals(exected, getStdOut(process));
        assertEquals("", getStdErr(process));
        process.waitFor(3, TimeUnit.SECONDS);
        assertEquals(0, process.exitValue());
    }

    @Test
    public void testNormalOperation() throws IOException, InterruptedException {
        Process process = new ProcessBuilder(executable, "--user", "me").start();

        String exected = String.format("Hello, me%n");

        assertEquals(exected, getStdOut(process));
        assertEquals("", getStdErr(process));
        process.waitFor(3, TimeUnit.SECONDS);
        assertEquals(0, process.exitValue());
    }

    @Test
    public void testInvalidInput() throws IOException, InterruptedException {
        Process process = new ProcessBuilder(executable, "--unknown").start();

        String exected = String.format("" +
                "Unknown option: '--unknown'%n" +
                "Usage: myapp [-hV] [--user=<user>]%n" +
                "Example native CLI app with colors%n" +
                "  -h, --help          Show this help message and exit.%n" +
                "      --user=<user>   User name%n" +
                "  -V, --version       Print version information and exit.%n");
        assertEquals(exected, getStdErr(process));
        assertEquals("", getStdOut(process));
        process.waitFor(3, TimeUnit.SECONDS);
        assertEquals(2, process.exitValue());
    }

    private String getStdOut(Process process) throws IOException {
        return readFully(process.getInputStream());
    }

    private String getStdErr(Process process) throws IOException {
        return readFully(process.getErrorStream());
    }

    private String readFully(InputStream in) throws IOException {
        byte[] buff = new byte[10 * 1024];
        int len = 0;
        int total = 0;
        while ((len = in.read(buff, total, buff.length - total)) > 0) {
            total += len;
        }
        return new String(buff, 0, total);
    }
}
