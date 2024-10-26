package dev.aries.iijra.constant;

public final class ExceptionConstant {

	public static final String DEPT_NAME_ALREADY_EXISTS = "Department already exists with name: ";
	public static final String STAFF_EMAIL_ALREADY_EXISTS = "Staff already exists with name: ";
	public static final String PASSWORD_ALREADY_USED = "Password already in use";

	public static final String DEPT_ID_DOESNT_EXIST = "Department does not exist with id: ";
	public static final String DEPT_NAME_DOESNT_EXIST = "Department does not exist with name: ";
	public static final String STAFF_ID_DOESNT_EXIST = "Staff does not exist with id: ";
	public static final String STAFF_EMAIL_DOESNT_EXIST = "Staff does not exist with email: ";
	public static final String TOKEN_VALUE_DOESNT_EXIST = "Token does not exist with value: ";

	public static final String ACCOUNT_DISABLED = "Cannot log in at this time, account has been disabled";
	public static final String ACCOUNT_DEACTIVATED = "Cannot log in at this time, account has been deactivated";
	public static final String INVALID_REQUEST = "There was a problem with the request";
	public static final String ACCESS_DENIED = "Access denied: Not authorized to access resource";
	public static final String INVALID_CREDENTIALS = "Authentication failed: Invalid credentials";
	public static final String INVALID_TOKEN = "Token is invalid or has expired";
	public static final String INVALID_ENUM_VALUE = "Invalid enum value provided";

	private ExceptionConstant() {
	}
}
