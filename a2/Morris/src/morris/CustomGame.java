package morris;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class CustomGame extends Environment {

	private static final long serialVersionUID = 1L;
	
	private final GamePane view;
	private final GameModel gameModel;

	public CustomGame() {
		super();
		gameModel = new GameModel(true);
		view = new GamePane(gameModel);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(view);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private Environment handleInGameMenu(Point mouseClick){
		if (view.clickInMenu(mouseClick, 0)) {
			dispose();
			return new Menu();
		}

		if (view.clickInMenu(mouseClick, 1)) {
			gameModel.nextTurn();
		}
		
		if(view.clickInMenu(mouseClick, 2)) {
			String error = gameModel.validateBoard();
			if(error != null){
				view.setErrorString(error);
			}
			else{
				gameModel.endCustomization();
				dispose();
				return new Game(gameModel);
			}
		}

		return null;
	}

	private void handleCustomize(Point mouseClick){
		
		for(int i = 0; i < gameModel.getPieceCount() * 2; i++)
			if(view.clickInTray(mouseClick, i))
				gameModel.cyclePieces(i);
		
		for (int i = 0; i < gameModel.getBoardSize(); i++)
			for (int j = 0; j < gameModel.getBoardSize(); j++)
				if (view.clickInBoard(mouseClick, i, j))
					gameModel.cyclePieces(i, j);
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {

		Point mouseClick = SwingUtilities.convertPoint(this, e.getPoint(), view);
		
		next = handleInGameMenu(mouseClick);

		handleCustomize(mouseClick);
		
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
