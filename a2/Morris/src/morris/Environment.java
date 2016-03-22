package morris;

import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

public abstract class Environment extends JFrame implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;
	
	protected Environment next = null;

	public Environment(){
		super("Six Men's Morris");
	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setVisible(true);
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
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
