package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.eymard.t3x.R;

import java.util.ArrayList;

import Classes.Course;

/**
 * Created by eymard on 20/01/2016.
 */
public class CourseAdapter extends ArrayAdapter<Course> {

    public CourseAdapter(Context context, ArrayList<Course> coursess) {
        super(context, 0, coursess);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Course course = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listcourse_layout, parent, false);
        }
        // Lookup view for data population
        TextView col1 = (TextView) convertView.findViewById(R.id.textViewCol1);
        TextView col2 = (TextView) convertView.findViewById(R.id.textViewCol2);
        TextView col3 = (TextView) convertView.findViewById(R.id.textViewCol3);
        TextView col4 = (TextView) convertView.findViewById(R.id.textViewCol4);
        // Populate the data into the template view using the data object
        col1.setText(course.depart);
        col2.setText(course.arrivee);
        col3.setText(course.driver);
        col4.setText(course.note);
        // Return the completed view to render on screen
        return convertView;
    }


}
