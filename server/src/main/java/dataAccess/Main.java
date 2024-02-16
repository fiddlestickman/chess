package dataAccess;
import chess.*;
import model.GameData;

public class Main {
    //the service class should take data from handlers (login, join game, etc), and make things happen
    //get data from the database via the dataAccess class
    //main job - actual code here
    public static void main(String[] args) {
        MemoryGameDAO temp = MemoryGameDAO.getInstance();
        GameData first = new GameData(52, "width", "breadth", "game1", new ChessGame());
        GameData second = new GameData(52, "hey","wait","game2",new ChessGame());
        temp.create(first);
        GameData out = temp.read(second, "gameID");
        temp.update(second);
        temp.create(first);
        temp.delete(second);
        temp.clear();
    }


}