package reactions;

import graphics.G;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import music.I;
import music.UC;

public class Ink implements I.Show, Serializable {

  public static final Buffer BUFFER = new Buffer();

  public static final int K = UC.normSampleSize;

//  public static G.VS TEMP = new G.VS(100, 100, 100, 100);

  public Norm norm;
  public G.VS vs;

  public Ink() {
    norm = new Norm();
    vs = BUFFER.bBox.getNewVS();
//    for (int i = 0; i < BUFFER.n; i++) {
//      points[i].set(BUFFER.points[i]);
//    }
//    BUFFER.subSample(this);
//    G.V.T.set(BUFFER.bBox, TEMP);
//    transform();
//    G.V.T.set(TEMP, BUFFER.bBox.getNewVS());
//    transform();
  }

  @Override
  public void show(Graphics g) {
    g.setColor(UC.inkColor);
    norm.drawAt(g, vs);
  }

  public static class Norm extends G.PL implements Serializable {
    public static final int N = UC.normSampleSize, MAX = UC.normCoorMax;
    public static final G.VS NCS = new G.VS(0, 0, MAX, MAX);  // normalized coordinate system
    public Norm() {
      super(N);
      BUFFER.subSample(this);
      G.V.T.set(BUFFER.bBox, NCS);
      transform();
    }

    public void drawAt(Graphics g, G.VS vs) {
      G.V.T.set(NCS, vs);  // move from normalized CS to vs
      for (int i = 1; i < N; i++) {
        g.drawLine(points[i - 1].tx(), points[i - 1].ty(), points[i].tx(), points[i].ty());
      }
    }

    public int dist(Norm n) {
      int res = 0;
      for (int i = 0; i < N; i++) {
        int dx = points[i].x - n.points[i].x;
        int dy = points[i].y - n.points[i].y;
        res += dx * dx + dy * dy;
      }
      return res;
    }


    public void blend(Norm norm, int nBlend) {
      for (int i = 0; i < N; i++) {
        points[i].blend(norm.points[i], nBlend);
      }
    }

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
