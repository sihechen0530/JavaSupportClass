package music;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import reactions.Mass;

public class Beam extends Mass {

  // coordinates of master beam
  public static int mx1, my1, mx2, my2;
  public Stem.List stems = new Stem.List();

  public Beam(Stem first, Stem last) {
    super("NOTE");
    addStem(first);
    addStem(last);
  }

  public static int yOfX(int x, int x1, int y1, int x2, int y2) {
    int dy = y2 - y1, dx = x2 - x1;
    return (x - x1) * dy / dx + y1;
  }

  public static boolean verticalLinesCrossesSegment(int x, int y1, int y2, int bX, int bY,
      int eX, int eY) {
    if (x < bX || x > eX) { return false; }
    int y = yOfX(x, bX, bY, eX, eY);
    return y1 < y2 ? y1 < y && y < y2 : y2 < y && y < y1;
  }

  public static int yOfX(int x) {
    return yOfX(x, mx1, my1, mx2, my2);
  }

  public static void setMasterBeam(int x1, int y1, int x2, int y2) {
    mx1 = x1;
    my1 = y1;
    mx2 = x2;
    my2 = y2;
  }

  public Stem first() {
    return stems.get(0);
  }

  public Stem last() {
    return stems.get(stems.size() - 1);
  }

  public void deleteBeam() {
    // note: stems still exist, flags and dots still exist, just remove beam from stems
    for (Stem stem : stems) {
      stem.beam = null;
    }
    deleteMass();
  }

  public void addStem(Stem stem) {
    if (stem.beam == null) {
      stems.add(stem);
      stem.beam = this;
      stem.nFlag = 1;
      stems.sort();
    }
  }

  public void setMasterBeam() {
    mx1 = first().X();
    my1 = first().yBeamEnd();
    mx2 = last().X();
    my2 = last().yBeamEnd();
  }

  public static Polygon poly;
  static {
    int[] foo = {0, 0, 0, 0};
    poly = new Polygon(foo, foo, 4);
  }

  public static void setPoly(int x1, int y1, int x2, int y2, int h) {
    int[] a = poly.xpoints;
    a[0] = x1; a[1] = x2; a[2] = x2; a[3] = x1;
    a = poly.ypoints;
    a[0] = y1; a[1] = y2; a[2] = y2 + h; a[3] = y1 + h;
  }

  public static void drawBeamStack(Graphics g, int n1, int n2, int x1, int x2, int h) {
    int y1 = yOfX(x1), y2 = yOfX(x2);
    for (int i = n1; i < n2; i++) {
      setPoly(x1, y1 + i * 2 * h, x2, y2 + i * 2 * h, h);
      g.fillPolygon(poly);
    }
  }

  public void show(Graphics g) { g.setColor(Color.BLACK); drawBeamGroup(g); }

  private void drawBeamGroup(Graphics g) {
    setMasterBeam();
    Stem firstStem = first();
    int H = firstStem.staff.fmt.H, sH = (firstStem.isUp ? H : -H); // signed h value
    int nPrev = 0, nCur = firstStem.nFlag, nNext = stems.get(1).nFlag;
    int pX, cX = firstStem.X();
    int bX = cX + 3 * H;  // forward leaning beamlet on first stem
    if (nCur > nNext) { drawBeamStack(g, nNext, nCur, cX, bX, sH); }
    for (int cur = 1; cur < stems.size(); cur++) {
      Stem sCur = stems.get(cur);
      pX = cX;
      cX = sCur.X();
      nPrev = nCur;
      nCur = nNext;
      nNext = (cur < (stems.size() - 1)) ? stems.get(cur + 1).nFlag : 0;
      int nBack = Math.min(nPrev, nCur);
      drawBeamStack(g, 0, nBack, pX, cX, sH);
      if (nCur > nPrev && nCur > nNext) {  // have beamlets
        if (nPrev < nNext) {
          bX = cX + 3 * H;
          drawBeamStack(g, nNext, nCur, cX, bX, sH);
        } else {
          bX = cX - 3 * H;
          drawBeamStack(g, nPrev, nCur, bX, cX, sH);
        }
      }
    }

  }

  public void removeStem(Stem s) {
    if (s == first() || s == last()) { deleteBeam(); }
    else {
      stems.remove(s);
      stems.sort();
    }
  }
}