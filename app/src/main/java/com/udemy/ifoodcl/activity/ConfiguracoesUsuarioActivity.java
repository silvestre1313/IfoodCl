package com.udemy.ifoodcl.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.udemy.ifoodcl.R;
import com.udemy.ifoodcl.helper.ConfiguracaoFirebase;
import com.udemy.ifoodcl.helper.UsuarioFirebase;
import com.udemy.ifoodcl.model.Empresa;
import com.udemy.ifoodcl.model.Usuario;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

    private EditText editUsuarioNome, editUsuarioEndereco;
    private String idUsuario;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);

        inicializarComponentes();
        idUsuario = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações Usuario");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recuperarDadosUsuario();

    }

    private void recuperarDadosUsuario(){

        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuario);
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    editUsuarioNome.setText(usuario.getNome());
                    editUsuarioEndereco.setText(usuario.getEndereco());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void validarDadosUsuario(View view){

        String nome = editUsuarioNome.getText().toString();
        String endereco = editUsuarioEndereco.getText().toString();

        if (!nome.isEmpty()){
            if (!endereco.isEmpty()){

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(idUsuario);
                usuario.setNome(nome);
                usuario.setEndereco(endereco);
                usuario.salver();
                Toast.makeText(this, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show();
                finish();

            }else{
                Toast.makeText(this, "Digite um endereço para entrega", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Digite o seu nome", Toast.LENGTH_SHORT).show();
        }

    }

    private void inicializarComponentes(){

        editUsuarioEndereco = findViewById(R.id.editUsuarioEndereco);
        editUsuarioNome = findViewById(R.id.editUsuarioNome);

    }

}