package com.hoc.sqlitesociss.ui.add;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.hoc.sqlitesociss.MyApp;
import com.hoc.sqlitesociss.R;
import com.hoc.sqlitesociss.data.ContactEntity;
import com.hoc.sqlitesociss.ui.main.MainActivity;

import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import static java.util.Objects.requireNonNull;

/**
 * Created by Peter Hoc on 10/12/2018.
 */
public class AddContactActivity extends AppCompatActivity implements View.OnClickListener {
  @Inject
  ViewModelProvider.Factory viewModelFactory;

  private MaterialButton mButtonAdd;
  private MaterialButton mButtonCancel;
  private RadioGroup mRadioGroup;
  private TextInputLayout mTextInputPhone;
  private TextInputLayout mTextInputAddress;
  private TextInputLayout mTextInputName;
  private AddContactViewModel mAddContactViewModel;

  @Nullable
  private ContactEntity contactEntity;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.add_contact_activity);

    MyApp.getAppComponent(this).inject(this);
    contactEntity = getIntent().getParcelableExtra(MainActivity.EXTRA_CONTACT_ENTITY);

    findViews();

    subscribe();

    editOrAdd();

    mButtonCancel.setOnClickListener(this);
    mButtonAdd.setOnClickListener(this);
  }

  private void editOrAdd() {
    final ActionBar actionBar = requireNonNull(getSupportActionBar());
    actionBar.setTitle(contactEntity != null ? R.string.edit_contact : R.string.add_new_contact);
    actionBar.setDisplayHomeAsUpEnabled(true);
    mButtonAdd.setText(contactEntity != null ? R.string.update : R.string.add);
    if (contactEntity != null) {
      requireNonNull(mTextInputName.getEditText()).setText(contactEntity.getName());
      requireNonNull(mTextInputPhone.getEditText()).setText(contactEntity.getPhone());
      requireNonNull(mTextInputAddress.getEditText()).setText(contactEntity.getAddress());
      mRadioGroup.check(contactEntity.isMale() ? R.id.radio_male : R.id.radio_female);
    }
  }

  private void subscribe() {
    mAddContactViewModel = ViewModelProviders.of(this, viewModelFactory).get(AddContactViewModel.class);
    mAddContactViewModel.getMessage().observe(this, charSequenceBooleanPair -> {
      if (charSequenceBooleanPair != null && charSequenceBooleanPair.first != null) {
        Snackbar.make(mButtonAdd.getRootView(), charSequenceBooleanPair.first, Snackbar.LENGTH_SHORT)
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

  private void findViews() {
    mTextInputName = findViewById(R.id.text_input_name);
    mTextInputAddress = findViewById(R.id.text_input_address);
    mTextInputPhone = findViewById(R.id.text_input_phone);
    mRadioGroup = findViewById(R.id.radio_group);
    mButtonAdd = findViewById(R.id.button_add);
    mButtonCancel = findViewById(R.id.button_cancel);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button_add:
        onButtonAddClicked();
        break;
      case R.id.button_cancel:
        onBackPressed();
        break;
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void onButtonAddClicked() {
    final String address = requireNonNull(mTextInputAddress.getEditText()).getText().toString();
    final String name = requireNonNull(mTextInputName.getEditText()).getText().toString();
    final String phone = requireNonNull(mTextInputPhone.getEditText()).getText().toString();
    final int checkedRadioButtonId = mRadioGroup.getCheckedRadioButtonId();

    if (contactEntity == null) {
      mAddContactViewModel.addContact(name, phone, address, checkedRadioButtonId);
    } else {
      mAddContactViewModel.updateContact(contactEntity.getId(), name, phone, address, checkedRadioButtonId);
    }
  }
}
