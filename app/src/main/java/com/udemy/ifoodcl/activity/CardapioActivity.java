package com.udemy.ifoodcl.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.udemy.ifoodcl.helper.RecyclerItemClickListener;
import com.udemy.ifoodcl.helper.UsuarioFirebase;
import com.udemy.ifoodcl.model.Empresa;
import com.udemy.ifoodcl.model.ItemPedido;
import com.udemy.ifoodcl.model.Pedido;
import com.udemy.ifoodcl.model.Produto;
import com.udemy.ifoodcl.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class CardapioActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutosCardapio;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio;
    private Empresa empresaSelecionada;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idEmpresa;
    private String idUsurioLogado;
    private Usuario usuario;
    private Pedido pedidoRecuperado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsurioLogado = UsuarioFirebase.getIdUsuario();

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

        recyclerProdutosCardapio.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerProdutosCardapio,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        confirmarQuantidade(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));

        recuperarProdutos();
        recuperarDadosUsuario();

    }

    private void confirmarQuantidade(final int posicao){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");

        EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");

        builder.setView(editQuantidade);

        builder.setPositiveButton("Confirmar", (dialogInterface, i) -> {
            String quantidade = editQuantidade.getText().toString();

            Produto produtoSelecionado = produtos.get(posicao);
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
            itemPedido.setNomeProduto(produtoSelecionado.getNome());
            itemPedido.setPreco(produtoSelecionado.getPreco());
            itemPedido.setQuantidade(Integer.parseInt(quantidade));
            itensCarrinho.add(itemPedido);

            if (pedidoRecuperado == null){
                pedidoRecuperado = new Pedido(idUsurioLogado, idEmpresa);
            }

            pedidoRecuperado.setNome(usuario.getNome());
            pedidoRecuperado.setEndereco(usuario.getEndereco());
            pedidoRecuperado.setItens(itensCarrinho);
            pedidoRecuperado.salvar();
        });

        builder.setNegativeButton("Cancelar", (dialogInterface, i) -> {

        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void recuperarDadosUsuario(){

        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsurioLogado);
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    usuario = snapshot.getValue(Usuario.class);
                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recuperarPedido() {



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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cardapio, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuPedido:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponentes(){

        recyclerProdutosCardapio = findViewById(R.id.recyclerProdutosCardapio);
        imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
        textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);

    }

}