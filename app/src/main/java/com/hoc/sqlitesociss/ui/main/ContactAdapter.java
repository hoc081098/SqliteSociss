package com.hoc.sqlitesociss.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hoc.sqlitesociss.data.ContactEntity;

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

    public Observable<ContactEntity> getClickObservable() {
        return publishSubject.hide();
    }

    ContactAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView text1;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(this);
        }

        void bind(ContactEntity item) {
            text1.setText(item.getName() + " #" + item.getId());
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
