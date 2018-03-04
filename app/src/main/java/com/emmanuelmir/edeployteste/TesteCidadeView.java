package com.emmanuelmir.edeployteste;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class TesteCidadeView extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TesteCidadeController mCidadeController;
    private LinearLayoutManager layoutManager;
    private ProgressBar mPbar;
    private EditText mSearchEdit;
    private EditText mSearchEditEstado;
    private CharSequence mCharParams;
    private CharSequence mCharParamsEstado;
    private AlertDialog.Builder mDialogBuilder;
    private AlertDialog mDialog;
    private InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste_cidade_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler);

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mCidadeController = new TesteCidadeController();

        mRecyclerView.setAdapter(mCidadeController);

        mPbar = (ProgressBar)findViewById(R.id.pb_loading_indicator);

        mSearchEdit = (EditText) findViewById(R.id.my_edit_search);

        mSearchEditEstado = (EditText)findViewById(R.id.my_edit_search_estado);

        mCharParams = mSearchEdit.getText();

        mCharParamsEstado = mSearchEditEstado.getText();

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HttpRequestTask().execute();
                showHideKeyboard();
            }
        });
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, TesteCidadeModel[]> {

        @Override
        protected void onPreExecute() {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mPbar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected TesteCidadeModel[] doInBackground(Void... params) {
            try {
                final String url = "http://wsteste.devedp.com.br/Master/CidadeServico.svc/rest/BuscaTodasCidades";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                TesteCidadeModel[] testeCidadeModel;
                testeCidadeModel = restTemplate.getForObject(url, TesteCidadeModel[].class);
                return testeCidadeModel;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(TesteCidadeModel[] cidadeData) {
            showCidadeData();
            if (cidadeData != null) {
                mCidadeController.setmCidadesData(cidadeData , mCharParams, mCharParamsEstado);
            }
            dialogShow("teste","Async concluída");

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teste_cidade_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showCidadeData(){
        mPbar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public class PontosHttpRequestTask extends AsyncTask<TesteCidadeModel , Void, String> {
        @Override
        protected void onPreExecute() {
            dialogShow("Aguarde", "Carregando dados do Servidor...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(TesteCidadeModel... mHolderCidade) {
            try {
                final String url = "http://wsteste.devedp.com.br/Master/CidadeServico.svc/rest/BuscaPontos";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                String pontosCidade;
                pontosCidade = restTemplate.postForObject(url,mHolderCidade[0],String.class);
                return pontosCidade;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String cidadePontos) {
            dialogCancel();
            if (cidadePontos != "") {
                dialogShow("Pontos Cidade", cidadePontos);

            }else{
                dialogShow("Pontos Cidade", "Sem resposta do servidor");
            }

        }

    }

    public void startAsync(TesteCidadeModel cidade){
       new PontosHttpRequestTask().execute(cidade);
    }

    public void dialogShow(String titulo, String message){
        mDialogBuilder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        mDialogBuilder.setTitle(titulo);
        mDialogBuilder.setMessage(message);
        mDialogBuilder.setPositiveButton("OK", null);
        mDialogBuilder.setNegativeButton("Cancel", null);
        mDialog = mDialogBuilder.create();
        mDialog.show();
    }
    public void dialogCancel(){
        mDialog.cancel();
    }

    public void showHideKeyboard(){
        if(imm.isActive()){
            imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
        }
    }

}
