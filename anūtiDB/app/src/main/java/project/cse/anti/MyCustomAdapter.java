package project.cse.anti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by akshay on 13/3/16.
 */
public class MyCustomAdapter extends ArrayAdapter<String> {
    private Context mContext;
    String[] names;
    String[] numbers;



    public MyCustomAdapter(Context context,String[] names,String[] numbers ){
        super(context, R.layout.number_list, names);
        this.mContext=context;
        this.names=names;
       this.numbers=numbers;
    }

    public class ViewHolder{
        TextView namesTv;
        TextView numberTv;
    }

    public View getView(int position,View convertView, ViewGroup parent){
        if(convertView==null){
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.number_list,null);
        }


        final ViewHolder holder = new ViewHolder();

        holder.namesTv=(TextView) convertView.findViewById(R.id.phoneNameTv);
        holder.numberTv=(TextView) convertView.findViewById(R.id.phoneNumberTv);
        holder.namesTv.setText(names[position]);

        holder.numberTv.setText(numbers[position]);

        return  convertView;
    }


}

