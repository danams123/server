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
    }

    @org.junit.jupiter.api.Test
    void courseReg() {
    }

    @org.junit.jupiter.api.Test
    void KDAMCheck() {
    }

    @org.junit.jupiter.api.Test
    void courseStat() {
    }

    @org.junit.jupiter.api.Test
    void studentStat() {
    }

    @org.junit.jupiter.api.Test
    void isRegistered() {
    }

    @org.junit.jupiter.api.Test
    void unRegister() {
    }

    @org.junit.jupiter.api.Test
    void getMyCourses() {
    }
}