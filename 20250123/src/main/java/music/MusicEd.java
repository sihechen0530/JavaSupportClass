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
      Glyph.CLEF_G.showAt(g, 8, 100, PAGE.margins.top + 4 * 8);  // which char in font, scale, x, y
//      Glyph.HEAD_W.showAt(g, 8, 200, PAGE.margins.top + 4 * 8);
    }
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
