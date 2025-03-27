package music;

import java.awt.Graphics;
import java.util.ArrayList;
import reactions.Mass;

public class Clef extends Mass implements Comparable<Clef> {
  public int x;
  public Glyph glyph;
  public Staff staff;

  public Clef(Staff staff, int x, Glyph glyph) {
    super("NOTE");
    this.x = x;
    this.staff = staff;
    this.glyph = glyph;
  }

  public void show(Graphics g) { glyph.showAt(g, staff.fmt.H, x, staff.yOfLine(4)); }

  @Override
  public int compareTo(Clef c) {
    return x - c.x;
  }

  // --------------- Clef.List --------------------
  public static class List extends ArrayList<Clef> {}
}
