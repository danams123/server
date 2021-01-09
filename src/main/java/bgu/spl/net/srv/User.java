package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class User {
    private String userName;
    private String Password;
    private AtomicBoolean isLogged;

    public User(String userName, String Password){
        this.userName = userName;
        this.Password = Password;
        this.isLogged = new AtomicBoolean(false);
    }

    public void Login(){isLogged.compareAndSet(false, true);}

    public void Logout(){isLogged.compareAndSet(true, false);}

    public boolean isLogged(){return isLogged.get();}

    public String getUserName(){return userName;}

    public String getPassword(){return Password;}

    //unused abstract function for the Student's use only
    public ArrayList<Integer> getMyCourses() {return null;}

    public void courseReg(Course toAdd){}

    public void unRegister(Course toDel){}
}


