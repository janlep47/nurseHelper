package com.android.janice.nursehelper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
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
    Context mContext;
    int layoutResourceId;
    List<AssessmentItem> data;
    TextView mEmptyView;

    public PastAssessmentsAdapter(Context context, int layoutResourceId, List<AssessmentItem> data,
                                  TextView emptyView) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = context;
        this.data = data;
        this.mEmptyView = emptyView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PastAssessmentsAdapter.AssessmentHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PastAssessmentsAdapter.AssessmentHolder();
            holder.txt1 = (TextView)row.findViewById(android.R.id.text1);
            holder.txt2 = (TextView)row.findViewById(android.R.id.text2);

            row.setTag(holder);
        } else {
            holder = (PastAssessmentsAdapter.AssessmentHolder)row.getTag();
        }

        String bpLabel = mContext.getResources().getString(R.string.bp_label);
        String tempLabel = mContext.getResources().getString(R.string.temp_label);
        String pulseLabel = mContext.getResources().getString(R.string.pulse_label);
        String rrLabel = mContext.getResources().getString(R.string.RR_label);
        String edemaLabel = mContext.getResources().getString(R.string.edema_label);
        String locnLabel = mContext.getResources().getString(R.string.locn_label);
        String pittingVal = mContext.getResources().getString(R.string.edema_pitting);
        String nonPittingVal = mContext.getResources().getString(R.string.edema_non_pitting);
        String painLabel = mContext.getResources().getString(R.string.pain_label);
        String findingsLabel = mContext.getResources().getString(R.string.findings_label);
        AssessmentItem assessment = data.get(position);

        holder.txt1.setText(assessment.getReadableTimestamp(mContext)+
                "\n  "+bpLabel+" "+assessment.getBloodPressure()+
                "  "+tempLabel+" "+assessment.getTemperature()+
                "\n  "+pulseLabel+" "+assessment.getPulse()+
                "  "+rrLabel+" "+assessment.getRespiratoryRate()+
                "\n  "+edemaLabel+" "+assessment.getEdema()+
                ((!assessment.getEdema().equals("0")) ? "  "+locnLabel+" "+assessment.getEdemaLocn()+
                        ((assessment.getPitting()) ? "  "+pittingVal : "  "+nonPittingVal) : " ") +
                "\n  "+painLabel+" "+String.valueOf(assessment.getPain()));
        holder.txt2.setText(findingsLabel+" \n"+assessment.getSignificantFindings());

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
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mEmptyView.setVisibility(getCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getPosition(AssessmentItem item) {
        return super.getPosition(item);
    }

    static class AssessmentHolder {
        TextView txt1, txt2;
    }

}
