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
    public static final int ACTION_NONE = 0;
    public static final int ACTION_ADD = 1;
    public static final int ACTION_REMOVE = 2;

    private int mAction = 0;
    private List<Usuario> mUsuarios;
    private UsuarioAdapter.ClickCallback mCallback;

    /**
     * Cria o adapter com o botão de ação escolhido
     *
     * @param usuarios   Lista de usuários
     * @param callback   Função a ser chamada ao pressionar o botao de ação da lista
     * @param actionType O tipo de ação. Deve ser um dos seguintes: {@link #ACTION_NONE},
     *                   {@link #ACTION_ADD}, ou {@link #ACTION_REMOVE}.
     */
    public UsuarioAdapter(List<Usuario> usuarios, ClickCallback callback, int actionType) {
        mUsuarios = usuarios;
        mCallback = callback;
        mAction = actionType;
    }

    /**
     * @param usuarios Lista de usuários
     * @param callback Função a ser chamada ao pressionar o botão de ação da lista
     * @param remove   Se `true`, mostra um botao de remover em vez de adicionar
     * @deprecated Use {@link UsuarioAdapter#UsuarioAdapter(List, ClickCallback, int)} com
     * {@link #ACTION_REMOVE} como terceiro parâmetro
     */
    public UsuarioAdapter(List<Usuario> usuarios, ClickCallback callback, boolean remove) {
        this(usuarios, callback, UsuarioAdapter.ACTION_REMOVE);
    }

    /**
     * Cria o adapter com ação de adicionar usuário
     *
     * @param usuarios Lista de usuários
     * @param callback Função a ser chamada ao pressionar o botao de adicionar usuário
     */
    public UsuarioAdapter(List<Usuario> usuarios, ClickCallback callback) {
        this(usuarios, callback, UsuarioAdapter.ACTION_ADD);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_member, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (mAction) {
            case ACTION_REMOVE:
                holder.button.setImageDrawable(holder.button.getContext().getDrawable(R.drawable.ic_remove_circle_black_24dp));
            case ACTION_ADD:
                holder.button.setVisibility(View.VISIBLE);
                break;
            default:
            case ACTION_NONE:
                holder.button.setVisibility(View.GONE);
                break;
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
            name = (TextView) itemView.findViewById(R.id.user_name);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            button = (ImageButton) itemView.findViewById(R.id.user_action);
            photo = (ImageView) itemView.findViewById(R.id.user_picture);
        }
    }

}
