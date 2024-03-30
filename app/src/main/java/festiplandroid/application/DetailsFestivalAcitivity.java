package festiplandroid.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class DetailsFestivalAcitivity extends AppCompatActivity {

    private String idFestivalInt;
    private String nomFestivalInt;
    private String categoriesFestivalInt;
    private String dateFestivalInt;
    private String descriptionFestivalInt;
    private String villeFestivalInt;
    private String cdpFestivalInt;

    private RequestQueue fileRequete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailsfestival);

        // recupération donné de l'intention
        Intent intention = getIntent();
        idFestivalInt = intention.getStringExtra("id_festival");
        nomFestivalInt = intention.getStringExtra("nom_festival");
        categoriesFestivalInt = intention.getStringExtra("categories_festival");
        dateFestivalInt = intention.getStringExtra("date_festival");
        descriptionFestivalInt = intention.getStringExtra("description_festival");
        villeFestivalInt = intention.getStringExtra("ville_festival");
        cdpFestivalInt = intention.getStringExtra("cdp_festival");

        // Appel de l'API et affichage des infos
        appelAPI(idFestivalInt);

        // affichage des donné de l'intention
        affichageInfosIntention();
    }

    /**
     * Renvoie la file d'attente pour les requêtes Web :
     * - si la file n'existe pas encore : elle est créée puis renvoyée
     * - si une file d'attente existe déjà : elle est renvoyée
     * On assure ainsi l'unicité de la file d'attente
     * @return RequestQueue une file d'attente pour les requêtes Volley
     */
    private RequestQueue getFileRequete() {
        if (fileRequete == null) {
            fileRequete = Volley.newRequestQueue(this);
        }
        // sinon
        return fileRequete;
    }

    /**
     * Gestion du clic sur le bouton rechercher
     * Une requête est envoyée au Web service pour rechercher le film saisi par l'utilisateur.
     * Le résultat de la requête est affiché en sous la forme d'une chaîne de caractères.
     * A défaut, c'est un message d'erreur qui est affiché
     */
    private void appelAPI(String idFestival) {
        // le titre saisi par l'utilisateur est récupéré et encodé en UTF-8

        // le titre du film est insésré dans l'URL de recherche du film
        String url = "http://10.0.2.2/festiplandroid/api/infosfestival/" + idFestival;
        /*
         * on crée une requête GET, paramètrée par l'url préparée ci-dessus,
         * Le résultat de cette requête sera une chaîne de caractères, donc la requête
         * est de type StringRequest
         */
        JsonObjectRequest requeteVolley = new JsonObjectRequest(Request.Method.GET, url, null,
                // écouteur de la réponse renvoyée par la requête
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject reponse) {
                        affichageInfosAPI(reponse);
                    }
                },
                // écouteur du retour de la requête si aucun résultat n'est renvoyé
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError erreur) {
                        Toast.makeText(DetailsFestivalAcitivity.this, R.string.erreurAPI, Toast.LENGTH_LONG).show();
                    }
                });
        // la requête est placée dans la file d'attente des requêtes
        getFileRequete().add(requeteVolley);
    }

    /**
     * Affecte les informations du festival
     * provenant de l'API aux TextView
     *
     * @param donnees donnees transmise par l'API
     */
    private void affichageInfosAPI(JSONObject donnees) {
        try {
            JSONArray spectacles = donnees.getJSONArray("spectacles");
            JSONObject responsable = donnees.getJSONObject("responsable");
            JSONArray organisateurs = donnees.getJSONArray("organisateurs");

            affichageSpectacles(spectacles);
            affichageResponsable(responsable);
            affichageOrganisateurs(organisateurs);
        } catch (JSONException e) {
            Toast.makeText(DetailsFestivalAcitivity.this, R.string.erreurJSON, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Affecte les informations du festival de l'intention
     * (de l'activité parente) aux TextView
     */
    private void affichageInfosIntention() {
        TextView nomFestival = findViewById(R.id.titreFestival);
        TextView categories = findViewById(R.id.categorie);
        TextView dates = findViewById(R.id.date);
        TextView ville = findViewById(R.id.ville);
        TextView codePostal = findViewById(R.id.codePostal);
        TextView description = findViewById(R.id.description);

        nomFestival.setText(nomFestivalInt);
        categories.setText(categoriesFestivalInt);
        dates.setText(dateFestivalInt);
        ville.setText(villeFestivalInt);
        codePostal.setText(cdpFestivalInt);
        description.setText(descriptionFestivalInt);
    }

    /**
     * Affichage des informations de spectacles aux TextView associé
     *
     * @param donneesSpectacles données à afficher
     */
    private void affichageSpectacles(JSONArray donneesSpectacles) {
        TextView spectacles = findViewById(R.id.spectacles);
        TextView durees = findViewById(R.id.dureeSpectacles);

        StringBuilder spectaclesString = new StringBuilder();
        StringBuilder dureesString = new StringBuilder();

        try {
            for (int i = 0; i < donneesSpectacles.length(); i++) {
                JSONObject spectacle = donneesSpectacles.getJSONObject(i);
                spectaclesString.append(spectacle.getString("titreSpectacle") + "\n");
                dureesString.append(spectacle.getString("dureeSpectacle") + "\n");
            }
            if (spectaclesString.length() > 0
                    && dureesString.length() > 0) {
                String affichageSpectacle
                        = spectaclesString.substring(0, spectaclesString.length() -1);
                String affichageDuree =
                        dureesString.substring(0, dureesString.length() -1);

                spectacles.setText(affichageSpectacle);
                durees.setText(affichageDuree);
            } else {
                spectacles.setText("non précisé");
            }
        } catch (JSONException e) {
            Toast.makeText(DetailsFestivalAcitivity.this, R.string.erreurJSON, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Affichage des informations des organisateurs
     * aux TextView associé
     *
     * @param donneesOrganisateurs données à afficher
     */
    private void affichageOrganisateurs(JSONArray donneesOrganisateurs) {
        TextView organisateurs = findViewById(R.id.organisateurs);
        TextView test = findViewById(R.id.labelOrganisateurs);

        StringBuilder organisateursString = new StringBuilder();
        try {
            for (int i = 0; i < donneesOrganisateurs.length(); i++) {
                JSONObject organisateur = donneesOrganisateurs.getJSONObject(i);
                organisateursString.append("- " + organisateur.getString("nomUser") + " "
                                           + organisateur.getString("prenomUser") + "\n  "
                                           + organisateur.get("emailUser") + "\n");
            }
//            test.setText(String.valueOf(organisateursString.length()));
            if (organisateursString.length() > 0) {
                String affichageOrganisateurs =
                        organisateursString.substring(0, organisateursString.length() -1);
                organisateurs.setText(affichageOrganisateurs);
            } else {
                organisateurs.setText("non précisé");
            }
        } catch (JSONException e) {
            Toast.makeText(DetailsFestivalAcitivity.this, R.string.erreurJSON, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Affichage des informations du responsable
     * aux TextView associé
     *
     * @param donneesResponsable données à afficher
     */
    private void affichageResponsable(JSONObject donneesResponsable) {
        TextView responsable = findViewById(R.id.responsable);

        StringBuilder responsableString = new StringBuilder();
        try {
            responsableString.append("- " + donneesResponsable.getString("nomUser") + " "
                                     + donneesResponsable.getString("prenomUser") + "\n  "
                                     + donneesResponsable.getString("emailUser"));
            responsable.setText(responsableString);
        } catch (JSONException e) {
            Toast.makeText(DetailsFestivalAcitivity.this, R.string.erreurJSON, Toast.LENGTH_LONG).show();
        }
    }
}
