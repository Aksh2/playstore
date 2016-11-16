package project.cse.anti.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import project.cse.anti.MyCustomAdapter;
import project.cse.anti.ContactsDB;
import project.cse.anti.ParseAdapter;
import project.cse.anti.R;
import project.cse.anti.addContact;

import android.widget.AdapterView;
import android.widget.ListView;


import com.github.florent37.tutoshowcase.TutoShowcase;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;


public class OneFragment extends Fragment {


    // Layout Inflation for the parseAdapter
    private LayoutInflater inflater;
    private ParseQueryAdapter<ContactsDB> contactsAdapter;
    int EDIT_ACTIVITY_CODE=10;

    CharSequence options[] = new CharSequence[]{"Delete" , "Edit"};

    FloatingActionButton fab;
    ListView lv;

    public OneFragment(){
        //Required empty constructor
    }

    

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){

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

        TutoShowcase.from(getActivity())
                .setContentView(R.layout.activity_one_fragment)
                .on(R.id.fab)
                .addCircle()
                .withBorder()
                .show();






        ParseQueryAdapter.QueryFactory<ContactsDB> factory = new ParseQueryAdapter.QueryFactory<ContactsDB>(){
            public ParseQuery<ContactsDB> create() {
                ParseQuery<ContactsDB> query = ContactsDB.getQuery();
                query.orderByAscending("Name");
                query.fromLocalDatastore();
                return query;
            }
        };


        contactsAdapter = new ParseAdapter(getActivity(),factory);


        lv.setAdapter(contactsAdapter);

       lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        ContactsDB contacts;
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contacts = contactsAdapter.getItem(position);


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

                                       contacts.deleteEventually();
                                       contactsAdapter.loadObjects();
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
                               ContactsDB dbObject = contacts;
                               Intent contacts = new Intent(getActivity(),addContact.class);
                               contacts.putExtra("number", dbObject.getNumber());
                               startActivity(contacts);
                               break;


                       }
                   }
               });
               builder.show();

           }
       });
        contactsAdapter.loadObjects();

        return rootView;
    }


   public void onResume(){
        super.onResume();

            if(contactsAdapter == null)
                lv.setAdapter(contactsAdapter);
            else
               contactsAdapter.loadObjects();
    }


    }
















