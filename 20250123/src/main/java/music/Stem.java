package music;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import reactions.Gesture;
import reactions.Reaction;

public class Stem extends Duration implements Comparable<Stem> {
  public Staff staff;
  public Head.List heads;
  public boolean isUp = true;
  public Beam beam = null;  // states that this variable can be legally null

  public Stem(Staff staff, Head.List heads, boolean up) {
    this.staff = staff;
    this.isUp = up;

    for (Head h : heads) {
      h.unStem();
      h.stem = this;
    }

    this.heads = heads;
    staff.sys.stems.addStem(this);
    setWrongSides();

    addReaction(new Reaction("E-E") {
      // increment flag on stem
      @Override
      public int bid(Gesture g) {
        int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
        int xs = Stem.this.X();
        if (x1 > xs || x2 < xs) { return UC.noBid; }
        int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
        if (y < y1 || y > y2) { return UC.noBid; }
        return Math.abs(y - (y1 + y2) / 2) + 60;  // biased: sys E-E can outbid
      }

      @Override
      public void act(Gesture g) {
        Stem.this.incFlag();
      }
    });

    addReaction(new Reaction("W-W") {
      // decrement flag on stem
      @Override
      public int bid(Gesture g) {
        int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
        int xs = Stem.this.X();
        if (x1 > xs || x2 < xs) { return UC.noBid; }
        int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
        if (y < y1 || y > y2) { return UC.noBid; }
        return Math.abs(y - (y1 + y2) / 2);
      }

      @Override
      public void act(Gesture g) {
        Stem.this.decFlag();
        if (nFlag == 0 && beam != null) {
          // invalid beam
          beam.deleteBeam();
        }
      }
    });
  }

  // factory method: gets stem if there are heads
  public static Stem getStem(Staff staff, Time time, int y1, int y2, boolean up) {
    // y1 y2 from gesture
    Head.List heads = new Head.List();
    for (Head h : time.heads) {
      int yh = h.Y();
      if (yh > y1 && yh < y2) { heads.add(h); }
    }
    if (heads.size() == 0) { return null; }
    Beam b = internalStem(staff.sys, time.x, y1, y2);  // this could be internal stem in beam group
    Stem res = new Stem(staff, heads, up);
    if (b != null) {
      b.addStem(res);
      res.nFlag = 1;
    }
    return res;
  }

  private static Beam internalStem(Sys sys, int x, int y1, int y2) {
    for (Stem s : sys.stems) {
      if (s.beam != null && s.X() < x && s.yLo() < y2 && s.yHi() > y1) {
        int bX = s.beam.first().X(), bY = s.beam.first().yBeamEnd();
        int eX = s.beam.last().X(), eY = s.beam.last().yBeamEnd(); // beginning xy and ending xy
        if (Beam.verticalLinesCrossesSegment(x, y1, y2, bX, bY, eX, eY)) { return s.beam; }
      }
    }
    return null;
  }

  public void show(Graphics g) {
    if (nFlag >= -1 && heads.size() > 0) {
      int x = X(), h = staff.fmt.H, yH = yFirstHead(), yB = yBeamEnd();
      g.drawLine(x, yH, x, yB);
      if (nFlag > 0 && beam == null) {
        if (nFlag == 1) { (isUp ? Glyph.FLAG1D : Glyph.FLAG1U).showAt(g, h, x, yB); }
        if (nFlag == 2) { (isUp ? Glyph.FLAG2D : Glyph.FLAG2U).showAt(g, h, x, yB); }
        if (nFlag == 3) { (isUp ? Glyph.FLAG3D : Glyph.FLAG3U).showAt(g, h, x, yB); }
        if (nFlag == 4) { (isUp ? Glyph.FLAG4D : Glyph.FLAG4U).showAt(g, h, x, yB); }
      }
    }
  }

  public int yBeamEnd() {
    if (heads.size() == 0) { return 100; }
    if (isInternalStem()) { beam.setMasterBeam(); return Beam.yOfX(X()); }
    Head h = lastHead();
    int line = h.line;
    line += (isUp ? -7 : 7);  // default length, one octave
    int flagInc = nFlag > 2 ? 2 * (nFlag - 2) : 0;
    line += (isUp ? -flagInc : flagInc);
    if ((isUp && line > 4) || (!isUp && line < 4)) { line = 4; }
    return h.staff.yOfLine(line);
  }

  private boolean isInternalStem() {
    if (beam == null) {
      return false;
    }
    if (this == beam.first() || this == beam.last()) {
      return false;
    }
    return true;
  }

  public int yLo() { return isUp ? yBeamEnd() : yFirstHead(); }

  public int yHi() { return isUp ? yFirstHead() : yBeamEnd(); }

  private int yFirstHead() {
    if (heads.size() == 0) { return 200; }
    Head h = firstHead();
    return h.staff.yOfLine(h.line);
  }

  public int X() {
    if (heads.size() == 0) { return 100; }
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
    //
    if (heads.size() != 0) { System.out.println("deleting stem with heads"); }
    staff.sys.stems.remove(this);
    if (beam != null) { beam.removeStem(this); }
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

  @Override
  public int compareTo(Stem stem) {
    return X() - stem.X();
  }


  // --------------- Stem.List -----------------------
  public static class List extends ArrayList<Stem> {
    public int yMin = 1_000_000, yMax = -1_000_000;
    public void addStem(Stem stem) {
      add(stem);
      if (stem.yLo() < yMin) { yMin = stem.yLo(); }
      if (stem.yHi() > yMax) { yMax = stem.yHi(); }
    }
    public boolean fastReject(int y) { return y > yMax || y < yMin; }
    public void sort() { Collections.sort(this); }
    public ArrayList<Stem> allIntersectors(int x1, int y1, int x2, int y2) {
      ArrayList<Stem> res = new ArrayList<Stem>();
      for (Stem s : this) {
        if (Beam.verticalLinesCrossesSegment(s.X(), s.yLo(), s.yHi(), x1, y1, x2, y2)) {
          res.add(s);
        }
      }
      return res;
    }
  }
}
