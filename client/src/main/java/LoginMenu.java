import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import model.LoginData;
import model.UserData;

import java.util.Objects;
import java.util.Scanner;

public class LoginMenu {

    private static String[] loginOptions = {"Help", "Login", "Register", "Quit"};
    private static String[] pregameOptions = {"Help", "Create Game", "List Games", "Join Game", "Join Observer", "Logout"};
    private String url;

    public LoginMenu(String url) {
        this.url = url;
    }

    public String LoginLoop() {
        //do the login stuff
        //eventually calls PregameLoop if logged in
        //make sure to program in error handling (not typing numbers, for instance)

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
            return Login(username, password);
        }
        else if (Objects.equals(input, "3") || Objects.equals(input, "register")) {
            String username = getString("Username: ");
            String email = getString("Email: ");
            String password = getString("Password: ");
            String password2 = getString("Reenter Password: ");
            if (password.equals(password2)) {
                return Register(username, password, email);
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

    private String Login(String username, String password) {
        HTTPHandler handler = new HTTPHandler(url);
        LoginData data = new LoginData(username, password);
        handler.serialize(data);
        try {
            Main.LoginResponse auth = (Main.LoginResponse) handler.Request("POST", data, Main.LoginResponse.class);
            return auth.authToken;
        } catch (Exception e) {
            //error handling
        }
        return null;
    }

    private String Register(String username, String password, String email) {
        HTTPHandler handler = new HTTPHandler(url + "/user");
        UserData data = new UserData(username, password, email);
        handler.serialize(data);
        try {
            Main.LoginResponse auth = (Main.LoginResponse) handler.Request("POST", data, Main.LoginResponse.class);
            return auth.authToken;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return null;
        }
    }

    private String getString(String prompt) {
        System.out.print(prompt);
        Scanner login = new Scanner(System.in);
        String line = login.nextLine();
        return line.strip();
    }


}
