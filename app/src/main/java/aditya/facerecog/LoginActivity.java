package aditya.facerecog;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button button = (Button) findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AuthThread().run();
            }
        });
    }

    class AuthThread implements Runnable {

        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName("192.168.43.67");
                Log.d("ClientActivity", "C: Connecting...");
                Socket socket = new Socket(serverAddr, 5000);
                try {


                    /*File myFile = new File (filepath);
                    byte [] mybytearray  = new byte [(int)myFile.length()];
                    FileInputStream fis = new FileInputStream(myFile);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);
                    OutputStream os = socket.getOutputStream();
                    Log.d("ClientActivity", "C: Sending command.");
                    //System.out.println("Sending...");
                    os.write(mybytearray,0,mybytearray.length);
                    os.flush();*/

                    Log.d("ClientActivity", "C: Sending command.");
                    /*PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                                .getOutputStream())), true);*/
                    // WHERE YOU ISSUE THE COMMANDS

                    OutputStream output = socket.getOutputStream();
                    InputStream input = socket.getInputStream();
                    Log.d("ClientActivity", "C: image writing.");
                    final TextInputEditText usernameField = (TextInputEditText) findViewById(R.id.username);
                    final TextInputEditText passwordField = (TextInputEditText) findViewById(R.id.password);
                    String username = usernameField.getText().toString();
                    String password = passwordField.getText().toString();
                    String auth = username + " " + password;
                    output.write(auth.getBytes());
                    output.flush();
                    if (input.read() != 1) {
                        Toast.makeText(LoginActivity.this, "Please check username password", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Sucess", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    // out.println("Hey Server!");
                    Log.d("ClientActivity", "C: Sent.");
                    output.close();
                    input.close();
                } catch (Exception e) {
                    Log.e("ClientActivity", "S: Error", e);
                }

                socket.close();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
            }
        }
    }
}
