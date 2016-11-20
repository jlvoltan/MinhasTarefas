package com.jlnv.minhastarefas;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText tarefaTexto;
    private Button botaoAdicionar;
    private ListView listaTarefas;
    private SQLiteDatabase bancoDados;

    //Para usar com o listView
    private ArrayAdapter<String> itensAdapt;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            //Associando componentes
            tarefaTexto = (EditText) findViewById(R.id.tarefaTexto);
            botaoAdicionar = (Button) findViewById(R.id.botaoAdicionar);
            listaTarefas = (ListView) findViewById(R.id.listaTarefas);

            //Criando o BD
            bancoDados = openOrCreateDatabase("BancoTarefas", MODE_PRIVATE, null);
            //Criando Tabelas
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tabelaTarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, nome VARCHAR)");
            //Queremos atalizar nossa relação de tarefas
            //Teste de inclusao ****   bancoDados.execSQL("INSERT INTO tabelaTarefas(nome) VALUES (' Va e faça')");
            RecuperarTarefas();

            listaTarefas.setLongClickable(true);
            listaTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    removerTarefa( ids.get( position ) );
                    return true;
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //BOTÃO ADICIONAR
    public void adicionar(View v) {
        String novaTarefa = tarefaTexto.getText().toString();
        if (novaTarefa.equals("")) {
            Toast.makeText(MainActivity.this, "Digite a tarefa a ser adicionada", Toast.LENGTH_SHORT).show();
        } else {
            bancoDados.execSQL("INSERT INTO tabelaTarefas (nome) VALUES ('" + novaTarefa + "')");
            Toast.makeText(MainActivity.this, "Tarefa Adicionada com sucesso!", Toast.LENGTH_SHORT).show();

            //Depois de Acrescentar, ueremos atualizar a relaçao
            RecuperarTarefas();
            tarefaTexto.setText("");
        }
    }
    private void RecuperarTarefas(){
        try{
            //Recuperando os dados usando um cursor
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tabelaTarefas ORDER BY id DESC",null);
            //Recupernado ID colunas
            int idColunaID = cursor.getColumnIndex("id");
            int idColunaNome = cursor.getColumnIndex("nome");



            //Usando o listView e o adaptador
            itens= new ArrayList<String>();
            ids=new ArrayList<Integer>();
            itensAdapt = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_2, android.R.id.text2,itens);

            listaTarefas.setAdapter(itensAdapt);
            //Voltar cursor
            cursor.moveToFirst();

            while(cursor != null){
                Log.i("Resultado -","Nome Tarefa:" + cursor.getString(idColunaNome));
                itens.add(cursor.getString(idColunaNome));
                //De modo similar guardamos os ids, mas convertemos string para int
                ids.add(Integer.parseInt(cursor.getString(idColunaID)));
                cursor.moveToNext();
            }
        }catch(Exception e){e.printStackTrace();}
    }

    private void removerTarefa(Integer idSelecionado){
        try{
            bancoDados.execSQL("DELETE FROM tabelaTarefas where id="+idSelecionado);
            RecuperarTarefas();
            Toast.makeText(MainActivity.this, "Tarefa Removida com sucesso!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){e.printStackTrace();}


    }
}
