package morris;

import java.awt.geom.Ellipse2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

public class GamePane extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private final GameModel gameModel;
	
	// used for spacing and sizing of the displayed board, menu, etc
	private final int winX = 600;
	private final int winY;
	private final double gapFraction = 0.1;
	private final int segmentLen;
	private final int pieceDiameter;
	private final int topGap;
	private final int leftGap;
	private final int trayGap;
	private final int traySpace;

	// these store rectangles that are used to detect if a click is on the board, trays, or menu items
	private final Rectangle[][] inputMapBoard;
	private final Rectangle[] inputMapTray;
	private final Rectangle[] inputMapMenu;

	// stores font info for rendering
	private final Font f;
	private final FontMetrics fm;

	private int highlight;

	// stores menu items
	private final String[] menuItems;
	
	// error string that is displayed during custom board creation -- null if no error
	private String errorString;

	public GamePane(GameModel gameModel) {
		super();
		
		this.gameModel = gameModel;
		
		// sets menu items (these are different depending on if creating a custom board or playing a game)
		if(gameModel.isCustomizing())
			menuItems = new String[]{ "Exit to Menu", "Switch Turn", "Play" };
		else
			menuItems = new String[]{ "Exit to Menu", "Save Game" };

		// sets window height based on width
		winY = (int) (1.2 * (double) winX);
		
		// set the preferred window size and background color
		setPreferredSize(new Dimension(winX, winY));
		setBackground(Color.lightGray);

		// next little bit sets up spacing/sizes for board display
		int boardLen = (int) ((1.0 - 2 * gapFraction) * winX);
		int segments = gameModel.getBoardSize() - 1;
		int segmentLen = 0;
		while (segmentLen * segments <= boardLen) {
			segmentLen++;
		}
		segmentLen--;

		leftGap = (winX - segmentLen * segments) / 2;
		topGap = leftGap + (winY - winX) / 2;
		trayGap = (winY - winX) / 2;

		pieceDiameter = segmentLen / 4;

		traySpace = (winX - 6 * leftGap) / (2 * (gameModel.getPieceCount() - 1));

		this.segmentLen = segmentLen;
		
		// set font to be used in text rendering
		f = new Font("Dialog", Font.PLAIN, 20);
		fm = getFontMetrics(f);

		inputMapBoard = new Rectangle[gameModel.getBoardSize()][gameModel.getBoardSize()];

		// create a map of clickable areas that correspond to the displayed board
		for (int i = 0; i < gameModel.getBoardSize(); i++) {
			for (int j = 0; j < gameModel.getBoardSize(); j++) {
				if (gameModel.isBoardSpaceAt(i, j))
					inputMapBoard[i][j] = new Rectangle(leftGap + segmentLen * i - pieceDiameter / 2, topGap + segmentLen * j - pieceDiameter / 2, pieceDiameter, pieceDiameter);
			}
		}

		
		inputMapTray = new Rectangle[gameModel.getPieceCount() * 2];
		
		// create a map of clickable areas that correspond to the displayed trays
		for (int i = 0; i < gameModel.getPieceCount(); i++) {
			inputMapTray[i] = new Rectangle(leftGap * 2 + traySpace * i - pieceDiameter / 2, trayGap - pieceDiameter / 2, pieceDiameter, pieceDiameter);
			inputMapTray[inputMapTray.length - 1 - i] = new Rectangle(winX - leftGap * 2 - traySpace * i - pieceDiameter / 2, trayGap - pieceDiameter / 2, pieceDiameter, pieceDiameter);
		}

		inputMapMenu = new Rectangle[menuItems.length];

		// create a map of clickable areas that correspond to the displayed menu items
		for (int i = 0; i < menuItems.length; i++) {
			int w = fm.stringWidth(menuItems[i]);
			int h = fm.getHeight();
			inputMapMenu[i] = new Rectangle(leftGap / 2, winY + leftGap/4 - (i + 1) * leftGap / 2 - h + fm.getDescent(), w, h);
		}

		highlight = -1;
		
		errorString = null;
	}

	// paints the game state to the screen
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(2));
		g2d.setFont(f);

		// draw the trays
		drawTrays(g2d);
		// draw the board
		drawBoard(g2d);
		// highlight legal moves in green
		drawMoves(g2d);
		// draw info:  displays who's turn it is, or declares a winner if someone has won
		drawInfo(g2d);
		// draw the in game menu
		drawMenu(g2d);
		
	}

	// iterates through trays and draws pieces
	private void drawTrays(Graphics2D g2d){
		for (int i = 0; i < gameModel.getPieceCount(); i++) {

			int j = gameModel.getPieceCount() * 2 - 1 - i;

			// draw small nodes representing the tray positions for custom game creation
			if(gameModel.isCustomizing()){
				drawNode(g2d, leftGap * 2 + traySpace * i, trayGap, Color.BLACK);
				drawNode(g2d, winX - leftGap * 2 - traySpace * i, trayGap, Color.BLACK);
			}
			
			// draw tray pieces
			if (!gameModel.isUnoccupiedAt(i))
				drawPiece(g2d, Color.BLUE, leftGap * 2 + traySpace * i, trayGap, gameModel.isSelectedAt(i));
			if (!gameModel.isUnoccupiedAt(j))
				drawPiece(g2d, Color.RED, winX - leftGap * 2 - traySpace * i, trayGap, gameModel.isSelectedAt(j));
		}
	}
	
	// iterates through board locations and draws lines and pieces that form the board
	private void drawBoard(Graphics2D g2d){
		for (int i = 0; i < gameModel.getBoardSize(); i++) {
			for (int j = 0; j < gameModel.getBoardSize(); j++) {
				// draw horizontal board lines
				if (i != gameModel.getBoardSize() - 1)
					if (gameModel.isValidAt(i, j) && gameModel.isValidAt(i + 1, j))
						g2d.drawLine(leftGap + segmentLen * i, topGap + segmentLen * j, leftGap + segmentLen * (i + 1), topGap + segmentLen * j);

				// draw vertical board lines
				if (j != gameModel.getBoardSize() - 1)
					if (gameModel.isValidAt(i, j) && gameModel.isValidAt(i, j + 1))
						g2d.drawLine(leftGap + segmentLen * i, topGap + segmentLen * j, leftGap + segmentLen * i, topGap + segmentLen * (j + 1));
				
				// draw small nodes at the board spaces
				if (gameModel.isBoardSpaceAt(i, j))
					drawNode(g2d, leftGap + segmentLen * i, topGap + segmentLen * j, Color.BLACK);

				// draw pieces
				if (gameModel.isBlueAt(i, j))
					drawPiece(g2d, Color.BLUE, leftGap + segmentLen * i, topGap + segmentLen * j, gameModel.isSelectedAt(i, j));
				else if (gameModel.isRedAt(i, j))
					drawPiece(g2d, Color.RED, leftGap + segmentLen * i, topGap + segmentLen * j, gameModel.isSelectedAt(i, j));

			}
		}
	}
	
	// displays legal moves (green nodes) if a piece is selected
	private void drawMoves(Graphics2D g2d){
		if (gameModel.isSelected() || gameModel.requiresDeletion()) {
			for (int i = 0; i < gameModel.getBoardSize(); i++)
				for (int j = 0; j < gameModel.getBoardSize(); j++)
					if (gameModel.isLegalAt(i, j))
						drawNode(g2d, leftGap + segmentLen * i, topGap + segmentLen * j, Color.GREEN);
		}
	}
	
	// draws game info:  displays the current turn or winner
	private void drawInfo(Graphics2D g2d){
		// if there is a winner, display the winner
		if (gameModel.isWinner()) {
			if (gameModel.winnerIsBlue()) {
				g2d.setColor(Color.BLUE);
				g2d.drawString("BLUE WINS", winX / 2 - fm.stringWidth("BLUE WINS") / 2, winY - leftGap);
			} else {
				g2d.setColor(Color.RED);
				g2d.drawString("RED WINS", winX / 2 - fm.stringWidth("RED WINS") / 2, winY - leftGap);
			}
		} 
		// or display the current turn if there is no winner
		else {
			if (gameModel.isBlueTurn()) {
				g2d.setColor(Color.BLUE);
				g2d.drawString("BLUE'S TURN", winX / 2 - fm.stringWidth("BLUE'S TURN") / 2, winY - leftGap);
			} else {
				g2d.setColor(Color.RED);
				g2d.drawString("RED'S TURN", winX / 2 - fm.stringWidth("RED'S TURN") / 2, winY - leftGap);
			}
		}
	}
	
	// draw the menu items
	private void drawMenu(Graphics2D g2d){
		
		// highlight a menu item in yellow if the mouse is over it (highlight will be set to the proper index if this is the case)
		if (highlight != -1) {
			g2d.setColor(Color.YELLOW);
			g2d.fill(inputMapMenu[highlight]);
		}
		
		// draw the menu text
		g2d.setColor(Color.BLACK);
		for (int i = 0; i < menuItems.length; i++)
			g2d.drawString(menuItems[i], leftGap / 2, winY + leftGap/4 - (i + 1) * leftGap / 2);

		// if the errorString is not null, display the error at the top of the screen
		if(errorString != null){
			int w = fm.stringWidth(errorString);
			int h = fm.getHeight();
			g2d.setColor(Color.YELLOW);
			g2d.fill(new Rectangle(winX / 2 - w / 2, leftGap/2 - h + fm.getDescent(), w, h));
			g2d.setColor(Color.RED);
			g2d.drawString(errorString, winX / 2 - fm.stringWidth(errorString) / 2, leftGap/2);
			g2d.setColor(Color.BLACK);
		}
	}
	
	// draws a game piece with center at (x, y)
	private void drawPiece(Graphics2D g2d, Color col, int x, int y, boolean selected) {

		x = x - pieceDiameter / 2;
		y = y - pieceDiameter / 2;

		// draw the piece of specified color
		g2d.setColor(col);
		g2d.fill(new Ellipse2D.Double(x, y, pieceDiameter, pieceDiameter));

		// draw a thick yellow circle around the piece if it is currently selected
		if (selected) {
			g2d.setColor(Color.YELLOW);
			g2d.setStroke(new BasicStroke(5));
			g2d.drawOval(x, y, pieceDiameter, pieceDiameter);
			g2d.setStroke(new BasicStroke(2));
		}

		// draw a thin black circle around the piece
		g2d.setColor(Color.BLACK);
		g2d.drawOval(x, y, pieceDiameter, pieceDiameter);

	}

	// draws a small node with center (x, y)
	private void drawNode(Graphics2D g2d, int x, int y, Color col) {

		x = x - pieceDiameter / 4;
		y = y - pieceDiameter / 4;

		// draw node of specified color
		g2d.setColor(col);
		g2d.fill(new Ellipse2D.Double(x, y, pieceDiameter / 2, pieceDiameter / 2));
		
		// draw a thin black circle around the node
		g2d.setColor(Color.BLACK);
		g2d.drawOval(x, y, pieceDiameter / 2, pieceDiameter / 2);

	}

	// checks if a point is contained in the on screen trays at index i
	public Boolean clickInTray(Point mouseClick, Integer i) {
		if (inputMapTray[i] == null)
			return false;
		if (inputMapTray[i].contains(mouseClick))
			return true;
		return false;
	}

	// checks if a point is contained in the on screen board at index (i, j)
	public Boolean clickInBoard(Point mouseClick, Integer i, Integer j) {
		if (inputMapBoard[i][j] == null)
			return false;
		if (inputMapBoard[i][j].contains(mouseClick))
			return true;
		return false;
	}

	// checks if a point is contained in the on screen menu at index i
	public Boolean clickInMenu(Point mouseClick, Integer i) {
		if (inputMapMenu[i] == null)
			return false;
		if (inputMapMenu[i].contains(mouseClick))
			return true;
		return false;
	}
	
	// sets error string
	public void setErrorString(String error){
		errorString = error;
	}

	// sets highlight to a menu item if it contains the specified point
	public void highlightMenu(Point p) {

		for (int i = 0; i < inputMapMenu.length; i++) {
			if (inputMapMenu[i] == null)
				continue;
			if (inputMapMenu[i].contains(p)) {
				highlight = i;
				break;
			}

			highlight = -1;
		}

	}
}
