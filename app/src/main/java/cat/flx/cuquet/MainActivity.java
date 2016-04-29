package cat.flx.cuquet;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
implements CuquetView.CuquetViewListener, SensorEventListener {

    private CuquetView cuquetView;
    private TextView tvScore, recordedScore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cuquetView = (CuquetView) findViewById(R.id.cuquetView);
        Button btnNewGame = (Button) findViewById(R.id.btnNewGame);
        tvScore = (TextView) findViewById(R.id.tvScore);
        recordedScore = (TextView) findViewById(R.id.BestScore);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        recordedScore.setText("Record: " + prefs.getString("Record", "0"));

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvScore.setText("0");
                recordedScore.setText("Record: " + prefs.getString("Record", "0"));
                cuquetView.newGame();

            }
        });
        cuquetView.setCuquetViewListener(this);
        initAccelerometer();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch(event.getKeyCode()) {
            case KeyEvent.KEYCODE_A: cuquetView.update(0, +10); break;
            case KeyEvent.KEYCODE_Q: cuquetView.update(0, -10); break;
            case KeyEvent.KEYCODE_O: cuquetView.update(-10, 0); break;
            case KeyEvent.KEYCODE_P: cuquetView.update(+10, 0); break;
        }
        return super.dispatchKeyEvent(event);
    }

    public void initAccelerometer() {
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor == null) {
            Toast.makeText(this, "No tenim acceleròmetre!!!", Toast.LENGTH_LONG).show();
            this.finish();
            return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    public void onPause() {
        super.onPause();
        sm.unregisterListener(this, sensor);
    }

    private SensorManager sm;
    private Sensor sensor;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float accelX = event.values[0];
        float accelY = event.values[1];
        cuquetView.update(-accelX, accelY);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void scoreUpdated(View view, int score) {
        tvScore.setText(Integer.toString(score));
    }

    @Override
    public void gameLost(View view) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Toast.makeText(this, "Has perdut!!!", Toast.LENGTH_LONG).show();
        recordedScore.setText("Record: " + prefs.getString("Record", "0"));
    }
}
