package bgu.spl.net.srv;

import bgu.spl.net.srv.Course;
import bgu.spl.net.srv.User;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {

    private ConcurrentHashMap<String, User> Users;
    private ConcurrentHashMap<Integer, Course> Courses;
    private ArrayList<Integer> coursesOrder;

    private static class SingletonHolder {
        private static Database instance = new Database();
    }
    Database() {
        this.Users = new ConcurrentHashMap<String, User>();
        this.Courses = new ConcurrentHashMap<Integer, Course>();
        this.coursesOrder = new ArrayList<Integer>();
        //initializing from the constructor
        initialize("./Courses.txt");
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * loades the courses from the file path specified
     * into the Database, returns true if successful.
     */

    boolean initialize(String coursesFilePath) {
        // if database already initialized, Courses won't be empty
        if(Courses.isEmpty()) {
            try {
                FileReader reader = new FileReader(coursesFilePath);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] arr = line.split("\\|");
                    int courseNum = Integer.parseInt(arr[0]);
                    String courseName = arr[1];
                    int[] KDAM;
                    if (arr[2].equals("[]")) {
                        KDAM = new int[0];
                    } else {
                        KDAM = Stream.of(arr[2].substring(1, arr[2].length() - 1).split(",")).mapToInt(Integer::parseInt).toArray();
                    }
                    int maxNumOfStudents = Integer.parseInt(arr[3]);
                    Courses.put(courseNum, new Course(courseNum, courseName, KDAM, maxNumOfStudents));
                    coursesOrder.add(courseNum);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            for(int courseNum : Courses.keySet()){
                Course course = Courses.get(courseNum);
                for (int i = 0; i <course.getKDAMCoursesList().length ; i++) {
                    for (int j = i; j < course.getKDAMCoursesList().length; j++) {
                        if(coursesOrder.indexOf(course.getKDAMCoursesList()[i]) > coursesOrder.indexOf(course.getKDAMCoursesList()[j])){
                            int tmp = course.getKDAMCoursesList()[j];
                            course.getKDAMCoursesList()[j] = course.getKDAMCoursesList()[i];
                            course.getKDAMCoursesList()[i] = tmp;
                        }

                    }
                }
            }
            return true;
        }
        return true;
    }

    public synchronized String adminRegister(String username, String password){
        if(!userExists(username)){
            Users.put(username, new Admin(username, password));
            return "ACK";
        }
        return "ERROR";
    }

    public synchronized String studentRegister(String username, String password){
        if(!userExists(username)){
            Users.put(username, new Student(username, password));
            return "ACK";
        }
        return "ERROR";
    }

    public synchronized String Login(String username, String password){
        if(userExists(username) && !isLogged(username) && checkPass(username, password)){
            Users.get(username).Login();
            return "ACK";
        }
        return "ERROR";
    }

    public String Logout(String username){
        if(userExists(username) && isLogged(username)){
            Users.get(username).Logout();
            return "ACK";
        }
        return "ERROR";
    }

    public synchronized String courseReg(String username, int courseNum){
        if(isStudent(username) && isLogged(username) && courseAvailable(username, courseNum)){
            Users.get(username).courseReg(Courses.get(courseNum));
            return "ACK";
        }
        return "ERROR";
    }

    public String KDAMCheck(String username, int courseNum){
        if(isStudent(username) && isLogged(username) && courseExists(courseNum)) {
            return stringOrg(Arrays.toString(Courses.get(courseNum).getKDAMCoursesList()));
        }
        return "ERROR";
    }

    public String courseStat(String admin, int courseNum){
        if(isAdmin(admin) && isLogged(admin) && courseExists(courseNum)) {
            Course course = Courses.get(courseNum);
            //creating an array so it could be sorted by String value
            Set<String> students = course.getStudents().keySet();
            ArrayList<String> toSend = new ArrayList<>(students.size());
            for (String student: students){
                toSend.add(student);
            }
            toSend.sort(String::compareTo);
            return "Course: (" + courseNum + ") " + course.getCourseName() + "\nSeats Available: " + course.getAvailableSeats()
                    + "/" + course.getNumOfMaxStudents() + "\nStudents Registered: " + stringOrg(toSend.toString());
        }
        return "ERROR";
    }

    public String studentStat(String admin, String username){
        if(isAdmin(admin) && isLogged(admin) && isStudent(username)) {
            String output = stringOrg(Users.get(username).getMyCourses().toString());
            return "Student: " + username + "\nCourses: " + output;
    }
        return "ERROR";
    }

    public String isRegistered(String username, int courseNum){
        if(isStudent(username) && isLogged(username) && courseExists(courseNum)) {
            if (studentInCourse(username, courseNum)) {
                return "REGISTERED";
            }
            return "NOT REGISTERED";
        }
        return "ERROR";
    }

    public synchronized String unRegister(String username, int courseNum){
        if(isStudent(username) && isLogged(username) && courseExists(courseNum) && studentInCourse(username, courseNum)){
            Users.get(username).unRegister(Courses.get(courseNum));
            return "ACK";
        }
        return "ERROR";
    }

    public String getMyCourses(String username){
        if(isStudent(username) && isLogged(username)){
            return stringOrg(Users.get(username).getMyCourses().toString());
        }
        return "ERROR";
    }

    //gets the spaces out of the array.toString() function
    public String stringOrg(String str){
        String [] s = str.split(",");
        str = null;
        for(String elem: s){
            if(str == null){
                str = elem;
            }
            else {
                str = str +","+ elem.substring(1);
            }
        }
        return str;
    }

    //A getter for Student so the course he registers to will be put in the right place organized as how we got in from the file
    public ArrayList<Integer> getCoursesOrder(){return coursesOrder;}

    //checks for the functions
    //Admin check - also checks existence
    public boolean isAdmin(String username){
        if(username != null) {
            return Users.get(username) instanceof Admin;
        }
        return false;
    }

    //Student check - also checks existence
    public boolean isStudent(String username){
        if(username != null) {
            return Users.get(username) instanceof Student;
        }
        return false;
    }

    //check if the user exists
    public boolean userExists(String username){
        if(username == null){
            return false;
        }
        return Users.containsKey(username);}

    //check if the user is logged in to the system
    public boolean isLogged(String username){
        return Users.get(username).isLogged();
    }

    //check if the course exists
    public boolean courseExists(int courseNum){return Courses.containsKey(courseNum);}

    //check if the student is registered to the course
    public boolean studentInCourse(String username, int courseNum){
        if(Users.get(username).getMyCourses() != null) {
            return Users.get(username).getMyCourses().contains(courseNum);
        }
        return false;
    }

    //check if the password given is correct
    public boolean checkPass(String username, String password){return Users.get(username).getPassword().equals(password);}

    //check if the course exists, if the student is registered to the course, if the course has available seats and if
    // the student has all the KDAM courses
    public boolean courseAvailable(String username, int courseNum){
        if(courseExists(courseNum) && !studentInCourse(username, courseNum) && Courses.get(courseNum).getAvailableSeats() > 0){
            for (Integer KDAM : Courses.get(courseNum).getKDAMCoursesList()) {
                if (!Users.get(username).getMyCourses().contains(KDAM)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    //clears the database when the server shuts down for next use
    public void clear(){
        Users.clear();
        Courses.clear();
        coursesOrder = null;
    }

}