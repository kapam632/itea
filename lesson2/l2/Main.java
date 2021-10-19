package l2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("pass the parameter locale");
            return;
        }
        Main main = new Main();
        Scanner scanner = new Scanner(System.in);
        main.process(main.prepareBundle(args[0]), new File(System.getProperty("user.dir")), scanner);
        scanner.close();
    }

    private void process(ResourceBundle bundle, File file, Scanner scanner) {
        boolean exit = false;
        while (!exit) {
            System.out.println(prepareHelpHeader(bundle));
            System.out.print(String.format("%s>", file.getAbsolutePath()));
            String userInput = scanner.nextLine();
            String[] split = userInput.split(" ");
            String cmd = split[0];
            switch (cmd) {
                case "..":
                    String parent = file.getParent();
                    file = Objects.isNull(parent) ? file : new File(parent);
                    break;
                case "cd":
                    if (split.length > 1) {
                        File inputFile = buildFile(split[1], file.getAbsolutePath());
                        if (Objects.nonNull(inputFile) && inputFile.isDirectory()) {
                            file = inputFile;
                        } else {
                            System.out.println("No such directory.");
                        }
                    }
                    break;
                case "fileName":
                    if (split.length > 1) {
                        printFileBody(buildFile(split[1], file.getAbsolutePath()));
                    }
                    break;
                case "stop":
                    exit = true;
                    break;
                case "ls":
                    printNames(file);
                    break;
                default:
                    System.out.println(prepareHelpHeader(bundle));
            }
        }
    }

    private ResourceBundle prepareBundle(String inputLang) {
        switch (inputLang) {
            case "de":
                return ResourceBundle.getBundle("message", Locale.GERMANY);
            case "fr":
                return ResourceBundle.getBundle("message", Locale.FRANCE);
            case "en":
                return ResourceBundle.getBundle("message", Locale.ENGLISH);
            default:
                throw new IllegalArgumentException("No such locale");
        }
    }

    private File buildFile(String dirName, String currentAbsolutePath) {
        File inputParamFile = new File(dirName);
        if (inputParamFile.isAbsolute()) {
            return inputParamFile;
        } else {
            File file = new File(currentAbsolutePath + File.separator + dirName);
            if (!file.exists()) {
                return null;
            }
            return file;
        }
    }

    private static String prepareHelpHeader(ResourceBundle bundle) {
        return "cd - " +
                bundle.getString("cd") +
                ".. - " +
                bundle.getString("..") +
                "fileName - " +
                bundle.getString("fileName");

    }

    private static void printNames(File file) {
        if (file.exists()) {
            printDirectoryName(file);
            printFileName(file);
        }
    }

    private static void printDirectoryName(File file) {
        Stream.of(file.listFiles()).filter(File::isDirectory).forEach((f) -> System.out.println(f.getName()));
    }

    private static void printFileName(File file) {
        Stream.of(file.listFiles()).filter(File::isFile).forEach((f) -> System.out.println(f.getName()));
    }

    private static void printFileBody(File file) {
        if (Objects.nonNull(file) && file.isFile() && file.canRead()) {
            try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
                br.lines().forEach(System.out::println);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No such file");
        }
    }
}
