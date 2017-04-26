package project.cse.anti;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.regex.Pattern;

import project.cse.anti.Utilities.Utilities;


public class addContact extends AppCompatActivity {

    private Switch messageSwitch;
    private TextView messageLabel;
    private Button saveButton;
    private EditText mName,mPhone,mMessage;
    private DBHelper mydb;
    private boolean defaultMessage=true;

    String num;

    Cursor cur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // inflating the edit texts
        mName=(EditText)findViewById(R.id.editName);
        mPhone=(EditText)findViewById(R.id.editPhone);
        mMessage=(EditText)findViewById(R.id.editMessage);
        saveButton=(Button)findViewById(R.id.button);
        // View are not yet created in this method
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mydb =DBHelper.getInstance(this);

        if(getIntent().hasExtra("number")){
            num = getIntent().getExtras().getString("number");
            Log.d("addContact->onCreate","num:"+num);
            Log.d("addContact->onCreate",mydb.getData(Integer.parseInt(num)).getColumnName(0).toString());
            Log.d("addContact->onCreate",mydb.getData(Integer.parseInt(num)).getColumnName(1).toString());
            Log.d("addContact->onCreate",mydb.getData(Integer.parseInt(num)).getColumnName(2).toString());
            Log.d("addContact->onCreate",mydb.getData(Integer.parseInt(num)).getColumnName(3).toString());
            cur=mydb.getData(Integer.parseInt(num)+1);
            cur.moveToNext();
            Log.d("addContact->onCreate","name:"+cur.getString(1));
            Log.d("addContact->onCreate","phone:"+cur.getString(2));
            Log.d("addContact->onCreate","message:"+cur.getString(3));


            mName.setText(cur.getString(1));
            mPhone.setText(cur.getString(2));
            mMessage.setText(cur.getString(3));
        }


        // To create the toolbar
        Toolbar myToolBar = (Toolbar) findViewById(R.id.addContactToolBar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Log.d("addContact->DB object:", mydb.toString());
        // Adding the heading to Activity
        final Activity activity = this;
        activity.setTitle("Add Contacts");

        // Switch action for default message and text field appearence
        messageSwitch = (Switch) findViewById(R.id.messageSwitch);
        messageSwitch.setChecked(true);

        final TextInputLayout message = (TextInputLayout) findViewById(R.id.textContainer2);
        messageLabel = (TextView)findViewById(R.id.emergencyMessage);



        messageSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(!isChecked){
                    message.setVisibility(View.VISIBLE);
                    mMessage.setVisibility(View.VISIBLE);
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
                Log.d("addContact->onClickList",""+mydb.numberOfRows());
                if (validate() && !(mydb.numberOfRows()>=5)) {


                    if(num==null){
                        saveInDB();
                    }
                    else{
                        Log.d("addContact->onClickList","value of num: " + num);
                        mydb.updateContact(Integer.parseInt(num)+1,mName.getText().toString(),mPhone.getText().toString(),mMessage.getText().toString());
                    }
                    finish();



                    }
                    else{
                    Toast.makeText(addContact.this, "You cannot add more than 5 contacts", Toast.LENGTH_SHORT).show();
                    finish();
                }

                }
        });




    }

    public void saveInDB(){
        Log.d("addContact->saveInDB():","running, defaultMessage: " +defaultMessage);
        String message;
        if(defaultMessage){
            message = getResources().getString(R.string.emergencyMessage);
            Log.d("addContact->saveInDB()","message: " + message);
        }else{
            message=mMessage.getText().toString();
        }
       mydb.insertContact(mName.getText().toString(),mPhone.getText().toString(),message);
       Log.d("addContact->saveInDB():", "Name" + mydb.getAllContacts("name"));
       Log.d("addContact->saveInDB():", "Phone" + mydb.getAllContacts("phone"));
        Log.d("addContact->saveInDB():", "Messages" + mydb.getAllContacts("message"));
    }



    public boolean validate(){
        Log.d("addContact->validate:","running");
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


