package festiplandroid.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ConsultationFestivalActivity extends AppCompatActivity {

    private ListView listeFestivals;

    private ArrayList<HashMap<String,String>> festivals;

    private RequestQueue fileRequete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultation_festival);

        appelAPI();
        Intent intention = getIntent();
        String idUserAuth = intention.getStringExtra(MainActivity.CLE_ID);

        Toast.makeText(this, idUserAuth, Toast.LENGTH_LONG).show();

        listeFestivals = findViewById(R.id.listeFestival);

        festivals = new ArrayList<HashMap<String, String>>();

        /* stub pour test*/
//        /* Creation d'un festival fictif, remplacé par un vrai a terme */
//        HashMap<String,String> mHashMap = new HashMap<>();
//        mHashMap.put("nomCle","Festival Test 1");
//        mHashMap.put("dateCle","aujourd'hui");
//        mHashMap.put("categorieCle","SAE");
//        mHashMap.put("descriptionCle","blablablabal");
//        mHashMap.put("villeCle","Rodez");
//        mHashMap.put("codePostalCle","12000");
//
//        /* Ajout du festival à la liste */
//        festivals.add(mHashMap);

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

    private RequestQueue getFileRequete() {
        if (fileRequete == null) {
            fileRequete = Volley.newRequestQueue(this);
        }
        // sinon
        return fileRequete;
    }

    private void appelAPI() {
        // le titre saisi par l'utilisateur est récupéré et encodé en UTF-8

        // le titre du film est insésré dans l'URL de recherche du film
        String url = "http://10.0.2.2/festiplandroid/api/listefestivals";
        /*
         * on crée une requête GET, paramètrée par l'url préparée ci-dessus,
         * Le résultat de cette requête sera une chaîne de caractères, donc la requête
         * est de type StringRequest
         */
        JsonArrayRequest requeteVolley = new JsonArrayRequest(Request.Method.GET, url, null,
                // écouteur de la réponse renvoyée par la requête
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray reponse) {
                        affichageInfos(reponse);
                    }
                },
                // écouteur du retour de la requête si aucun résultat n'est renvoyé
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError erreur) {
                        Toast.makeText(ConsultationFestivalActivity.this, R.string.erreurAPI, Toast.LENGTH_LONG).show();
                    }
                });
        // la requête est placée dans la file d'attente des requêtes
        getFileRequete().add(requeteVolley);
    }

    /**
     * Affichage des données de tous les festival
     *
     * @param donnees
     */
    private void affichageInfos(JSONArray donnees) {
            try {
                for (int i = 0; i < donnees.length(); i++) {
                    JSONObject festival = donnees.getJSONObject(i);
                    HashMap<String,String> mHashMap = new HashMap<>();
                    mHashMap.put("nomCle", festival.getString("nomFestival"));
                    mHashMap.put("dateCle", "Du " + festival.getString("dateDebutFestival")
                                            + " au " + festival.getString("dateFinFestival"));
                    /* STUB : categorie non envoyé */
                    mHashMap.put("categorieCle","STUB");
                    mHashMap.put("descriptionCle", festival.getString("descriptionFestival"));
                    mHashMap.put("villeCle", festival.getString("ville"));
                    mHashMap.put("codePostalCle", festival.getString("codePostal"));

                    festivals.add(mHashMap);
                }
            } catch (JSONException e) {
                Toast.makeText(ConsultationFestivalActivity.this, R.string.erreurJSON, Toast.LENGTH_LONG).show();
            }
//        Toast.makeText(ConsultationFestivalActivity.this, "ca marche :)", Toast.LENGTH_LONG).show();
        /* Laison données avec classe customListView */
        CustomListView customListView = new CustomListView(festivals,this);
        listeFestivals.setAdapter(customListView);
    }
}