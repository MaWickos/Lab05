package pollub.ism.lab05;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // Derklaracja zmiennych
    private Button zapisz = null;
    private Button odczytaj = null;
    private EditText nazwaZapis = null;
    private EditText notatka = null;
    private Spinner nazwaCzytaj = null;

    // Zmienne przechowujące listy rozwijane
    private ArrayList<String> nazwyPlikow = null;
    private ArrayAdapter<String> adapterSpinera = null;

    // Zmienne do przechowania danych przy przechodzeniu z aktywności do aktywności
    private final String NAZWA_PREFERENCES = "Aplikacja do notatek";
    private final String KLUCZ_DO_PREFERENCES = "Zapisane nazwy plików";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pobranie identyfikatorów
        zapisz = (Button) findViewById(R.id.przyciskZapisz);
        odczytaj = (Button) findViewById(R.id.przyciskCzytaj);
        nazwaZapis = (EditText) findViewById(R.id.editTextNazwaZapisz);
        notatka = (EditText) findViewById(R.id.editTextNotatka);
        nazwaCzytaj = (Spinner) findViewById(R.id.spinnerNazwaCzytaj);


        // Utworzenie listenerów pod przyciski
        zapisz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zapisanieNotatki();
            }
        });

        odczytaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                odczytanieNotatki();
            }
        });
    }

    @Override
    protected void onPause() {
        zapiszSharePreferences();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Instrukcje do wykonania podczas przejścia cyklu aktywności
        nazwyPlikow = new ArrayList<>();
        adapterSpinera = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,nazwyPlikow);
        nazwaCzytaj.setAdapter(adapterSpinera);

        odczytajSharePreferences();
    }

    private void zapisanieNotatki(){

        String nazwaPliku = nazwaZapis.getText().toString();
        String informacja = "Udało sie zapisać";

        if(!zapiszDoPliku(nazwaPliku, notatka)){
            informacja = "Nie udało się zapisać";
        }

        // Toast na ekranie aplikacji
        Toast.makeText(this, informacja,Toast.LENGTH_SHORT).show();
    }

    private void odczytanieNotatki() {

        String nazwaPliku = nazwaCzytaj.getSelectedItem().toString();
        String informacja = "Udało się przeczytać";

        notatka.getText().clear();

        if(!odczytajZPliku(nazwaPliku,notatka)){
            informacja = "Nie udało się przeczytać";
        }

        // Toast na ekranie aplikacji
        Toast.makeText(this, informacja,Toast.LENGTH_SHORT).show();
    }

    private boolean zapiszDoPliku(String nazwaPliku, EditText poleEdycyjne) {

        boolean sukcess = true;

        // Uchwyty do katalogu i pliku
        File katalog = getApplicationContext().getExternalFilesDir(null);
        File plik = new File(katalog + File.separator + nazwaPliku);
        BufferedWriter zapisywacz = null;

        // Wyjątek związany z zapisem
        try {
            zapisywacz = new BufferedWriter(new FileWriter(plik));
            zapisywacz.write(poleEdycyjne.getText().toString());

            // Wyczyszczenie pól
            nazwaZapis.setText("");
            notatka.setText("");

        } catch (Exception e) {
            sukcess = false;
        } finally {
            try {
                zapisywacz.close();
            } catch (Exception e) {
                sukcess = false;
            }
        }

        // Komunikaty
        if(sukcess && !nazwyPlikow.contains(nazwaPliku)){
            nazwyPlikow.add(nazwaPliku);
            adapterSpinera.notifyDataSetChanged();


        }

        return sukcess;
    }

    private boolean odczytajZPliku(String nazwaPliku, EditText poleEdycyjne){
        boolean sukcess = true;

        File katalog = getApplicationContext().getExternalFilesDir(null);
        File plik = new File(katalog + File.separator + nazwaPliku);
        BufferedReader odczytywacz = null;

        // Odczytanie istniejącego pliku
        if(plik.exists()){

            // Wyjątek związany z odczytem pliku
            try{
                odczytywacz = new BufferedReader(new FileReader(plik));
                String linia = odczytywacz.readLine() + "\n";
                while (linia != null){
                    poleEdycyjne.getText().append(linia);
                    linia = odczytywacz.readLine();
                }
            }catch (Exception e){
                sukcess = false;
            }finally {
                if(odczytywacz!=null){
                    try{
                        odczytywacz.close();
                    }catch (Exception e){
                        sukcess = false;
                    }
                }
            }
        }

        return sukcess;
    }

    // Zapisanie stanu aplikacji
    private void zapiszSharePreferences(){

        SharedPreferences preferences = getSharedPreferences(NAZWA_PREFERENCES, MODE_PRIVATE);

        SharedPreferences.Editor edytor = preferences.edit();

        edytor.putStringSet(KLUCZ_DO_PREFERENCES, new HashSet<String>(nazwyPlikow));

        edytor.apply();
    }

    // Odczytanie stanu aplikacji
    private void odczytajSharePreferences() {

        SharedPreferences sh = getSharedPreferences(NAZWA_PREFERENCES, MODE_PRIVATE);
        Set<String> zapisaneNazwy = sh.getStringSet(KLUCZ_DO_PREFERENCES, null);

        if (zapisaneNazwy != null) {
            nazwyPlikow.clear();
            for (String nazwa : zapisaneNazwy) {
                nazwyPlikow.add(nazwa);
            }
            adapterSpinera.notifyDataSetChanged();
        }
    }


}