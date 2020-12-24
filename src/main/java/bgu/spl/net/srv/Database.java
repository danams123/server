package bgu.spl.net.srv;

import jdk.internal.org.objectweb.asm.tree.InnerClassNode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
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
    private Database() {
      this.Users = new ConcurrentHashMap<String, User>();
      this.Courses = new ConcurrentHashMap<Integer, Course>();
      this.coursesOrder = new ArrayList<Integer>();
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
        try{
            FileReader reader = new FileReader(coursesFilePath);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] arr = line.split("\\|");
               int courseNum = Integer.parseInt(arr[0]);
               String courseName = arr[1];
                int[] KDAM;
                if(arr[2].equals("[]")){
                    KDAM = new int[0];
                }
                else {
                    KDAM = Stream.of(arr[2].substring(1, arr[2].length() - 1).split(",")).mapToInt(Integer::parseInt).toArray();
                }
               int maxNumOfStudents = Integer.parseInt(arr[3]);
               Courses.put(courseNum, new Course(courseNum, courseName, KDAM, maxNumOfStudents));
               coursesOrder.add(courseNum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isAdmin(String username){return Users.get(username) instanceof Admin;}

    public boolean isStudent(String username){return Users.get(username) instanceof Student;}

    public boolean adminRegister(String username, String password){
        if(!Users.containsKey(username)){
            Users.put(username, new Admin(username, password));
            return true;
        }
        return false;
    }

    public boolean studentRegister(String username, String password){
        if(!Users.containsKey(username)){
            Users.put(username, new Student(username, password));
            return true;
        }
        return false;
    }

    public boolean Login(String username, String password){
        if((Users.containsKey(username)) && (!Users.get(username).isLogged()) && (Users.get(username).getPassword().equals(password))){
            Users.get(username).Login();
            return true;
        }
        return false;
    }

    public boolean Logout(String username){
        if((Users.containsKey(username)) && (Users.get(username).isLogged())){
            Users.get(username).Logout();
            return true;
        }
        return false;
    }

    public synchronized boolean courseReg(String username, int courseNum){
        if((Users.containsKey(username)) && (Users.get(username).isLogged()) && (isStudent(username))
        && (!Users.get(username).getMyCourses().contains(courseNum)) && (Courses.containsKey(courseNum))
        && (Courses.get(courseNum).getAvailableSeats() > 0)){
            for(Integer KDAM : Courses.get(courseNum).getKDAMCoursesList()){
                if(!Users.get(username).getMyCourses().contains(KDAM)){
                    return false;
                }
            }
            Users.get(username).courseReg(Courses.get(courseNum));
            return true;
            //TODO organized set of coursesnum and call for coursereg student
        }
        return false;
    }

    public int[] KDAMCheck(int courseNum){return Courses.get(courseNum).getKDAMCoursesList();}

    public String courseStat(String admin, String username, int courseNum){
        //TODO check if String is good and how admin enters only
        if(isAdmin(admin) && Users.containsKey(username) && isStudent(username) && Courses.containsKey(courseNum)) {
            Course course = Courses.get(courseNum);
            return "Course: (" + courseNum + ") " + course.getCourseName() + "\nSeats Available: " + course.getAvailableSeats()
                    + " / " + course.getNumOfMaxStudents() + "\nStudents Registered: " + course.getStudents();
        }
        return null;
    }

    public String studentStat(String admin, String username){
        if(isAdmin(admin)) {
            return "Student: " + username + "\nCourses: " + Users.get(username).getMyCourses();
        }
        return null;
    }

    public synchronized String isRegistered(String username, int courseNum){
        if(Users.containsKey(username) && Users.get(username).isLogged() && Courses.containsKey(courseNum)) {
            if (Courses.get(courseNum).getStudents().contains(username)) {
                return "REGISTERED";
            }
            return "NOT REGISTERED";
        }
        return null;
    }

    public synchronized boolean unRegister(String username, int courseNum){
        if(Users.get(username).isLogged() && Courses.containsKey(courseNum) && Users.get(username).getMyCourses().contains(courseNum)){
            Users.get(username).unRegister(Courses.get(courseNum));
            return true;
        }
        return false;
    }

    public ArrayList<Integer> getMyCourses(String username){
        if(Users.get(username).isLogged() && isStudent(username)){
            return Users.get(username).getMyCourses();
        }
        return null;
    }

    public ArrayList<Integer> getCoursesOrder(){return coursesOrder;}

}