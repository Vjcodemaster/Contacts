package com.antimatter.contact;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import app_utility.PermissionHandler;

import static app_utility.PermissionHandler.WRITE_CONTACTS_PERMISSION;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    LinkedHashMap<String, ArrayList<ContactModel>> lHMContactsList = new LinkedHashMap<>();
    int nPermissionFlag = 0;

    ArrayList<String> alName = new ArrayList<>();
    ArrayList<String> alPhone = new ArrayList<>();
    ContactsListRVAdapter contactsListRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        recyclerView = findViewById(R.id.rv_contacts_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);

        //contactsListRVAdapter = new ContactsListRVAdapter(this, recyclerView, alName, alPhone);
        //recyclerView.setAdapter(contactsListRVAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!PermissionHandler.hasPermissions(MainActivity.this, WRITE_CONTACTS_PERMISSION)) {
            ActivityCompat.requestPermissions(MainActivity.this, WRITE_CONTACTS_PERMISSION, 1);
        } else {
            //setAdapter();
            //getContacts(MainActivity.this);
            if (alName.size() == 0)
                getAllContacts();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int PERMISSION_ALL, String permissions[], int[] grantResults) {
        StringBuilder sMSG = new StringBuilder();
        PermissionHandler permissionHandler;
        if (PERMISSION_ALL == 1) {
            for (String sPermission : permissions) {
                switch (sPermission) {
                    case Manifest.permission.READ_CONTACTS:
                        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                                //Show permission explanation dialog...
                                //showPermissionExplanation(SignInActivity.this.getResources().getString(R.string.phone_explanation));
                                //Toast.makeText(SignInActivity.this, "not given", Toast.LENGTH_SHORT).show();
                                sMSG.append("READ CONTACTS, ");
                                nPermissionFlag = 0;
                            } else {
                                //Never ask again selected, or device policy prohibits the app from having that permission.
                                //So, disable that feature, or fall back to another situation...
                                //@SuppressWarnings("unused") AlertDialogs alertDialogs = new AlertDialogs(HomeScreen.this, 1, mListener);
                                //Toast.makeText(SignInActivity.this, "permission never ask", Toast.LENGTH_SHORT).show();
                                //showPermissionExplanation(HomeScreenActivity.this.getResources().getString(R.string.phone_explanation));
                                sMSG.append("READ CONTACTS, ");
                                nPermissionFlag = 0;
                            }
                        } else {
                            getAllContacts();
                            //setAdapter();
                            //ArrayList<HashMap<String, Object>> contactList = getContacts();
                            //System.out.println("Contact List : " +contactList.get(0).get("phone"));
                            //Toast.makeText(MainActivity.this, "Contact List : " +contactList.get(0).get("phone"), Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
            }
            if (!sMSG.toString().equals("") && !sMSG.toString().equals(" ")) {
                permissionHandler = new PermissionHandler(MainActivity.this, 0, sMSG.toString(), nPermissionFlag);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {

                } else {
                    MainActivity.this.finish();
                }
                break;
        }
    }

    private void setAdapter(ArrayList<String> alName, ArrayList<String> alPhone) {
        /*alName.add("Vijay");
        alName.add("Vishal");
        alName.add("Santhosh");
        alName.add("Manish");
        alName.add("Suraj");

        alPhone.add("9036640528");
        alPhone.add("9649552626");
        alPhone.add("7795810444");
        alPhone.add("9901686584");
        alPhone.add("9738912399");*/

        contactsListRVAdapter = new ContactsListRVAdapter(this, recyclerView, alName, alPhone);
        recyclerView.setAdapter(contactsListRVAdapter);
    }

    public class ContactModel {
        public String id;
        public String name;
        public String mobileNumber;
        public Bitmap photo;
        public Uri photoURI;
    }

    public List<ContactModel> getContacts(Context ctx) {
        List<ContactModel> list = new ArrayList<>();
        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
                    Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                    Bitmap photo = null;
                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream);
                    }
                    while (cursorInfo.moveToNext()) {
                        ContactModel info = new ContactModel();
                        info.id = id;
                        info.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        info.mobileNumber = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        info.photo = photo;
                        info.photoURI = pURI;
                        list.add(info);
                    }

                    cursorInfo.close();
                }
            }
            cursor.close();
        }
        return list;
    }

    void getAllContacts() {
        long startnow;
        long endnow;

        startnow = android.os.SystemClock.uptimeMillis();
        ArrayList arrContacts = new ArrayList();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Cursor cursor = getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.Contacts._ID}, selection, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ContactModel contactInfo = new ContactModel();

            /*String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
            int contactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));*/
            contactInfo.mobileNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactInfo.name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            //int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
            contactInfo.id = String.valueOf(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
            ArrayList<ContactModel> alTmp = new ArrayList<>();
            alTmp.add(contactInfo);
            //Log.d("contacts: ", "name " + contactName + " " + " PhoneContactID " + phoneContactID + "  ContactID " + contactID +"," + "number" +" " + contactNumber);
            if(!alPhone.contains(contactInfo.mobileNumber)) {
                lHMContactsList.put(contactInfo.id, alTmp);
                alName.add(contactInfo.name);
                alPhone.add(contactInfo.mobileNumber);
            }
            cursor.moveToNext();
        }
        cursor.close();
        endnow = android.os.SystemClock.uptimeMillis();
        Log.d("END", "TimeForContacts " + (endnow - startnow) + " ms");

        Log.d("size of contacts: ", "" + lHMContactsList.size());
        setAdapter(alName, alPhone);

    }

}
