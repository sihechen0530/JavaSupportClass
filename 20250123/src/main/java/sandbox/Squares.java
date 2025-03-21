package sandbox;

import graphics.WinApp;
import graphics.G;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Timer;
import music.I;
import music.UC;

public class Squares extends WinApp implements ActionListener {

  public static boolean showSpline = false;
  public static G.VS theVS = new G.VS(100, 100, 200, 300);
  public static Color color = G.rndColor();
  public static Square.List squares = new Square.List();
  public static Square lastSquare;
  public static boolean dragging = false;
  public static G.V mouseDelta = new G.V(0, 0);
  public static Timer timer;
  public static G.V pressedLoc = new G.V(0, 0);

  public Squares() {
    super("Squares", 1000, 800);
    timer = new Timer(30, this);
    timer.setInitialDelay(5000);
    timer.start();
  }

  public static void main(String[] args) {
    PANEL = new Squares();
    WinApp.launch();
  }

  @Override
  public void paintComponent(Graphics g) {
    G.clearScreen(g);
//    theVS.fill(g, color);
    squares.draw(g);
    if (showSpline && squares.size() > 2) {
      g.setColor(Color.BLACK);
      G.V a = squares.get(0).loc;
      G.V b = squares.get(1).loc;
      G.V c = squares.get(2).loc;
      G.spline(g, a.x, a.y, b.x, b.y, c.x, c.y, 4);
    }
  }

  public static I.Area curArea;
  @Override
  public void mousePressed(MouseEvent me) {
    int x = me.getX(), y = me.getY();
    curArea = squares.hit(x, y);
    curArea.dn(x, y);
    repaint();
  }
  @Override
  public void mouseDragged(MouseEvent me) {
    curArea.drag(me.getX(), me.getY());
    repaint();
  }
  @Override
  public void mouseReleased(MouseEvent me) {
    curArea.up(me.getX(), me.getY());
    repaint();
  }
//  @Override
//  public void mousePressed(MouseEvent me) {
//    int x = me.getX(), y = me.getY();
//    if (theVS.hit(x, y)) {
//      color = G.rndColor();
//    }
//    lastSquare = squares.hit(x, y);
//    if (lastSquare == null) {
//      dragging = false;
//      lastSquare = new Square(x, y);
//      squares.add(lastSquare);
//    } else {
//      dragging = true;
//      mouseDelta.set(lastSquare.loc.x - x, lastSquare.loc.y - y);
//      lastSquare.dv.set(0, 0);
//      pressedLoc.set(x, y);
//    }
//    repaint();
//  }

//  @Override
//  public void mouseDragged(MouseEvent me) {
//    int x = me.getX(), y = me.getY();
//    if (dragging) {
//      lastSquare.moveTo(x + mouseDelta.x, y + mouseDelta.y);
//    } else {
//      lastSquare.resize(x, y);
//    }
//    repaint();
//  }

  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
  }

//  @Override
//  public void mouseReleased(MouseEvent me) {
//    if (dragging) {
//      lastSquare.dv.set(me.getX() - pressedLoc.x, me.getY() - pressedLoc.y);
//    }
//  }

  //-----------------Square------------------------------
  public static class Square extends G.VS implements I.Area {

    public static Square BACKGROUND = new Square(){
      // anonymous class: works only for this instance
      public void dn(int x, int y) {
        lastSquare = new Square(x, y);
        squares.add(lastSquare);
      }
      public void drag(int x, int y) { lastSquare.resize(x, y); }
    };
    public Color c = G.rndColor();
//    public G.V dv = new G.V(G.rnd(20) - 10,
//        G.rnd(20) - 10); // random velocity between -10 and 10 in both x and y
    public G.V dv = new G.V(0, 0);

    public Square(int x, int y) {
      super(x, y, 100, 100);
    }

    // overloading
    public Square() {
      super(0, 0, UC.largestPossibleCoordinate, UC.largestPossibleCoordinate);
      c = Color.WHITE;
    }

    public void draw(Graphics g) {
      fill(g, c);
      moveAndBounce();
    }

    public void resize(int x, int y) {
      if (x > loc.x && y > loc.y) {
        size.set(x - loc.x, y - loc.y);
      }
    }

    public void moveTo(int x, int y) {
      loc.set(x, y);
    }

    public void moveAndBounce() {
      // a lazy way of testing out animation
      loc.add(dv);
      if (xL() < 0 && dv.x < 0) {
        dv.x = -dv.x;
      }
      if (yL() < 0 && dv.y < 0) {
        dv.y = -dv.y;
      }
      if (xH() > 1000 && dv.x > 0) {
        dv.x = -dv.x;
      }
      if (yH() > 800 && dv.y > 0) {
        dv.y = -dv.y;
      }
    }

    @Override
    public void dn(int x, int y) { mouseDelta.set(loc.x - x, loc.y - y); }

    @Override
    public void up(int x, int y) {}

    @Override
    public void drag(int x, int y) { loc.set(mouseDelta.x + x, mouseDelta.y + y); }

    //------------------List----------------------------
    public static class List extends ArrayList<Square> {

      public List() {
        super();
        this.add(Square.BACKGROUND);
      }
      public void draw(Graphics g) {
        for (Square s : this) {
          s.draw(g);
        }
      }

      public void addNew(int x, int y) {
        add(new Square(x, y));
      }

      public Square hit(int x, int y) {
        Square res = null;
        for (int i = 0; i < this.size(); i++) {
          Square s = this.get(i);
          if (s.hit(x, y)) {
            res = s;
          }
        }
        return res;
      }
    }


  }

}