package my.pkg;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.jansi.graalvm.AnsiConsole; // not org.fusesource.jansi.AnsiConsole

@Command(name = "myapp", mixinStandardHelpOptions = true,
        version = {
                "myapp 1.0",
                //"JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})",
        },
        description = "Example native CLI app with colors")
public class MyApp implements Runnable {

    @Option(names = "--user", description = "User name")
    String user;

    public void run() {
        System.out.printf("Hello, %s%n", user);
    }

    public static void main(String[] args) {
        int exitCode;
        try (AnsiConsole ansi = AnsiConsole.windowsInstall()) { // enable colors on Windows
            exitCode = new CommandLine(new MyApp()).execute(args);
        } // Closeable does cleanup when done
        System.exit(exitCode);
    }
}
