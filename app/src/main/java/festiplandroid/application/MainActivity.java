package festiplandroid.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText identifiant;
    private EditText motDePasse;
    private Context context;
    private RequestQueue fileRequete;
    private TextView messageErreur;
    public static final String CLE_ID = "id de l'utilisateur";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        identifiant = findViewById(R.id.inputId);
        motDePasse = findViewById(R.id.inputMdp);
        messageErreur = findViewById(R.id.messageErreurAuth);
        context = this;

        // on vérifie si la connexion à Internet est possible
        ConnectivityManager gestionnaireConnexion =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo informationReseau = gestionnaireConnexion.getActiveNetworkInfo();
        if (informationReseau == null || ! informationReseau.isConnected()) {

            // problème de connexion réseau
            Toast.makeText(this,
                    "Hop hop hop la connexion n'est pas la !",
                    Toast.LENGTH_LONG).show();
        }
    }

    private RequestQueue getFileRequete() {
        if (fileRequete == null) {
            // notez en argument la présence d'un objet pour gérer le proxy
            fileRequete = Volley.newRequestQueue(this);
        }
        // sinon
        return fileRequete;
    }

    public void clicSeConnecter(View bouton) {
        String authLog = identifiant.getText().toString();
        String authPwd = motDePasse.getText().toString();

        JSONObject postData = new JSONObject();
        try {
            postData.put("authLog", authLog);
            postData.put("authPwd", authPwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "http://10.0.2.2/SAE/festiplandroid/api/authentification";

        appelleAPI(url, postData);
    }

    public void appelleAPI(String url, JSONObject postData) {
        // Création de la requête POST
        JsonObjectRequest requeteVolley = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)  {
                        // Obtention de la valeur de l'objet "id" en tant que chaîne de caractères
                        try {
                            String id = response.getString("id");
                            authentificationReussie(id);
                            messageErreur.setText("");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Gestion des erreurs
                        messageErreur.setText(R.string.err_id_mdp_incorrect);
                    }
                });

        // Ajout de la requête à la file d'attente
        getFileRequete().add(requeteVolley);
    }

    private void authentificationReussie(String id){
        Intent intention = new Intent(this, ConsultationFestivalActivity.class);
        intention.putExtra(CLE_ID, id);
        startActivity(intention);
    }

    /**
     * Méthode exécutée automatiquement lorsque l'on clique sur le bouton
     * "Mot de passe oublié"
     * @param bouton bouton sur lequel l'utilisateur a cliqué
     */
    public void clicMdpOublie(View bouton) {
        Toast.makeText(this, R.string.message_toast_pas_non_trouvee, Toast.LENGTH_LONG)
                .show();
    }

    /**
     * Méthode exécutée automatiquement lorsque l'on clique sur le bouton
     * "Identifiant oublié"
     * @param bouton bouton sur lequel l'utilisateur a cliqué
     */
    public void clicIdOublie(View bouton) {
        Toast.makeText(this, R.string.message_toast_pas_non_trouvee, Toast.LENGTH_LONG)
                .show();
    }
}
