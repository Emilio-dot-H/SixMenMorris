package morris;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

public class GameModel {

	private final PieceType[][] board;
	private final PieceType[] tray;
	private final PieceType[][] legalMoves;

	private int blueCount;
	private int redCount;
	private int blueToPlace;
	private int redToPlace;

	private PieceType turn;

	private boolean deletionRequired;
	private boolean placingPhase;
	private boolean pieceSelected;

	private int selectedX;
	private int selectedY;

	private PieceType winner;

	private final int boardSize = 7;
	private final int pieceCount = 6;
	private final int losingPieceCount = 2;
	
	private boolean customizing;

	private final String fileVersion = "MORRISFILE1.3";

	public GameModel(){
		this(false);
	}
	
	public GameModel(boolean customizing) {

		this.customizing = customizing;
		
		blueCount = redCount = customizing ? 0 : pieceCount;

		tray = new PieceType[pieceCount * 2];

		// set up the trays to be full of their respective pieces
		for (int i = 0; i < pieceCount; i++) {
			tray[i] = customizing ? PieceType.UNOCCUPIED : PieceType.BLUE;
			tray[pieceCount * 2 - 1 - i] = customizing ? PieceType.UNOCCUPIED : PieceType.RED;
		}

		// initialize all board positions to invalid
		board = new PieceType[boardSize][boardSize];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = PieceType.INVALID;
			}
		}

		// set valid spaces on outer square
		board[0][0] = board[0][3] = board[0][6] = board[3][0] = board[3][6] = board[6][0] = board[6][3] = board[6][6] = PieceType.UNOCCUPIED;

		// set valid spaces on inner square
		board[2][2] = board[2][3] = board[2][4] = board[3][2] = board[3][4] = board[4][2] = board[4][3] = board[4][4] = PieceType.UNOCCUPIED;

		// set paths
		board[0][1] = board[0][2] = board[0][4] = board[0][5] = board[1][0] = board[1][3] = board[1][6] = board[2][0] = board[2][6] = board[3][1] = board[3][5] = board[4][0] = board[4][6] = board[5][0] = board[5][3] = board[5][6] = board[6][1] = board[6][2] = board[6][4] = board[6][5] = PieceType.PATH;

		legalMoves = new PieceType[boardSize][boardSize];
		resetLegalMoves();

		// pick a random player to go first
		Random rand = new Random(System.currentTimeMillis());
		if (rand.nextBoolean())
			turn = PieceType.BLUE;
		else
			turn = PieceType.RED;

		deletionRequired = false;
		placingPhase = !customizing;
		pieceSelected = false;

		blueToPlace = redToPlace = customizing ? 0 : pieceCount;
		winner = PieceType.UNOCCUPIED;
		
	}

	public GameModel(File inFile) {

		this();

		try {
			Scanner input = new Scanner(inFile);

			if (input.next().compareTo(fileVersion) != 0) {
				input.close();
				return;
			}

			for (int i = 0; i < board.length; i++)
				for (int j = 0; j < board.length; j++)
					board[i][j] = PieceType.values()[input.nextInt()];

			for (int i = 0; i < tray.length; i++) {
				tray[i] = PieceType.values()[input.nextInt()];
			}

			for (int i = 0; i < legalMoves.length; i++)
				for (int j = 0; j < legalMoves.length; j++)
					legalMoves[i][j] = PieceType.values()[input.nextInt()];

			blueCount = input.nextInt();
			redCount = input.nextInt();
			blueToPlace = input.nextInt();
			redToPlace = input.nextInt();
			turn = PieceType.values()[input.nextInt()];
			placingPhase = input.nextBoolean();
			pieceSelected = input.nextBoolean();
			deletionRequired = input.nextBoolean();
			selectedX = input.nextInt();
			selectedY = input.nextInt();
			winner = PieceType.values()[input.nextInt()];

			input.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Integer getPieceCount() {
		return pieceCount;
	}

	public Boolean isSelected() {
		return pieceSelected;
	}

	public Boolean isOwnerAt(Integer x) {
		return tray[x] == turn || tray[x] == turn.select();
	}

	public Boolean isOwnerAt(Integer x, Integer y) {
		return board[x][y] == turn || board[x][y] == turn.select();
	}

	public void selectPiece(Integer x) {

		tray[x] = tray[x].select();
		selectedX = x;
		pieceSelected = true;
		setLegalMoves();

	}

	public void selectPiece(Integer x, Integer y) {

		board[x][y] = board[x][y].select();
		selectedX = x;
		selectedY = y;
		pieceSelected = true;
		setLegalMoves(x, y);

	}

	public void deselectPiece(Integer x) {
		tray[x] = tray[x].deselect();
		pieceSelected = false;
		resetLegalMoves();
	}

	public void deselectPiece(Integer x, Integer y) {
		board[x][y] = board[x][y].deselect();
		pieceSelected = false;
		resetLegalMoves();
	}

	public void placePiece(Integer x, Integer y) {

		board[x][y] = turn;
		if (placingPhase) {
			decreaseToPlace();

			if (blueToPlace == 0 && redToPlace == 0)
				placingPhase = false;
		}
	}

	public void removePiece(Integer x) {
		tray[x] = PieceType.UNOCCUPIED;
	}

	public void removePiece(Integer x, Integer y) {
		board[x][y] = PieceType.UNOCCUPIED;
		if (deletionRequired) {
			deletionRequired = false;
			switch (turn) {
			case BLUE:
				redCount--;
				break;
			case RED:
				blueCount--;
				break;
			default:
				break;
			}
		}
	}

	public void setWinnerCurrent() {
		winner = turn;
	}

	public void setWinnerOpponent() {
		winner = turn.swap();
	}

	public Boolean isWinner() {
		return winner != PieceType.UNOCCUPIED;
	}

	public Boolean winnerIsBlue() {
		return winner == PieceType.BLUE;
	}

	public Boolean winnerIsRed() {
		return winner == PieceType.RED;
	}

	public Boolean isBlueTurn() {
		return turn == PieceType.BLUE;
	}

	public Boolean isRedTurn() {
		return turn == PieceType.RED;
	}

	public void nextTurn() {
		turn = turn.swap();
	}

	public Boolean isPlacingPhase() {
		return placingPhase;
	}

	public Boolean isUnoccupiedAt(Integer x) {
		return tray[x] == PieceType.UNOCCUPIED;
	}

	public Boolean isUnoccupiedAt(Integer x, Integer y) {
		return board[x][y] == PieceType.UNOCCUPIED;
	}

	public Boolean isBlueAt(Integer x, Integer y) {
		return board[x][y] == PieceType.BLUE || board[x][y] == PieceType.BLUE_SELECTED;
	}

	public Boolean isRedAt(Integer x, Integer y) {
		return board[x][y] == PieceType.RED || board[x][y] == PieceType.RED_SELECTED;
	}

	public Boolean isSelectedAt(Integer x) {
		return tray[x] == PieceType.BLUE_SELECTED || tray[x] == PieceType.RED_SELECTED;
	}

	public Boolean isSelectedAt(Integer x, Integer y) {
		return board[x][y] == PieceType.BLUE_SELECTED || board[x][y] == PieceType.RED_SELECTED;
	}

	public Boolean isValidAt(Integer x, Integer y) {
		return board[x][y] != PieceType.INVALID;
	}

	public Boolean isBoardSpaceAt(Integer x, Integer y) {
		return board[x][y] != PieceType.INVALID && board[x][y] != PieceType.PATH;
	}

	public Integer[] getSelected() {
		return new Integer[] { selectedX, selectedY };
	}

	public Integer getBluePlaced() {
		return blueToPlace;
	}

	public Integer getRedPlaced() {
		return redToPlace;
	}

	public Integer getBoardSize() {
		return boardSize;
	}

	public PieceType getLegalAt(Integer x, Integer y) {
		return legalMoves[x][y];
	}

	public Boolean isLegalAt(Integer x, Integer y) {
		return legalMoves[x][y] == PieceType.LEGAL;
	}

	public Integer getCurrentCount() {
		return turn == PieceType.BLUE ? blueCount : redCount;
	}

	public Integer getOpponentCount() {
		return turn == PieceType.BLUE ? redCount : blueCount;
	}

	public Integer getLosingPieceCount() {
		return losingPieceCount;
	}

	private void decreaseToPlace() {
		switch (turn) {
		case RED:
			redToPlace--;
			break;
		case BLUE:
			blueToPlace--;
			break;
		default:
			break;
		}
	}

	public Boolean requiresDeletion() {
		return deletionRequired;
	}

	public Boolean checkForMill(Integer x, Integer y) {

		int count;
		int i;
		PieceType player = board[x][y];

		// check left to right
		i = 1;
		count = 1;
		while (x - i >= 0) {

			if (board[x - i][y] == player)
				count++;
			else if (board[x - i][y] != PieceType.PATH)
				break;
			i++;

		}

		i = 1;
		while (x + i < boardSize) {

			if (board[x + i][y] == player)
				count++;
			else if (board[x + i][y] != PieceType.PATH)
				break;
			i++;

		}

		if (count >= 3)
			return true;

		// check up and down
		i = 1;
		count = 1;
		while (y - i >= 0) {

			if (board[x][y - i] == player)
				count++;
			else if (board[x][y - i] != PieceType.PATH)
				break;
			i++;

		}

		i = 1;
		while (y + i < boardSize) {

			if (board[x][y + i] == player)
				count++;
			else if (board[x][y + i] != PieceType.PATH)
				break;
			i++;

		}

		if (count >= 3)
			return true;

		return false;
	}

	public void setValidDeletions() {

		PieceType player = turn.swap();

		resetLegalMoves();

		int nonMillPieces = 0;

		// check for pieces and make legal to delete if not in mill
		for (int i = 0; i < legalMoves.length; i++) {
			for (int j = 0; j < legalMoves.length; j++) {
				if (board[i][j] == player && !checkForMill(i, j)) {
					legalMoves[i][j] = PieceType.LEGAL;
					nonMillPieces++;
				}
			}
		}

		// if all pieces are in mills, then make deletion of any piece legal
		if (nonMillPieces == 0) {
			for (int i = 0; i < legalMoves.length; i++) {
				for (int j = 0; j < legalMoves.length; j++) {
					if (board[i][j] == player)
						legalMoves[i][j] = PieceType.LEGAL;
				}
			}
		}

		deletionRequired = true;

	}

	private void setLegalMoves() {
		resetLegalMoves();

		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] == PieceType.UNOCCUPIED)
					legalMoves[i][j] = PieceType.LEGAL;
			}
		}

	}

	private void setLegalMoves(Integer x, Integer y) {

		resetLegalMoves();

		Boolean searchLeft = true, searchRight = true, searchUp = true, searchDown = true;

		int i = 1;
		while (searchLeft || searchRight || searchDown || searchUp) {

			if (x - i < 0)
				searchLeft = false;
			if (x + i >= boardSize)
				searchRight = false;
			if (y - i < 0)
				searchUp = false;
			if (y + i >= boardSize)
				searchDown = false;

			if (searchLeft && !(board[x - i][y] == PieceType.PATH)) {
				searchLeft = false;
				if (board[x - i][y] == PieceType.UNOCCUPIED)
					legalMoves[x - i][y] = PieceType.LEGAL;
			}

			if (searchRight && !(board[x + i][y] == PieceType.PATH)) {
				searchRight = false;
				if (board[x + i][y] == PieceType.UNOCCUPIED)
					legalMoves[x + i][y] = PieceType.LEGAL;
			}

			if (searchUp && !(board[x][y - i] == PieceType.PATH)) {
				searchUp = false;
				if (board[x][y - i] == PieceType.UNOCCUPIED)
					legalMoves[x][y - i] = PieceType.LEGAL;
			}

			if (searchDown && !(board[x][y + i] == PieceType.PATH)) {
				searchDown = false;
				if (board[x][y + i] == PieceType.UNOCCUPIED)
					legalMoves[x][y + i] = PieceType.LEGAL;
			}

			i++;
		}

	}

	public Integer getTotalMoves() {

		int total = 0;

		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] == turn)
					total += countLegalMoves(i, j);
			}
		}

		return total;
	}

	private Integer countLegalMoves(Integer x, Integer y) {

		Integer moves = 0;
		int i = 1;
		Boolean searchLeft = true, searchRight = true, searchUp = true, searchDown = true;

		while (searchLeft || searchRight || searchDown || searchUp) {

			if (x - i < 0)
				searchLeft = false;
			if (x + i >= boardSize)
				searchRight = false;
			if (y - i < 0)
				searchUp = false;
			if (y + i >= boardSize)
				searchDown = false;

			if (searchLeft && !(board[x - i][y] == PieceType.PATH)) {
				searchLeft = false;
				if (board[x - i][y] == PieceType.UNOCCUPIED)
					moves++;
			}

			if (searchRight && !(board[x + i][y] == PieceType.PATH)) {
				searchRight = false;
				if (board[x + i][y] == PieceType.UNOCCUPIED)
					moves++;
			}

			if (searchUp && !(board[x][y - i] == PieceType.PATH)) {
				searchUp = false;
				if (board[x][y - i] == PieceType.UNOCCUPIED)
					moves++;
			}

			if (searchDown && !(board[x][y + i] == PieceType.PATH)) {
				searchDown = false;
				if (board[x][y + i] == PieceType.UNOCCUPIED)
					moves++;
			}

			i++;

		}

		return moves;

	}

	private void resetLegalMoves() {
		for (int i = 0; i < legalMoves.length; i++)
			for (int j = 0; j < legalMoves.length; j++)
				legalMoves[i][j] = PieceType.ILLEGAL;
	}

	public void save(File outFile) {

		try {
			PrintStream output = new PrintStream(outFile);

			output.println(fileVersion);

			for (int i = 0; i < board.length; i++)
				for (int j = 0; j < board.length; j++)
					output.println(board[i][j].ordinal());

			for (int i = 0; i < tray.length; i++)
				output.println(tray[i].ordinal());

			for (int i = 0; i < legalMoves.length; i++)
				for (int j = 0; j < legalMoves.length; j++)
					output.println(legalMoves[i][j].ordinal());

			output.println(blueCount);
			output.println(redCount);
			output.println(blueToPlace);
			output.println(redToPlace);
			output.println(turn.ordinal());
			output.println(placingPhase);
			output.println(pieceSelected);
			output.println(deletionRequired);
			output.println(selectedX);
			output.println(selectedY);
			output.println(winner.ordinal());

			output.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void cyclePieces(Integer i, Integer j){
		switch(board[i][j]){
		case BLUE:
			board[i][j] = PieceType.RED;
			blueCount--;
			redCount++;
			break;
		case RED:
			board[i][j] = PieceType.UNOCCUPIED;
			redCount--;
			break;
		case UNOCCUPIED:
			board[i][j] = PieceType.BLUE;
			blueCount++;
			break;
		default:
			break;
		}
	}
	
	public void cyclePieces(Integer i){
		switch(tray[i]){
		case UNOCCUPIED:
			if(i < pieceCount){
				tray[i] = PieceType.BLUE;
				blueToPlace++;
				blueCount++;
			}
			else {
				tray[i] = PieceType.RED;
				redToPlace++;
				redCount++;
			}
			break;
		case BLUE:
			tray[i] = PieceType.UNOCCUPIED;
			blueToPlace--;
			blueCount--;
			break;
		case RED:
			tray[i] = PieceType.UNOCCUPIED;
			redToPlace--;
			redCount--;
			break;
		default:
			break;
		}
		
		placingPhase = blueToPlace > 0 || redToPlace > 0;
	}
	
	public Boolean isCustomizing(){
		return customizing; 
	}

	public void endCustomization(){
		customizing = false;
	}
	
	public String validateBoard(){
		
		System.out.println(blueCount);
		System.out.println(redCount);
		
		if(blueCount < losingPieceCount)
			return "Number of blue pieces must be at least " + losingPieceCount;
		if(blueCount > pieceCount)
			return "Number of blue pieces must be at most " + pieceCount;
		if(redCount < losingPieceCount)
			return "Number of red pieces must be at least " + losingPieceCount;
		if(redCount > pieceCount)
			return "Number of red pieces must be at most " + pieceCount;
		
		int blueInMills = 0;
		int redInMills = 0;
		for (int i = 0; i < board.length; i++){
			for (int j = 0; j < board.length; j++){
				if(board[i][j] == PieceType.BLUE){
					if(checkForMill(i,j))
						blueInMills++;
				}
				else if(board[i][j] == PieceType.RED){
					if(checkForMill(i,j))
						redInMills++;
				}
			}
		}
		
		if(blueInMills == 3 && redCount > pieceCount - 1)
			return "Number of red pieces must be at most " + (pieceCount - 1) + " due to 1 blue mill";
		if(blueInMills > 3 && redCount > pieceCount - 2)
			return "Number of red pieces must be at most " + (pieceCount - 2) + " due to 2 blue mills";
		if(redInMills == 3 && blueCount > pieceCount - 1)
			return "Number of blue pieces must be at most " + (pieceCount - 1) + " due to 1 red mill";
		if(redInMills > 3 && blueCount > pieceCount - 2)
			return "Number of blue pieces must be at most " + (pieceCount - 2) + " due to 2 red mills";
		
		if(placingPhase){
			if(Math.abs(blueToPlace - redToPlace) > 1)
				return "Number of pieces in tray must be within 1";
			if(blueToPlace < redToPlace && turn == PieceType.BLUE)
				return "Turn must be red";
			if(redToPlace < blueToPlace && turn == PieceType.RED)
				return "Turn must be blue";
		}
		
		return null;
	}
	
}
