package music;

import java.awt.Graphics;
import reactions.Mass;

public class Head extends Mass {
  public Staff staff;
  public int line;  // line is y coordinate in disguise, 0 = top line of staff
  public Time time;
  public Head(Staff staff, int x, int y) {
    super("NOTE");
    this.staff = staff;
    time = staff.sys.getTime(x);
//    int H = staff.fmt.H;  // size
//    int top = staff.yTop() - H;  // one space above the top line
//    this.line = (y - top + H / 2) / H - 1;  // rounding
    this.line = staff.lineOfY(y);
//    System.out.println("line: " + line);
  }

  public void show(Graphics g) {
    int H = staff.fmt.H;
    Glyph.HEAD_Q.showAt(g, H, time.x, staff.yTop() + line * H);
  }

}
