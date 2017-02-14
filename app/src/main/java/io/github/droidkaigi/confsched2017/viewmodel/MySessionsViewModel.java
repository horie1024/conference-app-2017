package io.github.droidkaigi.confsched2017.viewmodel;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import java.util.List;

import javax.inject.Inject;

import io.github.droidkaigi.confsched2017.model.MySession;
import io.github.droidkaigi.confsched2017.repository.sessions.MySessionsRepository;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public final class MySessionsViewModel extends BaseObservable implements ViewModel {

    private static final String TAG = MySessionsViewModel.class.getSimpleName();

    private MySessionsRepository mySessionsRepository;

    private final ObservableList<MySessionViewModel> mySessionViewModels;

    private final CompositeDisposable compositeDisposable;

    @Inject
    MySessionsViewModel(MySessionsRepository mySessionsRepository, CompositeDisposable compositeDisposable) {
        this.mySessionsRepository = mySessionsRepository;
        this.mySessionViewModels = new ObservableArrayList<>();
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
    }

    public ObservableList<MySessionViewModel> getMySessionViewModels() {
        return mySessionViewModels;
    }


    private Single<List<MySession>> loadMySessions() {
        return mySessionsRepository.findAll();
    }

    public void start() {
        Disposable disposable = loadMySessions()
                .map(this::convertToViewModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::renderSponsorships,
                        throwable -> Timber.tag(TAG).e(throwable, "Failed to show my sessions.")
                );
        compositeDisposable.add(disposable);
    }

    private List<MySessionViewModel> convertToViewModel(List<MySession> mySessions) {
        return Stream.of(mySessions).map(MySessionViewModel::new).collect(Collectors.toList());
    }

    private void renderSponsorships(List<MySessionViewModel> mySessionViewModels) {
        this.mySessionViewModels.clear();
        this.mySessionViewModels.addAll(mySessionViewModels);
    }
}
