package br.ufrr.eng2.kanban.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import br.ufrr.eng2.kanban.R;
import br.ufrr.eng2.kanban.model.Tarefa;
import br.ufrr.eng2.kanban.model.Usuario;

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

        if(currentTarefa.getOwnedId() != null) {
            final ImageView teste = holder.photo;

            ValueEventListener tasksListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuario user= dataSnapshot.getValue(Usuario.class);
                    new DownloadImageTask(teste)
                            .execute(user.getUrlFoto());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("user");
            myRef.child(currentTarefa.getOwnedId()).addListenerForSingleValueEvent(tasksListener);
        }


    }

    @Override
    public int getItemCount() {
        return mTarefas.size();
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

    public interface OnItemClickListener {
        public void onItemClick(View view , long position);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                java.net.URL url = new java.net.URL(urldisplay);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                mIcon11 = BitmapFactory.decodeStream(input);
                return mIcon11;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }


        protected void onPostExecute(Bitmap bitmap) {

            Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

            BitmapShader shader = new BitmapShader (bitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            paint.setAntiAlias(true);
            Canvas c = new Canvas(circleBitmap);
            c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);

            bmImage.setImageBitmap(circleBitmap);


        }
    }
}
