package aditya.facerecog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for the Students list
 *
 * Created by dushyant on 17/05/17.
 */

class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentsHolder> {

  private Context context;
  private List<Student> students;

  StudentsAdapter(Context context, List<Student> students){
    this.context = context;
    if (this.context == null){
      throw new IllegalArgumentException("Context cannot be null");
    }
    this.students = students;
    if (this.students == null){
      this.students = new ArrayList<>();
    }
  }

  @Override
  public StudentsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
    return new StudentsHolder(view);
  }

  @Override
  public void onBindViewHolder(StudentsHolder holder, int position) {
    holder.bind(students.get(position));
  }

  @Override
  public int getItemCount() {
    return students.size();
  }

  class StudentsHolder extends RecyclerView.ViewHolder
      implements CompoundButton.OnCheckedChangeListener {

    private TextView rollNumber;
    private TextView name;
    private CheckBox present;
    private Student student;

    StudentsHolder(View itemView) {
      super(itemView);
      rollNumber = (TextView) itemView.findViewById(R.id.text_roll_number);
      name = (TextView) itemView.findViewById(R.id.text_name);
      present = (CheckBox) itemView.findViewById(R.id.checkbox_present);
    }

    void bind(Student student){
      this.student = student;

      rollNumber.setText(student.getRollNumber());
      name.setText(student.getName());
      present.setChecked(student.isPresent());

      present.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      student.setPresent(isChecked);
    }
  }
}
