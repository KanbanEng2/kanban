package br.ufrr.eng2.kanban.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.ufrr.eng2.kanban.R;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private OnItemClickListener mItemClickListener;

    public CardsAdapter() {
        // TODO Receber lista com os valores
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_card_view, parent, false);

        return new ViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // TODO: Pegar dados dinamicamente
        switch (position) {
            case 0:
                holder.title.setText("Criar protótipo");
                holder.tags.setText("#Design");
                holder.color.setBackgroundColor(Color.parseColor("#9C27B0"));
                break;
            case 1:
                holder.title.setText("Exemplo de card");
                holder.tags.setText("#Categoria");
                holder.color.setBackgroundColor(Color.parseColor("#FF5722"));
                break;
            default:
                holder.title.setText("Algo");
                holder.tags.setText("#Geral");
                holder.color.setBackgroundColor(Color.parseColor("#9E9E9E"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        // TODO
        return 3;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, tags;
        View color;
        ViewHolder(View itemView) {
            super(itemView);

            itemView.findViewById(R.id.card_view).setOnClickListener(this);

            title = (TextView) itemView.findViewById(R.id.card_title);
            tags = (TextView) itemView.findViewById(R.id.card_tags);
            color = itemView.findViewById(R.id.card_tag_color);

        }

        @Override
        public void onClick(View view) {

            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getItemId());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view , long position);
    }
}
