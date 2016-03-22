package morris;

import java.awt.FileDialog;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.SwingUtilities;;

public class Game extends Environment {

	private static final long serialVersionUID = 1L;
	
	private final GamePane view;
	private final GameModel gameModel;

	public Game() {
		super();
		gameModel = new GameModel();
		view = new GamePane(gameModel);
		
		add(view);
		pack();
		setLocationRelativeTo(null);
	}

	public Game(File inFile) {
		super();
		gameModel = new GameModel(inFile);
		view = new GamePane(gameModel);
		
		add(view);
		pack();
		setLocationRelativeTo(null);
	}
	
	public Game(GameModel gameModel) {
		super();
		this.gameModel = gameModel;
		view = new GamePane(gameModel);
		
		add(view);
		pack();
		setLocationRelativeTo(null);
	}

	private Environment handleInGameMenu(Point mouseClick) {
		if (view.clickInMenu(mouseClick, 0)) {
			dispose();
			return new Menu();
		}

		if (view.clickInMenu(mouseClick, 1)) {
			FileDialog fd = new FileDialog(this, "Save Game...");
			fd.setVisible(true);
			if (fd.getFile() != null)
				gameModel.save(fd.getFiles()[0]);
		}

		return null;
	}

	private void handlePlacement(Point mouseClick) {

		for (int i = 0; i < gameModel.getPieceCount() * 2; i++) {

			if (view.clickInTray(mouseClick, i)) {
				if (gameModel.isOwnerAt(i)) {
					if (gameModel.isSelectedAt(i)) {
						gameModel.deselectPiece(i);
						return;
					}
					if (gameModel.isSelected())
						gameModel.deselectPiece(gameModel.getSelected()[0]);
					gameModel.selectPiece(i);
				}

			}

		}

		for (int i = 0; i < gameModel.getBoardSize(); i++) {
			for (int j = 0; j < gameModel.getBoardSize(); j++) {
				if (view.clickInBoard(mouseClick, i, j) && gameModel.isSelected() && gameModel.isUnoccupiedAt(i, j)) {
					gameModel.placePiece(i, j);
					gameModel.deselectPiece(gameModel.getSelected()[0]);
					gameModel.removePiece(gameModel.getSelected()[0]);
					if (gameModel.checkForMill(i, j))
						gameModel.setValidDeletions();
					else {
						if (gameModel.isBlueTurn() && gameModel.getBluePlaced() == 0 || gameModel.isRedTurn() && gameModel.getRedPlaced() == 0)
							if (gameModel.getTotalMoves() == 0)
								gameModel.setWinnerOpponent();
						gameModel.nextTurn();
					}
				}
			}
		}

	}

	private void handleMove(Point mouseClick) {
		for (int i = 0; i < gameModel.getBoardSize(); i++) {
			for (int j = 0; j < gameModel.getBoardSize(); j++) {
				if (view.clickInBoard(mouseClick, i, j)) {
					if (gameModel.isOwnerAt(i, j)) {
						if (gameModel.isSelectedAt(i, j)) {
							gameModel.deselectPiece(i, j);
							return;
						}
						if (gameModel.isSelected())
							gameModel.deselectPiece(gameModel.getSelected()[0], gameModel.getSelected()[1]);
						gameModel.selectPiece(i, j);
					}

					else if (gameModel.isSelected() && gameModel.isLegalAt(i, j)) {
						gameModel.placePiece(i, j);
						gameModel.deselectPiece(gameModel.getSelected()[0], gameModel.getSelected()[1]);
						gameModel.removePiece(gameModel.getSelected()[0], gameModel.getSelected()[1]);
						if (gameModel.checkForMill(i, j))
							gameModel.setValidDeletions();
						else {
							gameModel.nextTurn();
							if (gameModel.getTotalMoves() == 0)
								gameModel.setWinnerOpponent();
						}
					}
				}

			}
		}
	}

	private void handleDeletion(Point mouseClick) {
		for (int i = 0; i < gameModel.getBoardSize(); i++) {
			for (int j = 0; j < gameModel.getBoardSize(); j++) {
				if (view.clickInBoard(mouseClick, i, j)) {
					if (gameModel.isLegalAt(i, j)) {
						gameModel.removePiece(i, j);
						if (gameModel.getOpponentCount() == gameModel.getLosingPieceCount())
							gameModel.setWinnerCurrent();
						gameModel.nextTurn();
						if (gameModel.getTotalMoves() == 0) {
							gameModel.setWinnerOpponent();
						}
					}
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		Point mouseClick = SwingUtilities.convertPoint(this, e.getPoint(), view);
		
		next = handleInGameMenu(mouseClick);

		if (!gameModel.isWinner()) {
			if (gameModel.requiresDeletion())
				handleDeletion(mouseClick);
			else if (gameModel.isPlacingPhase())
				handlePlacement(mouseClick);
			else
				handleMove(mouseClick);
		}
		
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

	@Override
	public void mouseDragged(MouseEvent e) {

		mouseClicked(e);
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		view.highlightMenu(SwingUtilities.convertPoint(this, e.getPoint(), view));
		
	}
	
	

}
