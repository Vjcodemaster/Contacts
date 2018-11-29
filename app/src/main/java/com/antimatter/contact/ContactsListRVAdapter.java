package com.antimatter.contact;

import android.content.Context;
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
import java.util.LinkedHashSet;

import app_utility.TextDrawable;

public class ContactsListRVAdapter extends RecyclerView.Adapter<ContactsListRVAdapter.ContactsListHolder> implements ICustomAdapter {
    private String sFullName;
    private String sTextDrawable;
    private String[] saTextDrawable;
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<String> alName;
    private ArrayList<String> alPhone;
    private ArrayList<String> alContacts;
    private TextDrawable drawable;
    private LinkedHashSet<Integer> lhsAlphabetsIndex = new LinkedHashSet<>();
    private String sTmp;

    ContactsListRVAdapter(Context context, RecyclerView recyclerView, ArrayList<String> alName, ArrayList<String> alPhone) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.alName = alName;
        this.alPhone = alPhone;
    }

    ContactsListRVAdapter(Context context, RecyclerView recyclerView, ArrayList<String> alContacts) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.alContacts = alContacts;
    }

    @NonNull
    @Override
    public ContactsListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_contacts_list, parent, false);
        return new ContactsListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactsListHolder holder, int position) {
        //String sFullName = alName.get(position);
        sFullName = alContacts.get(position).split("\"")[1];
        if (sFullName.contains(" ")) {
            saTextDrawable = sFullName.split(" ");
            /*if (saTextDrawable.length >= 3)
                sTextDrawable = saTextDrawable[0].substring(0, 1) + saTextDrawable[1].substring(0, 1) + saTextDrawable[2].substring(0, 1);
            else*/
            sTextDrawable = saTextDrawable[0].substring(0, 1) + saTextDrawable[1].substring(0, 1);
        } else {
            sTextDrawable = sFullName.substring(0, 1);
        }
        //sTextDrawable = sTextDrawable.replaceAll("[^a-zA-Z0-9\\s]", "");

        //sTextDrawable = sTextDrawable.replaceAll("\\d", "");
        if (!sTextDrawable.substring(0, 1).toLowerCase().contains("\\d"))
            sTextDrawable = sTextDrawable.replaceAll(":|\\d|\\+|\\)|\\(", "#").trim().toUpperCase();
        if (sTextDrawable.substring(0, 1).equals("#"))
            sTextDrawable = "";
        sTextDrawable = sTextDrawable.replaceAll("#", "");
            //if(!sTextDrawable.contains(".*\\\\d+.*"))
            holder.tvAlphabet.setText(sTextDrawable.toUpperCase());

        /*if(!sTextDrawable.equals(sTmp)){
            lhsAlphabetsIndex.add(position);
            //MainActivity.contactsInterface.onContactsChange("ALPHABET", sTextDrawable.substring(0,1), null, position);
        }*/
        /*
        TextDrawable will set image drawable like google contacts
         */
        /*drawable = TextDrawable.builder()
                .buildRect(sTextDrawable, context.getResources().getColor(R.color.colorPrimaryDark));

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context)
                            .load("")
                            .placeholder(drawable)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(holder.ivTextDrawable);
                }
            });*/

        /*Glide.with(context)
                .load(drawable)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<TextDrawable, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, TextDrawable model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, TextDrawable model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }

                })
                .into(holder.ivTextDrawable);*/

        holder.tvName.setText(sFullName);
        holder.tvPhone.setText(alContacts.get(position).split("\"")[2]);
        /*sTmp = sTextDrawable;
        int firstVisiblePosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        if(lhsAlphabetsIndex.contains(firstVisiblePosition)){
            MainActivity.contactsInterface.onContactsChange("ALPHABET", sTextDrawable.substring(0,1), null, position);
        }*/
        //holder.ivTextDrawable.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return alContacts.size();
    }

    @Override
    public String getCustomStringForElement(int element) {
        String sElement = alContacts.get(element).split("\"")[1].substring(0, 1);
        MainActivity.contactsInterface.onContactsChange("ALPHABET", sElement, null, element);
        return alName.get(element).substring(0, 1);
        //return sElement;
    }

    public class ContactsListHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
        TextView tvName;
        TextView tvPhone;
        ImageView ivTextDrawable;
        TextView tvAlphabet;

        public ContactsListHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_rv_name);
            tvPhone = itemView.findViewById(R.id.tv_rv_phone);
            ivTextDrawable = itemView.findViewById(R.id.circular_iv);
            tvAlphabet = itemView.findViewById(R.id.tv_alphabet);
        }

        @Override
        public void onClick(View view) {

        }

        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {

        }
    }


}
