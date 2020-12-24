package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Course {
    private int courseNum;
    private String courseName;
    private int[] KDAMCoursesList;
    private int numOfMaxStudents;
    private AtomicInteger availableSeats;
    private ArrayList<String> Students;

    public Course(int courseNum, String courseName, int[] KDAMCoursesList, int numOfMaxStudents){
        this.courseNum = courseNum;
        this.courseName = courseName;
        this.KDAMCoursesList = KDAMCoursesList;
        this.numOfMaxStudents = numOfMaxStudents;
        this.availableSeats = new AtomicInteger(numOfMaxStudents);
        this.Students = new ArrayList<String>();
    }

    public int getCourseNum(){return courseNum;}

    public String getCourseName(){return courseName;}

    public int[] getKDAMCoursesList(){return KDAMCoursesList;}

    public int getNumOfMaxStudents(){return numOfMaxStudents;}

    public int getAvailableSeats(){return availableSeats.intValue();}

    public ArrayList<String> getStudents(){return Students;}

    public void addStudent(String username){
        int oldValue = availableSeats.intValue();
        availableSeats.compareAndSet(oldValue, oldValue - 1);
        Students.add(username);
        Students.sort(String::compareTo);
    }

    public void delStudent(String username){
        int oldValue = availableSeats.intValue();
        availableSeats.compareAndSet(oldValue, oldValue + 1);
        Students.remove(username);
    }
}
