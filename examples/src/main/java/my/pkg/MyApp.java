package my.pkg;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.jansi.substratevm.AnsiConsole; // not org.fusesource.jansi.AnsiConsole

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
        AnsiConsole.systemInstall(); // enable colors on Windows
        int exitCode = new CommandLine(new MyApp()).execute(args);
        AnsiConsole.systemUninstall(); // cleanup when done
        System.exit(exitCode);
    }
}
