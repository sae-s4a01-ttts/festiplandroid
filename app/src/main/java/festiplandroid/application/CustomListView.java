package festiplandroid.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomListView extends BaseAdapter {
    private ArrayList<HashMap<String,String>> list;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public CustomListView(ArrayList<HashMap<String,String>> list,Context c) {
        this.list = list;
        this.mContext = c;
        mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View view = mLayoutInflater.inflate(R.layout.festival,parent,false);
        Festival festival = new Festival();

        // set id's
        festival.nom = (TextView)(view.findViewById(R.id.titreFestival));
        festival.date = (TextView)(view.findViewById(R.id.date));
        festival.categorie = (TextView)(view.findViewById(R.id.categorie));
        festival.description = (TextView)(view.findViewById(R.id.description));
        festival.image = (ImageView)(view.findViewById(R.id.imageFestival));
        festival.ville = (TextView)(view.findViewById(R.id.ville));
        festival.codePostal = (TextView)(view.findViewById(R.id.codePostal));

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap = list.get(position);

        festival.nom.setText(hashMap.get("nomCle"));
        festival.date.setText(hashMap.get("dateCle"));
        festival.categorie.setText(hashMap.get("categorieCle"));
        festival.description.setText(hashMap.get("descriptionCle"));
        festival.image.setImageResource(R.drawable.logofestiplan);
        festival.ville.setText(hashMap.get("villeCle"));
        festival.codePostal.setText(hashMap.get("codePostalCle"));

        return view;
    }


    private class Festival
    {
        TextView nom;
        TextView date;
        TextView categorie;
        TextView description;
        ImageView image;
        TextView ville;
        TextView codePostal;
    }
}
