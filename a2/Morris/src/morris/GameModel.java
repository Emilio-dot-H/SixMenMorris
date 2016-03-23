package morris;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

// This class contains the model of the game
// The board and trays are stored here, as well as the game logic
public class GameModel {

	// the game board
	// represented by PieceType:  	UNOCCUPIED = valid board position, contains no piece
	//								BLUE = valid board position, contains blue piece
	//								BLUE_SELECTED = valid board position, contains selected blue piece
	//								RED = valid board position, contains red piece
	//								RED_SELECTED = valid board position, contains selected red piece
	//								PATH = not a board position, but links two boards positions
	//								INVALID = not a board position i.e. unused index
	private final PieceType[][] board;
	
	// the piece trays:  either BLUE, RED, or UNOCCUPIED
	private final PieceType[] tray;
	
	// positions of legal moves (used when moving a piece or deleting an opponents piece)
	// either LEGAL or ILLEGAL
	private final PieceType[][] legalMoves;

	// current piece counts of red and blue
	private int blueCount;
	private int redCount;
	
	// number of pieces left in try for red and blue
	private int blueToPlace;
	private int redToPlace;

	// the current turn (BLUE if blue, RED if red)
	private PieceType turn;

	// true if a player has formed a mill and needs to delete an opponents piece
	private boolean deletionRequired;
	
	// true if pieces are still to be placed from tray
	private boolean placingPhase;
	
	// true if the current player has selected a piece to place/move
	private boolean pieceSelected;

	// the index of the currently selected piece (if piece is in tray, only selectedX is valid)
	private int selectedX;
	private int selectedY;

	// the winner of the game (UNOCCUPIED if none, BLUE if blue, RED if red)
	private PieceType winner;

	// the square dimension of the modeled board
	private final int boardSize = 7;
	
	// the number of pieces that each player starts with
	private final int pieceCount = 6;
	
	// the number of pieces that cause a player to lose the game
	private final int losingPieceCount = 2;
	
	// true if creating a custom board
	private boolean customizing;

	// used in saving/loading:  if first line of file is equal to this it is assumed to be a valid save file
	private final String fileVersion = "MORRISFILE1.3";

	// default constructor:  not a custom board
	public GameModel(){
		this(false);
	}
	
	// constructor takes boolean:  if true, a custom board being created
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

		// initialize legal moves to none (all ILLEGAL)
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
		
		selectedX = selectedY = 0;
		
	}

	// constructor creates board and loads states from save file
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

	// get piece count (i.e. number of pieces that each player starts with)
	public Integer getPieceCount() {
		return pieceCount;
	}

	// returns true if a piece is currently selected
	public Boolean isSelected() {
		return pieceSelected;
	}

	// 
	private Boolean isOwnerAt(Integer x) {
		return tray[x] == turn || tray[x] == turn.select();
	}

	private Boolean isOwnerAt(Integer x, Integer y) {
		return board[x][y] == turn || board[x][y] == turn.select();
	}

	// select piece at tray[x]
	private void selectPiece(Integer x) {

		tray[x] = tray[x].select();
		selectedX = x;
		pieceSelected = true;
		setLegalMoves();

	}

	// select piece at board[x][y]
	private void selectPiece(Integer x, Integer y) {

		board[x][y] = board[x][y].select();
		selectedX = x;
		selectedY = y;
		pieceSelected = true;
		setLegalMoves(x, y);

	}

	// deselect the currently selected piece
	private void deselectPiece() {
		if(placingPhase)
			tray[selectedX] = tray[selectedX].deselect();
		else
			board[selectedX][selectedY] = board[selectedX][selectedY].deselect();
		pieceSelected = false;
		resetLegalMoves();
	}

	// place a piece on the board
	private void placePiece(Integer x, Integer y) {
		
		// unselect the currently selected piece
		deselectPiece();
		// set the piece at board[x][y] to the current player (place the piece)
		board[x][y] = turn;
		
		// if in the placing phase...
		if (placingPhase) {
			
			// remove the placed piece from the tray
			removePiece(selectedX);
			
			// decrease the number of pieces to place
			decreaseToPlace();

			// end the placing phase if no piece left in tray
			if (blueToPlace == 0 && redToPlace == 0)
				placingPhase = false;
		} 
		// if not in placing phase, remove the piece moved from its original board location
		else
			removePiece(selectedX, selectedY);
	}

	// removes piece at tray[x]
	private void removePiece(Integer x) {
		tray[x] = PieceType.UNOCCUPIED;
	}

	// removes piece at board[x][y]
	private void removePiece(Integer x, Integer y) {
		// remove the piece
		board[x][y] = PieceType.UNOCCUPIED;
		// decrease piece counts if it was a deletion, and unset the deletion required flag
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

	// returns true if there is a winner
	public Boolean isWinner() {
		return winner != PieceType.UNOCCUPIED;
	}

	// returns true if blue has won
	public Boolean winnerIsBlue() {
		return winner == PieceType.BLUE;
	}

	// returns true if red has won
	public Boolean winnerIsRed() {
		return winner == PieceType.RED;
	}

	// returns true if it is blue's turn
	public Boolean isBlueTurn() {
		return turn == PieceType.BLUE;
	}

	// returns true if it is red's turn
	public Boolean isRedTurn() {
		return turn == PieceType.RED;
	}

	// switches turn
	public void nextTurn() {
		turn = turn.swap();
	}
	
	// returns true if a deletion is required to be made by the current player (due to forming a mill)
	public Boolean requiresDeletion(){
		return deletionRequired;
	}

	// returns true if the space at tray[x] is empty
	public Boolean isUnoccupiedAt(Integer x) {
		return tray[x] == PieceType.UNOCCUPIED;
	}

	// returns true if the board space at board[x][y] is empty (and is a valid location for a piece)
	public Boolean isUnoccupiedAt(Integer x, Integer y) {
		return board[x][y] == PieceType.UNOCCUPIED;
	}

	// returns true if a blue piece is at board[x][y]
	public Boolean isBlueAt(Integer x, Integer y) {
		return board[x][y] == PieceType.BLUE || board[x][y] == PieceType.BLUE_SELECTED;
	}

	// returns true if a red piece is at board[x][y]
	public Boolean isRedAt(Integer x, Integer y) {
		return board[x][y] == PieceType.RED || board[x][y] == PieceType.RED_SELECTED;
	}

	// returns true if the piece at tray[x] is selected (red or blue)
	public Boolean isSelectedAt(Integer x) {
		return tray[x] == PieceType.BLUE_SELECTED || tray[x] == PieceType.RED_SELECTED;
	}

	// returns true if the piece at board[x][y] is selected (red or blue)
	public Boolean isSelectedAt(Integer x, Integer y) {
		return board[x][y] == PieceType.BLUE_SELECTED || board[x][y] == PieceType.RED_SELECTED;
	}

	// returns true if the spot at board[x][y] is invalid (not used in the model)
	public Boolean isValidAt(Integer x, Integer y) {
		return board[x][y] != PieceType.INVALID;
	}

	// returns true if the spot at board[x][y] is a space where a player's piece can go
	public Boolean isBoardSpaceAt(Integer x, Integer y) {
		return board[x][y] != PieceType.INVALID && board[x][y] != PieceType.PATH;
	}

	// get square size of the board (the modeled board dimension)
	public Integer getBoardSize() {
		return boardSize;
	}

	// returns true if a legal move can be made at board[x][y]
	public Boolean isLegalAt(Integer x, Integer y) {
		return legalMoves[x][y] == PieceType.LEGAL;
	}

	// returns the number of pieces that the opponent currently has on the board
	private Integer getOpponentCount() {
		return turn == PieceType.BLUE ? redCount : blueCount;
	}

	// decrement the current player's number of pieces in tray
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

	
	// checks for a mill containing the piece at board[x][y]
	private Boolean checkForMill(Integer x, Integer y) {

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

		if (count == 3)
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

	
		if (count == 3)
			return true;

		return false;
	}

	// set legal moves in terms of which pieces may be deleted
	private void setValidDeletions() {

		// sets the player who's pieces will be examined to be the current opponent
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

		// set flag to notify that a deletion is required
		deletionRequired = true;

	}

	// sets legal moves for a selected tray piece
	private void setLegalMoves() {
		resetLegalMoves();

		// for a tray piece, a legal move is any unoccupied spot on the board
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] == PieceType.UNOCCUPIED)
					legalMoves[i][j] = PieceType.LEGAL;
			}
		}

	}

	// fills legalMoves will the positions of the legal moves for a piece located at board[x][y]
	private void setLegalMoves(Integer x, Integer y) {

		resetLegalMoves();

		Boolean searchLeft = true, searchRight = true, searchUp = true, searchDown = true;

		int i = 1;
		
		// search outwards in all directions from the piece at board[x][y] and set the move as legal if they lead to an unoccupied space
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

	// gets the total number of legal moves available to the current player
	private Integer getTotalMoves() {

		int total = 0;

		// iterate through board and count up the number of moves at each piece location of the current player
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] == turn)
					total += countLegalMoves(i, j);
			}
		}

		return total;
	}

	// counts the number of legal moves available to the piece at board[x][y]
	private Integer countLegalMoves(Integer x, Integer y) {

		if(board[x][y] == PieceType.UNOCCUPIED)
			return 0;
		
		Integer moves = 0;
		int i = 1;
		Boolean searchLeft = true, searchRight = true, searchUp = true, searchDown = true;

		// counts moves outwards (left, right, up, down) from piece at board[x][y]
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

	// sets legalMoves entries all to ILLEGAL
	private void resetLegalMoves() {
		for (int i = 0; i < legalMoves.length; i++)
			for (int j = 0; j < legalMoves.length; j++)
				legalMoves[i][j] = PieceType.ILLEGAL;
	}

	// save game to outFile
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
	
	// cycle pieces on the board:  UNOCCUPIED -> BLUE -> RED -> UNOCCUPIED...
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
	
	// cycle pieces in the trays:
	//		UNOCCUPIED -> BLUE -> UNOCCUPIED... in blue tray
	//		UNOCCUPIED -> RED -> UNOCCUPIED... in red tray
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
		
		// set placing phase -- it may change!
		placingPhase = blueToPlace > 0 || redToPlace > 0;
	}
	
	// true if the board is being customized
	public Boolean isCustomizing(){
		return customizing; 
	}

	// ends customization
	public void endCustomization(){
		customizing = false;
		
		// check for win conditions and set before play begins
		
		if (turn == PieceType.BLUE && blueToPlace == 0 || turn == PieceType.RED && redToPlace == 0)
			if (getTotalMoves() == 0)
				winner = turn.swap();
		
		if (blueCount == losingPieceCount)
			winner = PieceType.RED;
		if (redCount == losingPieceCount)
			winner = PieceType.BLUE;
		
	}
	
	
	// to be called before ending the creation of a customized game
	// produces an error string if the board state is not a legal game
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
		if(blueCount == losingPieceCount && redCount == losingPieceCount)
			return "Number of blue and red pieces cannot both be " + losingPieceCount;
		
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
			if(blueToPlace == pieceCount - 1 && blueCount != pieceCount)
				return "Blue must have 1 piece on the board with " + blueToPlace + " in tray";
			if(redToPlace == pieceCount - 1 && redCount != pieceCount)
				return "Red must have 1 piece on the board with " + redToPlace + " in tray";
			if(blueToPlace < pieceCount - 1 && blueCount - blueToPlace < 2)
				return "Blue must have at least 2 pieces on the board with " + blueToPlace + " in tray";
			if(redToPlace < pieceCount - 1 && redCount - redToPlace < 2)
				return "Red must have at least 2 pieces on the board with " + redToPlace + " in tray";
			
		}
		
		return null;
	}
	
	
	// main game logic: attempt to make play at tray[x]
	public void play(Integer x){
		
		// if there is a winner, do nothing
		if(winner != PieceType.UNOCCUPIED)
			return;
		
		// if the game is in the placing phase and no piece currently needs to be selected for deletion...
		if(placingPhase && !deletionRequired){
			// if the current player "owns" the piece at tray[x]...
			if (isOwnerAt(x)) {
				// if the piece at tray[x] is selected, unselect it and return
				if (isSelectedAt(x)) {
					deselectPiece();
					return;
				}
				// if some other piece is selected, unselect it
				if (pieceSelected)
					deselectPiece();
				// select the piece at tray[x]
				selectPiece(x);
			}
		}
	}
	
	// main game logic: attempt to make play at board[x][y]
	public void play(Integer x, Integer y){
		
		// if there is a winner, do nothing
		if(winner != PieceType.UNOCCUPIED)
			return;
		
		// if a piece needs to be selected for deletion...
		if(deletionRequired){
			// if the piece at board[x][y] is a valid piece to delete...
			if (isLegalAt(x, y)) {
				// delete it...
				removePiece(x, y);
				// and check if the opponent has been reduced to the losing number of pieces
				// 		if so -> declare this player the winner
				if (getOpponentCount() == losingPieceCount)
					winner = turn;
				// switch turns
				nextTurn();
				// check if the switched-to player can make any moves
				// 		if not -> declare opponent the winner
				if (getTotalMoves() == 0) {
					winner = turn.swap();
				}
			}
		}
		
		// else if it is the placing phase...
		else if(placingPhase){
			// and a piece is selected, and board[x][y] is unoccupied then move the selected piece to board[x][y]
			if(pieceSelected && board[x][y] == PieceType.UNOCCUPIED) {
				placePiece(x, y);
				// check if a mill is made;  if so -> set legal moves as the valid deletions
				if (checkForMill(x, y))
					setValidDeletions();
				// if a mill isn't made...
				else {
					// if the player placed the piece just emptied their tray, check if they can make a valid move
					//		if not -> declare opponent the winner
					if (turn == PieceType.BLUE && blueToPlace == 0 || turn == PieceType.RED && redToPlace == 0)
						if (getTotalMoves() == 0)
							winner = turn.swap();
					// switch turns
					nextTurn();
				}
			}
		} 
		// else it must be the moving phase...
		else {
			// if the current player "owns" the piece at board[x][y]...
			if (isOwnerAt(x, y)) {
				// if the piece at board[x][y] is currently selected, unselect it and return
				if (isSelectedAt(x, y)) {
					deselectPiece();
					return;
				}
				// if some other piece is selected, unselect it
				if (pieceSelected)
					deselectPiece();
				// select the piece at board[x][y]
				selectPiece(x, y);
			}

			else if (pieceSelected && isLegalAt(x, y)) {
				placePiece(x, y);
				if (checkForMill(x, y))
					setValidDeletions();
				else {
					nextTurn();
					if (getTotalMoves() == 0)
						winner = turn.swap();
				}
			}
		}	
	}
	
	
}
