package sandbox;

import graphics.G;
import graphics.WinApp;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import music.UC;
import reactions.Ink;

public class PaintInk extends WinApp {
  public static Ink.List inkList = new Ink.List();
  static {
    // code run at the initialization time
//    inkList.add(new Ink());
  };
  public PaintInk() {
    super("PaintInk", UC.mainWindowWidth, UC.mainWindowHeight);
  }
  public void paintComponent(Graphics g) {
    G.clearScreen(g);
    g.setColor(Color.RED);
    Ink.BUFFER.show(g);
    inkList.show(g);
  }

  public void mousePressed(MouseEvent me) {Ink.BUFFER.dn(me.getX(), me.getY()); repaint();}
  public void mouseDragged(MouseEvent me) {Ink.BUFFER.drag(me.getX(), me.getY()); repaint();}
  public void mouseReleased(MouseEvent me) {
    Ink.BUFFER.up(me.getX(), me.getY());
    inkList.add(new Ink());
    repaint();
  }

  public static void main(String[] args) {
    PANEL = new PaintInk();
    WinApp.launch();
  }
}
