package music;

import java.awt.Graphics;
import graphics.G;
import java.util.ArrayList;
import reactions.Mass;

public class Staff extends Mass {
  public Sys sys;
  public int iStaff;  // index of where it lives in the dad's list
  public G.HC staffTop;
  public Fmt fmt = new Fmt(5, 8);

  public Staff(Sys sys, int iStaff, G.HC staffTop) {
    super("BACK");
    this.sys = sys;
    this.iStaff = iStaff;
    this.staffTop = staffTop;
  }

  public int yTop() { return staffTop.v(); }
  // which line that put note; H is half space between lines
  public int yOfLine(int line) { return yTop() + line * fmt.H; }
  public int yBot() { return yOfLine(2 * (fmt.nLines - 1)); }

  public Staff copy(Sys newSys) {
    G.HC hc = new G.HC(newSys.staffs.sysTop, staffTop.dv);
    return new Staff(newSys, iStaff, hc);
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
    public Fmt(int nLines, int H) {
      this.nLines = nLines;
      this.H = H;
    }
  }

  // -------------- Staff.List --------------
  public static class List extends ArrayList<Staff> {
    public G.HC sysTop;
    public List(G.HC sysTop) { this.sysTop = sysTop; }
  }
}
