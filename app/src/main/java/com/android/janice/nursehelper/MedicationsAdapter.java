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

public class MedicationsAdapter extends ArrayAdapter<MedicationItem> {
    Context context;
    int layoutResourceId;
    List<MedicationItem> data;

    public MedicationsAdapter(Context context, int layoutResourceId, List<MedicationItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MedicationsHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MedicationsHolder();
            holder.txt1 = (TextView)row.findViewById(android.R.id.text1);
            holder.txt2 = (TextView)row.findViewById(android.R.id.text2);

            row.setTag(holder);
        } else {
            holder = (MedicationsHolder)row.getTag();
        }

        MedicationItem med = data.get(position);
        holder.txt1.setText(med.getGenericName());
        holder.txt2.setText(med.getTradeName());

        return row;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public MedicationItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getPosition(MedicationItem item) {
        return super.getPosition(item);
    }

    /*
    @Override
    public boolean hasStableIds() {
        return true;
    }
*/

    static class MedicationsHolder {
        TextView txt1, txt2;
    }
}
