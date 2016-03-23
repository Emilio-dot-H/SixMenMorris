package morris;

import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

// Abstract class used for the windows of the game
// Handles all inputs
public abstract class Environment extends JFrame implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;
	
	protected Environment next = null;

	// Set up defaults for the window
	public Environment(){
		super("Six Men's Morris");
	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setResizable(false);
		setVisible(true);
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	// Run loop redraws the window every 1/60 s
	// When the game needs to switch to a different window
	// the new window is returned by this method
	public Environment run() {

		while (true) {

			try {
				Thread.sleep(1000 / 60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (next != null)
				return next;
			
			repaint();
		}

	}

}
