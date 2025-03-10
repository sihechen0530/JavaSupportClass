package music;

import java.awt.Color;
import java.awt.Graphics;
import reactions.Gesture;
import reactions.Mass;
import reactions.Reaction;

public class Bar extends Mass {
  private static final int FAT = 2, RIGHT = 4, LEFT = 8;

  public Sys sys;
  public int x, barType = 0;  // 0 for default thin line; 1 for double line

  public Bar(Sys sys, int x) {
    super("BACK");
    this.sys = sys;
    this.x = x;
    int right = sys.page.margins.right;
    if (Math.abs(right - this.x) < UC.barToMarginSnap) { this.x = right; }
    addReaction(new Reaction("S-S") {
      // cycle bar
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM();
        if (Math.abs(x - Bar.this.x) > UC.barToMarginSnap) { return UC.noBid; }
        int y1 = g.vs.yL(), y2 = g.vs.yH();
        if (y1 < Bar.this.sys.yTop() - 20 || y2 > Bar.this.sys.yBot() + 20) { return UC.noBid; }
        return Math.abs(x - Bar.this.x);  // biggest bid: barToMarginSnap
      }

      @Override
      public void act(Gesture g) {
        Bar.this.cycleType();
      }
    });

    addReaction(new Reaction("DOT") {
      // dot this bar
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yM();
        if (y < Bar.this.sys.yTop() || y > Bar.this.sys.yBot()) { return UC.noBid; }
        int dist = Math.abs(x - Bar.this.x);
        if (dist > 3 * sys.page.maxH) { return UC.noBid; }
        return dist;
      }

      @Override
      public void act(Gesture g) {
        if (g.vs.xM() < Bar.this.x) {
          Bar.this.toggleLeft();
        } else {
          Bar.this.toggleRight();
        }
      }
    });
  }

  public void show(Graphics g) {
    int y1 = 0, y2 = 0;  // y1, y2 are top and bot of connected component
    boolean justSawBreak = true;
    for (int i = 0; i < sys.staffs.size(); i++) {
      Staff staff = sys.staffs.get(i);
      int staffTop = staff.yTop();
      if (justSawBreak) { y1 = staffTop; }
      y2 = staff.yBot();
      justSawBreak = !staff.fmt.barContinues;
      if (justSawBreak) {
        // got a connected component, draw
        drawLines(g, x, y1, y2);
      }
      if (barType > 3) {
        // 4 or 8
        drawDots(g, x, staffTop);
      }
    }
  }

  private void drawLines(Graphics g, int x, int y1, int y2) {
    int H = sys.page.maxH;
    if (barType == 0) { thinBar(g, x, y1, y2); }
    if (barType == 1) { thinBar(g, x, y1, y2); thinBar(g, x - H, y1, y2); }
    if (barType == 2) { fatBar(g, x - H, y1, y2, H); thinBar(g, x - 2 * H, y1, y2); }
    if (barType >= 4) {
      fatBar(g, x - H, y1, y2, H);
      if ((barType&LEFT) != 0) { thinBar(g, x - 2 * H, y1, y2); wings(g, x - 2 * H, y1, y2, -H, H); }
      if ((barType&RIGHT) != 0) { thinBar(g, x + H, y1, y2); wings(g, x + H, y1, y2, H, H); }
    }
  }

  public void cycleType() { barType++; if (barType > 2) { barType = 0; }}
  public void toggleLeft() { barType = barType^LEFT; }  // bit XOR
  public void toggleRight() { barType = barType^RIGHT; }

  public static void wings(Graphics g, int x, int y1, int y2, int dx, int dy) {
    // dx can be either positive or negative
    g.drawLine(x, y1, x + dx, y1 - dy);
    g.drawLine(x, y2, x + dx, y2 + dy);
  }

  public static void fatBar(Graphics g, int x, int y1, int y2, int dx) { g.fillRect(x, y1, dx, y2 - y1); }

  public static void thinBar(Graphics g, int x, int y1, int y2) { g.drawLine(x, y1, x, y2); }

  public void drawDots(Graphics g, int x, int top) {
    int H = sys.page.maxH;
    if ((barType&LEFT) != 0) {
      g.fillOval(x - 3 * H, top + 11 * H / 4, H / 2, H / 2);
      g.fillOval(x - 3 * H, top + 19 * H / 4, H / 2, H / 2);
    }
    if ((barType&RIGHT) != 0) {
      g.fillOval(x + 3 * H / 2, top + 11 * H / 4, H / 2, H / 2);
      g.fillOval(x + 3 * H / 2, top + 19 * H / 4, H / 2, H / 2);
    }
  }
}
