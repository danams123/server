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
        boolean stop = false;
        for (int i = 0; i < myCourses.size() && !stop; i++) {
            if(database.getCoursesOrder().indexOf(myCourses.get(i)) > database.getCoursesOrder().indexOf(toAdd.getCourseNum())){
                myCourses.add(i , toAdd.getCourseNum());
                stop = true;
            }
        }
        if(!stop){
            myCourses.add(toAdd.getCourseNum());
        }
    }

    public void unRegister(Course toDel){
        toDel.delStudent(getUserName());
        myCourses.remove(toDel.getCourseNum());
    }
}
