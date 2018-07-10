package com.antimatter.contact;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.turingtechnologies.materialscrollbar.ICustomAdapter;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.HashMap;

import app_utility.TextDrawable;

public class ContactsListRVAdapter extends RecyclerView.Adapter<ContactsListRVAdapter.ContactsListHolder> implements ICustomAdapter{
    Context context;
    RecyclerView recyclerView;
    ArrayList<String> alName;
    ArrayList<String> alPhone;

    ContactsListRVAdapter(Context context, RecyclerView recyclerView, ArrayList<String> alName, ArrayList<String> alPhone) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.alName = alName;
        this.alPhone = alPhone;
    }
    @NonNull
    @Override
    public ContactsListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_contacts_list, parent, false);
        return new ContactsListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsListHolder holder, int position) {
        String sFullName = alName.get(position);
        String sTextDrawable;
        String[] saTextDrawable;
        if(sFullName.contains(" ")) {
            saTextDrawable = sFullName.split(" ");
            sTextDrawable = saTextDrawable[0].substring(0, 1) + saTextDrawable[1].substring(0, 1);
        } else {
            sTextDrawable = sFullName.substring(0,1);
        }


        /*
        TextDrawable will set image drawable like google contacts
         */
        TextDrawable drawable = TextDrawable.builder()
                .buildRect(sTextDrawable,context.getResources().getColor(R.color.colorPrimaryDark));

        holder.tvName.setText(sFullName);
        holder.tvPhone.setText(alPhone.get(position));
        holder.ivTextDrawable.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return alName.size();
    }

    @Override
    public String getCustomStringForElement(int element) {

        return alName.get(element).substring(0, 1);
    }

    public class ContactsListHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
        TextView tvName;
        TextView tvPhone;
        ImageView ivTextDrawable;

        public ContactsListHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_rv_name);
            tvPhone = itemView.findViewById(R.id.tv_rv_phone);
            ivTextDrawable = itemView.findViewById(R.id.circular_iv);
        }

        @Override
        public void onClick(View view) {

        }

        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {

        }
    }


}
