package reactions;

import graphics.G;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import music.I;
import music.UC;

public class Ink extends G.PL implements I.Show {

  public static final Buffer BUFFER = new Buffer();

  public static final int K = UC.normSampleSize;

  public Ink() {
    super(K);
//    for (int i = 0; i < BUFFER.n; i++) {
//      points[i].set(BUFFER.points[i]);
//    }
    BUFFER.subSample(this);
  }

  @Override
  public void show(Graphics g) {
    g.setColor(UC.inkColor);
    draw(g);
  }

  // --------------------Buffer----------------------
  public static class Buffer extends G.PL implements I.Show, I.Area {

    public static final int MAX = UC.inkBufferMax;
    public int n; // how many points actually in buffer
    public G.BBox bBox = new G.BBox();

    // private constructor: singleton or factory
    private Buffer() {
      super(MAX);
    }

    public void add(int x, int y) {
      if (n < MAX) {
        points[n].set(x, y);
        n++;
        bBox.add(x, y);
      }
    }

    public void clear() {
      n = 0;
    }

    public void dn(int x, int y) {
      clear();
      add(x, y);
      bBox.set(x, y);
    }

    public void up(int x, int y) {
      add(x, y);
    }

    public void drag(int x, int y) {
      add(x, y);
    }

    public boolean hit(int x, int y) {
      return true;
    }

    public void show(Graphics g) {
      drawN(g, n);
      bBox.draw(g);
    }

    public void subSample(G.PL pl) {
      int k = pl.size();
      for (int i = 0; i < k; i++) {
        pl.points[i].set(this.points[i * (n - 1) / (k - 1)]);
      }
    }
  }

  // --------------------List------------------------
  public static class List extends ArrayList<Ink> implements I.Show {

    @Override
    public void show(Graphics g) {
      for (Ink i : this) {
        i.show(g);
      }
    }

  }
}
