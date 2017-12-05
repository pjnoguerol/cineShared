package com.cineshared.pjnogegonzalez.cineshared.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.cineshared.pjnogegonzalez.cineshared.MainActivity;
import com.cineshared.pjnogegonzalez.cineshared.R;

public class ConversacionActivity extends AppCompatActivity {

    private Toolbar barraConversacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversacion);

        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");

        //Cargamos el action BAR
        barraConversacion = (Toolbar) findViewById(R.id.barraConversacion);
        setSupportActionBar(barraConversacion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chat - " + nombreUsuario);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ConversacionActivity.this, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
