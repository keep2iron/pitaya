package io.github.keep2iron.main;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/12/19 16:35
 */
class TestParcelable implements Parcelable {
    int testInt;
    String testString;

    public TestParcelable(int testInt, String testString) {
        this.testInt = testInt;
        this.testString = testString;
    }

    public int getTestInt() {
        return testInt;
    }

    public void setTestInt(int testInt) {
        this.testInt = testInt;
    }

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }


    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.testInt);
        dest.writeString(this.testString);
    }

    protected TestParcelable(Parcel in) {
        this.testInt = in.readInt();
        this.testString = in.readString();
    }

    public static final Parcelable.Creator<TestParcelable> CREATOR = new Parcelable.Creator<TestParcelable>() {
        @Override public TestParcelable createFromParcel(Parcel source) {
            return new TestParcelable(source);
        }

        @Override public TestParcelable[] newArray(int size) {
            return new TestParcelable[size];
        }
    };
}
