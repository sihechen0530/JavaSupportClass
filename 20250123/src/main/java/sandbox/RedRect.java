package sandbox;

import graphics.WinApp;
import java.awt.Color;
import java.awt.Graphics;
import music.UC;

public class RedRect extends WinApp {

  public static final int W = UC.mainWindowWidth;
  public static final int H = UC.mainWindowHeight;
  public RedRect() {
    super("Red Rect", W, H);
  }

  @Override
  public void paintComponent(Graphics g) {
    g.setColor(Color.RED);
    g.fillRect(100, 100, 100, 100);
  }

  public static void main(String[] args) {
    PANEL = new RedRect();
    WinApp.launch();
  }

}
