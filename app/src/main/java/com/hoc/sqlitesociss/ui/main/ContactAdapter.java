package com.hoc.sqlitesociss.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoc.sqlitesociss.data.ContactEntity;
import com.hoc.sqlitesociss.databinding.ContactItemLayoutBinding;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

/**
 * Created by Peter Hoc on 10/11/2018.
 */
public class ContactAdapter extends ListAdapter<ContactEntity, ContactAdapter.ViewHolder> {
  private final static DiffUtil.ItemCallback<ContactEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<ContactEntity>() {
    @Override
    public boolean areItemsTheSame(@NonNull ContactEntity oldItem, @NonNull ContactEntity newItem) {
      return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull ContactEntity oldItem, @NonNull ContactEntity newItem) {
      return oldItem.equals(newItem);
    }
  };

  private final PublishSubject<ContactEntity> publishSubject = PublishSubject.create();

  ContactAdapter() {
    super(DIFF_CALLBACK);
  }

  public Observable<ContactEntity> getClickObservable() {
    return publishSubject.hide();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    final ContactItemLayoutBinding binding = ContactItemLayoutBinding.inflate(
        LayoutInflater.from(parent.getContext()),
        parent,
        false
    );
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.bind(getItem(position));
  }

  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final ContactItemLayoutBinding binding;

    ViewHolder(@NonNull ContactItemLayoutBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
      binding.setClickListener(this);
    }

    void bind(ContactEntity item) {
      binding.setContact(item);
      binding.executePendingBindings();
    }

    @Override
    public void onClick(View v) {
      final int adapterPosition = getAdapterPosition();
      if (adapterPosition != NO_POSITION) {
        publishSubject.onNext(getItem(adapterPosition));
      }
    }
  }
}
