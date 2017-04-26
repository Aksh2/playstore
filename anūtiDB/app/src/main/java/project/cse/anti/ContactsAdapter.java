package project.cse.anti;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by akshay on 2/1/17.
 */

public class ContactsAdapter extends ArrayAdapter<String> {

    private static ContactsAdapter adapter;
    private Context mContext;
    ArrayList<String> name;
    ArrayList<String> phone;
    ArrayList<String> message;
    public ContactsAdapter(Context context, ArrayList<String> name, ArrayList<String> phone, ArrayList<String> message){
        super(context,R.layout.contacts_list,name);
        this.mContext=context;
        this.name=name;
        this.phone=phone;
        this.message=message;
       Log.d("Contacts Adapter: ", "Names:" + name + "Phone:" + phone + "Message:" + message);
    }




    public View getView(int position,View convertView,ViewGroup parent){

        if(convertView==null){
            LayoutInflater inflater= LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.contacts_list,null);
        }

        final ViewHolder holder = new ViewHolder();



        holder.mnameTv=(TextView) convertView.findViewById(R.id.contactName);
        holder.mMessageTv=(TextView) convertView.findViewById(R.id.messageTv);
        holder.mNumberTv= (TextView) convertView.findViewById(R.id.numberTv);
        holder.mnameTv.setText(name.get(position));
        holder.mMessageTv.setText(message.get(position));
        holder.mNumberTv.setText(phone.get(position));



        return convertView;
    }

    public class ViewHolder{
        TextView mnameTv;
        TextView mMessageTv;
        TextView mNumberTv;
    }

    public static ContactsAdapter getInstance(Context context,ArrayList<String> name,ArrayList<String> phone,ArrayList<String> message){

        if(adapter==null) {
            adapter = new ContactsAdapter(context, name, phone, message);
        }
        return adapter;




    }


}
