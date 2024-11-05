package dev.aries.iijra.constant;

public final class ExceptionConstant {

	public static final String DEPT_NAME_ALREADY_EXISTS = "Department already exists with name: ";
	public static final String USER_EMAIL_ALREADY_EXISTS = "User already exists with email: ";

	public static final String INVALID_CURRENT_PASSWORD = "Current password is invalid";
	public static final String INVALID_REQUEST = "There was a problem with the request";
	public static final String INVALID_CREDENTIALS = "Invalid email or password";
	public static final String INVALID_TOKEN = "Token is invalid or has expired";
	public static final String INVALID_ENUM_VALUE = "Invalid enum value provided";

	public static final String DEPT_ID_DOESNT_EXIST = "Department does not exist with id: ";
	public static final String STAFF_ID_DOESNT_EXIST = "Staff does not exist with id: ";
	public static final String USER_ID_DOESNT_EXIST = "User does not exist with id: ";
	public static final String USER_EMAIL_DOESNT_EXIST = "User does not exist with email: ";
	public static final String TOKEN_USER_DOESNT_EXIST = "Token does not exist for user with email: ";
	public static final String CATEGORY_ID_DOESNT_EXIST = "Category does not exist with id: ";
	public static final String SOP_ID_DOESNT_EXIST = "SOP does not exist with id: ";

	public static final String ACCOUNT_DISABLED = "Cannot log in at this time, account has been disabled";
	public static final String ACCOUNT_DEACTIVATED = "Cannot log in at this time, account has been deactivated";
	public static final String ACCESS_DENIED = "Not authorized to access resource";

	public static final String USER_ALREADY_ARCHIVED = "Archiving failed for user with id: ";
	public static final String DEPT_ALREADY_ARCHIVED = "Archiving failed for department with id: ";
	public static final String USER_NOT_ARCHIVED = "Restore failed for user with id: ";
	public static final String DEPT_NOT_ARCHIVED = "Restore failed for department with id: ";

	public static final String FILE_UPLOAD_FAILURE = "There was a problem uploading the file";
	public static final String FILE_DOWNLOAD_FAILURE = "There was a problem downloading the file";
	public static final String FILE_DELETE_FAILURE = "There was a problem deleting the file";
	public static final String FILE_CONVERSION_FAILURE = "Multipart file conversion failed";

	private ExceptionConstant() {
	}
}
