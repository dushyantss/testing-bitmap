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
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkingFragment extends Fragment
    implements NetworkUplinkContract, NetworkDownlinkContract {

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
      private OutputStream outputStream;

      @Override
      protected void onLooperPrepared() {
        super.onLooperPrepared();

        mNetworkingHandler = getNetworkingHandler(this);
      }

      private void connectToServer() throws IOException {
        InetAddress serverAddr = InetAddress.getByName("192.168.2.9");
        socket = new Socket(serverAddr, 5000);
        outputStream = socket.getOutputStream();
        output = new PrintWriter(outputStream);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
              closeSocket();
              getLooper().quit();
            } else if (msg.what == 1) {
              try {
                final String result = getResultString(msg);
                mainHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    if (TextUtils.isEmpty(result)) {
                      userNotOk();
                      return;
                    }
                    if (result.equalsIgnoreCase("1")) {
                      userOk();
                    } else {
                      userNotOk();
                    }
                  }
                });
              } catch (IOException e) {
                handleIOException(e);
              } finally {
                closeSocket();
              }
            } else if (msg.what == 2) {
              try {
                final String result = getResultString(msg);
                mainHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    if (TextUtils.isEmpty(result)) {
                      deptNotOk();
                      return;
                    }
                    if (result.equalsIgnoreCase("1")) {
                      deptOk();
                    } else {
                      deptNotOk();
                    }
                  }
                });
              } catch (IOException e) {
                handleIOException(e);
              } finally {
                closeSocket();
              }
            } else if (msg.what == 3) {
              try {
                Log.d("Data", "sending image data");
                connectToServer();
                output.write("Image/0");
                output.flush();
                int in = input.read();
                String val = null;
                if (in != -1) {
                  val = String.valueOf((char) in);
                }
                Log.d("Data", val != null ? val : "val is null");
                if (TextUtils.isEmpty(val) || !val.equals("1")) {
                  mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                      imageNotOk();
                    }
                  });
                } else {
                  byte[] imageData = (byte[]) msg.obj;
                  Log.d("Data", "data is being sent " + imageData.length);
                  BufferedOutputStream os = new BufferedOutputStream(outputStream);
                  os.write(imageData);
                  os.flush();
                  mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                      imageOk();
                    }
                  });
                }
              } catch (IOException e) {
                handleIOException(e);
                mainHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    imageNotOk();
                  }
                });
              } finally {
                closeSocket();
              }
            } else if (msg.what == 4) {
              try {
                connectToServer();
                output.write("Request/0");
                output.flush();
                int in = input.read();
                String val = null;
                if (in != -1) {
                  val = String.valueOf((char) in);
                }
                if (TextUtils.isEmpty(val) || !val.equals("1")) {
                  mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                      studentsNotReceived();
                    }
                  });
                } else {
                  final List<Student> students = fetchStudents();
                  mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                      if (students != null) studentsReceived(students);
                      else studentsNotReceived();
                    }
                  });
                }
              } catch (IOException e) {
                handleIOException(e);
                mainHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    studentsNotReceived();
                  }
                });
              } finally {
                closeSocket();
              }
            } else if (msg.what == 5) {
              try {
                connectToServer();
                output.write("End/0");
                output.flush();
                int in = input.read();
                String val = null;
                if (in != -1) {
                  val = String.valueOf((char) in);
                }
                if (TextUtils.isEmpty(val) || !val.equals("1")) {
                  mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                      studentsNotOk();
                    }
                  });
                } else {
                  List<Student> students = (List<Student>) msg.obj;
                  final String str = createSendStudentsString(students);
                  output.write(str);
                  output.flush();
                  mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                      studentsOk();
                    }
                  });
                }
              } catch (IOException e) {
                handleIOException(e);
                mainHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    studentsNotOk();
                  }
                });
              } finally {
                closeSocket();
              }
            }
          }

          private String createSendStudentsString(List<Student> students) {
            StringBuilder builder = new StringBuilder();
            for (Student student : students) {
              if (student.isPresent()) {
                builder.append(student.getRollNumber());
                builder.append("/");
              }
            }
            builder.append("@@");

            return builder.toString();
          }

          private List<Student> fetchStudents() throws IOException {
            int i;
            char[] arr = new char[1024];
            int count = 0;
            // all the ones present
            while ((i = input.read()) != '@') {
              arr[count++] = (char) i;
            }
            String str = new String(arr);
            Pattern pattern = Pattern.compile("(([^/]+)/([^/]+))");
            Matcher matcher = pattern.matcher(str);
            List<Student> students = new ArrayList<>();
            while (matcher.find()) {
              String name = matcher.group(2);
              String rollNumber = matcher.group(3);

              students.add(new Student(rollNumber, name, true));
            }
            Arrays.fill(arr, (char) 0);
            count = 0;
            // all the ones absent
            while ((i = input.read()) != '@') {
              arr[count++] = (char) i;
            }

            str = new String(arr);
            matcher = pattern.matcher(str);
            while (matcher.find()) {
              String name = matcher.group(2);
              String rollNumber = matcher.group(3);

              students.add(new Student(rollNumber, name, false));
            }

            return students;
          }

          private String getResultString(Message msg) throws IOException {
            connectToServer();
            String send = (String) msg.obj;
            output.write(send);
            output.flush();
            String val = null;
            int in = input.read();
            if (in != -1) {
              val = String.valueOf((char) in);
            }

            return val;
          }

          private void closeSocket() {
            try {
              if (output != null) output.close();
              if (input != null) input.close();
              //if (outputStream != null) outputStream.close();
            } catch (IOException e1) {
              e1.printStackTrace();
            }
          }

          private void handleIOException(IOException e) {
            mainHandler.post(getIOExceptionRunnable());
            e.printStackTrace();
            quit();
            closeSocket();
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
    if (mNetworkingHandler.getLooper() != null && mNetworkingHandler.getLooper()
        .getThread()
        .isAlive()) {
      mNetworkingHandler.sendMessage(message);
    }
  }

  @Override
  public void checkUser(String userDetails) {
    if (mNetworkingHandler != null) {
      Message message = Message.obtain();
      message.what = 1;
      message.obj = userDetails;
      mNetworkingHandler.sendMessage(message);
    }
  }

  @Override
  public void checkDepartment(String departmentDetails) {
    if (mNetworkingHandler != null) {
      Message message = Message.obtain();
      message.what = 2;
      message.obj = departmentDetails;
      mNetworkingHandler.sendMessage(message);
    }
  }

  @Override
  public void sendImage(byte[] imageData) {
    if (mNetworkingHandler != null) {
      Message message = Message.obtain();
      message.what = 3;
      message.obj = imageData;
      mNetworkingHandler.sendMessage(message);
    }
  }

  @Override
  public void sendStudents(List<Student> students) {
    if (mNetworkingHandler != null) {
      Message message = Message.obtain();
      message.what = 5;
      message.obj = students;
      mNetworkingHandler.sendMessage(message);
    }
  }

  @Override
  public void receiveStudents() {
    if (mNetworkingHandler != null) {
      Message message = Message.obtain();
      message.what = 4;
      mNetworkingHandler.sendMessage(message);
    }
  }

  public void ioException() {
    Toast.makeText(getContext(), "Network Error, Restart app", Toast.LENGTH_SHORT).show();
    if (isAdded()) {
      getActivity().finish();
    }
  }

  @Override
  public void userOk() {
    if (isAdded()) {
      ((NetworkDownlinkContract) getActivity()).userOk();
    }
  }

  @Override
  public void userNotOk() {
    if (isAdded()) {
      ((NetworkDownlinkContract) getActivity()).userNotOk();
    }
  }

  @Override
  public void deptOk() {
    if (isAdded()) {
      ((NetworkDownlinkContract) getActivity()).deptOk();
    }
  }

  @Override
  public void deptNotOk() {
    if (isAdded()) {
      ((NetworkDownlinkContract) getActivity()).deptNotOk();
    }
  }

  @Override
  public void imageOk() {
    if (isAdded()) {
      ((NetworkDownlinkContract) getActivity()).imageOk();
    }
  }

  @Override
  public void imageNotOk() {
    if (isAdded()) {
      ((NetworkDownlinkContract) getActivity()).imageNotOk();
    }
  }

  @Override
  public void studentsOk() {
    if (isAdded()) {
      ((NetworkDownlinkContract) getActivity()).studentsOk();
    }
  }

  @Override
  public void studentsReceived(List<Student> students) {
    if (isAdded()) {
      ((NetworkDownlinkContract) getActivity()).studentsReceived(students);
    }
  }

  @Override
  public void studentsNotReceived() {
    if (isAdded()) {
      ((NetworkDownlinkContract) getActivity()).studentsNotReceived();
    }
  }

  @Override
  public void studentsNotOk() {
    if (isAdded()) {
      ((NetworkDownlinkContract) getActivity()).studentsNotOk();
    }
  }
}

interface NetworkUplinkContract {

  void checkUser(String userDetails);

  void checkDepartment(String departmentDetails);

  void sendImage(byte[] imageData);

  void sendStudents(List<Student> students);

  void receiveStudents();
}

interface NetworkDownlinkContract {

  void userOk();

  void userNotOk();

  void deptOk();

  void deptNotOk();

  void imageOk();

  void imageNotOk();

  void studentsOk();

  void studentsReceived(List<Student> students);

  void studentsNotReceived();

  void studentsNotOk();
}
