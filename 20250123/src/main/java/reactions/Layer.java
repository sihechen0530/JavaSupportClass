package reactions;


import java.awt.Graphics;
import java.util.ArrayList;
import music.I;

// a list of stuff shown in order (Z-ordering)
public class Layer extends ArrayList<I.Show> implements I.Show {
  public String name;
  public Layer(String name) {
    this.name = name;
  }
  public void show(Graphics g) {
    for (I.Show s : this) {
      s.show(g);
    }
  }
}
