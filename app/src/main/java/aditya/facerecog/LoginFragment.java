package aditya.facerecog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

  public LoginFragment() {
    // Required empty public constructor
  }
public static LoginFragment newInstance() {
    LoginFragment fragment = new LoginFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_login, container, false);

    return v;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Button button = (Button) view.findViewById(R.id.submit);
    final TextInputEditText username = (TextInputEditText) view.findViewById(R.id.username);
    final TextInputEditText password = (TextInputEditText) view.findViewById(R.id.password);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
      if (isAdded()){
        ((NetworkUplinkContract) getActivity()).checkUser("Login/" + username.getText().toString()
            + "/" + password.getText().toString());
      }
      }
    });
  }
}
