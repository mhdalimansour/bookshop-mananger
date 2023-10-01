package Adapter;

public class XLSFile {
  private String filePath;

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public XLSFile() {
  }

  public XLSFile(String filePath) {
    this.setFilePath(filePath);
  }
}
