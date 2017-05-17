package aditya.facerecog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements NetworkDownlinkContract, NetworkUplinkContract {

  private NetworkingFragment networkingFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    networkingFragment =
        (NetworkingFragment) getSupportFragmentManager().findFragmentByTag("networking");
    if (networkingFragment == null) {
      networkingFragment = NetworkingFragment.newInstance();
      getSupportFragmentManager().beginTransaction().add(networkingFragment, "networking").commit();
    }

    Fragment currentFragment =
        getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    if (currentFragment == null) {
      currentFragment = LoginFragment.newInstance();
      getSupportFragmentManager().beginTransaction()
          .add(R.id.fragment_container, currentFragment)
          .commit();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    networkingFragment = null;
  }

  @Override
  public void checkUser(String userDetails) {
    networkingFragment.checkUser(userDetails);
  }

  @Override
  public void checkDepartment(String departmentDetails) {
    networkingFragment.checkDepartment(departmentDetails);
  }

  @Override
  public void sendImage(byte[] imageData) {
    networkingFragment.sendImage(imageData);
  }

  @Override
  public void sendStudents(List<Student> students) {
    networkingFragment.sendStudents(students);
  }

  @Override
  public void userOk() {
    // go to department fragment
    Toast.makeText(this, "User ok", Toast.LENGTH_SHORT).show();
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_container, DepartmentFragment.newInstance())
        .commit();
  }

  @Override
  public void userNotOk() {
    Toast.makeText(this, "User details Invalid", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void deptOk() {
    // go to photo fragment
    Toast.makeText(this, "Department OK", Toast.LENGTH_SHORT).show();
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_container, ImageFragment.newInstance())
        .commit();
  }

  @Override
  public void deptNotOk() {
    Toast.makeText(this, "Department details invalid", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void imageOk() {
    Toast.makeText(this, "Image OK", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void imageNotOk() {
    Toast.makeText(this, "Image Not OK", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void studentsOk() {
    Toast.makeText(this, "Students OK", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void studentsNotOk() {
    Toast.makeText(this, "Students Not OK", Toast.LENGTH_SHORT).show();
  }
}
