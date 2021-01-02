package bgu.spl.net.srv;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    private Database db;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        db = Database.getInstance();
    }

    @org.junit.jupiter.api.Test
    void initialize() {
        db.initialize("./Courses.txt");
        System.out.println(db.getCoursesOrder());
        System.out.println(db.getCourses());
    }

    @org.junit.jupiter.api.Test
    void adminRegister() {
        db.adminRegister("Shani","1234"); //ACK 1-register admin
        assertTrue(db.getUsers().contains("Shani"));
        assertTrue(db.isAdmin("Shani"));
        db.adminRegister("Marina", "1024");//ACK 1- register admin
        assertTrue(db.getUsers().contains("Marina"));
        assertEquals("ERROR", db.adminRegister("Shani","1234")); //ERROR 1 - the admin is already registered in the server
        db.studentRegister("Lior","2121"); //ACK 2-register student
        assertEquals("ERROR", db.adminRegister("Lior","2121")); //ERROR 2 - the admin is already registered in the server
    }

    @org.junit.jupiter.api.Test
    void studentRegister() {
        db.studentRegister("Shani","1234"); //ACK 1-register admin
        assertTrue(db.getUsers().contains("Shani"));
        assertTrue(db.isStudent("Shani"));
        assertEquals("ACK", db.studentRegister("Marina", "1024"));//ACK 1- register admin
        assertTrue(db.getUsers().contains("Marina"));
        assertEquals("ERROR", db.studentRegister("Shani","1234")); //ERROR 1 - the admin is already registered in the server
        db.adminRegister("Lior","2121"); //ACK 2-register student
        assertEquals("ERROR", db.studentRegister("Lior","2121"));
    }

    @org.junit.jupiter.api.Test
    void login() {
        db.studentRegister("DanTheMan","12345");
        assertEquals("ERROR", db.Logout("DanTheMan")); //ERROR 4- the user wasnt logged in
        assertEquals("ACK", db.Login("DanTheMan","12345")); //ACK 4- logged in successfully KULULU
        assertTrue(db.getUsers().get("DanTheMan").isLogged());
        assertEquals("ERROR", db.Login("DanTheMan","12345"));
        assertEquals("ERROR", db.Login("DanTheMan","543"));
    }

    @org.junit.jupiter.api.Test
    void logout() {
        db.studentRegister("DanTheMan", "12345");
        assertEquals("ERROR", db.Logout("DanTheMan")); //ERROR 4- the user wasnt logged in
        assertEquals("ACK", db.Login("DanTheMan", "12345")); //ACK 4- logged in successfully KULULU
        assertTrue(db.getUsers().get("DanTheMan").isLogged());
        assertEquals("ERROR", db.Login("DanTheMan", "12345"));
        assertEquals("ERROR", db.Login("DanTheMan", "543"));
    }

    @org.junit.jupiter.api.Test
    void courseReg() {
        db.studentRegister("Zoro rononoaZ","yep");
        db.Login("Zoro rononoaZ","yep");
        assertEquals("ACK", db.courseReg("Zoro rononoaZ", 101));//ACK 5- registered successfully
        assertEquals("ERROR", db.courseReg("Zoro rononoaZ",882));//ERROR 5- course doesnt exist
        db.studentRegister("Shani","1234");
        db.Login("Shani","1234");
        assertEquals("ACK",db.courseReg("Shani", 101)); //ACK 5- registered successfully
        db.studentRegister("Lior","2121");
        db.Login("Lior","2121");
        assertEquals("ERROR",db.courseReg("Lior", 101)); //ERROR 5- no seats are available in this course
        assertEquals("ACK",db.courseReg("Zoro rononoaZ", 201));//ACK 5- registered successfully
        assertEquals("ERROR", db.courseReg("Lior",201));//ERROR 5-  the student does not have all the Kdam courses
        db.studentRegister("Shani", "710");
        assertEquals("ERROR", db.courseReg("Shani",101));//ERROR 5 - the user is not logged in
        db.adminRegister("Marina", "1024");//ACK 1- register admin
        db.Login("Marina", "1024");
        assertEquals("ERROR", db.courseReg("Marina",101));//ERROR 5- admin can’t register to courses
    }

    @org.junit.jupiter.api.Test
    void KDAMCheck() {
        db.studentRegister("Shani","Patal");
        db.Login("Shani","Patal");
        System.out.println(db.KDAMCheck("Shani",101));// []
        System.out.println(db.KDAMCheck("Shani",301));// [103,102,101,201,202]
       assertEquals("ERROR",db.KDAMCheck("Shani",882));//ERROR 6 -no such course
        assertEquals("ERROR",db.KDAMCheck("Zoro",101));//ERROR 6- who the f*** is that
    }

    @org.junit.jupiter.api.Test
    void courseStat() {
        assertEquals("ERROR",db.courseStat("Marina",101));//ERROR 7- no such admin
        db.studentRegister("Zoro rononoaZ","yep");
        db.Login("Zoro rononoaZ","yep");
        db.courseReg("Zoro rononoaZ", 101);
        assertEquals("ERROR",db.courseStat("Zoro rononoaZ",101));
        db.studentRegister("Regnar","Lagartha");
        db.Login("Regnar","Lagartha");
        db.courseReg("Regnar", 101);
        db.adminRegister("Marina", "1024");
        db.Login("Marina", "1024");
        assertEquals("Course: (101) Algebra2\0Seats Available: 3/5 \\0Students Registered: \" Zoro rononoaZ ,Regnar", db.courseStat("Marina",101));
    }

    @org.junit.jupiter.api.Test
    void studentStat() {
        assertEquals("ERROR", db.studentStat("Marina","DanTheMan"));
        db.studentRegister("Zoro rononoaZ","yep");
        db.Login("Zoro rononoaZ","yep");
        db.courseReg("Zoro rononoaZ", 101);
        db.adminRegister("Marina", "1024");
        db.Login("Marina", "1024");
        assertEquals("Student: Zoro rononoaZ \0Courses: 101",db.studentStat("Marina","Zoro rononoaZ"));
        assertEquals("ERROR", db.studentStat("Marina", "Regnar"));//ERROR 8 -no such student
    }

    @org.junit.jupiter.api.Test
    void isRegistered() {
        assertEquals("ERROR",db.isRegistered("Shaniqua",101)); //ERROR 9- no such student
        db.studentRegister("Shaniqua", "1234");
        db.Login("Shaniqua","1234");
        assertEquals("NOT REGISTERED", db.isRegistered("Shaniqua",101) );
        db.courseReg("Shaniqua",101);
        assertEquals("REGISTERED",db.isRegistered("Shaniqua",101));
    }

    @org.junit.jupiter.api.Test
    void unRegister() {
        assertEquals("ERROR",db.unRegister("AVATAR",101));//ERROR 10- no such user
        db.studentRegister("Shaniqua", "1234");
        assertEquals("ERROR",db.unRegister("Shaniqua",101));//ERROR 10- user isnt logged in
        db.Login("Shaniqua","1234");
        assertEquals("ERROR",db.unRegister("Shaniqua",101));//ERROR 10-the user isnt registered to this course
        db.courseReg("Shaniqua",101);
        assertTrue(db.getUsers().get("Shaniqua").getMyCourses().contains(101));
        assertEquals("ACK", db.unRegister("Shaniqua",101));//ACK 10- unregistered successfully
        assertTrue(!db.getUsers().get("Shaniqua").getMyCourses().contains(101));
    }

    @org.junit.jupiter.api.Test
    void getMyCourses() {
        assertEquals("ERROR",db.getMyCourses("Aerosmith"));//ERROR 11- no such student
        db.studentRegister("Aerosmith","Crazy"); //i go crazzyyy crazzyy crazy for you baby!
        db.Login("Aerosmith","Crazy");
        assertEquals("[]",db.getMyCourses("Aerosmith"));
        db.courseReg("Aerosmith",101);
        db.courseReg("Aerosmith",201);
        assertEquals("[101,201]",db.getMyCourses("Aerosmith"));

    }
}