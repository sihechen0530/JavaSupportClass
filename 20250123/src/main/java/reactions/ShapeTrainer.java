package reactions;

import graphics.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Objects;
import music.UC;

public class ShapeTrainer extends WinApp {

  public static String UNKNOWN = " <=this name is unknown";
  public static String KNOWN = " <=this is a known shape";
  public static String ILLEGAL = " <=this name not legal";

  public static String curName = "";
  public static String curState = ILLEGAL;

  public static Shape.Prototype.List pList = new Shape.Prototype.List();

  public ShapeTrainer() {
    super("ShapeTrainer", 1000, 800);
  }

  public void setState() {
    // curState = curName.equals("") || curName.equals("DOT") ? ILLEGAL : UNKNOWN;
    curState = !Shape.DB.isLegal(curName) ? ILLEGAL : UNKNOWN;
    if (curState == UNKNOWN) {
      if (Shape.DB.isKnown(curName)) {
        curState = KNOWN;
        pList = Shape.DB.get(curName).prototypes;
      } else {
        pList = null;
      }
    }
  }

  public void paintComponent(Graphics g) {
    G.clearScreen(g);
    g.setColor(Color.BLACK);
    g.drawString(curName, 600, 30);
    g.drawString(curState, 700, 30);
    g.setColor(Color.RED);
    Ink.BUFFER.show(g);
    if (pList != null) {
      pList.show(g);
    }
  }

  public void mousePressed(MouseEvent me) { Ink.BUFFER.dn(me.getX(), me.getY()); repaint(); }
  public void mouseDragged(MouseEvent me) { Ink.BUFFER.drag(me.getX(), me.getY()); repaint(); }
  public void mouseReleased(MouseEvent me) {
    Ink.BUFFER.up(me.getX(), me.getY());
    if (!Objects.equals(curState, ILLEGAL)) {
      // not training if not legal
      Ink ink = new Ink();
      Shape.Prototype proto;
      if (pList == null) {
        Shape s = new Shape(curName);
        Shape.DB.put(curName, s);
        pList = s.prototypes;
      }
      if (pList.bestDist(ink.norm) < UC.noMatchDist) {
        proto = Shape.Prototype.List.bestMatch;
        proto.blend(ink.norm);
      } else {
        proto = new Shape.Prototype();
        pList.add(proto);
      }
      setState();
    }
    repaint();
  }

  public void keyTyped(KeyEvent ke) {
    char c = ke.getKeyChar(); System.out.println("typed: " + c);
    // \n \r ascii
    curName = (c == ' ' || c == '\n' || c == '\r') ? "" : curName + c;
    if (c == '\n' || c == '\r') { Shape.Database.save(); }
    setState();
    repaint();
  }

  public static void main(String[] args) {
    PANEL = new ShapeTrainer();
    WinApp.launch();
  }

}
