package org.getopt.luke;

import java.io.*;

import org.eu.bitzone.Leia;

public class PanelPrintWriter extends PrintStream {
  static ByteArrayOutputStream baos = new ByteArrayOutputStream();
  Object panel;
  Leia leia;

  public PanelPrintWriter(Leia leia, Object panel) {
    super(baos);
    baos.reset();
    // retrieve previous text and separate it
    String text = leia.getString(panel, "text");
    if (text != null && text.length() > 0) {
      try {
        baos.write(text.getBytes());
        baos.write('\n');
        baos.write('\n');
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    this.panel = panel;
    this.leia = leia;
  }

  /* (non-Javadoc)
   * @see java.io.PrintWriter#println(java.lang.String)
   */
  @Override
  public void println(String x) {
    try {
      baos.write(x.getBytes());
      baos.write('\n');
    } catch (IOException e) {
      e.printStackTrace();
    }
    String text = new String(baos.toByteArray());
    leia.setString(panel, "text", text);
    leia.setInteger(panel, "start", text.length());
    leia.setInteger(panel, "end", text.length());
  }

}
