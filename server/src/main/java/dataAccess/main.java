package dataAccess;

public class main {
    public static void main(String[] args) {
        try {
            DatabaseManager data = new DatabaseManager();
            data.configureDatabase();

        } catch (DataAccessException e) {
            int i = 0;

        }
        int j = 0;
    }
}
