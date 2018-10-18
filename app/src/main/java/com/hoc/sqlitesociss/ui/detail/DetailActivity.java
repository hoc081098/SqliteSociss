package com.hoc.sqlitesociss.ui.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.hoc.sqlitesociss.MyApp;
import com.hoc.sqlitesociss.R;
import com.hoc.sqlitesociss.data.ContactEntity;
import com.hoc.sqlitesociss.ui.add.AddContactActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import static android.view.View.GONE;
import static com.hoc.sqlitesociss.ui.main.MainActivity.EXTRA_CONTACT_ENTITY;
import static java.util.Objects.requireNonNull;

/**
 * Created by Peter Hoc on 10/12/2018.
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
  private final DateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
  @Inject
  ViewModelProvider.Factory viewModelFactory;
  private DetailViewModel detailViewModel;
  private ContactEntity contactEntity;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private TextView textPhone;
  private TextView textAddress;
  private TextView textGender;
  private TextView textCreatedAt;
  private TextView textUpdatedAt;
  private TextView textTitleUpdatedAt;
  private View divider;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.detail_activity);

    MyApp.getAppComponent(this).inject(this);

    setSupportActionBar(findViewById(R.id.toolbar));
    requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
    textPhone = findViewById(R.id.text_phone);
    textAddress = findViewById(R.id.text_address);
    textGender = findViewById(R.id.text_gender);
    textCreatedAt = findViewById(R.id.text_created_at);
    textUpdatedAt = findViewById(R.id.text_updated_at);
    textTitleUpdatedAt = findViewById(R.id.text_title_updated_at);
    divider = findViewById(R.id.view4);
    findViewById(R.id.fab).setOnClickListener(this);
    findViewById(R.id.image_call).setOnClickListener(this);
    findViewById(R.id.image_sms).setOnClickListener(this);

    updateUi(requireNonNull(getIntent().getParcelableExtra(EXTRA_CONTACT_ENTITY)));

    subscribe();
  }

  private void subscribe() {
    detailViewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel.class);
    detailViewModel.setContactId(contactEntity.getId());
    detailViewModel.getContact().observe(this, this::updateUi);
    detailViewModel.getMessageAndFinish().observe(this, charSequenceBooleanPair -> {
      if (charSequenceBooleanPair.first != null) {
        Snackbar.make(collapsingToolbarLayout.getRootView(), charSequenceBooleanPair.first, Snackbar.LENGTH_SHORT)
            .addCallback(new Snackbar.Callback() {
              @Override
              public void onDismissed(Snackbar transientBottomBar, int event) {
                if (Objects.equals(charSequenceBooleanPair.second, true)) {
                  onBackPressed();
                }
              }
            })
            .show();
      }
    });
  }

  private void updateUi(@NonNull ContactEntity contact) {
    contactEntity = contact;
    collapsingToolbarLayout.setTitle(contact.getName());
    textPhone.setText(contact.getPhone());
    textAddress.setText(contact.getAddress());
    textGender.setText(contact.isMale() ? R.string.male : R.string.female);
    textCreatedAt.setText(simpleDateFormat.format(contact.getCreatedAt()));
    final Date updatedAt = contact.getUpdatedAt();
    if (updatedAt == null) {
      textUpdatedAt.setVisibility(GONE);
      textTitleUpdatedAt.setVisibility(GONE);
      divider.setVisibility(GONE);
    } else {
      textTitleUpdatedAt.setVisibility(View.VISIBLE);
      textUpdatedAt.setVisibility(View.VISIBLE);
      divider.setVisibility(View.VISIBLE);
      textUpdatedAt.setText(simpleDateFormat.format(updatedAt));
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (android.R.id.home == item.getItemId()) {
      onBackPressed();
      return true;
    }
    if (R.id.action_delete == item.getItemId()) {
      onDeleteContact();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void onDeleteContact() {
    new AlertDialog.Builder(this)
        .setTitle("Delete contact")
        .setIcon(R.drawable.ic_warning_black_24dp)
        .setMessage("Do you want to delete this contact")
        .setNegativeButton("Cancel", (dialog, __) -> dialog.dismiss())
        .setPositiveButton("Ok", (dialog, __) -> {
          dialog.dismiss();
          detailViewModel.deleteContact();
        })
        .show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.detail_menu, menu);
    return true;
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.fab) {
      final Intent intent = new Intent(this, AddContactActivity.class);
      intent.putExtra(EXTRA_CONTACT_ENTITY, contactEntity);
      startActivity(intent);
      return;
    }

    if (v.getId() == R.id.image_call) {
      if (isTelephonyEnabled()) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + contactEntity.getPhone()));
        startActivity(intent);
      } else {
        Snackbar.make(collapsingToolbarLayout.getRootView(), "Sim is not ready", Snackbar.LENGTH_SHORT)
            .show();
      }
      return;
    }

    if (v.getId() == R.id.image_sms) {
      if (isTelephonyEnabled()) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android-dir/mms-sms");
        intent.putExtra("address", contactEntity.getPhone());
        startActivity(intent);
      } else {
        Snackbar.make(collapsingToolbarLayout.getRootView(), "Sim is not ready", Snackbar.LENGTH_SHORT)
            .show();
      }
      return;
    }
  }


  private boolean isTelephonyEnabled() {
    final TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    return manager != null && manager.getSimState() == TelephonyManager.SIM_STATE_READY;
  }
}
