package morris;

// The PieceType enum is used in the game model to represent pieces and tray/board entries
public enum PieceType {
	
	// the piece types
	BLUE, RED, BLUE_SELECTED, RED_SELECTED, UNOCCUPIED, PATH, INVALID, LEGAL, ILLEGAL;

	private PieceType selected;
	private PieceType deselected;
	private PieceType swap;

	// definitions used for methods
	static {
		BLUE.selected = BLUE_SELECTED;
		RED.selected = RED_SELECTED;
		BLUE_SELECTED.selected = BLUE_SELECTED;
		RED_SELECTED.selected = RED_SELECTED;
		UNOCCUPIED.selected = UNOCCUPIED;
		PATH.selected = PATH;
		INVALID.selected = INVALID;
		LEGAL.selected = LEGAL;
		ILLEGAL.selected = ILLEGAL;

		BLUE.deselected = BLUE;
		RED.deselected = RED;
		BLUE_SELECTED.deselected = BLUE;
		RED_SELECTED.deselected = RED;
		UNOCCUPIED.deselected = UNOCCUPIED;
		PATH.deselected = PATH;
		INVALID.deselected = INVALID;
		LEGAL.deselected = LEGAL;
		ILLEGAL.deselected = ILLEGAL;

		BLUE.swap = RED;
		RED.swap = BLUE;
		BLUE_SELECTED.swap = BLUE_SELECTED;
		RED_SELECTED.swap = RED_SELECTED;
		UNOCCUPIED.swap = UNOCCUPIED;
		PATH.swap = PATH;
		INVALID.swap = INVALID;
		LEGAL.swap = LEGAL;
		ILLEGAL.swap = ILLEGAL;
	}

	// changes piece to selected:  BLUE -> BLUE_SELECTED or RED -> RED_SELECTED,  no effect on other pieces
	public PieceType select() {
		return selected;
	}

	// changes piece to unselected:  BLUE_SELECTED -> BLUE or RED_SELECTED -> RED,  no effect on other pieces
	public PieceType deselect() {
		return deselected;
	}

	// changes piece to opposite:  BLUE -> RED,  RED -> BLUE,  no effect on other pieces
	public PieceType swap() {
		return swap;
	}
}
