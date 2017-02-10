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
 * Created by janicerichards on 2/4/17.
 */

public class MedsGivenAdapter extends ArrayAdapter<MedGivenItem> {
    Context context;
    int layoutResourceId;
    List<MedGivenItem> data;

    public MedsGivenAdapter(Context context, int layoutResourceId, List<MedGivenItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MedsGivenHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MedsGivenHolder();
            holder.txt1 = (TextView)row.findViewById(android.R.id.text1);
            holder.txt2 = (TextView)row.findViewById(android.R.id.text2);

            row.setTag(holder);
        } else {
            holder = (MedsGivenHolder)row.getTag();
        }

        MedGivenItem med = data.get(position);
        holder.txt1.setText(med.getGenericName() + "   dosage: "+String.valueOf(med.getDosage()) +
            " "+med.getDosageUnits());
        String line2 = (med.getGiven() == 1 ? "    given " : "    refused ") + med.getReadableTimestamp();
        holder.txt2.setText(line2);
        return row;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public MedGivenItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getPosition(MedGivenItem item) {
        return super.getPosition(item);
    }


    static class MedsGivenHolder {
        TextView txt1, txt2;
    }
}
