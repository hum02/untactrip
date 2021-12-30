package com.example.untactrip3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.untactrip3.dto.Datum_code;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter_dong2 extends RecyclerView.Adapter<RecyclerAdapter_dong2.ItemViewHolder> {
    private ArrayList<Data> listData = new ArrayList<>();
    private Context context;
    private String gu;
    public static List<Datum_code> items;

    RecyclerAdapter_dong2(Context context, String gu, List<Datum_code> items){
        this.context = context;
        this.gu = gu;
        this.items = items;
    }
    @NonNull

    public ArrayList<String> DongOfGu(String gu){
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i <items.size(); i++) {
            if(items.get(i).getDong()== null)
                continue;
            if(items.get(i).getSiGunGu().equals(gu)){
                    list.add(items.get(i).getLDongCd());
            }
        }
        return list;
    }

    public String findDongbyCode(String code){
        int i;
        for(i = 0; i<items.size(); i++) {
            if (items.get(i).getLDongCd() == code)
                break;
        }
        return items.get(i).getDong();
    }

    public ArrayList<String> getList(String si){
        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i<items.size(); i++){
            if(items.get(i).getDong() != null)
                if(items.get(i).getSiGunGu().equals(si))
                    list.add(items.get(i).getLDongCd());
        }
        return list;
    }

    @Override
    public RecyclerAdapter_dong2.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seoul_item,parent,false);
        return new RecyclerAdapter_dong2.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter_dong2.ItemViewHolder holder, int position) {
        holder.onBind(DongOfGu(gu).get(position));
    }

    @Override
    public int getItemCount(){
        return DongOfGu(gu).size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView main_textView;

        ItemViewHolder(View itemView) {
            super(itemView);
            main_textView = itemView.findViewById(R.id.seoul_main_name);
        }

        void onBind(String data) {
            String dong = findDongbyCode(data);
            main_textView.setText(dong);
            itemView.setOnClickListener(this);
            main_textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(view.getContext(), FlowDensitySi.class);
//                    intent.putExtra("si", dong);
//                    intent.putStringArrayListExtra("lDongCdList", getList(data));
//                    context.startActivity(intent);
                }
            });
        }
        @Override
        public void onClick(View v){
        }
    }
}
