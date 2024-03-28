package festiplandroid.application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ConsultationFestivalActivity extends AppCompatActivity {

    private ListView listeFestivals;

    private ArrayList<HashMap<String,String>> festivals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultation_festival);

        listeFestivals = findViewById(R.id.listeFestival);

        festivals = new ArrayList<HashMap<String, String>>();

        /* Creation d'un festival fictif, remplacé par un vrai a terme */
        HashMap<String,String> mHashMap = new HashMap<>();
        mHashMap.put("nomCle","Festival Test 1");
        mHashMap.put("dateCle","aujourd'hui");
        mHashMap.put("categorieCle","SAE");
        mHashMap.put("descriptionCle","blablablabal");
        mHashMap.put("villeCle","Rodez");
        mHashMap.put("codePostalCle","12000");

        /* Ajout du festival à la liste */
        festivals.add(mHashMap);

        /* Laison données avec classe customListView */
        CustomListView customListView = new CustomListView(festivals,this);
        listeFestivals.setAdapter(customListView);

        listeFestivals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                // position contient l'indice de l'élément cliqué dans la ListView
                // Vous pouvez faire ce que vous voulez avec cet élément, par exemple afficher un Toast
                Toast.makeText(ConsultationFestivalActivity.this, "position : " + position, Toast.LENGTH_LONG).show();
            }
        });
    }
}