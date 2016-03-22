package morris;

public class Morris {

	private Environment env;

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
