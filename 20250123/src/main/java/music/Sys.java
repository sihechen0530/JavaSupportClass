package music;

import graphics.G;
import java.awt.Graphics;
import java.util.ArrayList;
import reactions.Gesture;
import reactions.Mass;
import reactions.Reaction;

public class Sys extends Mass {
  public Page page;
  public int iSys;
  public Staff.List staffs;
  public Time.List times;
  public Stem.List stems = new Stem.List();

  public Sys(Page page, G.HC sysTop) {
    super("BACK");
    this.page = page;
    iSys = page.sysList.size();
    staffs = new Staff.List(sysTop);
    times = new Time.List(this);
    if (iSys == 0) {
      // first system being created, create the first staff
      staffs.add(new Staff(this, 0, new G.HC(sysTop, 0), new Staff.Fmt(5, 8)));
    } else {
      // other systems are clones of the first system
      Sys oldSys = page.sysList.get(0);
      for (Staff oldStaff : oldSys.staffs) {
        Staff ns = oldStaff.copy(this);
        this.staffs.add(ns);
      }
    }

    addReaction(new Reaction("E-E") {  // beam two stems
      @Override
      public int bid(Gesture g) {
        int x1 = g.vs.xL(), y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();
        if (stems.fastReject((y1 + y2) / 2)) { return UC.noBid; }
        ArrayList<Stem> temp = stems.allIntersectors(x1, y1, x2, y2);
        if (temp.size() < 2) { return UC.noBid; }
        System.out.println("crossed " + temp.size() + " stems");
        Beam b = temp.get(0).beam;
        for (Stem s : temp) {
          if (s.beam != b) { return UC.noBid; }
        }
        if (b == null && temp.size() != 2) { return UC.noBid; }  // crossed more than 2 blank lines
        if (b == null && (temp.get(0).nFlag != 0 || temp.get(1).nFlag != 0)) { return UC.noBid; }
        return 50;
      }

      @Override
      public void act(Gesture g) {
        int x1 = g.vs.xL(), y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();
        ArrayList<Stem> temp = stems.allIntersectors(x1, y1, x2, y2);
        Beam b = temp.get(0).beam;
        if (b == null) { new Beam(temp.get(0), temp.get(1)); }
        else { for (Stem s : temp) { s.incFlag(); } }
      }
    });
  }

  public Time getTime(int x) { return times.getTime(x); }
  public int yTop() { return staffs.sysTop.v(); }
  public int yBot() { return staffs.get(staffs.size() - 1).yBot(); }
  public int height() { return yBot() - yTop(); }

  public void show(Graphics g) {
    int x = page.margins.left;
    g.drawLine(x, yTop(), x, yBot());
  }

  public void addNewStaff(int y) {
    int off = y - staffs.sysTop.v();
    G.HC staffTop = new G.HC(staffs.sysTop, off);
    staffs.add(new Staff(this, staffs.size(), staffTop, new Staff.Fmt(5, 8)));
    page.updateMaxH();
  }

  public static class List extends ArrayList<Sys> {

  }
}
