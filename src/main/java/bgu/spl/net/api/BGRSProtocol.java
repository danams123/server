package bgu.spl.net.api;

import bgu.spl.net.srv.Database;


public class BGRSProtocol implements MessagingProtocol<BGRSMessage> {

    private Database DB = Database.getInstance();
    private boolean shouldTerminate = false;
    private String senderName = null;


    @Override
    public BGRSMessage process(BGRSMessage msg) {
        String output;
        System.out.println("in process");
        if (msg.getOPcode() == 1) {
            output = DB.adminRegister(msg.getUserName(), msg.getPassword());
        }
        else if (msg.getOPcode() == 2) {
            output = DB.studentRegister(msg.getUserName(), msg.getPassword());
        }
        else if (msg.getOPcode() == 3) {
            output = DB.Login(msg.getUserName(), msg.getPassword());
            System.out.println(output);
            if(output.equals("ACK")) {
                senderName = msg.getUserName();
                System.out.println(senderName);
            }
        }
        else if (msg.getOPcode() == 4) {
            System.out.println(senderName);
            output = DB.Logout(senderName);
            if(output.equals("ACK 4")){
                shouldTerminate = true;
                DB.clear(); //TODO is it ok???
            }
        }
        else if (msg.getOPcode() == 5) {
            output = DB.courseReg(senderName, msg.getCourseNum());
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
            output = DB.unRegister(msg.getUserName(), msg.getCourseNum());
        }
        else {
            output = DB.getMyCourses(senderName);
        }
        if(output.equals("ERROR")){
            System.out.println("in error");
//            output = output + " " + msg.getOPcode();
            return new BGRSMessage(msg.getOPcode(), "", "ERROR", false);
        }
        else if(output.equals("ACK")) {
            System.out.println("in ack");
            return new BGRSMessage(msg.getOPcode(), "","ACK", false);
        }
        else{
            System.out.println("in special ack");
            return new BGRSMessage(msg.getOPcode(), output, "ACK", true);
        }
    }


    @Override
    public boolean shouldTerminate(){
        return shouldTerminate;
    }
}
