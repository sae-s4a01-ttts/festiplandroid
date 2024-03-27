package festiplandroid.application;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> lanceurFille;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager gestionnaireConnexion =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo informationReseau = gestionnaireConnexion.getActiveNetworkInfo();

        if (informationReseau == null || ! informationReseau.isConnected()) {
            Toast.makeText(this, "non connecté à internet", Toast.LENGTH_LONG).show();
        } else {
            lanceurFille = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::methodeAExecuter);

            Intent intent = new Intent(MainActivity.this, DetailsFestival.class);
            lanceurFille.launch(intent);
        }
    }

    private void methodeAExecuter(ActivityResult resultat) {
        // on récupère l'intention envoyée par la fille
        Intent intent = resultat.getData();
        // si le code retour indique que tout est ok
    }
}