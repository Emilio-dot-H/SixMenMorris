package Morris;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MorrisView {

	private JFrame frame;
	private JLabel background;
	private JPanel controlPanel, BPanel;
	private JButton pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8, pos9, pos10, pos11, pos12, pos13, pos14, pos15,
			pos16;
	private JButton disk;
	private JLabel headerLabel;

	public MorrisView() {
	    prepareGUI();
	}

	public static void main(String[] args) {
		MorrisView morris = new MorrisView();
		morris.labelBackground();
		morris.positions();
	}

	public void prepareGUI() {
		// Creates the frame
		frame = new JFrame("6 Men's Morris");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Generate background...
		labelBackground();
		// Allowing frame to use JLabel
		frame.setLayout(new BorderLayout());
		frame.setContentPane(background);
		
		frame.setSize(1000,1000);
	//	frame.getContentPane().setPreferredSize(new Dimension(1000, 1000));
		frame.setLayout(new GridBagLayout());
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	public void labelBackground() {
		try {
			BufferedImage img = ImageIO.read(new File("game_board.jpg"));
			background = new JLabel(new ImageIcon(img));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void positions() {
		GridBagConstraints point = new GridBagConstraints();
		//distance between the buttons (top, left, bottom, right)
		point.insets = new Insets(110, 40, 105, 40);
	
		//Bigger Square (TOP)
		pos1 = new JButton("(             )");
		point.gridx = 0;
		point.gridy = 0;
		pos1.setOpaque(false); //allows button to be invisible
		pos1.setContentAreaFilled(false);
		pos1.setBorderPainted(false);
		frame.add(pos1, point);
		pos2 = new JButton("Row1Col2");
		point.gridx = 2;
		point.gridy = 0;
		pos2.setOpaque(false); //allows button to be invisible
		pos2.setContentAreaFilled(false);
		pos2.setBorderPainted(false);
		frame.add(pos2, point);
		pos3 = new JButton("Row1Col3");
		point.gridx = 4;
		point.gridy = 0;
		pos3.setOpaque(false); //allows button to be invisible
		pos3.setContentAreaFilled(false);
		pos3.setBorderPainted(false);
		frame.add(pos3, point);

		//Big Square (MID) / Small Square (MID)
		pos7 = new JButton("Row3Col1"); //Big
		point.gridx = 0;
		point.gridy = 2;
		pos7.setOpaque(false); //allows button to be invisible
		pos7.setContentAreaFilled(false);
		pos7.setBorderPainted(false);
		frame.add(pos7, point);
		pos10 = new JButton("Row3Col4"); //Big
		point.gridx = 4;
		point.gridy = 2;
		pos10.setOpaque(false); //allows button to be invisible
		pos10.setContentAreaFilled(false);
		pos10.setBorderPainted(false);
		frame.add(pos10, point);

		//BIG SQUARE (BOTTOM)
		pos14 = new JButton("Row5Col1");
		point.gridx = 0;
		point.gridy = 4;
		pos14.setOpaque(false); //allows button to be invisible
		pos14.setContentAreaFilled(false);
		pos14.setBorderPainted(false);
		frame.add(pos14, point);
		pos15 = new JButton("Row5Col2");
		point.gridx = 2;
		point.gridy = 4;
		pos15.setOpaque(false); //allows button to be invisible
		pos15.setContentAreaFilled(false);
		pos15.setBorderPainted(false);
		frame.add(pos15, point);
		pos16 = new JButton("Row5Col3");
		point.gridx = 4;
		point.gridy = 4;
		pos16.setOpaque(false); //allows button to be invisible
		pos16.setContentAreaFilled(false);
		pos16.setBorderPainted(false);
		frame.add(pos16, point);
	
		frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        smallSquare();

	}
	public void smallSquare(){
		GridBagConstraints guard = new GridBagConstraints();
		guard.insets = new Insets(20, 20, 20, 20);
		
		//Small Square (TOP)
		pos4 = new JButton("Row2Col1");
		guard.gridx = 1;
		guard.gridy = 1;
		pos4.setOpaque(false); //allows button to be invisible
		pos4.setContentAreaFilled(false);
		pos4.setBorderPainted(false);
		frame.add(pos4, guard);
		pos5 = new JButton("Row2Col2");
		guard.gridx = 2;
		guard.gridy = 1;
		pos5.setOpaque(false); //allows button to be invisible
		pos5.setContentAreaFilled(false);
		pos5.setBorderPainted(false);
		frame.add(pos5, guard);
		pos6 = new JButton("Row2Col3");
		guard.gridx = 3;
		guard.gridy = 1;
		pos6.setOpaque(false); //allows button to be invisible
		pos6.setContentAreaFilled(false);
		pos6.setBorderPainted(false);
		frame.add(pos6, guard);
		
		//MIDDLE
		pos8 = new JButton("Row3Col2"); //Small
		guard.gridx = 1;
		guard.gridy = 2;
		pos8.setOpaque(false); //allows button to be invisible
		pos8.setContentAreaFilled(false);
		pos8.setBorderPainted(false);
		frame.add(pos8, guard);
		pos9 = new JButton("Row3Col3"); //Small
		guard.gridx = 3;
		guard.gridy = 2;
		pos9.setOpaque(false); //allows button to be invisible
		pos9.setContentAreaFilled(false);
		pos9.setBorderPainted(false);
		frame.add(pos9, guard);
		
		//Small Square(BOTTOM)
		pos11 = new JButton("Row4Col1");
		guard.gridx = 1;
		guard.gridy = 3;
		pos11.setOpaque(false); //allows button to be invisible
		pos11.setContentAreaFilled(false);
		pos11.setBorderPainted(false);
		frame.add(pos11, guard);
		pos12 = new JButton("Row4Col2");
		guard.gridx = 2;
		guard.gridy = 3;
		pos12.setOpaque(false); //allows button to be invisible
		pos12.setContentAreaFilled(false);
		pos12.setBorderPainted(false);
		frame.add(pos12, guard);
		pos13 = new JButton("Row4Col3");
		guard.gridx = 3;
		guard.gridy = 3;
		pos13.setOpaque(false); //allows button to be invisible
		pos13.setContentAreaFilled(false);
		pos13.setBorderPainted(false);
		frame.add(pos13, guard);
		
		frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        diskPosition();
		
	}
	public void diskPosition(){
		
		GridBagConstraints stone = new GridBagConstraints();
		JButton conv = new JButton();
		disk = randomStart(conv);
		stone.gridx=2;
		stone.gridy=2;
		
		frame.add(disk, stone);
		
		frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
	/*	blueDisk = new JButton("BlueDisks");
		redDisk = new JButton("RedDisks");
		JPanel diskPanel = new JPanel(new GridLayout(1,2)); // 1 row, 2 cols
		diskPanel.add(blueDisk);
		diskPanel.add(redDisk);
		frame.setLocationRelativeTo(null);
        frame.setVisible(true);*/
	} 
	private JButton randomStart(JButton conv){
		String x;
		int rand = (int)(Math.random() * ((1) + 1));
		System.out.println(rand);
		String icon;
		if (rand == 0){
		    //headerLabel.setText("Red's turn ");
			//disk.setBackground(Color.RED);
			x = "Red's Turn";
			icon = "RedPiece.png";
		} 
		else{
			//headerLabel.setText("Blue's turn "); 
			//disk.setBackground(Color.BLUE);
			x = "Blue's Turn";
			icon = "BluePiece.png";
			}
		ImageIcon piece = new ImageIcon(icon);
		conv = new JButton(x, piece);
		conv.setVerticalTextPosition(SwingConstants.BOTTOM);
		// headerLabel = new JLabel(x, SwingConstants.CENTER); 
		// frame.add(headerLabel);
		return conv;
	   }
}