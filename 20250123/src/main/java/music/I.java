package music;

import java.awt.Graphics;

public interface I {
  // nesting: namespace
  public interface Show { public void show(Graphics g); }
  public interface Hit { public boolean hit(int x, int y); }
  public interface Area extends Hit {
    public void dn(int x, int y);
    public void up(int x, int y);
    public void drag(int x, int y);
  }
}
