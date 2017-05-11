package aditya.facerecog;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class NetworkingFragment extends Fragment implements NetworkUplinkContract, NetworkDownlinkContract {

  private Handler mNetworkingHandler;

  public NetworkingFragment() {
    // Required empty public constructor
  }

  public static NetworkingFragment newInstance() {
    NetworkingFragment fragment = new NetworkingFragment();
    Bundle args = new Bundle();

    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);

    final Handler mainHandler = new Handler(Looper.getMainLooper());

    HandlerThread handlerThread = new HandlerThread("NetworkingHandler") {
      private Socket socket;
      private PrintWriter output;
      private BufferedReader input;

      @Override
      protected void onLooperPrepared() {
        super.onLooperPrepared();

        mNetworkingHandler = getNetworkingHandler(this);

        Log.d("ClientActivity", "C: Connecting...");
        try {
          InetAddress serverAddr = InetAddress.getByName("192.168.43.67");
          socket = new Socket(serverAddr, 5000);
          output = new PrintWriter(socket.getOutputStream());
          input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
          mainHandler.post(getIOExceptionRunnable());
          e.printStackTrace();
          quit();
          try {
            if (socket != null) socket.close();
            if (output != null) output.close();
            if (input != null) input.close();
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      }

      @NonNull
      private Runnable getIOExceptionRunnable() {
        return new Runnable() {
          @Override
          public void run() {
            ioException();
          }
        };
      }

      @NonNull
      private Handler getNetworkingHandler(final HandlerThread handlerThread) {
        return new Handler(handlerThread.getLooper()) {
          @Override
          public void handleMessage(Message msg) {
            if (msg.what == -1) {
              try {
                if (socket != null) socket.close();
                if (output != null) output.close();
                if (input != null) input.close();
              } catch (IOException e1) {
                e1.printStackTrace();
              }
              getLooper().quit();

            } else if (msg.what == 1) {
              try {
                String send = (String) msg.obj;
                output.write(send);
                output.flush();
                final String result = input.readLine();
                mainHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    if (TextUtils.isEmpty(result)){
                      Toast.makeText(getContext(), "Server sent empty response",
                          Toast.LENGTH_SHORT).show();
                      return;
                    }
                    if (result.equalsIgnoreCase("1")) userOk();
                    else userNotOk();
                  }
                });
              } catch (IOException e) {
                handleIOException(e);
              }

            } else if (msg.what == 2){
              try {
                String send = (String) msg.obj;
                output.write(send);
                output.flush();
                final String result = input.readLine();
                mainHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    if (TextUtils.isEmpty(result)){
                      Toast.makeText(getContext(), "Server sent empty response",
                          Toast.LENGTH_SHORT).show();
                      return;
                    }
                    if (result.equalsIgnoreCase("1")) deptOk();
                    else deptNotOk();
                  }
                });
              } catch (IOException e) {
                handleIOException(e);
              }
            }
          }

          private void handleIOException(IOException e) {
            mainHandler.post(getIOExceptionRunnable());
            e.printStackTrace();
            quit();
            try {
              if (socket != null) socket.close();
              if (output != null) output.close();
              if (input != null) input.close();
            } catch (IOException e1) {
              e1.printStackTrace();
            }
          }
        };
      }
    };

    handlerThread.start();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Message message = Message.obtain();
    message.what = -1;
    if (mNetworkingHandler.getLooper() != null
        && mNetworkingHandler.getLooper().getThread().isAlive()) {
      mNetworkingHandler.sendMessage(message);
    }
  }

  @Override
  public void checkUser(String userDetails) {
    if (mNetworkingHandler != null){
      Message message = Message.obtain();
      message.what = 1;
      message.obj = userDetails;
      mNetworkingHandler.sendMessage(message);
    }
  }

  @Override
  public void checkDepartment(String departmentDetails) {
    if (mNetworkingHandler != null){
      Message message = Message.obtain();
      message.what = 2;
      message.obj = departmentDetails;
      mNetworkingHandler.sendMessage(message);
    }
  }

  public void ioException() {
    Toast.makeText(getContext(), "Network Error, Restart app", Toast.LENGTH_SHORT).show();
    if (isAdded()){
      getActivity().finish();
    }
  }

  @Override
  public void userOk() {
    if (isAdded()){
      ((NetworkDownlinkContract) getActivity()).userOk();
    }
  }

  @Override
  public void userNotOk() {
    if (isAdded()){
      ((NetworkDownlinkContract) getActivity()).userNotOk();
    }
  }

  @Override
  public void deptOk() {
    if (isAdded()){
      ((NetworkDownlinkContract) getActivity()).deptOk();
    }
  }

  @Override
  public void deptNotOk() {
    if (isAdded()){
      ((NetworkDownlinkContract) getActivity()).deptNotOk();
    }
  }
}

interface NetworkUplinkContract {

  public void checkUser(String userDetails);

  public void checkDepartment(String departmentDetails);
}

interface NetworkDownlinkContract {

  public void userOk();

  public void userNotOk();

  public void deptOk();

  public void deptNotOk();

}
