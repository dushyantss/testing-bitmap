package aditya.facerecog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

  private static final String ARG_STUDENTS = "arg_students";

  public static ListFragment newInstance(ArrayList<Student> students) {
    Bundle args = new Bundle();
    args.putParcelableArrayList(ARG_STUDENTS, students);
    ListFragment fragment = new ListFragment();
    fragment.setArguments(args);
    return fragment;
  }
  
  public ListFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_list, container, false);

  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (getArguments() != null && getArguments().getParcelableArrayList(ARG_STUDENTS) != null){
      List<Student> students = getArguments().getParcelableArrayList(ARG_STUDENTS);

      RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
      recyclerView.setAdapter(new StudentsAdapter(getContext(), students));

      FloatingActionButton fabDone = (FloatingActionButton) view.findViewById(R.id.fab_done);
      fabDone.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
      });

    } else {
      Toast.makeText(getContext(), "No Students in the list", Toast.LENGTH_SHORT).show();
    }
  }
}
