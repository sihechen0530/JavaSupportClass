package reactions;

import java.awt.Graphics;
import music.I;

public abstract class Mass extends Reaction.List implements I.Show {
  public Layer layer;
  public Mass(String layerName) {
    layer = Layer.byName.get(layerName);
    if (layer != null) {
      layer.add(this);
    } else {
      System.out.println("Layer " + layerName + " does not exist");
    }
  }
  public void deleteMass() {
    clearAll();
    // equals(): value equality
    // '==': referential equality
    layer.remove(this);
  }
  public void show(Graphics g) {}
  // identical hash code
  private static int M = 1;
  private final int hash = M++;
  @Override
  public int hashCode() { return hash; }
  // referential equality
  @Override
  public boolean equals(Object obj) { return this == obj; }
}
