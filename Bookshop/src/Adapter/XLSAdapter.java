package Adapter;

import java.io.IOException;

public class XLSAdapter {
  private XLSXFile xlsxFile;

  public XLSAdapter(XLSXFile xlsxFile) {
    this.xlsxFile = xlsxFile;
  }

  public String convert(String outputPath) {
    String inputXLSX = this.xlsxFile.getFilePath();
    String outputFile = "D:\\CS\\Bookshop\\E-Manager\\" + outputPath;

    String pythonScriptPath = "D:\\CS\\Bookshop\\E-Manager\\converter.py";

    // Python conversion:
    try {
      ProcessBuilder process = new ProcessBuilder("python", pythonScriptPath, inputXLSX, outputFile);
      Process p = process.start();
      int exitCode = p.waitFor();
      System.out.println(exitCode);
      if (exitCode == 0) {
        System.out.println("Conversion completed successfully.");
      } else {
        System.err.println("Error during conversion.");
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

    return outputFile;
  }
}
