package reactions;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import graphics.G;
import java.util.Collection;
import java.util.TreeMap;
import music.I;
import music.UC;

/**
 * with a name; list of prototypes
 */
public class Shape implements Serializable {

  public static Trainer TRAINER = new Trainer();
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
    private Database() {
      super();
      String dot = "DOT";
      put(dot, new Shape(dot));
    }

    private Shape forceGet(String name) {
      if (!DB.containsKey(name)) {
        DB.put(name, new Shape(name));
      }
      return DB.get(name);
    }

    public void train(String name, Ink.Norm norm) {
      if (isLegal(name)) {
        forceGet(name).prototypes.train(norm);
      }
    }

    public static Database load() {
      Database res;
      try {
        System.out.println("Loading " + fileName);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
        res = (Shape.Database) ois.readObject();
        System.out.println("Successfully loaded " + res.keySet());
        ois.close();
      } catch (Exception e) {
        System.out.println("Failed to load");
        System.out.println(e);
        res = new Database();
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

      public void train(Ink.Norm norm) {
        if (bestDist(norm) < UC.noMatchDist) {
          bestMatch.blend(norm);
        } else {
          add(new Shape.Prototype());
        }
      }
    }
  }

  // ----------------------------- Trainer -------------------------------------
  public static class Trainer implements I.Show, I.Area {
    public static String UNKNOWN = " <=this name is unknown";
    public static String KNOWN = " <=this is a known shape";
    public static String ILLEGAL = " <=this name not legal";

    public static String curName = "";
    public static String curState = ILLEGAL;

    public static Shape.Prototype.List pList = new Shape.Prototype.List();

    public void setState() {
      // curState = curName.equals("") || curName.equals("DOT") ? ILLEGAL : UNKNOWN;
      curState = !Shape.DB.isLegal(curName) ? ILLEGAL : UNKNOWN;
      if (curState == UNKNOWN) {
        if (Shape.DB.isKnown(curName)) {
          curState = KNOWN;
          pList = Shape.DB.get(curName).prototypes;
        } else {
          pList = null;
        }
      }
    }

    public void show(Graphics g) {
      G.clearScreen(g);
      g.setColor(Color.BLACK);
      g.drawString(curName, 600, 30);
      g.drawString(curState, 700, 30);
      g.setColor(Color.RED);
      Ink.BUFFER.show(g);
      if (pList != null) {
        pList.show(g);
      }
    }

    public boolean hit(int x, int y) {
      return true;
    }

    public void dn(int x, int y) {
      Ink.BUFFER.dn(x, y);
    }

    public void drag(int x, int y) {
      Ink.BUFFER.drag(x, y);
    }

    public void up(int x, int y) {
      Ink.BUFFER.up(x, y);
      Ink ink = new Ink();
      Shape.DB.train(curName, ink.norm);
      setState();
    }

    public void keyTyped(KeyEvent ke) {
      char c = ke.getKeyChar(); System.out.println("typed: " + c);
      // \n \r ascii
      curName = (c == ' ' || c == '\n' || c == '\r') ? "" : curName + c;
      if (c == '\n' || c == '\r') { Shape.Database.save(); }
      setState();
    }
  }

}
