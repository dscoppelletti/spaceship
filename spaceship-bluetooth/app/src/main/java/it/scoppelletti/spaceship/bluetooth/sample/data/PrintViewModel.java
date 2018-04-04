package it.scoppelletti.spaceship.bluetooth.sample.data;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import it.scoppelletti.spaceship.bluetooth.sample.BR;
import it.scoppelletti.spaceship.bluetooth.sample.R;
import it.scoppelletti.spaceship.widget.EditTextValidator;

public final class PrintViewModel extends BaseObservable implements Parcelable {

    public static final Creator<PrintViewModel> CREATOR = new Creator<PrintViewModel>() {

        @Override
        public PrintViewModel createFromParcel(Parcel in) {
            return new PrintViewModel(in);
        }

        @Override
        public PrintViewModel[] newArray(int size) {
            return new PrintViewModel[size];
        }
    };

    private final EditTextValidator myBodyValidator;
    private String myBody;
    private int myBodyErr;

    public PrintViewModel() {
        myBodyValidator = () -> {
            if (TextUtils.isEmpty(myBody)) {
                setBodyError(R.string.err_bodyRequired);
                return false;
            }

            setBodyError(0);
            return true;
        };
    }

    private PrintViewModel(Parcel in) {
        this();

        myBody = in.readString();
        myBodyErr = in.readInt();
    }

    @Bindable
    @Nullable
    public String getBody() {
        return myBody;
    }

    public void setBody(@Nullable String value) {
        if (!TextUtils.equals(value, myBody)) {
            notifyPropertyChanged(BR.body);
            myBody = value;
        }

        myBodyValidator.validate();
    }

    @Bindable
    public int getBodyError() {
        return myBodyErr;
    }

    public void setBodyError(int value) {
        if (value != myBodyErr) {
            myBodyErr = value;
            notifyPropertyChanged(BR.bodyError);
        }
    }

    @NonNull
    @Bindable
    public EditTextValidator getBodyValidator() {
        return myBodyValidator;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myBody);
        dest.writeInt(myBodyErr);
    }
}
