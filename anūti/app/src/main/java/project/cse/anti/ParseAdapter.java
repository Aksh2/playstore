package project.cse.anti;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseQueryAdapter;

import org.w3c.dom.Text;

import project.cse.anti.ContactsDB;
/**
 * Created by akshay on 28/3/16.
 */
public class ParseAdapter extends ParseQueryAdapter<ContactsDB> {
    private ContactsDB storedContacts;

    public ParseAdapter(Context context,ParseQueryAdapter.QueryFactory<ContactsDB> queryFactory){
        super(context,queryFactory);
    }

    @Override
    public View getItemView(ContactsDB contact, View view, ViewGroup parent){
        ViewHolder holder = new ViewHolder();
                if(view==null){
                    LayoutInflater inflater= LayoutInflater.from(getContext());
                    view =inflater.inflate(R.layout.contacts_list,parent,false);
                    holder.mnameTv=(TextView)view.findViewById(R.id.contactName);
                    holder.mMessageTv=(TextView) view.findViewById(R.id.messageTv);
                    holder.mNumberTv=(TextView) view.findViewById(R.id.numberTv);
                    view.setTag(holder);
                }
                else{
                    holder =(ViewHolder) view.getTag();
                }
                //storedContacts = new ContactsDB();

                TextView contactName = holder.mnameTv;
                 contactName.setText(contact.getName());
                holder.mMessageTv.setText(contact.getMessage());
                holder.mNumberTv.setText(contact.getNumber());



        return view;
    }

    private static class ViewHolder{
        TextView mnameTv;
        TextView mMessageTv;
        TextView mNumberTv;
    }

}
