package Classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by eymard on 20/01/2016.
 */
public class Course {
    public String depart;
    public String arrivee;
    public String driver;
    public String note;

    public Course(String a, String b, String c, String d) {
        this.depart = a;
        this.arrivee = b;
        this.driver=c;
        this.note=d;
    }


    public Course(JSONObject object){
        try {
            this.depart = object.getString("descDep");
            this.arrivee = object.getString("descArr");
            this.driver = object.getString("username");
            this.note=object.getString("note");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Factory method to convert an array of JSON objects into a list of objects
    // User.fromJson(jsonArray);
    public static ArrayList<Course> fromJson(JSONArray jsonObjects) {
        ArrayList<Course> courses = new ArrayList<Course>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                courses.add(new Course(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return courses;
    }


}
