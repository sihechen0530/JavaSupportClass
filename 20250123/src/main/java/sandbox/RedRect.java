package sandbox;

import graphics.WinApp;
import java.awt.Color;
import java.awt.Graphics;

public class RedRect extends WinApp {

  public RedRect() {
    super("Red Rect", 1000, 700);
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
