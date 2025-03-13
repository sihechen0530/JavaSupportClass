package music;

import java.awt.Graphics;
import graphics.G;
import java.util.ArrayList;
import reactions.Gesture;
import reactions.Mass;
import reactions.Reaction;

public class Staff extends Mass {
  public Sys sys;
  public int iStaff;  // index of where it lives in the dad's list
  public G.HC staffTop;
  public Fmt fmt;

  public Staff(Sys sys, int iStaff, G.HC staffTop, Staff.Fmt fmt) {
    super("BACK");
    this.sys = sys;
    this.iStaff = iStaff;
    this.staffTop = staffTop;
    this.fmt = fmt;

    addReaction(new Reaction("S-S") {
      @Override
      public int bid(Gesture g) {
        Page PAGE = sys.page;
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        int left = PAGE.margins.left, right = PAGE.margins.right;
        if (x < left || x > (right + UC.barToMarginSnap)) { return UC.noBid; }
//        if (x < PAGE.margins.left || x > PAGE.margins.right) { return UC.noBid; }
        int d = Math.abs(y1 - yTop()) + Math.abs(y2 - yBot());  // measure
        int bias = UC.barToMarginSnap;  // maximum cycle bar bid must outbid create bar
        return d < 30 ? d + bias : UC.noBid;
      }

      @Override
      public void act(Gesture g) {
        new Bar(Staff.this.sys, g.vs.xM());
      }
    });

    addReaction(new Reaction("S-S") {
      // toggle barContinues
      @Override
      public int bid(Gesture g) {
        if (Staff.this.sys.iSys != 0) { return UC.noBid; }
        int y1 = g.vs.yL(), y2 = g.vs.yH();
        if (iStaff == sys.staffs.size() - 1) { return UC.noBid; } // last staff, cannot set
        if (Math.abs(y1 - yBot()) > 20) { return UC.noBid; }  // close to bottom
        Staff nextStaff = sys.staffs.get(iStaff + 1);
        if (Math.abs(y2 - nextStaff.yTop()) > 20) { return UC.noBid; }  // too far from next top
        return 10;  // low value
      }

      @Override
      public void act(Gesture g) {
        fmt.toggleBarContinues();
      }
    });

    addReaction(new Reaction("SW-SW") {  // add note to staff
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yM();
        if (x < sys.page.margins.left || x > sys.page.margins.right) { return UC.noBid; }
        int H = fmt.H, top = yTop() - H, bot = yBot() + H;
        if (y < top || y > bot) { return UC.noBid; }
        return 10;
      }

      @Override
      public void act(Gesture g) {
        new Head(Staff.this, g.vs.xM(), g.vs.yM());
      }
    });

    addReaction(new Reaction("W-S") {
      // add q rest
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xL(), y = g.vs.yM();
        if (x < sys.page.margins.left || x > sys.page.margins.right) { return UC.noBid; }
        int H = fmt.H, top = yTop() - H, bot = yBot() + H;
        if (y < top || y > bot) { return UC.noBid; }
        return 10;
      }

      @Override
      public void act(Gesture g) {
        Time t = Staff.this.sys.getTime(g.vs.xL());
        new Rest(Staff.this, t);
      }
    });

    addReaction(new Reaction("E-S") {  // E for eighth rest
      // add q rest
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xL(), y = g.vs.yM();
        if (x < sys.page.margins.left || x > sys.page.margins.right) { return UC.noBid; }
        int H = fmt.H, top = yTop() - H, bot = yBot() + H;
        if (y < top || y > bot) { return UC.noBid; }
        return 10;
      }

      @Override
      public void act(Gesture g) {
        Time t = Staff.this.sys.getTime(g.vs.xL());
        (new Rest(Staff.this, t)).nFlag = 1;
      }
    });
  }

  public int yTop() { return staffTop.v(); }
  // which line that put note; H is half space between lines
  public int yOfLine(int line) { return yTop() + line * fmt.H; }
  public int yBot() { return yOfLine(2 * (fmt.nLines - 1)); }

  public int yLine(int n) { return yTop() + n * fmt.H; }
  public int lineOfY(int y) {
    int H = fmt.H;
    int bias = 100;  // because integer truncation rounds toward 0; for negative numbers;
    int top = yTop() - H * bias;
    return (y - top + H / 2) / H - bias;
  }

  public Staff copy(Sys newSys) {
    G.HC hc = new G.HC(newSys.staffs.sysTop, staffTop.dv);
    return new Staff(newSys, iStaff, hc, fmt);
  }

  public void show(Graphics g) {
    Page.Margins m = sys.page.margins;
    int x1 = m.left, x2 = m.right,y = yTop(), h = fmt.H*2;
    for (int i = 0; i < fmt.nLines; i++) {
      g.drawLine(x1, y + i * h, x2, y + i * h);
    }
  }


  // ------------- Format -----------------
  public static class Fmt {
    public int nLines, H;
    public boolean barContinues = false;
    public Fmt(int nLines, int H) {
      this.nLines = nLines;
      this.H = H;
    }
    public void toggleBarContinues() { barContinues = !barContinues; }
  }

  // -------------- Staff.List --------------
  public static class List extends ArrayList<Staff> {
    public G.HC sysTop;
    public List(G.HC sysTop) { this.sysTop = sysTop; }
  }
}
