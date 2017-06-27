package com.example.lenovot550.downloadviewapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovot550.downloadviewapp.rest.ApiClient;
import com.example.lenovot550.downloadviewapp.rest.ApiInterface;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // @LOG_TAG log tagui priskiriamas klases pavadinimas
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    // @API_KEY api raktas is duoto linko
    private final static String API_KEY = "a87e23b864a045e0978012524e2d8eef";
    private ProgressBar progressBar;
    private TextView textView;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Surandami visi Views layout'e
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);  //textView
        Button downloadButton = (Button) findViewById(R.id.download);
        Button readButton = (Button) findViewById(R.id.read);
        downloadButtonClick(downloadButton);
        readButtonClick(readButton);
    }

    private void readButtonClick(Button readButton) {
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Persiskaito preferences reiksmes kuriu key - status ir source ir priskiria jas
                // @status, @source
                SharedPreferences settings =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String status, source;
                status = settings.getString("status", "");
                source = settings.getString("source", "");
                if (!status.isEmpty() && !source.isEmpty()) {
                    textView.setText(getString(R.string.statusPrintToTextView) + " " +
                            status + "/n" + getString(R.string.SourcePrintToTextView) + " " + source);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.waitForDownloadFinish, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void downloadButtonClick(Button downloadButton) {
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (API_KEY.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.apiKeyMissing, Toast.LENGTH_SHORT).show();
                    return;

                }
                if (isInternetConnected()) {
                    progressBar.setVisibility(View.VISIBLE);
                    // Retrofit bibliotekos magija
                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    Call<ArticleResponse> call = apiService.getStatusAndSource(API_KEY);
                    call.enqueue(new Callback<ArticleResponse>() {
                        // @onResponse ivykdomas kai baigiama GET uzklausa.
                        // @param response turi duomenis gautus is uzklausos.
                        @Override
                        public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                            progressBar.setVisibility(View.GONE);
                            // Tikrinamas HTTP response kodas
                            // Visi kodai - https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
                            if (response.code() == HttpsURLConnection.HTTP_OK) {
                                // I preferences irasomos reiksmes gautos is uzklausos.
                                // Reiksmes i preferences irasomos uzmapinant kartu su raktais
                                // (source ir status), kad butu galima veliau gauti tas pacias
                                // reiksmes.
                                textView.setText(R.string.downloadFinished);
                                SharedPreferences settings = PreferenceManager.
                                        getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("status", response.body().getStatus());
                                editor.putString("source", response.body().getSource());
                                editor.apply();
                            }
                            // Jei HTTP response kodas blogas Log'e jis bus parodomas,
                            // pvz MainActivity: Response code of connection : 401.
                            // Vartotojui bus parodomas labiau user friendly Toast pranesimas
                            else {
                                Log.e(LOG_TAG, String.format
                                        (getString(R.string.connectionResponseCode),
                                                response.code()));
                                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                            }
                        }

                        // Sitas vykdomas kai GET uzklausos nepavyksta ivykdyt :?
                        @Override
                        public void onFailure(Call<ArticleResponse> call, Throwable t) {
                            Log.e(LOG_TAG, t.toString());
                        }
                    });
                } else
                    textView.setText(R.string.checkInternet);
            }
        });
    }

    private boolean isInternetConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
