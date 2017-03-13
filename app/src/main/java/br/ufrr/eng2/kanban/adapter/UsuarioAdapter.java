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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import br.ufrr.eng2.kanban.R;
import br.ufrr.eng2.kanban.model.Usuario;

/**
 * Created by rafaelsa on 12/03/17.
 */

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder>  {
    private List<Usuario> mUsuarios;
    private UsuarioAdapter.ClickCallback mCallback;

    public UsuarioAdapter(List<Usuario> usuarios, ClickCallback callback) {
        mUsuarios = usuarios;
        mCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_card_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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

    public interface ClickCallback{
        void onClick(View v, Usuario user);
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView name;
        Button button;
        ImageView photo;
        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.card_title);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            button = (Button) itemView.findViewById(R.id.card_button);
            photo = (ImageView) itemView.findViewById(R.id.user);
        }
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
