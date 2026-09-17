import java.util.Scanner;

public class ProgrammingA1 {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Part 1 - Registration and Login Feature");
        System.out.println("---------------------------------------");

        String firstName = prompt("Enter first name: ");
        String lastName = prompt("Enter last name: ");
        String username = prompt("Create username: ");
        String password = prompt("Create password: ");
        String cellPhoneNumber = prompt("Enter South African cell number with international code: ");

        Login login = new Login(firstName, lastName, username, password, cellPhoneNumber);

        System.out.println();
        System.out.println(login.getUserNameMessage());
        System.out.println(login.getPasswordMessage());
        System.out.println(login.getCellPhoneNumberMessage());

        String registrationStatus = login.registerUser();
        System.out.println(registrationStatus);

        if (!Login.REGISTRATION_SUCCESS_MESSAGE.equals(registrationStatus)) {
            System.out.println("Registration failed. Please restart the application and try again.");
            return;
        }

        System.out.println();
        System.out.println("Login to the account using the same username and password.");
        String loginUsername = prompt("Enter username: ");
        String loginPassword = prompt("Enter password: ");

        boolean loginSuccessful = login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus(loginSuccessful));
    }

    private static String prompt(String message) {
        System.out.print(message);
        return SCANNER.nextLine();
    }
}
