package br.ufrr.eng2.kanban.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.ufrr.eng2.kanban.R;
import br.ufrr.eng2.kanban.model.Tarefa;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {
    private List<Tarefa> mTarefas;
    private ClickCallback mCallback;


    public CardsAdapter(List<Tarefa> tarefas, ClickCallback callback) {
        mTarefas = tarefas;
        mCallback = callback;
    }


    public interface ClickCallback{
        void onClick(View v, Tarefa t);
        boolean onLongClick(View v);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_card_view, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tarefa currentTarefa = mTarefas.get(position);

        switch (currentTarefa.getCategoriaTarefa()) {
            case Tarefa.CATEGORIA_ANALISE:
                holder.title.setText(currentTarefa.getNomeTarefa());
                holder.tags.setText("#Design");
                holder.color.setBackgroundColor(Color.parseColor("#9C27B0"));
                break;
            case Tarefa.CATEGORIA_CORRECAO:
                holder.title.setText(currentTarefa.getNomeTarefa());
                holder.tags.setText("#Categoria");
                holder.color.setBackgroundColor(Color.parseColor("#FF5722"));
                break;
            default:
                holder.title.setText(currentTarefa.getNomeTarefa());
                holder.tags.setText("#Geral");
                holder.color.setBackgroundColor(Color.parseColor("#9E9E9E"));
                break;
        }
    }


    @Override
    public int getItemCount() {
        return mTarefas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, tags;
        View color;
        ViewHolder(View itemView) {
            super(itemView);


            title = (TextView) itemView.findViewById(R.id.card_title);
            tags = (TextView) itemView.findViewById(R.id.card_tags);
            color = itemView.findViewById(R.id.card_tag_color);
        }


    }

    public interface OnItemClickListener {
        public void onItemClick(View view , long position);
    }
}
