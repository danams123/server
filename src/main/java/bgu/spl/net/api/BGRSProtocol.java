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
            //if senderName isnt null it means the client is logged in and can't reg any more users.
            if(senderName == null) {
                output = DB.adminRegister(msg.getUserName(), msg.getPassword());
            }
            else {
                output = "ERROR";
            }
        }
        else if (msg.getOPcode() == 2) {
            //if senderName isnt null it means the client is logged in and can't reg any more users.
            if(senderName == null) {
                output = DB.studentRegister(msg.getUserName(), msg.getPassword());
            }
            else{
                output = "ERROR";
            }
        }
        else if (msg.getOPcode() == 3) {
            // can't log in if senderName isnt null - it means its alreadly logged in
            if(senderName == null) {
                output = DB.Login(msg.getUserName(), msg.getPassword());
                // if logged in successfully to the database, we save the username for future use
                if (output.equals("ACK")) {
                    senderName = msg.getUserName();
                }
            }
            else{
                output = "ERROR";
            }
        }
        else if (msg.getOPcode() == 4) {
            output = DB.Logout(senderName);
            //if we logged out successfully we signal the connectionHandler its time to terminate
            if(output.equals("ACK")){
                shouldTerminate = true;
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
            output = DB.unRegister(senderName, msg.getCourseNum());
        }
        else {
            output = DB.getMyCourses(senderName);
        }
        //build an error message
        if(output.equals("ERROR")){
            return new BGRSMessage(msg.getOPcode(), "", "ERROR", false);
        }
        //build an ack message
        else if(output.equals("ACK")) {
            return new BGRSMessage(msg.getOPcode(), "","ACK", false);
        }
        //build an ack message with optional data
        else{
            return new BGRSMessage(msg.getOPcode(), output, "ACK", true);
        }
    }


    @Override
    public boolean shouldTerminate(){
        return shouldTerminate;
    }
}
