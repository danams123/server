package bgu.spl.net.srv;

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
    //to prevent user from creating new Database
    Database() {
      this.Users = new ConcurrentHashMap<String, User>();
      this.Courses = new ConcurrentHashMap<Integer, Course>();
      this.coursesOrder = new ArrayList<Integer>();
      initialize("./Courses.txt");//is it good???
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        //can initialize from here as well
        return SingletonHolder.instance;
    }

    /**
     * loades the courses from the file path specified
     * into the Database, returns true if successful.
     */

    boolean initialize(String coursesFilePath) {
        //check if the courses file has been loaded already
        if(Courses.isEmpty()) {//is it good?
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
            return true;
        }
        return true;
    }

    public String adminRegister(String username, String password){
        if(!userExists(username)){
            Users.put(username, new Admin(username, password));
            return "ACK";
        }
        return "ERROR";
    }

    public String studentRegister(String username, String password){
        if(!userExists(username)){
            Users.put(username, new Student(username, password));
            System.out.println(Users.get(username).getUserName());
            System.out.println(Users.get(username).getPassword());
            return "ACK";
        }
        return "ERROR";
    }

    public String Login(String username, String password){
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
        if(isStudent(username) && isLogged(username) && isStudent(username) && courseAvailable(username, courseNum)){
            Users.get(username).courseReg(Courses.get(courseNum));
            return "ACK";
        }
        return "ERROR";
    }

    public String KDAMCheck(String username, int courseNum){
        if(isStudent(username) && isLogged(username) && courseExists(courseNum)) {
            return Arrays.toString(Courses.get(courseNum).getKDAMCoursesList());
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
            return "Course: (" + courseNum + ") " + course.getCourseName() + "\0Seats Available: " + course.getAvailableSeats()
                    + " / " + course.getNumOfMaxStudents() + "\0Students Registered: " + toSend;
        }
        return "ERROR";
    }

    public String studentStat(String admin, String username){
        if(isAdmin(admin) && isLogged(admin) && isStudent(username)) {
            return "Student: " + username + "\0Courses: " + Users.get(username).getMyCourses();
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

    public String unRegister(String username, int courseNum){
        if(isStudent(username) && isLogged(username) && courseExists(courseNum) && studentInCourse(username, courseNum)){
            Users.get(username).unRegister(Courses.get(courseNum));
            return "ACK";
        }
        return "ERROR";
    }

    public String getMyCourses(String username){
        if(isStudent(username) && isLogged(username)){
            return Users.get(username).getMyCourses().toString();
        }
        return "ERROR";
    }

    public ConcurrentHashMap<Integer, Course> getCourses(){return Courses;}

    public ConcurrentHashMap<String, User> getUsers(){return Users;}

    //A getter for Student so the course he registers to will be put in the right place organized as how we got in from the file
    public ArrayList<Integer> getCoursesOrder(){return coursesOrder;}

//checks for the functions
    //Admin check - also checks existence
    public boolean isAdmin(String username){return Users.get(username) instanceof Admin;}

    //Student check - also checks existence
    public boolean isStudent(String username){return Users.get(username) instanceof Student;}

   //check if the user exists
    public boolean userExists(String username){
        if(username == null){
            return false;
        }
        System.out.println("got here in userexists");
        System.out.println(username);
        return Users.containsKey(username);}

    //check if the user is logged in to the system
    public boolean isLogged(String username){return Users.get(username).isLogged();}

    //check if the course exists
    public boolean courseExists(int courseNum){return Courses.containsKey(courseNum);}

    //check if the student is registered to the course
    public boolean studentInCourse(String username, int courseNum){return Users.get(username).getMyCourses().contains(courseNum);}

    //check if the password given is correct
    public boolean checkPass(String username, String password){return Users.get(username).getPassword().equals(password);}

    //check if the course exists, if the student is registered to the course, if the course has available seats and if
    // the student has all the KDAM courses
    public boolean courseAvailable(String username, int courseNum){
        if(courseExists(courseNum) && studentInCourse(username, courseNum) && Courses.get(courseNum).getAvailableSeats() > 0){
            for (Integer KDAM : Courses.get(courseNum).getKDAMCoursesList()) {
                if (!Users.get(username).getMyCourses().contains(KDAM)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public void clear (){
        Users.clear();
        Courses.clear();
        coursesOrder = null;
    }

}