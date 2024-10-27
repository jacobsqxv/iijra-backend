package dev.aries.iijra.constant;

public final class Patterns {

	public static final String EMAIL = "^[A-Za-z0-9._%+-]{3,64}@[A-Za-z0-9-]+(\\.[A-Za-z]{2,4}){1,2}$";

	public static final String USER_NAME = "^([A-Za-z]{3,20}(?:-?[A-Za-z]{3,20})?\\s?){1,5}$";

	public static final String DEPARTMENT_NAME = "^[A-Za-z\\s]{3,50}$";

	@SuppressWarnings("java:S2068")
	public static final String PASSWORD = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?=\\S+$).{8,30}$";

	private Patterns() {
		throw new UnsupportedOperationException();
	}

}
