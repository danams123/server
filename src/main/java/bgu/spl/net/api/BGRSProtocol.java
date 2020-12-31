package bgu.spl.net.api;

import bgu.spl.net.srv.Database;


public class BGRSProtocol implements MessagingProtocol<BGRSMessage> {

    private Database DB = Database.getInstance();
    private boolean shouldTerminate = false;
    private String senderName = null;


    @Override
    public BGRSMessage process(BGRSMessage msg) {
        String output;
        if (msg.getOPcode() == 1) {
            output = DB.adminRegister(msg.getUserName(), msg.getPassword()) + " " + msg.getOPcode();
        }
        else if (msg.getOPcode() == 2) {
            output = DB.studentRegister(msg.getUserName(), msg.getPassword()) + " " + msg.getOPcode();
        }
        else if (msg.getOPcode() == 3) {
            output = DB.Login(msg.getUserName(), msg.getPassword()) + " " + msg.getOPcode();
            if(output.equals("ACK")) {
                senderName = msg.getUserName();
            }
        }
        else if (msg.getOPcode() == 4) {
            shouldTerminate = true;
            output = DB.Logout(senderName) + " " + msg.getOPcode();
        }
        else if (msg.getOPcode() == 5) {
            output = DB.courseReg(senderName, msg.getCourseNum()) + " " + msg.getOPcode();
        }
        else if (msg.getOPcode() == 6) {
            output = DB.KDAMCheck(senderName, msg.getCourseNum());
        }
        else if (msg.getOPcode() == 7) {
            output = DB.courseStat(senderName, msg.getCourseNum());
        }
        else if (msg.getOPcode() == 8) {
            output = DB.studentStat(senderName, msg.getUserName());
        }
        else if (msg.getOPcode() == 9) {
            output = DB.isRegistered(senderName, msg.getCourseNum());
        }
        else if (msg.getOPcode() == 10) {
            output = DB.unRegister(msg.getUserName(), msg.getCourseNum()) + " " + msg.getOPcode();
        }
        else {
            output = DB.getMyCourses(senderName);
        }
        if(output.equals("ERROR")){
            output = output + " " + msg.getOPcode();
        }
        return new BGRSMessage(msg.getOPcode(), output);
        }


    @Override
    public boolean shouldTerminate(){
        return shouldTerminate;
    }
}
