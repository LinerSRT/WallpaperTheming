package ru.liner.wallpapertheming.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

import ru.liner.wallpapertheming.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    private List<MyData> dataList;

    public RecyclerAdapter(List<MyData> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyData data = dataList.get(position);
        holder.textView.setText(data.getText());
        holder.switchMaterial.setChecked(data.isValue());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private SwitchMaterial switchMaterial;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.recyclerText);
            switchMaterial = itemView.findViewById(R.id.recyclerSwitch);
        }
    }
}
