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

/**
 * Implementação de AsyncTasks de acordo com as best practices para programação Android, para que
 * requisições conexão a serviços web sejam feitas em thread.
 */

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mRecyclerView = findViewById(R.id.my_recycler);

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mCidadeController = new TesteCidadeController();

        mRecyclerView.setAdapter(mCidadeController);

        mPbar = findViewById(R.id.pb_loading_indicator);

        mSearchEdit = findViewById(R.id.my_edit_search);

        mSearchEditEstado = findViewById(R.id.my_edit_search_estado);

        mCharParams = mSearchEdit.getText();

        mCharParamsEstado = mSearchEditEstado.getText();

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBuscaCidadesAsync();
                showHideKeyboard();
            }
        });
    }

    /**
     * Implementação de AsyncTask para solicitação de requests do método GET para o serviço de Busca de Todas as Cidades.
     */
    private class HttpRequestTask extends AsyncTask<Void, Void, TesteCidadeModel[]> {

        @Override
        protected void onPreExecute() {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mPbar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        /**
         * @return de objetos instanciados através do Template do Spring.
         */
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

    public void startBuscaCidadesAsync(){
        new HttpRequestTask().execute();
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


    /**
     * Implementação de AsyncTask para solicitação de requests do método Post para o serviço de Busca de Pontos.
     */
    private class PontosHttpRequestTask extends AsyncTask<TesteCidadeModel , Void, String> {

        /**
         * Override opcional do método onPreExecute com implementação de um dialog para informação do carregamento de dados, para melhor usabilidade do usuário.
         */
        @Override
        protected void onPreExecute() {
            dialogShow("Aguarde", "Carregando dados do Servidor...");
            super.onPreExecute();
        }

        /**
         * @return de um objeto String instanciado através do Template do Spring, passado como parâmetro para classe PostExecute
         */
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
        /**
         * Override do opcional método onPostExecute para um melhor feedback do usuário.
         */
        @Override
        protected void onPostExecute(String cidadePontos) {
            dialogCancel();
            if (cidadePontos.equals("")) {
                dialogShow("Pontos Cidade", cidadePontos);

            }else{
                dialogShow("Pontos Cidade", "Sem resposta do servidor");
            }

        }

    }

    /**
     * método para solicitação de execução de uma Thread de conexão no context da Activity.
     */
    public void startAsync(TesteCidadeModel cidade){
       new PontosHttpRequestTask().execute(cidade);
    }

    /**
     * Implementação do método para criação do AlertDialog principal da Activity.
     * @param titulo título do Alert Dialog
     * @param message mensagem do Alert Dialog
     * Style customizado definido no res/values/styles.xml
     */
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

    /**
     * Implementação de função para controle do Keyboard com o fim de escondê-lo.
     */
    public void showHideKeyboard(){
        if(imm.isActive()){
            imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
        }
    }

}
