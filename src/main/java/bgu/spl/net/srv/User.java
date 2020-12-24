package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.Set;

public abstract class User {
    private String userName;
    private String Password;
    private boolean isLogged;

    public User(String userName, String Password){
        this.userName = userName;
        this.Password = Password;
        this.isLogged = false;
    }

    public void Login(){isLogged = true;}

    public void Logout(){isLogged = false;}

    public boolean isLogged(){return isLogged;}

    public String getUserName(){return userName;}

    public String getPassword(){return Password;}

    public ArrayList<Integer> getMyCourses() {return null;}

    public void courseReg(Course toAdd){}

    public void unRegister(Course toDel){}
}
