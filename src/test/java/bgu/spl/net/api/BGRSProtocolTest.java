package bgu.spl.net.api;

import bgu.spl.net.srv.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BGRSProtocolTest {
    private Database db;
    BGRSProtocol p = new BGRSProtocol();
    BGRSMessage m1 = new BGRSMessage((short) 1,"Shaniqua","1234");
    BGRSMessage m2 = new BGRSMessage((short)2,"Shaniqua","012");
    BGRSMessage m3 = new BGRSMessage((short)3,"Shaniqua","1234");
    BGRSMessage m4 = new BGRSMessage((short)4);
    BGRSMessage m5 = new BGRSMessage((short)5,(short)101);
    BGRSMessage m6 = new BGRSMessage((short)6,(short)201);
    BGRSMessage m7 = new BGRSMessage((short)7,(short)101);
    BGRSMessage m8 = new BGRSMessage("Shaniqua",(short)8);
    BGRSMessage m9 = new BGRSMessage((short)9,(short)101);
    BGRSMessage m10 = new BGRSMessage((short)10,(short)101);
    BGRSMessage m11 = new BGRSMessage((short)11);

    @BeforeEach
    void setUp() {
        db = Database.getInstance();
        BGRSMessage o1 = p.process(m1);
        assertTrue(db.getUsers().contains("Shaniqua"));
        assertEquals("ACK 1", o1.getOutput());

        BGRSMessage o2 = p.process(m2);
        assertTrue(db.getUsers().contains("Shaniqua"));
        assertEquals("ACK 2", o2.getOutput());

        BGRSMessage o3 = p.process(m3);
        assertEquals("ACK 3", o3.getOutput());
        assertTrue(db.isLogged("Shaniqua"));

        BGRSMessage o4 = p.process(m4);
        assertEquals("ACK 4", o4.getOutput());
        assertTrue(!db.isLogged("Shaniqua"));

        BGRSMessage o5 = p.process(m5);
        assertEquals("ACK 5", o5.getOutput());
        assertTrue(db.getUsers().get("Shaniqua").getMyCourses().contains(101));

        BGRSMessage o6 = p.process(m6);
        assertEquals("[101]",o6.getOutput());

        BGRSMessage o7 = p.process(m7);
        assertEquals("Course: (101) Algebra2\0Seats Available: 4/5 \\0Students Registered: \" Shaniqua", o7.getOutput());

        BGRSMessage o8 = p.process(m8);
        assertEquals("Student: Shaniqua \0Courses: 101",o8.getOutput());

        BGRSMessage o9 = p.process(m9);
        assertEquals("REGISTERED",o9.getOutput());

        BGRSMessage o10 = p.process(m10);
        assertEquals("ACK 10",o10.getOutput());
        assertTrue(!db.getUsers().get("Shaniqua").getMyCourses().contains(101));

        BGRSMessage o11 = p.process(m11);
        db.courseReg("Shaniqua", 101);
        db.courseReg("Shaniqua", 201);
        assertEquals("[101,201]", o11.getOutput());
    }

    @Test
    void shouldTerminate() {
    }
}