package morris;

import java.awt.FileDialog;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

public class Menu extends Environment {

	private static final long serialVersionUID = 1L;
	
	private final MenuPane view;

	public Menu() {

		super();
		view = new MenuPane();
		
		add(view);
		pack();
		setLocationRelativeTo(null);
		
	}
	
	// handles clicks on menu options
	private Environment handleMenu(Point mouseClick){
		
		// start new game
		if (view.clickInMenu(mouseClick, 0)) {
			dispose();
			return new Game();
		}
		
		// start customizing game
		else if (view.clickInMenu(mouseClick, 1)) {
			dispose();
			return new CustomGame();
		}

		// load a saved game
		else if (view.clickInMenu(mouseClick, 2)) {
			FileDialog fd = new FileDialog(this, "Load Game...");
			fd.setVisible(true);
			if (fd.getFile() != null){
				dispose();
				return new Game(fd.getFiles()[0]);
			}
		}

		return null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		Point mouseClick = SwingUtilities.convertPoint(this, e.getPoint(), view);
		
		next = handleMenu(mouseClick);
		
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
