package music;

import java.awt.Graphics;
import java.util.Collections;

public class Stem extends Duration {
  public Staff staff;
  public Head.List heads = new Head.List();
  public boolean isUp = true;

  public Stem(Staff staff, boolean up) {
    this.staff = staff;
    this.isUp = up;
  }

  public void show(Graphics g) {
    if (nFlag >= -1 && heads.size() > 0) {
      int x = X(), h = staff.fmt.H, yH = yFirstHead(), yB = yBeamEnd();
      g.drawLine(x, yH, x, yB);
    }
  }

  private int yBeamEnd() {
    Head h = lastHead();
    int line = h.line;
    line += (isUp ? -7 : 7);  // default length, one octave
    int flagInc = nFlag > 2 ? 2 * (nFlag - 2) : 0;
    line += (isUp ? -flagInc : flagInc);
    if ((isUp && line > 4) || (!isUp && line < 4)) { line = 4; }
    return h.staff.yOfLine(line);
  }

  private int yFirstHead() {
    Head h = firstHead();
    return h.staff.yOfLine(h.line);
  }

  public int X() {
    Head h = firstHead();
    return h.time.x + (isUp ? h.W() : 0);
  }

  public Head firstHead() {
    return heads.get(isUp ? heads.size() - 1 : 0);
  }

  public Head lastHead() {
    return heads.get(isUp ? 0 : heads.size() - 1);
  }

  public void deleteStem() {
    deleteMass();
  }

  public void setWrongSides() {
    Collections.sort(heads);
    int i, last, next;
    if (isUp) { i = heads.size() - 1; last = 0; next = -1; }
    else { i = 0; last = heads.size() - 1; next = 1; }
    Head ph = heads.get(i);
    ph.wrongSide = false;
    while (i != last) {
      i += next;
      Head nh = heads.get(i);
//      nh.wrongSide = Math.abs(nh.line - ph.line) <= 1 && !ph.wrongSide;
      // this line doesn't do cross staff seconds
      nh.wrongSide = ((ph.staff == nh.staff) && (Math.abs(nh.line - ph.line) <= 1) && !ph.wrongSide);
      ph = nh;
    }
  }
}
