package crux;

import java.io.FileInputStream;

/**
 * This Class is the Entry point for the project, it contains the main function.
 * 
 */
public final class Compiler {
  private static Driver driver = new Driver();

  public static void main(String[] args) {
    try {
      if (!handleArgs(args)) {
        return;
      }

      var result = driver.run();
      if (result == State.Error)
        System.exit(-1);
    } catch (Exception e) {
      System.err.println("error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * This method handle parsing the command line args and enable the compiler's stage flag based on
   * the parsed input
   */
  private static boolean handleArgs(String[] args) {
    for (var i = 0; i < args.length; ++i) {
      var arg = args[i];
      switch (arg) {
        case "--help":
          displayHelp();
          return false;
        case "--authors":
          displayAuthors();
          return false;
        case "--check-types":
          driver.enableTypeCheck();
          break;
        case "--include-types":
          driver.enableIncludeTypes();
          break;
        case "--print-pt":
          driver.enablePrintParseTree();
          break;
        case "--print-ast":
          driver.enablePrintAst();
          break;
        case "--serialize":
          driver.enableSerialize();
          break;
        case "--print-ir":
          driver.enablePrintIR();
          break;
        case "--emulator":
          driver.enableEmulator();
          break;
        case "--debug-emulator":
          driver.enableDebugEmulator();
          break;
        case "--read-ast":
          try {
            driver.readAST(new FileInputStream(args[i + 1]));
          } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
          }
          i++;
          break;
        case "--read-type":
          try {
            driver.readASTTYPE(new FileInputStream(args[i + 1]));
          } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
          }
          i++;
          break;
        case "--read-ir":
          try {
            driver.readIR(new FileInputStream(args[i + 1]));
          } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
          }
          i++;
          break;
        case "--emulator-input": {
          if (driver.hasEmulatorInputFile())
            throw new RuntimeException("multiple input files");
          var inputFile = args[++i];
          driver.setEmulatorInputFile(inputFile);
          break;
        }
        default:
          if (arg.startsWith("-"))
            throw new RuntimeException(String.format("unrecognized command line option '%s'", arg));
          if (driver.hasInputFile())
            throw new RuntimeException(
                String.format("multiple input files: '%s' and '%s'", driver.getInputFile(), arg));
          driver.setInputFile(arg);
          break;
      }
    }

    if (!driver.hasInputFile()) {
      displayHelp();
      throw new RuntimeException("no input file.");
    }

    return true;
  }

  private static void displayHelp() {
    System.out.println("usage: <crux> [options] [file]");
    System.out.println("options:");
    System.out.println("--help\t\t\t\tDisplay this information.");
    System.out.println("--authors\t\t\tDisplay the list of authors.");
    System.out.println("--check-types\t\t\tPerform a type check for the input program.");
    System.out
        .println("--include-types\t\t\tInclude type information in printed ast (see --print-ast).");
    System.out.println("--print-pt\t\t\tPrint the parse tree to stdout.");
    System.out.println("--print-ast\t\t\tPrint the abstract syntax tree to stdout.");
    System.out.println(
        "--serialize\t\t\tDump serialized versions of compiler represenatations out to files.");
    System.out.println("--print-ir\t\t\tDump dot compatible representation of IR.");
    System.out.println("--emulator\t\t\tRun Emulator on IR.");
    System.out.println("--debug-emulator\t\tRun Emulator on IR with debugging turned on.");
    System.out.println("--emulator-input <input file>\tInput File for the emulator");
    System.out.println("--read-ast <input file>\t\tRead serialized version of AST in,");
    System.out.println("--read-type <input file>\tRead serialized version of type-checked AST in,");
    System.out.println("--read-ir <input file>\t\tRead serialized version of IR/CFG in,");
  }

  private static void displayAuthors() {
    var separator = "";
    for (var author : Authors.all) {
      System.out.print(separator);
      System.out.printf("name: %s%n", author.name);
      System.out.printf("student id: %s%n", author.studentId);
      System.out.printf("UCINetID: %s%n", author.uciNetId);
      separator = System.lineSeparator();
    }
  }
}
