package festiplandroid.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

        Intent intention = getIntent();
        String idUserAuth = intention.getStringExtra(MainActivity.CLE_ID);

        Toast.makeText(this, idUserAuth, Toast.LENGTH_LONG).show();

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
    }
}