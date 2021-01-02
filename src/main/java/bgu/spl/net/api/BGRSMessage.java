package bgu.spl.net.api;

import java.io.Serializable;

public class BGRSMessage implements Serializable {

    private short OPcode;
    private String userName;
    private String Password;
    private short courseNum;
    public String output;

    //OPcode 1,2,3
    public BGRSMessage(short OPcode, String userName, String Password){
        this.OPcode = OPcode;
        this.userName = userName;
        this.Password = Password;
    }

    //OPcode 8
    public BGRSMessage(String userName, short OPcode){
        this.OPcode = OPcode;
        this.userName = userName;
    }

    //OPcode 4,11
    public BGRSMessage(short OPcode){
        this.OPcode = OPcode;
    }

    //OPcode 5,6,7,9,10
    public BGRSMessage(short OPcode, short courseNum){
        this.OPcode = OPcode;
        this.courseNum = courseNum;
    }

    //OPcode 12,13
    public BGRSMessage(short OPcode, String output){
        this.OPcode = OPcode;
        this.output = output;
    }

    public short getOPcode(){return OPcode;}
    public String getUserName(){return userName;}
    public String getPassword(){return Password;}
    public short getCourseNum(){return courseNum;}
    public String getOutput(){return output;}
}
