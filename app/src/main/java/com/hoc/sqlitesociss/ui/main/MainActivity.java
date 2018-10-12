package com.hoc.sqlitesociss.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.snackbar.Snackbar;
import com.hoc.sqlitesociss.MyApp;
import com.hoc.sqlitesociss.R;
import com.hoc.sqlitesociss.ui.add.AddContactActivity;

import javax.inject.Inject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private final ContactAdapter adapter = new ContactAdapter();

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        MyApp.getAppComponent(this).inject(this);

        final RecyclerView recycler = findViewById(R.id.recycler_contacts);
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
                .setMessage("Do you want to delete all contacts")
                .setNegativeButton("Cancel", (dialog, __) -> dialog.dismiss())
                .setPositiveButton("Ok", (dialog, __) -> {
                    dialog.dismiss();
                    mainViewModel.deleteAllContacts();
                })
                .show();
    }
}
