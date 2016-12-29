package project.cse.anti;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.regex.Pattern;

import project.cse.anti.Utilities.Utilities;


public class addContact extends AppCompatActivity {

    private Switch messageSwitch;
    private TextView messageLabel;
    private Button saveButton;
    private EditText mName,mPhone,mMessage;
    
    private boolean defaultMessage=true;

    String num;
    ContactsDB contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(getIntent().hasExtra("number")){
            num = getIntent().getExtras().getString("number");

            ParseQuery<ContactsDB> editQuery = ContactsDB.getQuery();
            editQuery.fromLocalDatastore();
            editQuery.whereEqualTo("Number",num);
            editQuery.getFirstInBackground(new GetCallback<ContactsDB>() {
                @Override
                public void done(ContactsDB object, ParseException e) {
                    if(!isFinishing()){
                        contacts = object;

                        mName.setText(contacts.getName());
                        mPhone.setText(contacts.getNumber());
                        mMessage.setText(contacts.getMessage());

                        Log.i("contacts object:", String.valueOf(contacts.getName()));
                    }
                }
            });



        }




        setContentView(R.layout.activity_add_contact);
//        ActiveAndroid.initialize(this);

        // To create the toolbar
        Toolbar myToolBar = (Toolbar) findViewById(R.id.addContactToolBar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Adding the heading to Activity
        final Activity activity = this;
        activity.setTitle("Add Contacts");

        // Switch action for default message and text field appearence
        messageSwitch = (Switch) findViewById(R.id.messageSwitch);
        messageSwitch.setChecked(true);

        final EditText message = (EditText) findViewById(R.id.editMessage);
        messageLabel = (TextView)findViewById(R.id.emergencyMessage);



        messageSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(!isChecked){
                    message.setVisibility(View.VISIBLE);
                    messageLabel.setVisibility(View.GONE);
                    defaultMessage=false;

                }
                else
                {
                    message.setVisibility(View.GONE);
                    messageLabel.setVisibility(View.VISIBLE);
                    defaultMessage=true;

                }
            }


        });

        // Inflating the edit text views
        mName=(EditText)findViewById(R.id.editName);
        mPhone=(EditText)findViewById(R.id.editPhone);
        mMessage=(EditText)findViewById(R.id.editMessage);
        saveButton=(Button)findViewById(R.id.button);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(addContact.this);
        if(!prefs.getBoolean("addContactShowCase",false)) {

            new Utilities(getApplicationContext()).createShowCaseView(this,new ViewTarget(R.id.button,this),"Adding the Contacts","\nFill all the details and press the save button to add contacts.\nYou have the option of choosing between default and customised alert messages.");
            SharedPreferences.Editor editor= prefs.edit();
            editor.putBoolean("addContactShowCase",true);
            editor.commit();

        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate() ) {

                    if(num == null) {

                        ParseQuery<ContactsDB> query = ContactsDB.getQuery();
                        query.fromLocalDatastore();
                        query.whereNotEqualTo("Name"," ");
                        query.countInBackground(new CountCallback() {

                            public void done(int count, ParseException e) {
                                if(e==null){
                                    Log.d("Count DB entry","No. of entries "+count);
                                    if(count<5) {
                                        contacts = new ContactsDB();
                                        saveContacts();
                                    }
                                    else{
                                        Toast.makeText(addContact.this, "You can add only 5 contacts", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                }
                                else{
                                    Log.d("Count DB entry","Count not retrieve the db entries");
                                }



                            }
                        });


                    }
                    else{
                       saveContacts();

                    }








                }

                }
        });




    }





    public void saveContacts(){
        contacts.setName(mName.getText().toString());
        if (defaultMessage) {
            String Message = getResources().getString(R.string.emergencyMessage);
            contacts.setMessage(Message);
        } else {
            contacts.setMessage(mMessage.getText().toString());
        }
        contacts.setNumber(mPhone.getText().toString());
                        /* Displaying the save message as a toast.
                        Toast.makeText(getApplicationContext(), contacts.getMessage(), Toast.LENGTH_LONG).show();
                        */
        contacts.pinInBackground("SaveContacts", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (isFinishing()) {
                    return;
                }
                if (e == null) {
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error Saving:" + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

    }



    public boolean validate(){
        boolean valid = true;
        final String PHONE_RE = "\\d{10}";
        if(mPhone.getText().toString().isEmpty()||mPhone.getText().toString().length()<10||!Pattern.matches(PHONE_RE, mPhone.getText().toString())){
            mPhone.setError("Enter a valid 10 digit phone number excluding +91");
            valid= false;
        }
        else
        {
            mPhone.setError(null);
        }
        if(mName.getText().toString().isEmpty()){
            mName.setError("Please Enter a valid name");
            valid= false;
        }
        else
        {
            mName.setError(null);
        }


        return valid;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivityForResult(myIntent,0);
        return true;
    }

}


