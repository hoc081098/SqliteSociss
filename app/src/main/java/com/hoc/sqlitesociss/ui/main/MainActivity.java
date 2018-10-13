package com.hoc.sqlitesociss.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.snackbar.Snackbar;
import com.hoc.sqlitesociss.MyApp;
import com.hoc.sqlitesociss.R;
import com.hoc.sqlitesociss.ui.add.AddContactActivity;
import com.hoc.sqlitesociss.ui.detail.DetailActivity;

import javax.inject.Inject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_CONTACT_ENTITY = "EXTRA_CONTACT_ENTITY";

    private final ContactAdapter adapter = new ContactAdapter();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MainViewModel mainViewModel;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        MyApp.getAppComponent(this).inject(this);

        recycler = findViewById(R.id.recycler_contacts);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);
        mainViewModel.getContacts().observe(this, adapter::submitList);
        mainViewModel.getMessage().observe(this, charSequence -> {
            if (charSequence != null) {
                Snackbar.make(recycler.getRootView(), charSequence, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Disposable disposable = adapter.getClickObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        contactEntity -> {
                            final Intent intent = new Intent(this, DetailActivity.class);
                            intent.putExtra(EXTRA_CONTACT_ENTITY, contactEntity);
                            startActivity(intent);
                        },
                        e -> Snackbar.make(recycler.getRootView(), e.getMessage(), Snackbar.LENGTH_SHORT)
                                .show()
                );
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.action_create_new) {
            startActivity(new Intent(this, AddContactActivity.class));
            return true;
        }
        if (itemId == R.id.action_delete_all) {
            onDeleteAll();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDeleteAll() {
        new AlertDialog.Builder(this)
                .setTitle("Delete all")
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setMessage("Do you want to delete all contacts")
                .setNegativeButton("Cancel", (dialog, __) -> dialog.dismiss())
                .setPositiveButton("Ok", (dialog, __) -> {
                    dialog.dismiss();
                    mainViewModel.deleteAllContacts();
                })
                .show();
    }
}
