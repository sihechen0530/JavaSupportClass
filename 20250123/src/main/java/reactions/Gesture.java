package reactions;

import graphics.G;
import java.util.ArrayList;
import music.I;

// similar to ink
public class Gesture {
  private static List UNDO = new List();
  public Shape shape;
  public G.VS vs;
  // singleton, factory method
  private Gesture(Shape shape, G.VS vs) {
    this.shape = shape;
    this.vs = vs;
  }
  public static Gesture getNew(Ink ink) {
    // can return null
    Shape s = Shape.recognize(ink);
    return s == null ? null : new Gesture(s, ink.vs);
  }
  private void redoGesture() {
    Reaction r = Reaction.best(this);
    if (r != null) { r.act(this); }
  }
  private void doGesture() {
    Reaction r = Reaction.best(this);
    if (r != null) {
      UNDO.add(this);
      r.act(this);
    } else {
      recognized += " no bids";
    }
  }
  public static void undo() {
    if (!UNDO.isEmpty()) {
      UNDO.remove(UNDO.size() - 1);
      Layer.nuke();
      Reaction.nuke();
      UNDO.redo();
    }
  }

  public static I.Area AREA = new I.Area() {
    // anonymous class
    @Override
    public boolean hit(int x, int y) {
      return true;
    }
    @Override
    public void dn(int x, int y) { Ink.BUFFER.dn(x, y); }
    @Override
    public void drag(int x, int y) { Ink.BUFFER.drag(x, y); }
    @Override
    public void up(int x, int y) {
      Ink.BUFFER.up(x, y);
      Ink ink = new Ink();
      Gesture gest = Gesture.getNew(ink);
      Ink.BUFFER.clear();
      recognized = gest == null ? "null" : gest.shape.name;
      if (gest != null) {
        // TODO: hardwired undo
        if (gest.shape.name.equals("N-N")) {
          undo();
        } else {
          gest.doGesture();
        }
//        // best reaction matching the gesture
//        Reaction r = Reaction.best(gest);
//        if (r != null) {
//          r.act(gest);
//        } else {
//          recognized += " no bids";
//        }
      }
    }
  }; // end of AREA
  public static String recognized = "null";

  // ----------------- List ------------------------
  public static class List extends ArrayList<Gesture> {
    private void redo() {
      for (Gesture gest : this) {
        gest.redoGesture();
      }
    }
  }

}
