package morris;

// This class acts as the entry point for the game
public class Morris {

	private Environment env;

	// Game loop starts by creating a main menu window
	// and switches to different game windows as necessary
	public Morris() {

		env = new Menu();

		while (true) {
			Environment next = env.run();
			env = next;
		}

	}

	public static void main(String[] args) {
		new Morris();
	}

}
