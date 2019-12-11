package cyber.bletarget.ui.home;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cyber.bletarget.BeaconManager;

public class HomeViewModel extends AndroidViewModel {

    private BeaconManager beaconManager;
    public MutableLiveData<String> mText;

    public HomeViewModel(@NonNull Application application) {
        super(application);

        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        Log.i("TAG1", "BeaconManager is " + (beaconManager == null ? "null" : "instantiated"));
        if (beaconManager == null)
            beaconManager = new BeaconManager(application.getApplicationContext(), this);

        beaconManager.connectBeacons();

    }

    public LiveData<String> getText() {
        return mText;
    }
}