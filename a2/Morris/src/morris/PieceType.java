package morris;

public enum PieceType {
	BLUE, RED, BLUE_SELECTED, RED_SELECTED, UNOCCUPIED, PATH, INVALID, LEGAL, ILLEGAL;

	private PieceType selected;
	private PieceType deselected;
	private PieceType swap;

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

	public PieceType select() {
		return selected;
	}

	public PieceType deselect() {
		return deselected;
	}

	public PieceType swap() {
		return swap;
	}
}
