package morris;

import java.awt.FileDialog;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.SwingUtilities;;

// Window for the actual game (this is the game controller)
public class Game extends Environment {

	private static final long serialVersionUID = 1L;
	
	// the view and model for the game
	private final GamePane view;
	private final GameModel gameModel;

	// default constructor starts a new game
	public Game() {
		super();
		gameModel = new GameModel();
		view = new GamePane(gameModel);
		
		add(view);
		pack();
		setLocationRelativeTo(null);
	}

	// constructor to create game from a saved file
	public Game(File inFile) {
		super();
		gameModel = new GameModel(inFile);
		view = new GamePane(gameModel);
		
		add(view);
		pack();
		setLocationRelativeTo(null);
	}
	
	// constructor to create game from a custom created board
	public Game(GameModel gameModel) {
		super();
		this.gameModel = gameModel;
		view = new GamePane(gameModel);
		
		add(view);
		pack();
		setLocationRelativeTo(null);
	}

	// checks if a mouse click was on a menu item
	// if so, the corresponding action is carried out
	private Environment handleInGameMenu(Point mouseClick) {
		
		// return to main menu
		if (view.clickInMenu(mouseClick, 0)) {
			dispose();
			return new Menu();
		}

		// save game
		if (view.clickInMenu(mouseClick, 1)) {
			FileDialog fd = new FileDialog(this, "Save Game...", FileDialog.SAVE);
			fd.setVisible(true);
			if (fd.getFile() != null)
				gameModel.save(fd.getFiles()[0]);
		}

		return null;
	}
	
	// checks if a mouse click was on the game board
	// if so, passes the tray/board location to the model for processing
	private void handlePlay(Point mouseClick){
		
		for (int i = 0; i < gameModel.getPieceCount() * 2; i++) 
			if (view.clickInTray(mouseClick, i))
				gameModel.play(i);
		
		for (int i = 0; i < gameModel.getBoardSize(); i++) 
			for (int j = 0; j < gameModel.getBoardSize(); j++) 
				if (view.clickInBoard(mouseClick, i, j)) 
					gameModel.play(i,j);
		
		
	}
	
	// if mouse is clicked, check if click is on menu item or the tray/game board
	@Override
	public void mouseClicked(MouseEvent e) {

		Point mouseClick = SwingUtilities.convertPoint(this, e.getPoint(), view);
		
		next = handleInGameMenu(mouseClick);
		
		handlePlay(mouseClick);
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	// treat drag as mouse click
	@Override
	public void mouseDragged(MouseEvent e) {

		mouseClicked(e);
		
	}

	// when mouse is moved, check if the mouse is over menu options and highlight if so
	@Override
	public void mouseMoved(MouseEvent e) {
		
		view.highlightMenu(SwingUtilities.convertPoint(this, e.getPoint(), view));
		
	}
	
	

}
