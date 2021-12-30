package com.example.untactrip3;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.untactrip3.dto.Datum_dong;

import java.util.List;

public class RecyclerAdapter_avg extends RecyclerView.Adapter<RecyclerAdapter_avg.ViewHolder>{
    public static List<Datum_dong> items;
    private double avg;
    private Context context;
    private int h;

public RecyclerAdapter_avg(double avg,Context context,int h,List<Datum_dong> items)
        {
            this.avg=avg;
            this.context=context;
            this.h=h;
            this.items=items;
        }

@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_row_item, parent, false);
        return new ViewHolder(itemView);
        }

@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    if (avg > items.get(h).getFlowDensityPercentile()) {
        holder.getTextView().setText("평소보다 사람이 적어요\n" + Double.toString(avg));

    } else {
        holder.getTextView().setText("평소보다 사람이 많아요!\n" + Double.toString(avg));
    }

        }




@Override
public int getItemCount() {
        return 1;
        }

public static class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;

    public ViewHolder(View view) {
        super(view);
        // Define click listener for the ViewHolder's View

        textView = (TextView) view.findViewById(R.id.textViewavg);
    }

    public TextView getTextView() {
        return textView;
    }



}



}
