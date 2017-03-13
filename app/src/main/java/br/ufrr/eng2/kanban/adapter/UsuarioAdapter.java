package br.ufrr.eng2.kanban.adapter;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import br.ufrr.eng2.kanban.DownloadImageTask;
import br.ufrr.eng2.kanban.R;
import br.ufrr.eng2.kanban.model.Usuario;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {
    private boolean mRemove = false;
    private List<Usuario> mUsuarios;
    private UsuarioAdapter.ClickCallback mCallback;

    public UsuarioAdapter(List<Usuario> usuarios, ClickCallback callback, boolean remove) {
        mUsuarios = usuarios;
        mCallback = callback;
        mRemove = remove;
    }

    public UsuarioAdapter(List<Usuario> usuarios, ClickCallback callback) {
        mUsuarios = usuarios;
        mCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_member, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mRemove) {
            holder.button.setImageDrawable(holder.button.getContext().getDrawable(R.drawable.ic_remove_circle_black_24dp));
        }

        final Usuario currentUser = mUsuarios.get(position);

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClick(v, currentUser);
            }
        });

        final ImageView teste = holder.photo;
        new DownloadImageTask(teste)
                .execute(currentUser.getUrlFoto());

        holder.name.setText(currentUser.getNomeUsuario());
    }

    @Override
    public int getItemCount() {
        return mUsuarios.size();
    }

    public interface ClickCallback {
        void onClick(View v, Usuario user);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView name;
        ImageButton button;
        ImageView photo;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.card_title);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            button = (ImageButton) itemView.findViewById(R.id.card_button);
            photo = (ImageView) itemView.findViewById(R.id.user);
        }
    }

}
