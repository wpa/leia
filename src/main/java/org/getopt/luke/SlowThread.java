package org.getopt.luke;

import org.eu.bitzone.Leia;

public abstract class SlowThread extends Thread {
  private Object ui;
  private Leia app;

  public SlowThread(Leia app) {
    this.app = app;
    ui = app.addComponent(null, "/xml/wait.xml", null, null);
  }

  public abstract void execute();

  public final void run() {
    app.add(ui);
    try {
      execute();
    } catch (Throwable t) {
      t.printStackTrace();
      app.showStatus(t.getMessage());
    }
    app.remove(ui);
    app.repaint();
  }
}
