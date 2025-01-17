package bgu.spl.net.srv;

import java.util.ArrayList;


public class Student extends User{

    private ArrayList<Integer> myCourses;
    private Database database;

    public Student(String userName, String Password) {
        super(userName, Password);
        this.myCourses = new ArrayList<Integer>();
        this.database = Database.getInstance();
    }

    public ArrayList<Integer> getMyCourses() {return myCourses;}

    public void courseReg(Course toAdd){
        toAdd.addStudent(getUserName());
        //putting it in the right location oredered as we got it from the courses file - now in coursesOrder of Database
        boolean stop = false;
        for (int i = 0; i < myCourses.size() && !stop; i++) {
            if(database.getCoursesOrder().indexOf(myCourses.get(i)) > database.getCoursesOrder().indexOf(toAdd.getCourseNum())){
                myCourses.add(i , toAdd.getCourseNum());
                stop = true;
            }
        }
        //if the stop is false, the course we're trying to add is in the end of the courses list. as ordered in the
        //courses file.
        if(!stop){
            myCourses.add(toAdd.getCourseNum());
        }
    }

    public void unRegister(Course toDel){
        toDel.delStudent(getUserName());
        myCourses.remove(myCourses.indexOf(toDel.getCourseNum()));
    }
}
