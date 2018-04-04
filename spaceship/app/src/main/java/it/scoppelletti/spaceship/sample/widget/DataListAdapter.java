package it.scoppelletti.spaceship.sample.widget;

import java.util.List;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import it.scoppelletti.spaceship.databinding.BindingViewHolder;
import it.scoppelletti.spaceship.sample.data.DataViewModel;
import it.scoppelletti.spaceship.sample.databinding.DataItemBinding;

public class DataListAdapter extends RecyclerView.Adapter<
        BindingViewHolder<DataItemBinding>> {
    private List<DataViewModel> myData;

    public DataListAdapter() {
        setHasStableIds(true);
    }

    @Override
    public int getItemCount() {
        return (myData == null) ? 0 : myData.size();
    }

    @Override
    public long getItemId(int position) {
        if (myData == null || position < 0 || position >= myData.size()) {
            return RecyclerView.NO_ID;
        }

        return myData.get(position).getId();
    }

    public void changeData(@Nullable List<DataViewModel> data) {
        if (data != myData) {
            myData = data;
            notifyDataSetChanged();
        }
    }

    @Override
    public BindingViewHolder<DataItemBinding> onCreateViewHolder(
            ViewGroup parent, int viewType) {
        LayoutInflater inflater;
        DataItemBinding binding;

        inflater = LayoutInflater.from(parent.getContext());
        binding = DataItemBinding.inflate(inflater, parent, false);
        return new BindingViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder<DataItemBinding> holder,
            int position) {
        if (myData == null) {
            return;
        }

        holder.getBinding().setModel(myData.get(position));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public void onViewRecycled(BindingViewHolder<DataItemBinding> holder) {
        holder.getBinding().setModel(null);
        holder.getBinding().executePendingBindings();
    }
}
