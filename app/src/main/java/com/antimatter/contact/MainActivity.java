package com.antimatter.contact;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    ArrayList<String> alName = new ArrayList<>();
    ArrayList<String> alPhone = new ArrayList<>();
    ContactsListRVAdapter contactsListRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        recyclerView = findViewById(R.id.rv_contacts_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);

        alName.add("Vijay");
        alName.add("Vishal");
        alName.add("Santhosh");
        alName.add("Manish");
        alName.add("Suraj");

        alPhone.add("9036640528");
        alPhone.add("9649552626");
        alPhone.add("7795810444");
        alPhone.add("9901686584");
        alPhone.add("9738912399");

        contactsListRVAdapter = new ContactsListRVAdapter(this, recyclerView, alName, alPhone);
        recyclerView.setAdapter(contactsListRVAdapter);
    }
}
