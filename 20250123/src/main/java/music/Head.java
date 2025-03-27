package music;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import reactions.Gesture;
import reactions.Mass;
import reactions.Reaction;

public class Head extends Mass implements Comparable<Head> {

  public Staff staff;
  public int line;  // line is y coordinate in disguise, 0 = top line of staff
  public Time time;
  public Glyph forcedGlyph = null;  // overwrite glyph
  public Stem stem = null;
  public boolean wrongSide = false;

  public Head(Staff staff, int x, int y) {
    super("NOTE");
    this.staff = staff;
    time = staff.sys.getTime(x);
//    int H = staff.fmt.H;  // size
//    int top = staff.yTop() - H;  // one space above the top line
//    this.line = (y - top + H / 2) / H - 1;  // rounding
    this.line = staff.lineOfY(y);
//    System.out.println("line: " + line);
    time.heads.add(this);

    addReaction(new Reaction("S-S") {  // stem or unstem heads
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        int W = Head.this.W(), Y = Head.this.Y();
        System.out.println("head reaction");
        // cross
        if (y1 > Y || y2 < Y) {
          return UC.noBid;
        }
        System.out.println("head reaction y ok");
        int HL = Head.this.time.x, HR = HL + W;
        // too far
        if (x < HL - W || x > HR + W) {
          return UC.noBid;
        }
        System.out.println("head reaction x, y ok");
        if (x < HL + W / 2) {
          System.out.println("left");
          return HL - x;
        }
        if (x > HR - W / 2) {
          System.out.println("right");
          return x - HR;
        }
        return UC.noBid;
      }

      @Override
      public void act(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        Staff staff = Head.this.staff;
        Time t = Head.this.time;
        int W = Head.this.W();
        boolean up = x > (t.x + W / 2);  // gesture to shape relation
        if (Head.this.stem == null) {
//          t.stemHeads(staff, up, y1, y2);
          Stem.getStem(staff, time, y1, y2, up);
        } else {
          t.unStemHeads(y1, y2);
        }
      }
    });

    addReaction(new Reaction("DOT") {
      @Override
      public int bid(Gesture g) {
        int xh = X(), yh = Y(), h = staff.fmt.H, w = W();
        int x = g.vs.xM(), y = g.vs.yM();
        if (x < xh || x > xh + 2 * w || y < yh - h || y > yh + h) { return UC.noBid; }
        return Math.abs(xh + w - x) + Math.abs(yh - y);
      }
      @Override
      public void act(Gesture g) {
        if (Head.this.stem != null) { Head.this.stem.cycleDot(); }
      }
    });
  }

  public void show(Graphics g) {
    int H = staff.fmt.H;
//    Glyph.HEAD_Q.showAt(g, H, time.x, staff.yTop() + line * H);
//    g.setColor(wrongSide ? Color.GREEN : Color.BLUE);
//    if (stem != null && stem.heads.size() != 0 && this == stem.firstHead()) {
//      // first head
//      g.setColor(Color.RED);
//    }
    g.setColor(stem == null ? Color.RED : Color.BLACK);
    (forcedGlyph != null ? forcedGlyph : normalGlyph()).showAt(g, H, X(), Y());
    if (stem != null) {
      int off = UC.augDotOffset, sp = UC.augDotSpacing;
      for (int i = 0; i < stem.nDot; i++) {
        g.fillOval(time.x + off + i * sp, Y(), 2 * H / 3, 2 * H / 3);
      }
    }
  }

  private Glyph normalGlyph() {
    if (stem == null) { return Glyph.HEAD_Q; }
    if (stem.nFlag == -1) { return Glyph.HEAD_HALF; }
    if (stem.nFlag == -2) { return Glyph.HEAD_W; }
    return Glyph.HEAD_Q;
  }

  public int X() {
    int res = time.x;
    if (wrongSide) { res += (stem != null && stem.isUp ? W() : -W());}
    return res;
  }

  public int Y() {
    return staff.yOfLine(line);
  }

  public int W() {
    return 24 * staff.fmt.H / 10;
  }

  public void delete() {
    // stub
    time.heads.remove(this);
  }

  public void unStem() {
    if (stem != null) {
      stem.heads.remove(this);
      if (stem.heads.size() == 0) {
        // last head off
        stem.deleteStem();
      }
      stem = null;
      wrongSide = false;
    }
  }

//  public void joinStem(Stem s) {
//    if (stem != null) {
//      unStem();
//    }
//    s.heads.add(this);
//    stem = s;
//  }

  @Override
  public int compareTo(Head h) {
    return (staff.iStaff != h.staff.iStaff) ? staff.iStaff - h.staff.iStaff : line - h.line;
  }

  // ---------------- Head.List -----------------
  public static class List extends ArrayList<Head> {

  }

}
