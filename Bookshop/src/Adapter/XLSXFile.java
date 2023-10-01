package Adapter;

import java.io.File;

public class XLSXFile {
  private String filePath;

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public XLSXFile() {
  }

  public XLSXFile(String filePath) {
    this.setFilePath(filePath);
  }

  public String getFileName() {
    if (filePath != null) {
      File file = new File(filePath);
      return file.getName();
    }
    return null; // Handle cases where filePath is null.
  }
}
