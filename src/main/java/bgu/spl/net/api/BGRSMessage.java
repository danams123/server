package bgu.spl.net.api;

import java.io.Serializable;

public class BGRSMessage implements Serializable {

    private int OPcode;
    private String userName;
    private String Password;
    private int courseNum;
    public String output;

    //OPcode 1,2,3
    public BGRSMessage(int OPcode, String userName, String Password){
        this.OPcode = OPcode;
        this.userName = userName;
        this.Password = Password;
    }

    //OPcode 8
    public BGRSMessage(String userName, int OPcode){
        this.OPcode = OPcode;
        this.userName = userName;
    }

    //OPcode 4,11
    public BGRSMessage(int OPcode){
        this.OPcode = OPcode;
    }

    //OPcode 5,6,7,9,10
    public BGRSMessage(int OPcode, int courseNum){
        this.OPcode = OPcode;
        this.courseNum = courseNum;
    }

    //OPcode 12,13
    public BGRSMessage(int OPcode, String output){
        this.OPcode = OPcode;
        this.output = output;
    }

    public int getOPcode(){return OPcode;}
    public String getUserName(){return userName;}
    public String getPassword(){return Password;}
    public int getCourseNum(){return courseNum;}
    public String getOutput(){return output;}
}
