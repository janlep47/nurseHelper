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

public class PastAssessmentsAdapter extends ArrayAdapter<AssessmentItem> {
    Context context;
    int layoutResourceId;
    List<AssessmentItem> data;

    public PastAssessmentsAdapter(Context context, int layoutResourceId, List<AssessmentItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PastAssessmentsAdapter.AssessmentHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PastAssessmentsAdapter.AssessmentHolder();
            holder.txt1 = (TextView)row.findViewById(android.R.id.text1);
            holder.txt2 = (TextView)row.findViewById(android.R.id.text2);

            row.setTag(holder);
        } else {
            holder = (PastAssessmentsAdapter.AssessmentHolder)row.getTag();
        }

        // FIX LATER
        AssessmentItem assessment = data.get(position);
        //holder.txt1.setText(assessment.getRoomNumber());
        //holder.txt2.setText(assessment.getBloodPressure());
        holder.txt1.setText(assessment.getReadableTimestamp()+
                "\n  BP: "+assessment.getBloodPressure()+
                "  temp: "+assessment.getTemperature()+
                "\n  pulse: "+assessment.getPulse()+
                "  RR: "+assessment.getRespiratoryRate()+
                "\n  edema: "+assessment.getEdema()+
                ((!assessment.getEdema().equals("0")) ? "  locn: "+assessment.getEdemaLocn()+
                        ((assessment.getPitting()) ? "  pitting" : "  non-pitting") : " ") +
                "\n  pain: "+String.valueOf(assessment.getPain()));
        holder.txt2.setText("findings: \n"+assessment.getSignificantFindings());

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
