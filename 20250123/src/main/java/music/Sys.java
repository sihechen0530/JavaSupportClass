package music;

import graphics.G;
import java.awt.Graphics;
import java.util.ArrayList;
import reactions.Mass;

public class Sys extends Mass {
  public Page page;
  public int iSys;
  public Staff.List staffs;
  public Time.List times;

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
