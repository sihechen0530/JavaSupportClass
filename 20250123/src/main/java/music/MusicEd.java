package music;

import graphics.WinApp;
import graphics.G;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import reactions.Ink;
import reactions.Layer;
import reactions.Gesture;
import reactions.Reaction;
import reactions.Shape;

public class MusicEd extends WinApp {
  static {
    new Layer("BACK");
    new Layer("NOTE");
    new Layer("FORE");
  }  // static block run at initialization
  public static boolean training = false;
  public static I.Area curArea = Gesture.AREA;  // switch between training and gesture at any time
  public static Page PAGE;  // single constant

  public MusicEd() {
    super("Music Editor", UC.mainWindowWidth, UC.mainWindowHeight);
    Reaction.initialReactions.addReaction(new Reaction("W-W") {
      public int bid(Gesture g) {
        return 0;
      }
      public void act(Gesture g) {
        int y = g.vs.yM();
        PAGE = new Page(y);
        disable();
      }
    });
  }

  public void paintComponent(Graphics g) {
    G.clearScreen(g);
    if (training) { Shape.TRAINER.show(g); return; }
    Layer.ALL.show(g);
    g.setColor(Color.BLACK);
    Ink.BUFFER.show(g);
    g.drawString(Gesture.recognized, 900, 30);
    if (PAGE != null) {
//      Glyph.CLEF_G.showAt(g, 8, 100, PAGE.margins.top + 4 * 8);  // which char in font, scale, x, y
//      Glyph.HEAD_W.showAt(g, 8, 200, PAGE.margins.top + 4 * 8);
      // draw boxes to get font size
//      int H = 32;
//      Glyph.HEAD_Q.showAt(g, H, 200, PAGE.margins.top + 4 * H);
//      g.setColor(Color.RED);
//      g.drawRect(200, PAGE.margins.top + 3 * H, 24 * H / 10, 24 * H / 10);
    }

    // test beam stack
//    int H = 8, x1 = 100, x2 = 200;
//    Beam.setMasterBeam(x1, 100 + G.rnd(100), x2, 100 + G.rnd(100));
//    g.drawLine(0, Beam.my1, x1, Beam.my1);
//    Beam.drawBeamStack(g, 0, 1, x1, x2, H);
//    g.setColor(Color.ORANGE);
//    Beam.drawBeamStack(g, 1, 3, x1 + 10, x2 - 10, H);
  }

  public void mousePressed(MouseEvent me) { curArea.dn(me.getX(), me.getY()); repaint(); }
  public void mouseDragged(MouseEvent me) { curArea.drag(me.getX(), me.getY()); repaint(); }
  public void mouseReleased(MouseEvent me) {
    curArea.up(me.getX(), me.getY());
    trainBtn(me);
    repaint();
  }

  public void keyTyped(KeyEvent ke) { if (training) {Shape.TRAINER.keyTyped(ke); repaint(); }}
  public void trainBtn(MouseEvent me) {
    // TODO: magic number
    if (me.getX() > (UC.mainWindowWidth - 40) && me.getY() < 40) {
      training = !training; // toggle training
      curArea = training ? Shape.TRAINER : Gesture.AREA;
    }
  }
  public static void main(String[] args) {
    PANEL = new MusicEd();
    WinApp.launch();
  }

}
