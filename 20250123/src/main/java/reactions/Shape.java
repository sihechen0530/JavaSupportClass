package reactions;

import java.awt.Color;
import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import graphics.G;
import java.util.Collection;
import java.util.TreeMap;
import music.UC;

/**
 * with a name; list of prototypes
 */
public class Shape implements Serializable {

  public static Shape.Database DB = Shape.Database.load();
  public static Shape DOT = DB.get("DOT");  // placeholder, magic number
  public static Collection<Shape> LIST = DB.values(); // collection: Lists, Maps; automatically updated
  public Prototype.List prototypes = new Prototype.List();
  public String name;

  public Shape(String name) {
    this.name = name;
  }

  public static Shape recognize(Ink ink) {
    if (ink.vs.size.x < UC.dotThreshold && ink.vs.size.y < UC.dotThreshold) { return DOT; }
    Shape bestMatch = null;
    int bestSoFar = UC.noMatchDist;
    for (Shape s : LIST) {
      int d = s.prototypes.bestDist(ink.norm);
      if (d < bestSoFar) {
        bestSoFar = d;
        bestMatch = s;
      }
    }
    return bestMatch;
  }

  // -------------------- Database ---------------------
  public static class Database extends TreeMap<String, Shape> implements Serializable {
    // hashmap O(1), hashing
    // treemap O(logn), sorting
    // key value pairs

    // serialization incompatibility: different jvm, different versions of code

    // serialize one single object: build a class to contain the objects

    private static final String fileName = UC.shapeDatabaseFileName;

    public static Database load() {
      Database res = new Database();
      res.put("DOT", new Shape("DOT"));
      try {
        System.out.println("Loading " + fileName);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
        res = (Shape.Database) ois.readObject();
        System.out.println("Successfully loaded " + res.keySet());
        ois.close();
      } catch (Exception e) {
        System.out.println("Failed to load");
        System.out.println(e);
      }
      return res;
    }

    public static void save() {
      try {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(DB);
        System.out.println("Successfully saved " + fileName);
        oos.close();
      } catch (Exception e) {
        System.out.println("Failed to save");
        System.out.println(e);
      }
    }

    public boolean isKnown(String name) { return containsKey(name); }

    public boolean unKnown(String name) { return !containsKey(name); }

    public boolean isLegal(String name) { return !name.equals("") && !name.equals("DOT"); }




  }

  // ------------------ Prototype ---------------------
  public static class Prototype extends Ink.Norm implements Serializable {

    int nBlend = 1;

    public void blend(Ink.Norm norm) {
      blend(norm, nBlend);
      nBlend++;
    }

    // -------------- List --------------------
    public static class List extends ArrayList<Prototype> implements Serializable {
      public static Prototype bestMatch;  // set as side effect of bestDist
      public int bestDist(Ink.Norm norm) {
        bestMatch = null;
        int bestSoFar = UC.noMatchDist;
        for (Prototype p : this) {
          int d = p.dist(norm);
          if (d < bestSoFar) {
            bestMatch = p;
            bestSoFar = d;
          }
        }
        return bestSoFar;
      }
      private static int m = 10, w = 60;
      private static G.VS showBox = new G.VS(m, m, w, w);
      public void show(Graphics g) {
        g.setColor(Color.ORANGE);
        for (int i = 0; i < size(); i++) {
          Prototype p = get(i);
          int x = m + i * (m + w);
          showBox.loc.set(x, m);
          p.drawAt(g, showBox);
          g.drawString("" + p.nBlend, x, 20);
        }
      }

    }
  }

}
