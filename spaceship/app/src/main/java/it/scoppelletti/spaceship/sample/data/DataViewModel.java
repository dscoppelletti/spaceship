package it.scoppelletti.spaceship.sample.data;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import org.greenrobot.eventbus.EventBus;
import it.scoppelletti.spaceship.sample.BR;
import it.scoppelletti.spaceship.sample.DataChangeEvent;
import it.scoppelletti.spaceship.sample.DataEditEvent;
import it.scoppelletti.spaceship.sample.R;
import it.scoppelletti.spaceship.widget.EditTextValidator;

public final class DataViewModel extends BaseObservable implements Parcelable {
    private final EditTextValidator myDescValidator;
    private final EditTextValidator myNameValidator;
    private long myId;
    private String myName;
    private int myNameErr;
    private String myDesc;
    private int myDescErr;
    private boolean myNewData;
    private boolean myChanged;

    public static final Creator<DataViewModel> CREATOR = new Creator<DataViewModel>() {

        @Override
        public DataViewModel createFromParcel(Parcel in) {
            return new DataViewModel(in);
        }

        @Override
        public DataViewModel[] newArray(int size) {
            return new DataViewModel[size];
        }
    };

    public DataViewModel() {
        myNameValidator = () -> {
            if (TextUtils.isEmpty(myName)) {
                setNameError(R.string.err_name_required);
                return false;
            }

            setNameError(0);
            return true;
        };

        myDescValidator = () -> {
            if (TextUtils.isEmpty(myDesc)) {
                setDescError(R.string.err_desc_required);
                return false;
            }

            setDescError(0);
            return true;
        };

        myNewData = true;
        myChanged = false;
    }

    private DataViewModel(Parcel in) {
        this();

        myId = in.readLong();
        myName = in.readString();
        myNameErr = in.readInt();
        myDesc = in.readString();
        myDescErr = in.readInt();
        myNewData = (in.readInt() != 0);
        myChanged = (in.readInt() != 0);
    }

    public long getId() {
        return myId;
    }

    public void setId(long value) {
        myId = value;
    }

    @Bindable
    @Nullable
    public String getName() {
        return myName;
    }

    public void setName(@Nullable String value) {
        if (!TextUtils.equals(value, myName)) {
            notifyPropertyChanged(BR.name);
            myName = value;
            setChanged(true);
        }

        myNameValidator.validate();
    }

    @Bindable
    public int getNameError() {
        return myNameErr;
    }

    public void setNameError(int value) {
        if (value != myNameErr) {
            myNameErr = value;
            notifyPropertyChanged(BR.nameError);
        }
    }

    @NonNull
    @Bindable
    public EditTextValidator getNameValidator() {
        return myNameValidator;
    }

    @Bindable
    @Nullable
    public String getDesc() {
        return myDesc;
    }

    public void setDesc(@Nullable String value) {
        if (!TextUtils.equals(value, myDesc)) {
            myDesc = value;
            notifyPropertyChanged(BR.desc);
            setChanged(true);
        }

        myDescValidator.validate();
    }

    @Bindable
    public int getDescError() {
        return myDescErr;
    }

    public void setDescError(int value) {
        if (value != myDescErr) {
            myDescErr = value;
            notifyPropertyChanged(BR.descError);
        }
    }

    @NonNull
    @Bindable
    public EditTextValidator getDescValidator() {
        return myDescValidator;
    }

    @Bindable
    public boolean isNewData() {
        return myNewData;
    }

    public void setNewData(boolean value) {
        if (value != myNewData) {
            myNewData = value;
            notifyPropertyChanged(BR.newData);
        }
    }

    @Bindable
    public boolean isChanged() {
        return myChanged;
    }

    public void setChanged(boolean value) {
        if (value != myChanged) {
            myChanged = value;
            notifyPropertyChanged(BR.changed);
        }
    }

    public void onEditClick(View view) {
        if (myId > 0) {
            EventBus.getDefault().post(new DataEditEvent(myId));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(myId);
        dest.writeString(myName);
        dest.writeInt(myNameErr);
        dest.writeString(myDesc);
        dest.writeInt(myDescErr);
        dest.writeInt((myNewData) ? 1 : 0);
        dest.writeInt((myChanged) ? 1 : 0);
    }

    @Override
    public void notifyPropertyChanged(int fieldId) {
        super.notifyPropertyChanged(fieldId);
        EventBus.getDefault().post(DataChangeEvent.getInstance());
    }
}
