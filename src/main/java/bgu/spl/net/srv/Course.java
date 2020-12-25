package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Course {
    private int courseNum;
    private String courseName;
    private int[] KDAMCoursesList;
    private int numOfMaxStudents;
    private ConcurrentHashMap<String,String> Students;

    public Course(int courseNum, String courseName, int[] KDAMCoursesList, int numOfMaxStudents){
        this.courseNum = courseNum;
        this.courseName = courseName;
        this.KDAMCoursesList = KDAMCoursesList;
        this.numOfMaxStudents = numOfMaxStudents;
        this.Students = new ConcurrentHashMap<>();
    }

    public int getCourseNum(){return courseNum;}

    public String getCourseName(){return courseName;}

    public int[] getKDAMCoursesList(){return KDAMCoursesList;}

    public int getNumOfMaxStudents(){return numOfMaxStudents;}

    public int getAvailableSeats(){return numOfMaxStudents - Students.size();}

    public ConcurrentHashMap<String, String> getStudents(){return Students;}

    public void addStudent(String username){
        Students.putIfAbsent(username, null);
    }

    public void delStudent(String username){
        Students.remove(username);
    }
}
