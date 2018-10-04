package com.antimatter.contact;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import app_utility.PermissionHandler;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;
import static app_utility.PermissionHandler.WRITE_CONTACTS_PERMISSION;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    TextView tvAlphabet;
    ImageView ivDemo;
    int overallXScroll;

    LinkedHashMap<String, ArrayList<ContactModel>> lHMContactsList = new LinkedHashMap<>();
    ArrayList<String> alContact = new ArrayList<>();
    int nPermissionFlag = 0;
    LinearLayoutManager mLinearLayoutManager;

    ArrayList<String> alName = new ArrayList<>();
    ArrayList<String> alPhone = new ArrayList<>();
    ContactsListRVAdapter contactsListRVAdapter;

    float dX, dY;
    ObjectAnimator fadeOutAnimator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        tvAlphabet = findViewById(R.id.tv_alphabet);
        //ivDemo = findViewById(R.id.iv_demo);

        recyclerView = findViewById(R.id.rv_contacts_list);
        mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //Log.d("Adapter: ", String.valueOf(recyclerView.getChildAdapterPosition(recyclerView.getLayoutManager().getChildAt(1))));
                TextView tv = recyclerView.getLayoutManager().getChildAt(1).findViewById(R.id.tv_rv_name);
                if(tv.getText().toString().length()>=1) {
                    String sAlphabet = tv.getText().toString().substring(0, 1);
                    tvAlphabet.setText(sAlphabet);
                }
                //Log.d("Dragging scroll", "Scrolling" + dy);
                /*if(dy >0 || dy <0){
                    fadeInAndVisibleImage(ivDemo);
                } else
                    fadeOutAndHideImage(ivDemo);*/

                /*LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int position = manager.findFirstVisibleItemPosition();
                View firstItemView = manager.findViewByPosition(position);
                float Offset = firstItemView.getBottom();

                overallXScroll = overallXScroll + dy;
                Log.i("check","overall y  = " + overallXScroll);
                ObjectAnimator anim = ObjectAnimator.ofFloat(ivDemo,"y",Offset);
                anim.setDuration(3000); // duration 3 seconds
                anim.start();*/
            }


            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //ivDemo.setVisibility(View.GONE);
                        fadeOutAndHideImage(tvAlphabet);
                        Log.d("NO SCROLL", "The RecyclerView is not scrolling");
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        fadeInAndVisibleImage(tvAlphabet);
                        //ivDemo.setVisibility(View.VISIBLE);
                        Log.d("Scrolling", "Scrolling now");
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        tvAlphabet.setVisibility(View.VISIBLE);
                        //ivDemo.setVisibility(View.VISIBLE);
                        Log.d("SCROLL SETTLE", "Scroll settling");
                        break;
                }
                /*
                use below code to move bubble
                 */
                /*ObjectAnimator anim = null;
                int[] nOffSetLocation = new int[2];

                if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {
                    //getFastScrollThumbPoint(recyclerView);
                    //float lastFirstVisiblePosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).getChildAt(0).getY();
                    ((LinearLayoutManager)recyclerView.getLayoutManager()).getChildAt(10).getLocationOnScreen(nOffSetLocation);
                    //int offset = recyclerView.computeVerticalScrollOffset();
                    int offset = recyclerView.getChildAt(0).getScrollY();
                    anim = ObjectAnimator.ofFloat(ivDemo,"y",nOffSetLocation[1]);
                    //anim.setTarget(recyclerView.focusSearch(recyclerView, View.FOCUS_RIGHT));
                    anim.setDuration(150);
                    anim.start();
                } else if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                    if(anim!=null) {
                        anim.cancel();
                    }
                } else {
                    // Do something
                }*/
            }
        });
        //((DragScrollBar) findViewById(R.id.dragScrollBar)).setIndicator(new CustomIndicator(this), true);
        //contactsListRVAdapter = new ContactsListRVAdapter(this, recyclerView, alName, alPhone);
        //recyclerView.setAdapter(contactsListRVAdapter);
    }

    /*private Point getFastScrollThumbPoint(final RecyclerView listView) {
        try {
            final Class<?> fastScrollerClass = Class.forName("android.support.v7.widget.RecyclerView");

            final int[] listViewLocation = new int[2];
            listView.getLocationInWindow(listViewLocation);
            int x = listViewLocation[0];
            int y = listViewLocation[1];

            final Field fastScrollerField;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                RecyclerView.class.getDeclaredClasses();
                fastScrollerField = RecyclerView.class.getDeclaredField("smoothScroller");
            }
            else {
                fastScrollerField = RecyclerView.class.getDeclaredField("smoothScroller");
            }
            fastScrollerField.setAccessible(true);

            final Object fastScroller = fastScrollerField.get(listView);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                final Field thumbImageViewField = fastScrollerClass.getDeclaredField("mThumbImage");
                thumbImageViewField.setAccessible(true);
                final ImageView thumbImageView = (ImageView) thumbImageViewField.get(fastScroller);

                final int[] thumbViewLocation = new int[2];
                thumbImageView.getLocationInWindow(thumbViewLocation);

                x += thumbViewLocation[0] + thumbImageView.getWidth() / 2;
                y += thumbViewLocation[1] + thumbImageView.getHeight() / 2;
            }
            else {
                final Field thumbDrawableField = fastScrollerClass.getDeclaredField("mThumbDrawable");
                thumbDrawableField.setAccessible(true);
                final Drawable thumbDrawable = (Drawable) thumbDrawableField.get(fastScroller);
                final Rect bounds = thumbDrawable.getBounds();

                final Field thumbYField = fastScrollerClass.getDeclaredField("mThumbY");
                thumbYField.setAccessible(true);
                final int thumbY = (Integer) thumbYField.get(fastScroller);

                x += bounds.left + bounds.width() / 2;
                y += thumbY + bounds.height() / 2;
            }

            return new Point(x, y);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }*/

    /*public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }*/

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        Point pt = new Point( (int)event.getX(), (int)event.getY() );
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Toast.makeText(MainActivity.this, "Down Eevent", Toast.LENGTH_SHORT).show();
        }
            *//*if (*//**//*this is an interesting event my View will handle*//**//*) {
                // here is the fix! now without NPE
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                clicked_on_image = true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (clicked_on_image) {
                //do stuff, drag the image or whatever
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            clicked_on_image = false;
        }*//*
        return true;
    }*/

    @Override
    public void onStart() {
        super.onStart();
        if (!PermissionHandler.hasPermissions(MainActivity.this, WRITE_CONTACTS_PERMISSION)) {
            ActivityCompat.requestPermissions(MainActivity.this, WRITE_CONTACTS_PERMISSION, 1);
        } else {
            //setAdapter();
            //getContacts(MainActivity.this);
            if (alContact.size() == 0)
                //new fetchContacts().execute();
            new fetchContacts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                //getAllContacts();
        }
    }

    private void fadeInAndVisibleImage(final TextView tv) {
        fadeOutAnimator = ObjectAnimator.ofFloat(tv, View.ALPHA, 0, 1);
        fadeOutAnimator.setInterpolator(new AccelerateInterpolator());
        fadeOutAnimator.setDuration(250);

        fadeOutAnimator.start();

        fadeOutAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                tv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void fadeOutAndHideImage(final TextView tv) {
        fadeOutAnimator = ObjectAnimator.ofFloat(tv, View.ALPHA, 1, 0);
        fadeOutAnimator.setInterpolator(new AccelerateInterpolator());
        fadeOutAnimator.setDuration(400);

        fadeOutAnimator.start();

        fadeOutAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                tv.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        /*Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(400);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                img.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });*/

        //img.startAnimation(fadeOut);
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

        /*contactsListRVAdapter = new ContactsListRVAdapter(this, recyclerView, alName, alPhone);
        recyclerView.setAdapter(contactsListRVAdapter);*/
        contactsListRVAdapter = new ContactsListRVAdapter(this, recyclerView, alContact);
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
        ArrayList<ContactModel> alTmp;
        HashSet<String> hsPhoneNo = new HashSet<>();

        startnow = android.os.SystemClock.uptimeMillis();
        /*ArrayList arrContacts = new ArrayList();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Cursor cursor = getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.Contacts._ID}, selection, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");*/

        String selectionFields =  ContactsContract.RawContacts.ACCOUNT_TYPE + " = ?";
        String[] selectionArgs = new String[]{"com.google"};
        Cursor cursor = getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[] {ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, selectionFields, selectionArgs,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ContactModel contactInfo = new ContactModel();

            /*String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
            int contactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));*/
            //String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //String phone = contactNumber.replaceAll("[- ()]", "");
            //hsPhoneNo.add(phone);

            //if (hsPhoneNo.size() > alContact.size()) {
                contactInfo.mobileNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //contactInfo.mobileNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[- ]","");
                contactInfo.name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                //int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                contactInfo.id = String.valueOf(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                alTmp = new ArrayList<>();
                alTmp.add(contactInfo);
                //Log.d("contacts: ", "name " + contactName + " " + " PhoneContactID " + phoneContactID + "  ContactID " + contactID +"," + "number" +" " + contactNumber);
                //if(!alPhone.contains(contactInfo.mobileNumber)) {
                //lHMContactsList.put(contactInfo.id, alTmp);
                //alContact.add(contactInfo.id + "," + contactInfo.name + "," + contactInfo.mobileNumber);
                //alContact.add(contactInfo.id + "\"" + contactInfo.name + "\"" + contactInfo.mobileNumber);
                alContact.add(contactInfo.id + "\"" + contactInfo.name + "\"" + contactInfo.mobileNumber);
                /*alName.add(contactInfo.name);
                alPhone.add(contactInfo.mobileNumber);*/

            //}
            cursor.moveToNext();
        }
        cursor.close();

        /*
        this sorts order of alContact list by numbers first and then letters
         */
        /*Collections.sort(alContact, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                String s11 = s1.split(",")[1];
                String s12 = s2.split(",")[1];
                return s11.compareToIgnoreCase(s12);
            }

        });*/
        /*
        sorts the order of alContact list by letters first and then numbers and others
         */

        Collections.sort(alContact, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                String s11 = lhs.split("\"")[1];
                String s12 = rhs.split("\"")[1];
                boolean lhsStartsWithLetter = Character.isLetter(s11.charAt(0));
                boolean rhsStartsWithLetter = Character.isLetter(s12.charAt(0));

                if ((lhsStartsWithLetter && rhsStartsWithLetter) || (!lhsStartsWithLetter && !rhsStartsWithLetter)) {
                    // they both start with letters or not-a-letters
                    return s11.compareToIgnoreCase(s12);
                } else if (lhsStartsWithLetter) {
                    // the first string starts with letter and the second one is not
                    return -1;
                } else {
                    // the second string starts with letter and the first one is not
                    return 1;
                }
            }

        });


        /*Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        Or if you are using Java 8:

        list.sort(String::compareToIgnoreCase);*/
        endnow = android.os.SystemClock.uptimeMillis();
        Log.e("END", "TimeForContacts " + (endnow - startnow) + " ms");

        Log.e("size of contacts: ", "" + alContact.size());
        setAdapter(alName, alPhone);
    }

    @SuppressLint("StaticFieldLeak")
    private class fetchContacts extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            long startnow;
            long endnow;
            HashSet<String> hsPhoneNo = new HashSet<>();

            startnow = android.os.SystemClock.uptimeMillis();

            String selectionFields =  ContactsContract.RawContacts.ACCOUNT_TYPE + " = ?";
            String[] selectionArgs = new String[]{"com.google"};
            Cursor cursor = getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[] {ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, selectionFields, selectionArgs,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ContactModel contactInfo = new ContactModel();

                contactInfo.mobileNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactInfo.name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                contactInfo.id = String.valueOf(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                alContact.add(contactInfo.id + "\"" + contactInfo.name + "\"" + contactInfo.mobileNumber);
                cursor.moveToNext();
            }
            cursor.close();

            Collections.sort(alContact, new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    String s11 = lhs.split("\"")[1];
                    String s12 = rhs.split("\"")[1];
                    boolean lhsStartsWithLetter = Character.isLetter(s11.charAt(0));
                    boolean rhsStartsWithLetter = Character.isLetter(s12.charAt(0));

                    if ((lhsStartsWithLetter && rhsStartsWithLetter) || (!lhsStartsWithLetter && !rhsStartsWithLetter)) {
                        // they both start with letters or not-a-letters
                        return s11.compareToIgnoreCase(s12);
                    } else if (lhsStartsWithLetter) {
                        // the first string starts with letter and the second one is not
                        return -1;
                    } else {
                        // the second string starts with letter and the first one is not
                        return 1;
                    }
                }

            });

            endnow = android.os.SystemClock.uptimeMillis();
            Log.e("END", "TimeForContacts " + (endnow - startnow) + " ms");

            Log.e("size of contacts: ", "" + alContact.size());
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            setAdapter(alName, alPhone);
        }
    }

    /*void getAllContacts() {
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

            *//*String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
            int contactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));*//*
            String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String phone = contactNumber.replaceAll("[- ]", "");

            if (!alPhone.contains(phone)) {
                contactInfo.mobileNumber = phone;
                //contactInfo.mobileNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[- ]","");
                contactInfo.name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                //int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                contactInfo.id = String.valueOf(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                ArrayList<ContactModel> alTmp = new ArrayList<>();
                alTmp.add(contactInfo);
                //Log.d("contacts: ", "name " + contactName + " " + " PhoneContactID " + phoneContactID + "  ContactID " + contactID +"," + "number" +" " + contactNumber);
                //if(!alPhone.contains(contactInfo.mobileNumber)) {
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
    }*/

    /*private void getToken(){
        String CLIENT_ID = "837282398437-6ei51k7v57fpi5adm61eq7tq6in1218r.apps.googleusercontent.com";
        String CLIENT_SECRET = "see instructions in accepted answer";
        String REFRESH_TOKEN = "see instructions in accepted answer";

        GoogleCredential.Builder builder = new GoogleCredential.Builder();
        try {
            builder.setTransport(GoogleNetHttpTransport.newTrustedTransport());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        builder.setJsonFactory(JacksonFactory.getDefaultInstance());
        builder.setClientSecrets(CLIENT_ID, CLIENT_SECRET);

        Credential credential = builder.build();
        credential.setRefreshToken(REFRESH_TOKEN);
        try {
            credential.refreshToken(); // gets the access token, using the refresh token
        } catch (IOException e) {
            e.printStackTrace();
        }

        ContactsService contactsService.setOAuth2Credentials(credential);

        DownloadManager.Query query = null;
        try {
            query = new DownloadManager.Query(new URL("https://www.google.com/m8/feeds/contacts/default/full"));
            query.setMaxResults(10_000);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        ContactFeed allContactsFeed = contactsService.getFeed(query, ContactFeed.class);

        LOGGER.log(Level.INFO, allContactsFeed.getTitle().getPlainText());
    }*/

}
