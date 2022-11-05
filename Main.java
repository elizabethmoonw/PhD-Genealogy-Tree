package a4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The main program of the A4 assignment. It reads an academic genealogy in CSV format and supports
 * various commands querying the genealogy tree.
 */
public class Main {
    /**
     * Name of the file to read commands from, or an empty string to signify reading from console.
     */
    public static String inputFile = "";
    /**
     * Name of the file to read the genealogy from
     */
    public static String csvFileName = "professors.csv";
    /**
     * The academic genealogy tree
     */
    public static PhDTree professorTree;

    /**
     * Effect: Print a usage message to standard error.
     */
    public static void usage() {
        System.err.println("Usage: a4.Main [--help] [-i <input script>] [filename.csv]");
    }

    public static void main(String[] args) {
        if (!processProgramArguments(args)) {
            usage();
            System.exit(1);
        }
        try {
            professorTree = csvToTree(csvFileName);
            processCommands();
        } catch (IOException e) {
            System.err.println("Could not read file: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Effect: Parse the program arguments given in {@code args} and set global parameters like
     * {@code inputFile} and {@code csvFileName} appropriately.
     * Returns true iff all arguments were parsed and the flag "--help" was not encountered.
     */
    private static boolean processProgramArguments(String[] args) {
        int i;
        for (i = 0; i < args.length; i++) {
            if (args[i].equals("-i")) {
                if (i + 1 < args.length) {
                    inputFile = args[i + 1];
                    i++;
                } else {
                    return false;
                }
            } else if (args[i].equals("--help")) {
                return false;
            } else {
                break;
            }
        }
        if (i < args.length) {
            csvFileName = args[i];
            i++;
        }
        return (i == args.length);
    }

    /**
     * Returns a PhDTree representation of the CSV file named by filename. Throws an appropriate
     * IOException if there was an error reading the file.
     */
    public static PhDTree csvToTree(String filename)
            throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String[] header = reader.readLine().split(","); // skip header line
            if (!Arrays.equals(header,
                    new String[]{"advisee", "year", "advisor"})) {
                throw new IOException("Invalid file format");
            }

            // TODO 1
            String[] ln1 = reader.readLine().split(",");
            String[] name = ln1[0].split(" ");
            Professor rootProf = new Professor(name[0], name[1]);
            PhDTree tree = new PhDTree(rootProf, Integer.valueOf(ln1[1]));

            String line = reader.readLine();
            while (line != null) {
                String[] currLine = line.split(",");
                String[] adviseeInfo = currLine[0].split(" ");
                String adviseeFirst = adviseeInfo[0];
                String adviseeLast = adviseeInfo[1];

                String[] advisorInfo = currLine[2].split(" ");
                String advisorFirst = advisorInfo[0];
                String advisorLast = advisorInfo[1];
                Professor advisee = new Professor(adviseeFirst, adviseeLast);
                int year = Integer.valueOf(currLine[1]);

                Professor advisor = new Professor(advisorFirst, advisorLast);

                tree.insert(advisor, advisee, year);
                line = reader.readLine();
            }
            return tree;

        } catch (NotFound e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Effect: Reads commands from a Scanner and executes them. Returns when the "exit" command is
     * read.
     */
    public static void processCommands() {
        Scanner sc;
        if (inputFile.isEmpty()) {
            sc = new Scanner(System.in);
        } else {
            try {
                sc = new Scanner(new File(inputFile));
            } catch (IOException exc) {
                System.out.println(exc.getMessage());
                return;
            }
        }
        while (true) {
            if (inputFile.isEmpty()) {
                System.out.print("Please enter a command: ");
            }
            try {
                String input = sc.nextLine().trim();
                String[] cmd = input.split(" +");
                switch (cmd[0].toLowerCase()) {
                    case "help":
                        doHelp();
                        break;
                    case "contains":
                        doContains(cmd);
                        break;
                    case "size":
                        doSize(cmd);
                        break;
                    case "advisor":
                        doAdvisor(cmd);
                        break;
                    case "ancestor":
                        doAncestor(cmd);
                        break;
                    case "find":
                        doFind(cmd);
                        break;
                    case "lineage":
                        doLineage(cmd);
                        break;
                    case "print":
                        doPrint(cmd);
                        break;
                    case "exit":
                        return;
                    default:
                        System.out.println(
                                "This is not a valid command. For help, enter the command \"help\"");
                }
            } catch (NoSuchElementException exc) {
                // no more lines on input
                return;
            }
        }
    }

    /**
     * Effect: Print a message about the command being invalid.
     */
    private static void invalidCommand(String cmd) {
        System.out.println("Invalid " + cmd + " command. " +
                "Enter the command \"help\" for information about that command.");
    }

    /** Effect: perform the help command */
    public static void doHelp() {
        System.out.println("\nhelp\n" +
                "contains <firstName> <lastName> : whether this professor is in the PhD tree\n"
                +
                "size <firstName> <lastName> : the number of nodes in the tree with the given professor at the root\n"
                +
                "advisor <firstName> <lastName> : the direct advisor of the given professor\n" +
                "ancestor <firstName1> <lastName1> <firstName2> <lastName2> : the common ancestor between the two given professors\n"
                +
                "find <firstName> <lastName> : the PhDTree with the given professor at the root\n"
                +
                "lineage <firstName> <lastName> : the sequence of professors that are related and come before this professor\n"
                +
                "print : prints out to the console the entire tree in your .csv file\n" +
                "exit : exit the program\n");
    }

    /**
     * Effect: perform the "contains" command, where cmd contains the words in the command.
     */
    public static void doContains(String[] cmd) {
    // TODO 2
        Professor prof = new Professor(cmd[1],cmd[2]);
        if (professorTree.contains(prof)){ System.out.println("This professor is contained in the PhD tree.");}
        else {System.out.println("This professor is not contained in the PhD tree.");}
    }

    /**
     * Effect: perform the "size" command, where cmd contains the words in the command.
     */
    public static void doSize(String[] cmd) {

    // TODO 3
        Professor prof = new Professor(cmd[1],cmd[2]);
        try {
            PhDTree foundTree = professorTree.findTree(prof);
            System.out.println("The number of nodes in this tree is: " + foundTree.size() + ".");
        } catch (NotFound e) {
            System.out.println("This person does not exist in the tree.");
        }
    }

    /**
     * Effect: perform the "advisor" command, where cmd contains the words in the command.
     */
    public static void doAdvisor(String[] cmd) {
    // TODO 4
        Professor profAdvisor = new Professor(cmd[1],cmd[2]);
        try {
            if (professorTree.findAdvisor(profAdvisor) != null){
            System.out.println("The advisor of this advisee is: " + professorTree.findAdvisor(profAdvisor) + ".");
            } else {
                System.out.println("This person does not have an advisor.");
            }
        } catch (NotFound e) {
            if (professorTree.contains(profAdvisor)) { System.out.println("This person does not have an advisor.");}
            else {System.out.println("This professor does not exist in the tree.");}
        }

    }

    /**
     * Effect: perform the "ancestor" command, where cmd contains the words in the command.
     */
    public static void doAncestor(String[] cmd) {
    // TODO 5
        Professor prof1  = new Professor(cmd[1],cmd[2]);
        Professor prof2 = new Professor(cmd[3],cmd[4]);
        try {
            System.out.println("The common ancestor of these scholars is: " +
                    professorTree.commonAncestor(prof1, prof2) + ".");
        } catch (NotFound e) {
            System.out.println("These scholars do not have a common ancestor");
        }
    }

    /**
     * Effect: perform the "find" command, where cmd contains the words in the command.
     */
    public static void doFind(String[] cmd) {
    // TODO 6
        Professor prof = new Professor(cmd[1],cmd[2]);
        String firstName = cmd[1];
        String lastName = cmd[2];
        try {
            PhDTree found = professorTree.findTree(prof);
            System.out.println("The PhDTree with "+ firstName + " " + lastName + " at the root is " +
                    found.toString() + ".");
        } catch (NotFound e) {
            System.out.println("This person does not exist in the tree.");
        }
    }

    /**
     * Effect: perform the "print" command, where cmd contains the words in the command.
     */
    public static void doPrint(String[] cmd) {
    // TODO 7
        System.out.println(professorTree.toStringVerbose());
    }

    /**
     * Effect: perform the "lineage" command, where cmd contains the words in the command.
     */
    public static void doLineage(String[] cmd) {
    // TODO 8
        Professor prof = new Professor(cmd[1],cmd[2]);

        try{
            System.out.print("The lineage is: ");
            List lineagePrint = professorTree.findAcademicLineage(prof);
            for (int j = 0; j < lineagePrint.size()-1; j++ ){
                System.out.print(lineagePrint.get(j) + "--");
            }
            System.out.print(prof + "." + "\n");

        } catch (NotFound e){
            System.out.println("This person does not exist in the tree.");
        }

    }
}
