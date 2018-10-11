package com.hoc.sqlitesociss.ui;

import android.os.Bundle;
import android.widget.Toast;

import com.hoc.sqlitesociss.MyApp;
import com.hoc.sqlitesociss.R;
import com.squareup.sqlbrite3.BriteDatabase;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static com.hoc.sqlitesociss.data.ContactEntity.MAPPER;
import static com.hoc.sqlitesociss.data.DatabaseContract.ContactEntry.TABLE_NAME;

public class MainActivity extends AppCompatActivity {
    private final ContactAdapter adapter = new ContactAdapter();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Inject
    BriteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        MyApp.getAppComponent(this).inject(this);

        final RecyclerView recycler = findViewById(R.id.recycler_contacts);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        compositeDisposable.add(
                db.createQuery(TABLE_NAME, "SELECT * FROM " + TABLE_NAME)
                        .mapToList(MAPPER)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                adapter::submitList,
                                e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                        )
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.dispose();
    }
}
