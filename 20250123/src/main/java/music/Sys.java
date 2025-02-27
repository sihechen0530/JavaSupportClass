package music;

import graphics.G;
import java.awt.Graphics;
import java.util.ArrayList;
import reactions.Mass;

public class Sys extends Mass {
  public Page page;
  public int iSys;
  public Staff.List staffs;

  public Sys(Page page, G.HC sysTop) {
    super("BACK");
    this.page = page;
    iSys = page.sysList.size();
    staffs = new Staff.List(sysTop);
    if (iSys == 0) {
      // first system being created, create the first staff
      staffs.add(new Staff(this, 0, new G.HC(sysTop, 0)));
    } else {
      // other systems are clones of the first system
      Sys oldSys = page.sysList.get(0);
      for (Staff oldStaff : oldSys.staffs) {
        Staff ns = oldStaff.copy(this);
        this.staffs.add(ns);
      }
    }
  }

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
    staffs.add(new Staff(this, staffs.size(), staffTop));
  }

  public static class List extends ArrayList<Sys> {

  }
}
