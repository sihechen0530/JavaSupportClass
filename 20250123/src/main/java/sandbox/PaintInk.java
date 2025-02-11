package sandbox;

import graphics.G;
import graphics.WinApp;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import music.UC;
import reactions.Ink;
import reactions.Shape;
import reactions.Shape.Prototype.List;

public class PaintInk extends WinApp {
  public static Shape.Prototype.List pList = new Shape.Prototype.List();
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
//    g.drawString("points: " + Ink.BUFFER.n, 600, 30);
    if (inkList.size() > 1) {
      // at least two elements for closest neighbor matching
      int last = inkList.size() - 1;
      int dist = inkList.get(last).norm.dist(inkList.get(last - 1).norm);
      g.setColor(dist > UC.noMatchDist ? Color.RED : Color.BLACK);
      g.drawString("Dist: " + dist, 600, 60);
      pList.show(g);
    }
  }

  public void mousePressed(MouseEvent me) {Ink.BUFFER.dn(me.getX(), me.getY()); repaint();}
  public void mouseDragged(MouseEvent me) {Ink.BUFFER.drag(me.getX(), me.getY()); repaint();}
  public void mouseReleased(MouseEvent me) {
    Ink.BUFFER.up(me.getX(), me.getY());
    Ink ink = new Ink();
    Shape.Prototype proto;
    inkList.add(ink);
    if (pList.bestDist(ink.norm) < UC.noMatchDist) {
      // match found, so blend
      proto = Shape.Prototype.List.bestMatch;
      proto.blend(ink.norm);
    } else {
      proto = new Shape.Prototype();
      pList.add(proto);
    }
    ink.norm = proto;
    repaint();
  }

  public static void main(String[] args) {
    PANEL = new PaintInk();
    WinApp.launch();
  }
}
