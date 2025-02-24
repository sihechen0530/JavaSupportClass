package sandbox;

import graphics.G;
import graphics.WinApp;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.GregorianCalendar;
import music.UC;
import reactions.Gesture;
import reactions.Ink;
import reactions.Layer;
import reactions.Mass;
import reactions.Reaction;

public class ReactionTest extends WinApp {
  static {
    new Layer("BACK");
    new Layer("FORE");
  }
  public ReactionTest() {
    super("ReactionTest", 1000, 800);
    Reaction.initialReactions.addReaction(new Reaction("SW-SW") {
      @Override
      public int bid(Gesture g) {
        return 0;
      }
      @Override
      public void act(Gesture g) {
        new Box(g.vs);
      }
    });
  }
  @Override
  public void paintComponent(Graphics g) {
    G.clearScreen(g);
    Layer.ALL.show(g);
    g.setColor(Color.BLUE);
    Ink.BUFFER.show(g);
    g.drawString(Gesture.recognized, 900, 30);
  }
  public void mousePressed(MouseEvent me) {
    Gesture.AREA.dn(me.getX(), me.getY());
    repaint();
  }
  public void mouseDragged(MouseEvent me) {
    Gesture.AREA.drag(me.getX(), me.getY());
    repaint();
  }
  public void mouseReleased(MouseEvent me) {
    Gesture.AREA.up(me.getX(), me.getY());
    repaint();
  }
  public static void main(String[] args) {
    PANEL = new ReactionTest();
    WinApp.launch();
  }
  public static class Box extends Mass {
    public G.VS vs;
    public Color c = G.rndColor();
    public Box(G.VS vs) {
      super("BACK");
      this.vs = vs;
      addReaction(new Reaction("S-S") {
        // delete a box
        @Override
        public int bid(Gesture g) {
          int x = g.vs.xM();
          int y = g.vs.yL();
          if (Box.this.vs.hit(x, y)) {
            return Math.abs(x - Box.this.vs.xM());
          } else {
            return UC.noBid;
          }
        }
        @Override
        public void act(Gesture g) {
          Box.this.deleteMass();
        }
      });
    }
    @Override
    public void show(Graphics g) {
      vs.fill(g, c);
    }
  }
}
