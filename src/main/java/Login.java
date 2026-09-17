import java.util.regex.Pattern;

public class Login {
    public static final String USERNAME_SUCCESS_MESSAGE = "Username successfully captured.";
    public static final String USERNAME_ERROR_MESSAGE =
            "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
    public static final String PASSWORD_SUCCESS_MESSAGE = "Password successfully captured.";
    public static final String PASSWORD_ERROR_MESSAGE =
            "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    public static final String CELL_SUCCESS_MESSAGE = "Cell number successfully captured.";
    public static final String CELL_ERROR_MESSAGE =
            "Cell phone number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
    public static final String REGISTRATION_SUCCESS_MESSAGE = "User has been registered successfully.";
    public static final String LOGIN_ERROR_MESSAGE = "Username or password incorrect, please try again.";

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;
    private String loginUsername;
    private String loginPassword;
    private boolean lastLoginSuccessful;

    public Login() {
        this("", "", "", "", "");
    }

    public Login(String firstName, String lastName, String username, String password, String cellPhoneNumber) {
        this.firstName = clean(firstName);
        this.lastName = clean(lastName);
        this.username = clean(username);
        this.password = password == null ? "" : password;
        this.cellPhoneNumber = clean(cellPhoneNumber);
        this.loginUsername = "";
        this.loginPassword = "";
        this.lastLoginSuccessful = false;
    }

    public boolean checkUserName() {
        return checkUserName(username);
    }

    public boolean checkUserName(String username) {
        String value = clean(username);
        return value.contains("_") && value.length() <= 5;
    }

    public boolean checkPasswordComplexity() {
        return checkPasswordComplexity(password);
    }

    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasCapitalLetter = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;

        for (char character : password.toCharArray()) {
            if (Character.isUpperCase(character)) {
                hasCapitalLetter = true;
            } else if (Character.isDigit(character)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(character)) {
                hasSpecialCharacter = true;
            }
        }

        return hasCapitalLetter && hasNumber && hasSpecialCharacter;
    }

    public boolean checkCellPhoneNumber() {
        return checkCellPhoneNumber(cellPhoneNumber);
    }

    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        String value = clean(cellPhoneNumber);
        /*
         * Reference for the regular expression approach:
         * Oracle. (2024). Pattern (Java SE 17 & JDK 17). https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html
         */
        Pattern southAfricanInternationalCellPattern = Pattern.compile("^\\+27\\d{9}$");
        return southAfricanInternationalCellPattern.matcher(value).matches();
    }

    public String registerUser() {
        if (!checkUserName()) {
            return USERNAME_ERROR_MESSAGE;
        }

        if (!checkPasswordComplexity()) {
            return PASSWORD_ERROR_MESSAGE;
        }

        if (!checkCellPhoneNumber()) {
            return CELL_ERROR_MESSAGE;
        }

        return REGISTRATION_SUCCESS_MESSAGE;
    }

    public String registerUser(String username, String password, String cellPhoneNumber) {
        setUsername(username);
        setPassword(password);
        setCellPhoneNumber(cellPhoneNumber);
        return registerUser();
    }

    public boolean loginUser() {
        lastLoginSuccessful = clean(loginUsername).equals(username) && loginPassword.equals(password);
        return lastLoginSuccessful;
    }

    public boolean loginUser(String username, String password) {
        setLoginUsername(username);
        setLoginPassword(password);
        return loginUser();
    }

    public String returnLoginStatus() {
        return returnLoginStatus(lastLoginSuccessful);
    }

    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }

        return LOGIN_ERROR_MESSAGE;
    }

    public String getUserNameMessage() {
        return checkUserName() ? USERNAME_SUCCESS_MESSAGE : USERNAME_ERROR_MESSAGE;
    }

    public String getPasswordMessage() {
        return checkPasswordComplexity() ? PASSWORD_SUCCESS_MESSAGE : PASSWORD_ERROR_MESSAGE;
    }

    public String getCellPhoneNumberMessage() {
        return checkCellPhoneNumber() ? CELL_SUCCESS_MESSAGE : CELL_ERROR_MESSAGE;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = clean(firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = clean(lastName);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = clean(username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? "" : password;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = clean(cellPhoneNumber);
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = clean(loginUsername);
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword == null ? "" : loginPassword;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
