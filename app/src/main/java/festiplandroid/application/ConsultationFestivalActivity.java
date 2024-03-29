package festiplandroid.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import java.util.List;

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
            ArrayList<String> festivalDetails = new ArrayList<>();

            // Parcourir le JSONArray pour récupérer les détails de chaque festival
            for (int i = 0; i < donnees.length(); i++) {
                JSONObject festival = donnees.getJSONObject(i);
                String nom = festival.getString("nomFestival");
                String date = "Du " + festival.getString("dateDebutFestival") + " au " + festival.getString("dateFinFestival");
                /* STUB : categorie non envoyé */
                String categorie = "STUB";
                String description = festival.getString("descriptionFestival");
                String ville = festival.getString("ville");
                String codePostal = festival.getString("codePostal");

                // Concaténer les détails du festival
                String festivalInfo = nom + "\n" + date + "\n" + categorie + "\n" + description + "\n" + ville + "\n" + codePostal;

                // Ajouter les détails du festival à la liste
                festivalDetails.add(festivalInfo);
            }

            // Créer un adaptateur personnalisé pour utiliser le layout festival
            FestivalAdapter adapter = new FestivalAdapter(this, R.layout.festival, festivalDetails);

            // Associer l'adaptateur à la ListView
            listeFestivals.setAdapter(adapter);

            // Ajout d'un listener sur la liste pour gérer les clics sur les éléments
            listeFestivals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Récupérer les détails du festival sélectionné
                    String festivalInfo = festivalDetails.get(position);

                    // Rediriger vers DetailsFestivalActivity avec les détails du festival
                    Intent intent = new Intent(ConsultationFestivalActivity.this, festiplandroid.application.DetailsFestivalAcitivity.class);
                    intent.putExtra("festivalInfo", festivalInfo);
                    startActivity(intent);
                }
            });

        } catch (JSONException e) {
            Toast.makeText(ConsultationFestivalActivity.this, R.string.erreurJSON, Toast.LENGTH_LONG).show();
        }
    }

    private class FestivalAdapter extends ArrayAdapter<String> {
        private ArrayList<String> festivalDetails;
        private Context mContext;

        public FestivalAdapter(Context context, int resource, ArrayList<String> details) {
            super(context, resource, details);
            this.mContext = context;
            this.festivalDetails = details;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.festival, parent, false);

            // Trouver les vues dans le layout personnalisé
            TextView nomTextView = rowView.findViewById(R.id.titreFestival);
            TextView dateTextView = rowView.findViewById(R.id.date);
            TextView categorieTextView = rowView.findViewById(R.id.categorie);
            TextView descriptionTextView = rowView.findViewById(R.id.description);
            TextView villeTextView = rowView.findViewById(R.id.ville);
            TextView codePostalTextView = rowView.findViewById(R.id.codePostal);

            // Extraire les détails du festival pour la position actuelle
            String festivalInfo = festivalDetails.get(position);
            String[] detailsArray = festivalInfo.split("\n");

            // Afficher les détails dans les vues correspondantes
            nomTextView.setText(detailsArray[0]);
            dateTextView.setText(detailsArray[1]);
            categorieTextView.setText(detailsArray[2]);
            descriptionTextView.setText(detailsArray[3]);
            villeTextView.setText(detailsArray[4]);
            codePostalTextView.setText(detailsArray[5]);

            // Ajout d'un listener sur l'élément de la liste pour gérer les clics
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Récupérer les détails du festival sélectionné
                    String festivalInfo = festivalDetails.get(position);

                    // Afficher les détails du festival dans un Toast
                    Toast.makeText(mContext, festivalInfo, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(ConsultationFestivalActivity.this, festiplandroid.application.DetailsFestivalAcitivity.class);
                    intent.putExtra("festivalInfo", festivalInfo);
                    startActivity(intent);
                }
            });

            return rowView;
        }
    }
}