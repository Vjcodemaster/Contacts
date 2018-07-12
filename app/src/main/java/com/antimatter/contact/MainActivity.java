package com.antimatter.contact;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.turingtechnologies.materialscrollbar.CustomIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import app_utility.PermissionHandler;

import static app_utility.PermissionHandler.WRITE_CONTACTS_PERMISSION;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    ImageView ivDemo;
    int overallXScroll;

    LinkedHashMap<String, ArrayList<ContactModel>> lHMContactsList = new LinkedHashMap<>();
    int nPermissionFlag = 0;
    LinearLayoutManager mLinearLayoutManager;

    ArrayList<String> alName = new ArrayList<>();
    ArrayList<String> alPhone = new ArrayList<>();
    ContactsListRVAdapter contactsListRVAdapter;

    float dX, dY;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        ivDemo = findViewById(R.id.iv_demo);

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
                ObjectAnimator anim = null;
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
                }
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
    }

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
