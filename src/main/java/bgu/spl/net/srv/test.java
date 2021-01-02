package bgu.spl.net.srv;

import bgu.spl.net.api.BGRSEncoderDecoder;
import bgu.spl.net.api.BGRSProtocol;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class test {
    public static void main (String[]args){
        Database db= Database.getInstance();
        db.initialize("../Courses.txt");
//        Admin a1= new Admin("Shani","1234");
//        Admin a2 = new Admin("Marina", "1024");
//        Student s1= new Student("DanTheMan","12345");
//        Student s2= new Student("Lior","2121");
//        Student s3= new Student("Omer","7710");

        //----------------------------------DATABASE TESTS-----------------------------------------
        //----------------------------------test1----------------------------------
        db.adminRegister("Shani","1234"); //ACK 1-register admin
        db.adminRegister("Marina", "1024");//ACK 1- register admin
        db.adminRegister("Shani","1234"); //ERROR 1 - the admin is already registered in the server

        db.studentRegister("Lior","2121"); //ACK 2-register student
        db.adminRegister("Lior","2121"); //ERROR 2 - the admin is already registered in the server

        //----------------------------------test2----------------------------------
        db.Login("Marina", "1024");//ERROR 3- the user doesn’t exist
        db.adminRegister("Marina", "1024");//ACK 1- register admin
        db.Login("Marina", "1234");//ERROR 3- the password doesnt match the one entered for the username
        db.Login("Marina", "1234");//ACK 3- Login admin
        db.Login("Marina", "1234");//ERROR 3- the user is already logged in

        db.Login("Lior","2121");//ERROR 3- the user doesn’t exist
        db.studentRegister("Lior","2121");//ACK 2- register student
        db.Login("Lior","2121");//ERROR 3- the password doesnt match the one entered for the username
        db.Login("Marina", "1234");//ACK 3- Login admin
        db.Login("Lior","2121");//ERROR 3- the user is already logged in

        //----------------------------------test3----------------------------------
        db.studentRegister("DanTheMan","12345");
        db.Logout("DanTheMan"); //ERROR 4- the user wasnt logged in
        db.Login("DanTheMan","12345"); //ACK 4- logged in successfully KULULU

        //----------------------------------test4----------------------------------
        db.studentRegister("Zoro rononoaZ","yep");
        db.Login("Zoro rononoaZ","yep");
        db.courseReg("Zoro rononoaZ", 101); //ACK 5- registered successfully
        db.courseReg("Zoro rononoaZ",882);//ERROR 5- course doesnt exist
        db.studentRegister("Shani","1234");
        db.Login("Shani","1234");
        db.courseReg("Shani", 101); //ACK 5- registered successfully
        db.studentRegister("Lior","2121");
        db.Login("Lior","2121");
        db.courseReg("Lior", 101); //ERROR 5- no seats are available in this course
        db.courseReg("Zoro rononoaZ", 201);//ACK 5- registered successfully
        db.courseReg("Lior",201);//ERROR 5-  the student does not have all the Kdam courses
        db.studentRegister("Shani", "710");
        db.courseReg("Shani",101);//ERROR 5 - the user is not logged in
        db.adminRegister("Marina", "1024");//ACK 1- register admin
        db.Login("Marina", "1024");
        db.courseReg("Marina",101);//ERROR 5- admin can’t register to courses

        //----------------------------------test5----------------------------------
        db.studentRegister("Shani","Patal");
        db.Login("Shani","Patal");
        db.KDAMCheck("Shani",101);// ACK 6- []
        db.KDAMCheck("Shani",301);//ACK 6- [103,102,101,201,202]
        db.KDAMCheck("Shani",882);//ERROR 6 -no such course
        db.KDAMCheck("Zoro",101);//ERROR 6- who the f*** is that

        //----------------------------------test6----------------------------------
        Object o= db.courseStat("Marina",101);//ERROR 7- no such admin
        db.studentRegister("Zoro rononoaZ","yep");
        db.Login("Zoro rononoaZ","yep");
        db.courseReg("Zoro rononoaZ", 101);
        Object o1= db.courseStat("Zoro rononoaZ",101);//ERROR 7- student cant request a courstat
        db.studentRegister("Regnar","Lagartha");
        db.Login("Regnar","Lagartha");
        db.courseReg("Regnar", 101);
        db.adminRegister("Marina", "1024");
        db.Login("Marina", "1024");
        Object o2 = db.courseStat("Marina",101);

        //----------------------------------test7----------------------------------
        Object o3 = db.studentStat("Marina","DanTheMan");////ERROR 8- no such admin
        db.studentRegister("Zoro rononoaZ","yep");
        db.Login("Zoro rononoaZ","yep");
        db.courseReg("Zoro rononoaZ", 101);
        db.adminRegister("Marina", "1024");
        db.Login("Marina", "1024");
        Object o4 = db.studentStat("Marina","Zoro rononoaZ");
        Object o5 = db.studentStat("Marina", "Regnar");//ERROR 8 -no such student

        //----------------------------------test8----------------------------------
        db.isRegistered("Shaniqua",101);//ERROR 9- no such student
        db.studentRegister("Shaniqua", "1234");
        db.Login("Shaniqua","1234");
        db.isRegistered("Shaniqua",101);// NOT REGISTERED
        db.courseReg("Shaniqua",101);
        db.isRegistered("Shaniqua",101);//REGISTERED

        //----------------------------------test9----------------------------------
        db.unRegister("AVATAR",101);//ERROR 10- no such user
        db.studentRegister("Shaniqua", "1234");
        db.unRegister("Shaniqua",101);//ERROR 10- user isnt logged in
        db.Login("Shaniqua","1234");
        db.unRegister("Shaniqua",101);//ERROR 10-the user isnt registered to this course
        db.courseReg("Shaniqua",101);
        db.unRegister("Shaniqua",101);//ACK 10- unregistered successfully

        //----------------------------------test10----------------------------------
        db.getMyCourses("Aerosmith");//ERROR 11- no such student
        db.studentRegister("Aerosmith","Crazy"); //i go crazzyyy crazzyy crazy for you baby!
        db.Login("Aerosmith","Crazy");
        db.getMyCourses("Aerosmith"); //[]
        db.courseReg("Aerosmith",101);
        db.courseReg("Aerosmith",201);
        db.getMyCourses("Aerosmith"); //[101,201]

        //----------------------------------END OF DATABASE TESTS-----------------------------------------

        //----------------------------------PROTOCOL TESTS-----------------------------------------

        Serializable c1 = "ADMINREG  <Shani> <1234>";
        Serializable c2 = "STUDENTREG  <DanTheMan> <012>";
        Serializable c3 = "LOGIN <Username> <Password>";
        Serializable c4 = "LOGOUT";
        Serializable c5 = "COURSEREG <101>";
        Serializable c6 = "KDAMCHECK <201>";
        Serializable c7 = "COURSESTAT <101>";
        Serializable c8 = "STUDENTSTAT <Regnar>";
        Serializable c9 = "ISREGISTERED <101>";
        Serializable c10 = "UNREGISTER <101>";
        Serializable c11= "MYCOURSES";

//        BGRSProtocol p = new BGRSProtocol();
//        Serializable output1 = p.process((Command)c1); //AdminREG
//        System.out.println(db.userExists(output1.getUserName()));//TRUE
//
//        Serializable output2 = p.process((Command)c2); //StudentREG
//        System.out.println(db.userExists(output2.getUserName()));//TRUE
//
//        Serializable output3 = p.process((Command)c3); //LOGIN
//        System.out.println(db.isLogged(output3.getUserName()));//TRUE
//
//        Serializable output4 = p.process((Command)c4); //LOGOUT
//        System.out.println(db.isLogged(output4.getUserName()));//FALSE
//
//        Serializable output5 = p.process((Command)c5); //courseREG
//        System.out.println(db.isRegistered(output5.getUserName(), output5.getCourseNum()));
//
//        Serializable output6 = p.process((Command)c6); //KDAMCHECK
//        System.out.println(output6);
//
//        Serializable output7 = p.process((Command)c7); //CourseStat
//        System.out.println(output7);
//
//        Serializable output8 = p.process((Command)c8); //StudentStat
//        System.out.println(output8);
//
//        Serializable output9 = p.process((Command)c9); //IsRegistered
//        System.out.println(output9);
//
//        Serializable output10 = p.process((Command)c10); //Unregister
//        System.out.println(output10);
//
//        Serializable output11 = p.process((Command)c11); //MyCourses
//        System.out.println(output11);

        //----------------------------------END OF PROTOCOL TESTS-----------------------------------------
        //----------------------------------ENCODERDECODER TESTS-----------------------------------------

        //----------------------------------test1----------------------------------
//        BGRSEncoderDecoder encdec = new BGRSEncoderDecoder();
//        ByteBuffer buf = ByteBuffer.allocate(8);
//        buf.put((byte)1);
//        Serializable nextMessage = encdec.decodeNextByte(buf.get());
//        System.out.println(nextMessage);
//
//        byte [] b = encdec.encode(nextMessage);
//        b.toString();
//
//        }

        //----------------------------------test2----------------------------------

//        BGRSEncoderDecoder encdec = new BGRSEncoderDecoder();
//        ByteBuffer buf = ByteBuffer.allocate(8);
//        buf.put((byte)1);
//        Serializable nextMessage = encdec.decodeNextByte(buf.get());
//        byte [] b = encdec.encode(nextMessage);
//        b.toString();

}
}
