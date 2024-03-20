import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import model.LoginData;

import java.util.Objects;
import java.util.Scanner;

public class LoginMenu {

    private static String[] loginOptions = {"Help", "Login", "Register", "Quit"};
    private static String[] pregameOptions = {"Help", "Create Game", "List Games", "Join Game", "Join Observer", "Logout"};
    private String url;

    public LoginMenu(String url) {
        this.url = url;
    }

    public void LoginLoop() {
        //do the login stuff
        //eventually calls PregameLoop if logged in
        //make sure to program in error handling (not typing numbers, for instance)

        boolean loop = true;

        System.out.print("[Logged out]>>> ");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        var numbers = line.split(" ");
        //may have a problem if a number wasn't typed
        String input = numbers[0].strip();
        input = input.toLowerCase();
        if (Objects.equals(input, "1") || Objects.equals(input, "help")) {
            System.out.printf("%d. %s - Explains the different options available%n", 1, loginOptions[1]);
            System.out.printf("%d. %s - Asks for an existing username and password, then grants access to the rest of the program%n", 2, loginOptions[2]);
            System.out.printf("%d. %s - Asks for a new username, password, and email, then automatically logs you in%n", 3, loginOptions[3]);
            System.out.printf("%d. %s - Exits the program%n%n", 4, loginOptions[4]);
        }
        else if (Objects.equals(input, "2") || Objects.equals(input, "login")) {
            System.out.print("Username: ");
            Scanner login = new Scanner(System.in);
            line = login.nextLine();
            String username = line.strip();

            System.out.print("Password: ");
            login = new Scanner(System.in);
            line = login.nextLine();
            String password = line.strip();


        }
        else if (Objects.equals(input, "3") || Objects.equals(input, "register")) {
            //register
        }
        else if (Objects.equals(input, "4") || Objects.equals(input, "quit")) {
            //quit
        }
        else {
            System.out.print("Input not understood. Try entering \"Help\" to view options.\n");
        }

        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        var numbers = line.split(" ");

        int result = 0;
        for (var number : numbers) {
            result += Integer.parseInt(number);
        }
        var equation = String.join(" + ", numbers);
        System.out.printf("%s = %d%n", equation, result);


        if (loop) {
            LoginLoop();
        }
    }

    private void Login(String username, String password) {
        HTTPHandler handler = new HTTPHandler(url);
        LoginData data = new LoginData(username, password);
        handler.serialize(data);
        try {
            handler.Request("GET", data, String.class);
        } catch (Exception e) {
            //error handling
        }
    }



    public static void PregameLoop() {
        //do the pregame stuff
        //lets you join games
    }

}
