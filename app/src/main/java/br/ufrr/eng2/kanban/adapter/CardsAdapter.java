package br.ufrr.eng2.kanban.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.ufrr.eng2.kanban.R;
import br.ufrr.eng2.kanban.controller.FirebaseController;
import br.ufrr.eng2.kanban.model.Tarefa;
import br.ufrr.eng2.kanban.model.Usuario;
import br.ufrr.eng2.kanban.util.CircleTransform;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {
    private List<Tarefa> mTarefas;
    private ClickCallback mCallback;

    public CardsAdapter(List<Tarefa> tarefas, ClickCallback callback) {
        mTarefas = tarefas;
        mCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_card_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Tarefa currentTarefa = mTarefas.get(position);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClick(v, currentTarefa);
            }
        });

        Context context = holder.cardView.getContext();
        switch (currentTarefa.getCategoriaTarefa()) {
            case Tarefa.CATEGORIA_ANALISE:
                holder.title.setText(currentTarefa.getNomeTarefa());
                holder.tags.setText("#Análise");
                holder.color.setBackgroundColor(context.getResources().getColor(R.color.category_analysis));
                break;
            case Tarefa.CATEGORIA_CORRECAO:
                holder.title.setText(currentTarefa.getNomeTarefa());
                holder.tags.setText("#Correção");
                holder.color.setBackgroundColor(context.getResources().getColor(R.color.category_fix));
                break;
            default:
                holder.title.setText(currentTarefa.getNomeTarefa());
                holder.tags.setText("#Desenvolvimento");
                holder.color.setBackgroundColor(context.getResources().getColor(R.color.category_development));
                break;
        }

        if (currentTarefa.getOwnedId() != null && !currentTarefa.getOwnedId().equals("")) {
            final ImageView userPhoto = holder.photo;
            holder.photo.setVisibility(View.VISIBLE);

            ValueEventListener tasksListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuario user= dataSnapshot.getValue(Usuario.class);
                    Picasso.with(userPhoto.getContext())
                            .load(user.getUrlFoto())
                            .placeholder(R.drawable.ic_person)
                            .transform(new CircleTransform())
                            .into(userPhoto);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            FirebaseDatabase database = FirebaseController.getInstance();
            DatabaseReference myRef = database.getReference("user");
            myRef.child(currentTarefa.getOwnedId()).addListenerForSingleValueEvent(tasksListener);
        } else {
            holder.photo.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mTarefas.size();
    }

    public interface ClickCallback {
        void onClick(View v, Tarefa t);

        boolean onLongClick(View v);
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, long position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title, tags;
        View color;
        ImageView photo;
        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.card_title);
            tags = (TextView) itemView.findViewById(R.id.card_tags);
            color = itemView.findViewById(R.id.card_tag_color);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            photo = (ImageView) itemView.findViewById(R.id.user);
        }
    }

}
