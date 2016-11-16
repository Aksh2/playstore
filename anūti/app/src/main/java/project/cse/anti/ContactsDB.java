package project.cse.anti;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.UUID;

/**
 * Created by akshay on 27/3/16.
 */
@ParseClassName("ContactsDB")
public class ContactsDB extends ParseObject{

      /**
     * Created by akshay on 27/3/16.
     */

        public String getName(){
            return this.getString("Name");
        }

        public void setName(String name){
            put("Name",name);

        }

        public String getNumber(){
            return this.getString("Number");
        }

        public void setNumber(String number){
            put("Number",number);

        }

        public  String getMessage(){
            return this.getString("Message");
        }

        public void setMessage(String message){
            put("Message",message);

        }

        public void setUuidString(){
            UUID uuid = UUID.randomUUID();
            put("uuid",uuid.toString());
        }

        public void setLocation(ParseGeoPoint value)
        {
            put("Location",value);

        }

        public String getLocation(){
            return this.getString("xcord" + "ycord");
        }

        public String getUuidString(){
            return this.getString("uuid");
        }

        public ContactsDB getContactObject() { return this;}

        public static ParseQuery<ContactsDB> getQuery(){
            return ParseQuery.getQuery(ContactsDB.class);
        }

    }


