package com.adps.markintoch.ubidots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class Menu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        findViewById(R.id.Monitoreo).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Menu.this, MainActivity.class);
                startActivity(i);
            }
        });
        findViewById(R.id.Hum).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Menu.this, Humedad.class);
                startActivity(i);
            }
        });
        findViewById(R.id.Temp).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Menu.this, Temperatura.class);
                startActivity(i);
            }
        });
    }

}
