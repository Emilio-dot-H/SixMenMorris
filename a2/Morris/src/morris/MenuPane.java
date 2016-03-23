package morris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

public class MenuPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private final int winX = 300;
	private final int winY = 300;
	private final int menuSpace;
	private final Font f;
	private final FontMetrics fm;

	// the items in the menu
	private final String[] menuItems = { "Start New Game", "Create Custom Game", "Load Game" };

	private final Rectangle inputMap[];

	private int highlight;

	public MenuPane() {

		// set the preferred window size and background color
		setPreferredSize(new Dimension(winX, winY));
		setBackground(Color.lightGray);

		// set font info for rendering
		f = new Font("Dialog", Font.PLAIN, 20);
		fm = getFontMetrics(f);

		// set up spacing for rendering
		menuSpace = winY / (menuItems.length + 1);

		inputMap = new Rectangle[menuItems.length];
		
		// map clickable areas to the viewable menu items
		for (int i = 0; i < menuItems.length; i++) {
			int w = fm.stringWidth(menuItems[i]);
			int h = fm.getHeight();
			inputMap[i] = new Rectangle(winX / 2 - w / 2, menuSpace * (i + 1) - h + fm.getDescent(), w, h);
		}

		highlight = -1;

		setVisible(true);
	}

	@Override
    protected void paintComponent(Graphics g)  {
		Graphics2D g2d = (Graphics2D) g;

		// highlight menu option if mouse is over it
		g2d.setColor(Color.YELLOW);
		if (highlight != -1)
			g2d.fill(inputMap[highlight]);

		// render the menu text for each item
		g2d.setFont(f);
		g2d.setColor(Color.BLACK);
		for (int i = 0; i < menuItems.length; i++)
			g2d.drawString(menuItems[i], winX / 2 - fm.stringWidth(menuItems[i]) / 2, menuSpace * (i + 1));

	}

	// returns true if a click is contained in menu item at index i
	public Boolean clickInMenu(Point mouseClick, Integer i) {
		if (inputMap[i] == null)
			return false;
		if (inputMap[i].contains(mouseClick))
			return true;
		return false;
	}

	// sets highlight to a menu item if it contains the specified point
	public void highlightMenu(Point p) {

		for (int i = 0; i < inputMap.length; i++) {
			if (inputMap[i] == null)
				continue;
			if (inputMap[i].contains(p)) {
				highlight = i;
				break;
			}

			highlight = -1;
		}

	}
}
