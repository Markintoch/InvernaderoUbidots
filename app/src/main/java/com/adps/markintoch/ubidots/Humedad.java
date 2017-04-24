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

public class Humedad extends AppCompatActivity {
    TextView humedad;
    TextView humedadTierra;
    Button riego;
    Button ventilacion;
    int lastValueVentilacion;
    int lastValueRiego;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humedad);
        ventilacion = (Button) findViewById(R.id.Ventilacion);
        riego = (Button) findViewById(R.id.Riego);
        humedad = (TextView) findViewById(R.id.Humedad2);
        humedadTierra = (TextView) findViewById(R.id.HumedadTierra);
    }

    @Override
    protected void onStart(){
        super.onStart();
        new UltimoValorVentilador().execute();
        new UltimoValorRociador().execute();
        new SensorHumedad2().execute();
        new SensorHumedadTierra().execute();
    }

    @Override
    protected void onResume(){
        super.onResume();
        botonVentiladorChange();
        botonRociadorChange();
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
            new SensorHumedad2().execute();
            new SensorHumedadTierra().execute();
        }
    }
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void botonRociadorChange(){
        riego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastValueRiego==0){
                    riego.setBackgroundColor(getResources().getColor(R.color.BotonRiego));
                    new Riego().execute(1);
                    lastValueRiego = 1;
                }else{
                    riego.setBackgroundColor(getResources().getColor(R.color.ColorPresionado));
                    new Riego().execute(0);
                    lastValueRiego = 0;
                }
            }
        });
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
                if(valorInicializador.equals("0.0")){ //Si el valor del led es 1 entonces esta encendido
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
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class SensorHumedad2 extends AsyncTask<Object, Object, Value[]>{
        private final String LLAVE_API = "fabc7bf006afb187c9c44595ff3fc1e035c5092d";//API KEY necesaria para entrar a tu perfil en Ubidots
        private final String SENSOR = "58f2d3ef76254254ab546f2a";//ID de la variable a utilizar

        protected Value[] doInBackground(Object... params){
            try{
                ApiClient ubidots= new ApiClient(LLAVE_API);//Objeto ApiClient donde como parametro recibe la llave a Ubidots
                Variable sensorHumedad = ubidots.getVariable(SENSOR);//Objeto sensorHumedad con el que se podra manipular el ID variable de Ubidots
                Value[] valorHumedad = sensorHumedad.getValues(); //De la variable del sensor trae los valores
                return valorHumedad; //Retorna el array de valores
            }catch(Exception e){
                return null; //Si algo sale mal con la conexion a Ubidots, retornara un Null
            }
        }

        protected void onPostExecute(Value[] valorHumedad){
            if(valorHumedad != null){
                String valorH = Double.toString(valorHumedad[0].getValue());
                humedad.setText(valorH+"%");
            }
            else{
                Toast.makeText(getApplicationContext(),"No hay conexion a internet", Toast.LENGTH_SHORT).show();
            }
        }

    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class UltimoValorRociador extends AsyncTask<Object,Object,Value[]> {
        private final String API_KEY = "fabc7bf006afb187c9c44595ff3fc1e035c5092d";//API KEY necesaria para entrar a tu perfil en Ubidots
        private final String Riego_ID= "58faa5757625420d872282c1";//ID de la variable a utilizar
        protected Value[] doInBackground(Object... params){
            try{
                ApiClient ultimoValor = new ApiClient(API_KEY);//Objeto ApiClient donde como parametro recibe la llave a Ubidots
                Variable ultimoValorRiego = ultimoValor.getVariable(Riego_ID); //Objeto ultimoValorLED con el que se podra manipular el ID variable de Ubidots
                Value[] valorRiego = ultimoValorRiego.getValues(); //De la variable LED trae los valores
                return valorRiego; //Retorna el array de valores del LED
            }catch(Exception e){
                return null;
            }
        }
        protected void onPostExecute(Value[] ultimoValor){
            if(ultimoValor != null){
                String valorInicializador = Double.toString(ultimoValor[0].getValue()); //El ultimo valor del LED lo pasa a String
                if(valorInicializador.equals("0.0")){ //Si el valor del led es 1 entonces esta encendido
                    riego.setBackgroundColor(getResources().getColor(R.color.ColorPresionado)); //El valor del Switch esta en ON
                }
                else {
                    riego.setBackgroundColor(getResources().getColor(R.color.BotonRiego)); //El valor del Switch esta en OFF
                }
            }else{
                Toast.makeText(getApplicationContext(),"No hay conexion a internet", Toast.LENGTH_SHORT).show();
            }
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class Riego extends AsyncTask<Integer, Void, Void> {
        private final String LLAVE_API = "fabc7bf006afb187c9c44595ff3fc1e035c5092d";//API KEY necesaria para entrar a tu perfil en Ubidots
        private final String Riego_ID= "58faa5757625420d872282c1";//ID de la variable a utilizar

        protected Void doInBackground(Integer... params) { //Recibe como parametros lo que se pongra en un .execute()
            try{
                ApiClient ubidots = new ApiClient(LLAVE_API); //Objeto ApiClient donde como parametro recibe la llave a Ubidots
                Variable riego = ubidots.getVariable(Riego_ID); //Objeto led con el que se podra manipular el ID variable de Ubidots
                riego.saveValue(params[0]); //saveValue es el valor que se enviara a esa variable en Ubidots
                //En este caso envia el primer paramtro introducido de tipo Integer, este parametro proviene de un .execute()
                return null;
            }catch (Exception e){
                return null;
            }
        }
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public class SensorHumedadTierra extends AsyncTask<Object, Object, Value[]>{
        private final String LLAVE_API = "fabc7bf006afb187c9c44595ff3fc1e035c5092d";//API KEY necesaria para entrar a tu perfil en Ubidots
        private final String SENSOR = "58faa55b7625420d8a494c52";//ID de la variable a utilizar

        protected Value[] doInBackground(Object... params){
            try{
                ApiClient ubidots= new ApiClient(LLAVE_API);//Objeto ApiClient donde como parametro recibe la llave a Ubidots
                Variable sensorHumedadTierra = ubidots.getVariable(SENSOR);//Objeto sensorHumedad con el que se podra manipular el ID variable de Ubidots
                Value[] valorHumedadTierra = sensorHumedadTierra.getValues(); //De la variable del sensor trae los valores
                return valorHumedadTierra; //Retorna el array de valores
            }catch(Exception e){
                return null; //Si algo sale mal con la conexion a Ubidots, retornara un Null
            }
        }

        protected void onPostExecute(Value[] valorHumedadTierra){
            if(valorHumedadTierra != null){
                String valorHT = Double.toString(valorHumedadTierra[0].getValue());
                humedadTierra.setText(valorHT+"%");
            }
            else{
                Toast.makeText(getApplicationContext(),"No hay conexion a internet", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
