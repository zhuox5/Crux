package crux.backend;

import java.util.*;
import java.io.*;
import crux.printing.IRValueFormatter;

public class CodePrinter {
  PrintStream out;
  public StringBuffer myStringBuffer = new StringBuffer();

  public void bufferCode(String s){
    myStringBuffer.append("    " + s + "\n");
  }

  public void printBuffer(){
    out.print(myStringBuffer);
    myStringBuffer = new StringBuffer();//init it each time after print
  }

  public CodePrinter(String name) {
    try {
      out = new PrintStream(name);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public void printLabel(String s) {
    out.println(s);
  }

  public void printCode(String s) {
    out.println("    " + s);
  }

  public void close() {
    out.close();
  }

}
