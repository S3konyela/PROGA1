import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginTest {
    @Test
    void usernameCorrectlyFormattedReturnsMessage() {
        Login login = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");

        assertEquals(Login.USERNAME_SUCCESS_MESSAGE, login.getUserNameMessage());
    }

    @Test
    void usernameIncorrectlyFormattedReturnsMessage() {
        Login login = new Login("Kyle", "Smith", "kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976");

        assertEquals(Login.USERNAME_ERROR_MESSAGE, login.getUserNameMessage());
    }

    @Test
    void passwordMeetsComplexityRequirementsReturnsMessage() {
        Login login = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");

        assertEquals(Login.PASSWORD_SUCCESS_MESSAGE, login.getPasswordMessage());
    }

    @Test
    void passwordDoesNotMeetComplexityRequirementsReturnsMessage() {
        Login login = new Login("Kyle", "Smith", "kyl_1", "password", "+27838968976");

        assertEquals(Login.PASSWORD_ERROR_MESSAGE, login.getPasswordMessage());
    }

    @Test
    void cellPhoneNumberCorrectlyFormattedReturnsMessage() {
        Login login = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");

        assertEquals(Login.CELL_SUCCESS_MESSAGE, login.getCellPhoneNumberMessage());
    }

    @Test
    void cellPhoneNumberIncorrectlyFormattedReturnsMessage() {
        Login login = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "08966553");

        assertEquals(Login.CELL_ERROR_MESSAGE, login.getCellPhoneNumberMessage());
    }

    @Test
    void successfulRegistrationReturnsMessage() {
        Login login = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");

        assertEquals(Login.REGISTRATION_SUCCESS_MESSAGE, login.registerUser());
    }

    @Test
    void failedRegistrationReturnsFirstErrorMessage() {
        Login login = new Login("Kyle", "Smith", "kyle!!!!!!!", "password", "08966553");

        assertEquals(Login.USERNAME_ERROR_MESSAGE, login.registerUser());
    }

    @Test
    void loginSuccessfulReturnsTrue() {
        Login login = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");

        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    void loginFailedReturnsFalse() {
        Login login = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");

        assertFalse(login.loginUser("kyl_1", "password"));
    }

    @Test
    void usernameCorrectlyFormattedReturnsTrue() {
        Login login = new Login();

        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    void usernameIncorrectlyFormattedReturnsFalse() {
        Login login = new Login();

        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    @Test
    void passwordMeetsComplexityRequirementsReturnsTrue() {
        Login login = new Login();

        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    void passwordDoesNotMeetComplexityRequirementsReturnsFalse() {
        Login login = new Login();

        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    void cellPhoneNumberCorrectlyFormattedReturnsTrue() {
        Login login = new Login();

        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    void cellPhoneNumberIncorrectlyFormattedReturnsFalse() {
        Login login = new Login();

        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    @Test
    void successfulLoginStatusReturnsWelcomeMessage() {
        Login login = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        boolean loginSuccessful = login.loginUser("kyl_1", "Ch&&sec@ke99!");

        assertEquals("Welcome Kyle, Smith it is great to see you again.", login.returnLoginStatus(loginSuccessful));
    }

    @Test
    void failedLoginStatusReturnsErrorMessage() {
        Login login = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        boolean loginSuccessful = login.loginUser("kyl_1", "password");

        assertEquals(Login.LOGIN_ERROR_MESSAGE, login.returnLoginStatus(loginSuccessful));
    }
}
