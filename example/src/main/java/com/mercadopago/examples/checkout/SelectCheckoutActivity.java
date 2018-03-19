package com.mercadopago.examples.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.examples.R;

import java.util.List;

import static android.support.v7.widget.RecyclerView.ViewHolder;
import static com.mercadopago.examples.utils.ExamplesUtils.getOptions;
import static com.mercadopago.examples.utils.ExamplesUtils.resolveCheckoutResult;

public class SelectCheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_checkout);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setAdapter(new SelectionAdapter(getOptions(this)));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        resolveCheckoutResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ItemHolder> {

        private final List<Pair<String, MercadoPagoCheckout.Builder>> options;

        SelectionAdapter(final List<Pair<String, MercadoPagoCheckout.Builder>> options) {
            this.options = options;
        }

        class ItemHolder extends ViewHolder {

            private final TextView text;

            ItemHolder(final View itemView) {
                super(itemView);
                text = (TextView) itemView;
            }

            void setOption(final Pair<String, MercadoPagoCheckout.Builder> pair) {
                text.setText(pair.first);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        assert pair.second != null;
                        pair.second.startForPayment();
                    }
                });
            }
        }

        @Override
        public ItemHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_option_row, parent, false));
        }

        @Override
        public void onBindViewHolder(final ItemHolder holder, final int position) {
            holder.setOption(options.get(position));
        }

        @Override
        public int getItemCount() {
            return options.size();
        }
    }
}
