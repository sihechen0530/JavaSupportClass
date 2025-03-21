package music;

import java.awt.Graphics;
import reactions.Mass;

public abstract class Duration extends Mass {
  public int nFlag = 0, nDot = 0;
  public Duration() { super("NOTE"); }
  public abstract void show(Graphics g);

  public void incFlag() { if (nFlag < 4) nFlag++; }
  public void decFlag() { if (nFlag > -2) nFlag--; }
  public void cycleDot() { nDot++; if (nDot > 3) nDot = 0; }
}
