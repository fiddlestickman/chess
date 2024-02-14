package model;

public record UserData(String username, String password, String email){
    UserData changePassword(String newPassword){
        return new UserData(username, newPassword, email);
    }
}

