package festiplandroid.application;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ListView.FixedViewInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class DetailsFestival extends AppCompatActivity {

    private ArrayAdapter<String> adapterSpectacles;

    private ArrayAdapter<String> adapterScenes;

    private ArrayAdapter<String> adapterOrganisateurs;

    private ListView listSpectacles;

    private ListView listScenes;

    private ListView listOrganisateurs;

    private RequestQueue fileRequete;

    private TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailsfestival);

        test = findViewById(R.id.ville);
        appelleAPI();

        listSpectacles = findViewById(R.id.listeSpectacles);
        listScenes = findViewById(R.id.listeScenes);
        listOrganisateurs = findViewById(R.id.listeOrganisateurs);

        // valeur de test
        ArrayList<String> spectaclesTest = new ArrayList<>();
        ArrayList<String> scenesTest = new ArrayList<>();
        ArrayList<String> organisateursTest = new ArrayList<>();

        spectaclesTest.addAll(Arrays.asList("spectacle1","spectacle2", "spectacle3"));
        scenesTest.addAll(Arrays.asList("scene1", "scene2", "scene3"));
        organisateursTest.addAll(Arrays.asList("Lacam Samuel samuel.lacam@iut-rodez.fr",
                                               "Lemaire Thomas thomas.lemaire@iut-rodez.fr"));

        adapterSpectacles = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spectaclesTest);
        adapterScenes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scenesTest);
        adapterOrganisateurs = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, organisateursTest);

        listSpectacles.setAdapter(adapterSpectacles);
        listScenes.setAdapter((adapterScenes));
        listOrganisateurs.setAdapter(adapterOrganisateurs);

        listSpectacles.setScrollContainer(false);
        listScenes.setScrollContainer(false);
        listOrganisateurs.setScrollContainer(false);

//        ArrayList<TextView> spt = new ArrayList<>();
//        TextView sp1 = new TextView(this);
//        TextView sp2 = new TextView(this);
//        TextView sp3 = new TextView(this);
//        sp1.setText("spectacle 1&");
//        sp2.setText("spectacle 2&");
//        sp3.setText("spectacle 3&");
//        spt.add(sp1);
//        spt.add(sp2);
//        spt.add(sp3);
//        sp1.setTextColor(getColor(R.color.white));
//        ArrayAdapter<TextView> adpsp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spt);
//        listSpectacles.setAdapter(adpsp);
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
    public void appelleAPI() {
        // le titre saisi par l'utilisateur est récupéré et encodé en UTF-8

        // le titre du film est insésré dans l'URL de recherche du film
        String url = "http://10.0.2.2/sae-api/api/festiplandroid/api/infosfestival/1";
        /*
         * on crée une requête GET, paramètrée par l'url préparée ci-dessus,
         * Le résultat de cette requête sera une chaîne de caractères, donc la requête
         * est de type StringRequest
         */
        StringRequest requeteVolley = new StringRequest(Request.Method.GET, url,
                // écouteur de la réponse renvoyée par la requête
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String reponse) {
                        test.setText("Début de la réponse obtenue"
                                + reponse.substring(0, Math.min(400, reponse.length())));
                    }
                },
                // écouteur du retour de la requête si aucun résultat n'est renvoyé
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError erreur) {
//                        System.out.println("erreur : " + erreur.getMessage());
                        test.setText("aucun résultat : " + erreur.getMessage());
                    }
                });
        // la requête est placée dans la file d'attente des requêtes
        getFileRequete().add(requeteVolley);
    }

}
