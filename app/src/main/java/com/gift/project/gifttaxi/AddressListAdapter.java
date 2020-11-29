package com.gift.project.gifttaxi;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gift.project.gifttaxi.models.Documents;

import java.util.ArrayList;

public class AddressListAdapter extends BaseAdapter {
    private final ArrayList<Documents> documents;
    private Context context;

    public AddressListAdapter(ArrayList<Documents> documents, Context context){
        this.documents=documents;
        this.context=context;
    }

    @Override
    public int getCount() {
        return this.documents.size();
    }

    @Override
    public Object getItem(int i) {
        return  this.documents.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Documents document = (Documents) this.documents.get(i);
        TextView itemView=new TextView(this.context);
        itemView.setTextSize(TypedValue.COMPLEX_UNIT_PT,7);
        itemView.setText(document.address.addressName);
        itemView.setPadding(30,10,10,10);
        return itemView;
    }
}
