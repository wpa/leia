package org.getopt.luke;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.eu.bitzone.Leia;

public class Progress implements Observer {
  Object ui;
  Object bar, msg;
  boolean showing = false;
  Leia leia;

  public Progress(Leia leia) {
    try {
      ui = leia.parse("/xml/progress.xml", this);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    this.leia = leia;
    bar = leia.find(ui, "bar");
    msg = leia.find(ui, "msg");
  }

  public void setMessage(String message) {
    leia.setString(msg, "text", message);
  }

  public void show() {
    leia.add(ui);
    leia.repaint();
    showing = true;
  }

  public void hide() {
    leia.remove(ui);
    showing = false;
  }

  public void cancel(Object dialog) {

  }

  public void update(Observable o, Object arg) {
    if (arg instanceof ProgressNotification) {
      ProgressNotification pn = (ProgressNotification)arg;
      if (pn.message != null) {
        leia.setString(msg, "text", pn.message);
      }
      leia.setInteger(bar, "minimum", pn.minValue);
      leia.setInteger(bar, "maximum", pn.maxValue);
      leia.setInteger(bar, "value", pn.curValue);
    } else {
      leia.setString(msg, "text", arg.toString());
    }
    if (!showing) {
      show();
    }
    leia.doLayout(ui);
  }
}
