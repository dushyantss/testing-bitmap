package aditya.facerecog;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The model for students
 *
 * Created by dushyant on 17/05/17.
 */

class Student implements Parcelable{
  private String rollNumber;
  private String name;
  private boolean present;

  public Student(String rollNumber, String name, boolean present) {
    this.rollNumber = rollNumber;
    this.name = name;
    this.present = present;
  }

  private Student(Parcel in) {
    rollNumber = in.readString();
    name = in.readString();
    present = in.readByte() != 0;
  }

  public static final Creator<Student> CREATOR = new Creator<Student>() {
    @Override
    public Student createFromParcel(Parcel in) {
      return new Student(in);
    }

    @Override
    public Student[] newArray(int size) {
      return new Student[size];
    }
  };

  public String getRollNumber() {
    return rollNumber;
  }

  public void setRollNumber(String rollNumber) {
    this.rollNumber = rollNumber;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isPresent() {
    return present;
  }

  public void setPresent(boolean present) {
    this.present = present;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Student student = (Student) o;

    return rollNumber.equals(student.rollNumber);
  }

  @Override
  public int hashCode() {
    return rollNumber.hashCode();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(rollNumber);
    dest.writeString(name);
    dest.writeByte((byte) (present ? 1 : 0));
  }
}
