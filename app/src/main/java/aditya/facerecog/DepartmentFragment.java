package aditya.facerecog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class DepartmentFragment extends Fragment {

  public DepartmentFragment() {
    // Required empty public constructor
  }

  public static DepartmentFragment newInstance() {
    DepartmentFragment fragment = new DepartmentFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_department, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final Spinner courseSpinner = (Spinner) view.findViewById(R.id.spinner_course);
    final Spinner semesterSpinner = (Spinner) view.findViewById(R.id.spinner_semester);
    final Spinner branchSpinner = (Spinner) view.findViewById(R.id.spinner_branch);

    Button submitButton = (Button) view.findViewById(R.id.btn_submit);

    ArrayAdapter<CharSequence> courseAdapter = ArrayAdapter.createFromResource(getContext(),
        R.array.courses_array, android.R.layout.simple_spinner_item);
    courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    courseSpinner.setAdapter(courseAdapter);

    ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.createFromResource(getContext(),
        R.array.semesters_array, android.R.layout.simple_spinner_item);
    semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    semesterSpinner.setAdapter(semesterAdapter);

    ArrayAdapter<CharSequence> branchAdapter = ArrayAdapter.createFromResource(getContext(),
        R.array.branches_array, android.R.layout.simple_spinner_item);
    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    branchSpinner.setAdapter(branchAdapter);

    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isAdded()){
          String course = (String) courseSpinner.getSelectedItem();
          String semester = (String) semesterSpinner.getSelectedItem();
          String branch = (String) branchSpinner.getSelectedItem();
          String dept = course + "/" + semester + "/" + branch;
          ((NetworkUplinkContract) getActivity()).checkDepartment(dept);
        }
      }
    });

  }
}
