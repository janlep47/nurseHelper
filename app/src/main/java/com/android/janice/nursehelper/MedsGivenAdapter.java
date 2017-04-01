package com.android.janice.nursehelper;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/*
 * Created by janicerichards on 2/4/17.
 */

public class MedsGivenAdapter extends ArrayAdapter<MedGivenItem> {
    private final Context context;
    private final int layoutResourceId;
    List<MedGivenItem> data;
    private final TextView mEmptyView;

    public MedsGivenAdapter(Context context, int layoutResourceId, List<MedGivenItem> data, TextView emptyView) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.mEmptyView = emptyView;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        MedsGivenHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MedsGivenHolder();
            holder.txt1 = (TextView) row.findViewById(android.R.id.text1);
            holder.txt2 = (TextView) row.findViewById(android.R.id.text2);

            row.setTag(holder);
        } else {
            holder = (MedsGivenHolder) row.getTag();
        }

        String dosageLabel = context.getResources().getString(R.string.med_dosage_label);
        String givenLabel = context.getResources().getString(R.string.med_given);
        String refusedLabel = context.getResources().getString(R.string.med_refused);
        MedGivenItem med = data.get(position);
        holder.txt1.setText(med.getGenericName() + "   " + dosageLabel + " " + String.valueOf(med.getDosage()) +
                " " + med.getDosageUnits());
        String line2 = (med.getGivenOrRefused() ? "   " + givenLabel + "  " : "   " + refusedLabel + "  ") +
                med.getReadableTimestamp(context) +
                "  " + med.getNurseName();
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
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mEmptyView.setVisibility(getCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getPosition(MedGivenItem item) {
        return super.getPosition(item);
    }


    static class MedsGivenHolder {
        TextView txt1, txt2;
    }
}
