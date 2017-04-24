package com.adps.markintoch.ubidots;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.ubidots.ApiClient;
import com.ubidots.Value;
import com.ubidots.Variable;
import java.util.Timer;
import java.util.TimerTask;

public class Temperatura extends AppCompatActivity {
    TextView temperatura;
    Button ventilacion;
    int lastValueVentilacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperatura);
        ventilacion = (Button) findViewById(R.id.Ventilacion2);
        temperatura = (TextView) findViewById(R.id.Temperatura2);

    }
    @Override
    protected void onStart(){
        super.onStart();
        new UltimoValorVentilador().execute();
        new SensorTemperatura().execute();
    }
    protected void onResume(){
        super.onResume();
        botonVentiladorChange();
        Timer correr = new Timer(); //Se crea un nuevo objeto tiempo
        correr.schedule(new SiempreCorriendoSensor(),0,1000); //Corre la clase del Sensor
    }

    @Override
    public void onBackPressed(){
        Intent i=new Intent(this,Menu.class);
        startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    class SiempreCorriendoSensor extends TimerTask {
        public void run(){
            new SensorTemperatura().execute();
        }
    }
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class UltimoValorVentilador extends AsyncTask<Object,Object,Value[]> {
        private final String API_KEY = "fabc7bf006afb187c9c44595ff3fc1e035c5092d";//API KEY necesaria para entrar a tu perfil en Ubidots
        private final String Ventilador_ID= "58b22866762542682f9e2c1e";//ID de la variable a utilizar
        protected Value[] doInBackground(Object... params){
            try{
                ApiClient ultimoValor = new ApiClient(API_KEY);//Objeto ApiClient donde como parametro recibe la llave a Ubidots
                Variable ultimoValorVentilador = ultimoValor.getVariable(Ventilador_ID); //Objeto ultimoValorLED con el que se podra manipular el ID variable de Ubidots
                Value[] valorVentilador = ultimoValorVentilador.getValues(); //De la variable LED trae los valores
                return valorVentilador; //Retorna el array de valores del LED
            }catch(Exception e){
                return null;
            }
        }
        protected void onPostExecute(Value[] ultimoValor){
            if(ultimoValor != null){
                String valorInicializador = Double.toString(ultimoValor[0].getValue()); //El ultimo valor del LED lo pasa a String
                if(valorInicializador.equals("0.0")){ //Si el valor del led es 0 entonces esta encendido
                    ventilacion.setBackgroundColor(getResources().getColor(R.color.ColorPresionado)); //El valor del Switch esta en ON
                }
                else {
                    ventilacion.setBackgroundColor(getResources().getColor(R.color.BotonVentilacion)); //El valor del Switch esta en OFF
                }
            }else{
                Toast.makeText(getApplicationContext(),"No hay conexion a internet", Toast.LENGTH_SHORT).show();
            }
        }
    }
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void botonVentiladorChange(){
        ventilacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastValueVentilacion==0){
                    ventilacion.setBackgroundColor(getResources().getColor(R.color.BotonVentilacion));
                    new Ventilador().execute(1);
                    lastValueVentilacion = 1;
                }else{
                    ventilacion.setBackgroundColor(getResources().getColor(R.color.ColorPresionado));
                    new Ventilador().execute(0);
                    lastValueVentilacion = 0;
                }
            }
        });
    }
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class Ventilador extends AsyncTask<Integer, Void, Void> {
        private final String LLAVE_API = "fabc7bf006afb187c9c44595ff3fc1e035c5092d";//API KEY necesaria para entrar a tu perfil en Ubidots
        private final String Ventilador_ID= "58b22866762542682f9e2c1e";//ID de la variable a utilizar

        protected Void doInBackground(Integer... params) { //Recibe como parametros lo que se pongra en un .execute()
            try{
                ApiClient ubidots = new ApiClient(LLAVE_API); //Objeto ApiClient donde como parametro recibe la llave a Ubidots
                Variable ventilador = ubidots.getVariable(Ventilador_ID); //Objeto led con el que se podra manipular el ID variable de Ubidots
                ventilador.saveValue(params[0]); //saveValue es el valor que se enviara a esa variable en Ubidots
                //En este caso envia el primer paramtro introducido de tipo Integer, este parametro proviene de un .execute()
                return null;
            }catch (Exception e){
                return null;
            }
        }
    }
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class SensorTemperatura extends AsyncTask<Object, Object, Value[]>{
        private final String LLAVE_API = "fabc7bf006afb187c9c44595ff3fc1e035c5092d";//API KEY necesaria para entrar a tu perfil en Ubidots
        private final String SENSOR = "58c1a1b47625422fa0ef0486";//ID de la variable a utilizar

        protected Value[] doInBackground(Object... params){
            try{
                ApiClient ubidots= new ApiClient(LLAVE_API);//Objeto ApiClient donde como parametro recibe la llave a Ubidots
                Variable sensorTemperatura = ubidots.getVariable(SENSOR);//Objeto sensorHumedad con el que se podra manipular el ID variable de Ubidots
                Value[] valorTemperatura = sensorTemperatura.getValues(); //De la variable del sensor trae los valores
                return valorTemperatura; //Retorna el array de valores
            }catch(Exception e){
                return null; //Si algo sale mal con la conexion a Ubidots, retornara un Null
            }
        }

        protected void onPostExecute(Value[] valorTemperatura){
            if(valorTemperatura != null){
                String valorT = Double.toString(valorTemperatura[0].getValue());
                temperatura.setText(valorT+"°C");
            }
            else{
                Toast.makeText(getApplicationContext(),"No hay conexion a internet", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
