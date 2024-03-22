package client;

import java.util.Objects;
import java.util.Scanner;

public class LoginMenu {

    private static String[] loginOptions = {"Help", "Login", "Register", "Quit"};
    private ServerFacade facade;


    public LoginMenu(String url) {
        facade = new ServerFacade(url, null);
    }

    public String LoginLoop() {

        System.out.print("[Logged out]>>> ");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        var numbers = line.split(" ");
        //may have a problem if a number wasn't typed
        String input = numbers[0].strip();
        input = input.toLowerCase();
        if (Objects.equals(input, "1") || Objects.equals(input, "help")) {
            System.out.printf("%d. %s - Explains the different options available%n", 1, loginOptions[0]);
            System.out.printf("%d. %s - Asks for an existing username and password, then grants access to the rest of the program%n", 2, loginOptions[1]);
            System.out.printf("%d. %s - Asks for a new username, password, and email, then automatically logs you in%n", 3, loginOptions[2]);
            System.out.printf("%d. %s - Exits the program%n%n", 4, loginOptions[3]);
            return "keep looping";
        }
        else if (Objects.equals(input, "2") || Objects.equals(input, "login")) {
            String username = getString("Username: ");
            String password = getString("Password: ");
            return facade.Login(username, password);
        }
        else if (Objects.equals(input, "3") || Objects.equals(input, "register")) {
            String username = getString("Username: ");
            String email = getString("Email: ");
            String password = getString("Password: ");
            String password2 = getString("Reenter Password: ");
            if (password.equals(password2)) {
                return facade.Register(username, password, email);
            } else {
                System.out.print("Password doesn't match, please try again");
                return "keep looping";
            }
        }
        else if (Objects.equals(input, "4") || Objects.equals(input, "quit")) {
            return "stop looping";
        }
        else {
            System.out.print("Input not understood. Try entering \"Help\" to view options.\n");
            return "keep looping";
        }
    }

    private String getString(String prompt) {
        System.out.print(prompt);
        Scanner login = new Scanner(System.in);
        String line = login.nextLine();
        return line.strip();
    }


}
