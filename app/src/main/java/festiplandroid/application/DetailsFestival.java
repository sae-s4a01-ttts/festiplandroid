package festiplandroid.application;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class DetailsFestival extends AppCompatActivity {

    private ArrayAdapter<String> adapterSpectacles;

    private ArrayAdapter<String> adapterScenes;

    private ArrayAdapter<String> adapterOrganisateurs;

    private ListView listSpectacles;

    private ListView listScenes;

    private ListView listOrganisateurs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailsfestival);

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
}
