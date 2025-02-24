package reactions;


import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import music.I;

// a list of stuff shown in order (Z-ordering)
public class Layer extends ArrayList<I.Show> implements I.Show {
  // initialization error
  // 1. swap two lines
  // 2. static block
  public static HashMap<String, Layer> byName = new HashMap<>();
  public static Layer ALL = new Layer("all");

  public String name;
  public Layer(String name) {
    this.name = name;
    if (!name.equals("all")) {
      byName.put(name, this);
      ALL.add(this);
    }
  }

  public static void nuke() {
    for (I.Show lay : ALL) {
      ((Layer) lay).clear();
    }
  }

  public void show(Graphics g) {
    for (I.Show s : this) {
      s.show(g);
    }
  }
}
