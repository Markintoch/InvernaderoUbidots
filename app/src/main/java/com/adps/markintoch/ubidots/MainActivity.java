package com.adps.markintoch.ubidots;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;
import com.ubidots.ApiClient;
import com.ubidots.Value;
import com.ubidots.Variable;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public TextView ventiladorStatus; //TextView que muesta el estado del Ventilador
    public Switch ventiladorControl;  //Switch que controla el estado del Ventilador
    public TextView sensorTemperatura; // TextView que muestra el valor del Sensor de Temperatura
    public TextView sensorHumedad; // TextView que muestra el valor del Sensor de Humedad
    // Constantes String que tiene valores de encendidos y apagados por default
    String VentiladorON = "El Ventilador esta encendido"; //Constante ON
    String VentiladorOFF = "El Ventilador esta apagado"; //Constante OFF
    boolean internetStatus = true;//Booleano que indica si existe una conexion a internet o no

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorTemperatura = (TextView) findViewById(R.id.SensorTemperatura); //Se ubica por ID del XML el TextView a utilizar
        sensorHumedad = (TextView) findViewById(R.id.SensorHumedad); //Se ubica por ID del XML el Switch a utilizar
        ventiladorControl = (Switch) findViewById(R.id.Ventilador); //Se ubica por ID del XML el Switch a utilizar
        ventiladorStatus = (TextView) findViewById(R.id.VentiladorStatus); //Ubica el ID del XML del TextView que mostrara el valor del sensor
    }

    @Override
    protected void onStart(){
        super.onStart();
        new UltimoValorVentilador().execute(); // Funcion que inicializa el switch de acuerdo al valor actual del Ventilador
        new SensorHumedad().execute();
        new SensorTemperatura().execute();
        VentiladorChange();
    }
    @Override
    protected void onResume(){
        super.onResume();
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
            new SensorHumedad().execute();
            new SensorTemperatura().execute();//Se ejecuta la clase SensorHumedad
        }
    }
//------------------------------------------------------------------------------------------------------
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
                    ventiladorControl.setChecked(true); //El valor del Switch esta en ON
                    ventiladorStatus.setText(VentiladorON); //Despliega el mensaje que el led esta encendido
                    internetStatus = true; //Al obtener el ultimo valor exitosamente, la conexion a internet con Ubidots es verdadera
                }
                else {
                    ventiladorControl.setChecked(false); //El valor del Switch esta en OFF
                    ventiladorStatus.setText(VentiladorOFF); //Despliega el mensaje que el led esta apagado
                    internetStatus = true; //Al obtener el ultimo valor exitosamente, la conexion a internet con Ubidots es verdadera
                }
            }else{
                ventiladorStatus.setText("Error conexion con ubidots");
                Toast.makeText(getApplicationContext(),"No hay conexion a internet", Toast.LENGTH_SHORT).show();
            }
        }
    }
//------------------------------------------------------------------------------------------------------------------------------------
    public class SensorHumedad extends AsyncTask<Object, Object, Value[]>{
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
                sensorHumedad.setText(valorH+"%");
            }
            else{
                Toast.makeText(getApplicationContext(),"No hay conexion a internet", Toast.LENGTH_SHORT).show();
            }
        }

    }
 //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
                sensorTemperatura.setText(valorT+"°C");
            }
            else{
                Toast.makeText(getApplicationContext(),"No hay conexion a internet", Toast.LENGTH_SHORT).show();
            }
        }

    }
//--------------------------------------------------------------------------------------------------------------------------------------
    public void VentiladorChange (){//Se inicializa el estado del Switch
        ventiladorControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    new Ventilador().execute(0);//Conexion a ubidots, envia como parametro un 1, que el led esta encendido
                    if(internetStatus){ //Se comprueba el estado de la conexion a internet
                        ventiladorStatus.setText(VentiladorON); //Despliega el mensaje que el led esta encendido
                }else{ //En caso de no haber conexion a internet, despliega un SnackBar
                    Toast.makeText(getApplicationContext(),"No hay conexion a internet", Toast.LENGTH_SHORT).show();
                    ventiladorStatus.setText(VentiladorOFF); //Despliega el mensaje que el led esta apagado
                    ventiladorControl.setChecked(false); //El switch se mantiene en el estado anterior
                }
            }else{
                new Ventilador().execute(1);//Conexion a ubidots, envia como parametro un 0, que el led esta apagado
                if(internetStatus){ //Se comprueba el estado de la conexion a internet
                    ventiladorStatus.setText(VentiladorOFF); //Despliega el mensaje que el led esta apagado
                }else{//En caso de no haber conexion a internet, despliega un SnackBar
                    Toast.makeText(getApplicationContext(),"No hay conexion a internet", Toast.LENGTH_SHORT).show();
                    ventiladorStatus.setText(VentiladorON); //Despliega el mensaje que el led esta encendido
                    ventiladorControl.setChecked(true); //El switch se mantiene en el estado anterior
                    }
                }
            }
        });
    }
//----------------------------------------------------------------------------------------------------------------------------------
    public class Ventilador extends AsyncTask<Integer, Void, Boolean>{
        private final String LLAVE_API = "fabc7bf006afb187c9c44595ff3fc1e035c5092d";//API KEY necesaria para entrar a tu perfil en Ubidots
        private final String Ventilador_ID= "58b22866762542682f9e2c1e";//ID de la variable a utilizar

        protected Boolean doInBackground(Integer... params) { //Recibe como parametros lo que se pongra en un .execute()
            try{
                ApiClient ubidots = new ApiClient(LLAVE_API); //Objeto ApiClient donde como parametro recibe la llave a Ubidots
                Variable ventilador = ubidots.getVariable(Ventilador_ID); //Objeto led con el que se podra manipular el ID variable de Ubidots
                ventilador.saveValue(params[0]); //saveValue es el valor que se enviara a esa variable en Ubidots
                //En este caso envia el primer paramtro introducido de tipo Integer, este parametro proviene de un .execute()
                return true; //Retorna True si existe una conexion a Internet con Ubidots
            }catch (Exception e){
                return false; //Retorna False si no existe una conexion a Internet con Ubidots
                }
         }
        protected void onPostExecute(Boolean estado){ //Recibe como parametro el boolean de la conexion a ubidots
            internetStatus = estado; //La variable internetStatus toma el valor del boolean de la corrida a la conexion ubidots
        }
    }
}
