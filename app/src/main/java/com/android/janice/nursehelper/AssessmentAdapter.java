package com.android.janice.nursehelper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by janicerichards on 2/5/17.
 */

public class AssessmentAdapter extends ArrayAdapter<AssessmentItem> {
    Context context;
    int layoutResourceId;
    List<AssessmentItem> data;

    public AssessmentAdapter(Context context, int layoutResourceId, List<AssessmentItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AssessmentAdapter.AssessmentHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new AssessmentAdapter.AssessmentHolder();
            holder.txt1 = (TextView)row.findViewById(android.R.id.text1);
            holder.txt2 = (TextView)row.findViewById(android.R.id.text2);

            row.setTag(holder);
        } else {
            holder = (AssessmentAdapter.AssessmentHolder)row.getTag();
        }

        // FIX LATER
        AssessmentItem assessment = data.get(position);
        holder.txt1.setText(assessment.getRoomNumber());
        holder.txt2.setText(assessment.getBloodPressure());

        return row;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public AssessmentItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getPosition(AssessmentItem item) {
        return super.getPosition(item);
    }

    /*
    @Override
    public boolean hasStableIds() {
        return true;
    }
*/

    static class AssessmentHolder {
        TextView txt1, txt2;
    }

}
