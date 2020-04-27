package com.example.clientapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.serverapplication.IMyAidlInterface;

import static com.example.clientapplication.MainActivity.LOG_TAG;

public class InvokeOp extends AppCompatActivity {
    private EditText num1Field;
    private EditText num2Field;
    private Button opButton;
    private TextView resultField;
    private OpServiceConnection opServiceConnection;
    private IMyAidlInterface opService;
    private static final String LOG_TAG = "InvokeOp";
    private String category;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        if (i != null) {
            // categories: [androidsrc.intent.category.ADD_PLUGIN, androidsrc.intent.category.MUL_PLUGIN]

            category = i.getStringExtra(MainActivity.BUNDLE_EXTRAS_CATEGORY);
        }
        setContentView(R.layout.serviceinvoker);


        num1Field = (EditText) findViewById(R.id.num1);
        num2Field = (EditText) findViewById(R.id.num2);

        resultField = (TextView) findViewById(R.id.result);

        opButton = (Button) findViewById(R.id.op);

        opButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                calculate();
            }
        });
        bindOpService();
    }
    public void onDestroy() {
        super.onDestroy();
        releaseOpService();
    }
    private void bindOpService() {
        if (category != null) {
            opServiceConnection = new OpServiceConnection();
            // public static final String ACTION_PICK_PLUGIN = "androidsrc.intent.action.PICK_PLUGIN";
           // Intent i = new Intent(MainActivity.ACTION_PICK_PLUGIN);
            Intent i = new Intent(IMyAidlInterface.class.getName());
            i.setAction("androidsrc.intent.action.PICK_PLUGIN");
            i.setPackage("com.example.serverapplication");
            i.addCategory(category); // 0/1 already picked..
            bindService(i, opServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }
    private void releaseOpService() {
        unbindService(opServiceConnection);
        opServiceConnection = null;
    }

    private void calculate() {
        String num1String = num1Field.getText().toString();
        String num2String = num2Field.getText().toString();
        String message = null;
        int result = 0;
        try {
            message = "Illegal number: '" + num1String + "'";
            int i1 = Integer.parseInt(num1String);
            message = "Illegal number: '" + num2String + "'";
            int i2 = Integer.parseInt(num2String);
            message = null;
            try {
                result = opService.operation(i1, i2);
            } catch (DeadObjectException ex) {
                Log.e(LOG_TAG, "DeadObjectException", ex);
                message = "Service error";
            } catch (RemoteException ex) {
                Log.e(LOG_TAG, "RemoteException", ex);
                message = "Service error";
            }
        } catch (NumberFormatException ex) {
        }
        if (message != null)
            resultField.setText(message);
        else {
            resultField.setText(Integer.toString(result));
        }
    }
    class OpServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className,
                                       IBinder boundService) {
            opService = IMyAidlInterface.Stub.asInterface((IBinder) boundService);
            Log.d(LOG_TAG, "onServiceConnected");
            opButton.setEnabled(true);
        }

        public void onServiceDisconnected(ComponentName className) {
            opService = null;
            Log.d(LOG_TAG, "onServiceDisconnected");
            opButton.setEnabled(false);
        }
    };

}
