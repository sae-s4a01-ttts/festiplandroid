package festiplandroid.application;

import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    // private static final String API_URL = "https://salon-eureka.fr/authentification";
    private static final String API_URL = "http://10.0.2.2/festiplandroid/api/authentification";
    // private static final String API_URL = "https://cat-fact.herokuapp.com/facts";

    private EditText identifiant;
    private EditText motDePasse;
    private Context context;
    private RequestQueue fileRequete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        identifiant = findViewById(R.id.inputId);
        motDePasse = findViewById(R.id.inputMdp);
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
            fileRequete = Volley.newRequestQueue(this, new GestionProxy());
        }
        // sinon
        return fileRequete;
    }

    public void clicSeConnecter(View bouton) {
        String authLog = identifiant.getText().toString();
        String authPwd = motDePasse.getText().toString();

        // Création de l'objet JSON contenant les informations d'authentification
        JSONObject postData = new JSONObject();
        try {
            postData.put("authLog", authLog);
            postData.put("authPwd", authPwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Création de la requête StringRequest en méthode POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Erreur de connexion: " + error.toString());
                Toast.makeText(context, "Erreur de connexion", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return postData.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        // Ajout de la requête à la file d'attente
        getFileRequete().add(stringRequest);
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