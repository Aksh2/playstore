package project.cse.anti.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import project.cse.anti.MyCustomAdapter;
import project.cse.anti.ContactsAdapter;
import project.cse.anti.DBHelper;
import project.cse.anti.R;
import project.cse.anti.Utilities.Utilities;
import project.cse.anti.addContact;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import java.util.ArrayList;

public class OneFragment extends Fragment {


    public static final String CONTACTS_COLUMN_NAME="name";
    public static final String CONTACTS_COLUMN_PHONE="phone";
    public static final String CONTACTS_COLUMN_MESSAGE="message";
    public static final String CONTACTS_COLUMN_ID="id";

    CharSequence options[] = new CharSequence[]{"Delete" , "Edit"};

    ContactsAdapter contactsAdapter;

    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> phones = new ArrayList<String>();
    ArrayList<String> messages = new ArrayList<String>();


    DBHelper db;
    FloatingActionButton fab;
    ListView lv;

    public OneFragment(){
        //Required empty constructor
    }

    

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.activity_one_fragment, container, false);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        lv = (ListView)rootView.findViewById(R.id.listView1);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), addContact.class);
                startActivity(intent);


            }
        });

        db = DBHelper.getInstance(getActivity());
        getUpdatedDBData();

      contactsAdapter = ContactsAdapter.getInstance(getActivity(),names,phones,messages);
        Log.d("oneFragment:","contactsAdapter" + contactsAdapter);
      lv.setAdapter(contactsAdapter);
      contactsAdapter.notifyDataSetChanged();

       lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

               AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

               builder.setTitle("Options");
               builder.setItems(options, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int which) {
                       DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){

                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                               switch (which){
                                   case DialogInterface.BUTTON_POSITIVE:
                                       Log.d("onClickListener:","position:" + position);
                                       Integer results;

                                       results=db.deleteContact(position+1);



                                      getUpdatedDBData();
                                       contactsAdapter = new ContactsAdapter(getActivity(),names,phones,messages);
                                       lv.setAdapter(contactsAdapter);

                                       break;
                                   case DialogInterface.BUTTON_NEGATIVE:
                                       break;
                               }
                           }
                       };

                       switch (which) {
                           case 0:
                               AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                               builder.setMessage("Are you sure you want to delete?").setPositiveButton("Yes",dialogClickListener).setNegativeButton("No",dialogClickListener).show();

                               break;
                           case 1:

                               Intent contacts = new Intent(getActivity(),addContact.class);
                               Log.d("1fragment->onItemClick","value of position" + position);
                               contacts.putExtra("number", ""+position);
                               startActivity(contacts);
                               break;


                       }
                   }
               });
               builder.show();

           }
       });

          contactsAdapter.notifyDataSetChanged();

        return rootView;
    }

    public void onStart(){
        super.onStart();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(!prefs.getBoolean("oneFragmentShowCase",false)) {
            new Utilities(getContext()).createShowCaseView(getActivity(), new ViewTarget(R.id.fab, getActivity()), "Add Contacts Button", "\n\nClick this button to add new contacts.\n\nThis will send emergency messages to the saved contacts during emergencies.\n\nOnce all the contacts are added shake to phone to send alerts.");
           SharedPreferences.Editor editor= prefs.edit();
           editor.putBoolean("oneFragmentShowCase",true);
            editor.commit();

        }
    }

   public void getUpdatedDBData(){
       names.clear();
       phones.clear();
       messages.clear();

       Log.d("OneFragment->db Object",db.toString());
       names=  db.getAllContacts(CONTACTS_COLUMN_NAME);
       Log.d("oneFragment:names",names.toString());
       phones= db.getAllContacts(CONTACTS_COLUMN_PHONE);
       Log.d("oneFragment:phones",phones.toString());
       messages=db.getAllContacts(CONTACTS_COLUMN_MESSAGE);
       Log.d("oneFragment:messages",messages.toString());
       ArrayList<String> id = new ArrayList<String>();
      id=db.getAllContacts(CONTACTS_COLUMN_ID);
       Log.d("oneFragment:ids",id.toString());


   }


   public void onResume(){
        super.onResume();
            Log.d("oneFragement->onResume","running");
            getUpdatedDBData();
            contactsAdapter = new ContactsAdapter(getActivity(),names,phones,messages);

            lv.setAdapter(contactsAdapter);

            Log.d("oneFragement->onResume"," names " + names + " phones " + phones + " messages " +messages);

    }

    }
