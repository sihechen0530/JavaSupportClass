package music;

import graphics.G;
import java.awt.Color;
import java.awt.Graphics;
import reactions.Gesture;
import reactions.Mass;
import reactions.Reaction;

public class Page extends Mass {
  public Margins margins = new Margins();
  public int sysGap;
  public G.HC pageTop;
  public Sys.List sysList;
  public int maxH = 0;

  public Page(int y) {
    super("BACK");
    margins.top = y;
    pageTop = new G.HC(G.HC.ZERO, y);
    G.HC sysTop = new G.HC(pageTop, 0);
    sysList = new Sys.List();
    sysList.add(new Sys(this, sysTop));
    updateMaxH();

    addReaction(new Reaction("W-W") {
      // add newStaff to first system
      public int bid(Gesture g) {
        // only allow adding staffs in first new system
        if (sysList.size() != 1) {
          return UC.noBid;
        }
        Sys sys = sysList.get(0);
        int y = g.vs.yM();
        if (y < sys.yBot() + UC.minStaffGap) {
          return UC.noBid;
        }
        return 1000;
      }
      public void act(Gesture g) {
        sysList.get(0).addNewStaff(g.vs.yM());
      }
    });
    addReaction(new Reaction("W-E") {
      public int bid(Gesture g) {
        Sys lastSys = sysList.get(sysList.size() - 1);
        int y = g.vs.yM();
        if (y < lastSys.yBot() + UC.minSysGap) {
          return UC.noBid;
        }
        return 1000;
      }
      public void act(Gesture g) {
        addNewSys(g.vs.yM());
      }
    });
  }

  public void updateMaxH() {
    Sys sys = sysList.get(0);
    int newH = sys.staffs.get(sys.staffs.size() - 1).fmt.H;
    if (maxH < newH) { maxH = newH; }
    // maxH = max(maxH, newH);
  }

  public void addNewSys(int y) {
    int nSys = sysList.size(), sysHeight = sysList.get(0).height();
    if (nSys == 1) {
      // adding the second system
      sysGap = y - sysHeight - pageTop.v();
    }
    G.HC sysTop = new G.HC(pageTop, nSys * (sysHeight + sysGap));
    sysList.add(new Sys(this, sysTop));
  }

  public void show(Graphics g) {
    g.setColor(Color.BLACK);
  }

  // ---------------- Margins ---------------------
  public static class Margins {
    private static final int MM = 50;
    public int top = MM, left = MM, bot = UC.mainWindowHeight - MM, right = UC.mainWindowWidth - MM;

  }
}
