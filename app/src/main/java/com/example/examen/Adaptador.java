package com.example.examen;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.examen.Detalle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Adaptador extends RecyclerView.Adapter<Adaptador.UserViewHolder> {

    private JSONArray usersArray;
    private Context context;

    // Constructor para recibir los datos y el contexto
    public Adaptador(JSONArray usersArray, Context context) {
        this.usersArray = usersArray;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla (crea) la vista de cada item a partir del layout item_user.xml
        View view = LayoutInflater.from(context).inflate(R.layout.user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        try {
            // Obtiene el objeto JSON del usuario para la posición actual
            JSONObject user = usersArray.getJSONObject(position);

            // Extrae los datos del objeto JSON
            String firstName = user.getJSONObject("name").getString("first");
            String lastName = user.getJSONObject("name").getString("last");
            String fullName = firstName + " " + lastName;

            String email = user.getString("email");
            String country = user.getJSONObject("location").getString("country");
            String imageUrl = user.getJSONObject("picture").getString("thumbnail");

            // Asigna los datos a las vistas del ViewHolder
            holder.txtName.setText(fullName);
            holder.txtEmail.setText(email);
            holder.txtCountry.setText(country);

            // Usa la librería Glide para cargar la imagen desde la URL
            Glide.with(context)
                    .load(imageUrl)
                    .circleCrop() // Opcional: para hacer la imagen circular
                    .into(holder.imgProfile);

            // Configura el OnClickListener para cada item
            holder.itemView.setOnClickListener(v -> {
                // Crea un Intent para abrir DetailActivity
                Intent intent = new Intent(context, Detalle.class);
                // Envía TODOS los datos del usuario como un String a la siguiente actividad
                intent.putExtra("userData", user.toString());
                context.startActivity(intent);
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        // Retorna el número total de items en la lista
        return usersArray.length();
    }

    // El ViewHolder que contiene las vistas de cada item
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile;
        TextView txtName, txtEmail, txtCountry;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgUserProfile);
            txtName = itemView.findViewById(R.id.txtUserName);
            txtEmail = itemView.findViewById(R.id.txtUserEmail);
            txtCountry = itemView.findViewById(R.id.txtUserCountry);
        }
    }
}