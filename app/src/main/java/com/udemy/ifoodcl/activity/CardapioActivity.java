package com.udemy.ifoodcl.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.udemy.ifoodcl.R;
import com.udemy.ifoodcl.adapter.AdapterProduto;
import com.udemy.ifoodcl.helper.ConfiguracaoFirebase;
import com.udemy.ifoodcl.model.Empresa;
import com.udemy.ifoodcl.model.Produto;

import java.util.ArrayList;
import java.util.List;

public class CardapioActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutosCardapio;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio;
    private Empresa empresaSelecionada;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            empresaSelecionada = (Empresa) bundle.getSerializable("empresa");
            textNomeEmpresaCardapio.setText(empresaSelecionada.getNome());
            idEmpresa = empresaSelecionada.getIdUsuario();
            String url = empresaSelecionada.getUrlImagem();
            Picasso.get().load(url).into(imageEmpresaCardapio);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cardapio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerProdutosCardapio.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutosCardapio.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutosCardapio.setAdapter(adapterProduto);

        recuperarProdutos();

    }

    private void recuperarProdutos(){

        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child(idEmpresa);

        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtos.clear();

                for (DataSnapshot ds: snapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                adapterProduto.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarComponentes(){

        recyclerProdutosCardapio = findViewById(R.id.recyclerProdutosCardapio);
        imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
        textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);

    }

}