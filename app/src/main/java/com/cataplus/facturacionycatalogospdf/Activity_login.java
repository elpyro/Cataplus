package com.cataplus.facturacionycatalogospdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cataplus.facturacionycatalogospdf.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.firebase.ui.auth.AuthUI;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import modelos.Modelo_usuario;

// crear login fon facebook y gmail https://www.youtube.com/watch?v=IwdjCApjIzA&t=987s
public class Activity_login extends AppCompatActivity implements FacebookCallback<LoginResult> {

    //autenticacion https://www.youtube.com/watch?v=IwdjCApjIzA&t=442s
    private Button button_cerrar_sesion, button_respaldarbd, button_restaurar_bd;
    private FirebaseAuth firebaseAutenticacion;
    private TextView editText_nombre, edittext_label;
    private ImageView imageView_foto_usuario;
    private FirebaseAuth.AuthStateListener authStateListener;
    public static String user_id="";
    public static String premium="false";
    public static Modelo_usuario datos_usuario;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private String fecha_base_datos;



    public static final int REQUEST_CODE=1234;
    List<AuthUI.IdpConfig> provinder= Arrays.asList(
           // new AuthUI.IdpConfig.FacebookBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build()
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        button_cerrar_sesion=findViewById(R.id.login);
        button_respaldarbd=findViewById(R.id.button2);
        button_restaurar_bd=findViewById(R.id.button3);
        editText_nombre=findViewById(R.id.textView6);
        edittext_label=findViewById(R.id.textView4);
        //loginButton = (LoginButton) findViewById(R.id.login_button);

        //callbackManager= CallbackManager.Factory.create();

        //loginButton.registerCallback(callbackManager,this);
        //loginButton.setReadPermissions("email");


        firebaseAutenticacion =FirebaseAuth.getInstance();

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAutenticacion.getCurrentUser();
                if(user!=null){



                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                    Date date = new Date();
                    String fecha_hora = dateFormat.format(date);

                    // Write a message to the database

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    FirebaseApp.initializeApp(Activity_login.this);

                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

                    Query dataQuery = dbRef.child("Usuarios").orderByChild("id").equalTo(user.getUid());
                    dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot dataSnapshot_objeto: dataSnapshot.getChildren()) {
                                    // si existe


                                    datos_usuario = dataSnapshot_objeto.getValue(Modelo_usuario.class);
                                    datos_usuario.setId(datos_usuario.getId());
                                    datos_usuario.setNombre(datos_usuario.getNombre());
                                    datos_usuario.setCorreo(datos_usuario.getCorreo());
                                    datos_usuario.setFecha_registro(datos_usuario.getFecha_registro());
                                    datos_usuario.setFecha_ultima_ingreso(fecha_hora);
                                    datos_usuario.setPremium(datos_usuario.getPremium());
                                    datos_usuario.setBase_datos(datos_usuario.getBase_datos());
                                    datos_usuario.setFecha_base_datos(datos_usuario.getFecha_base_datos());
                                    fecha_base_datos=datos_usuario.getFecha_base_datos();

                                    editText_nombre.setText(user.getDisplayName()+"\n"+"Ultimo Respaldo: "+fecha_base_datos);
                                    if (datos_usuario.getPremium().equals("true")){
                                        button_restaurar_bd.setVisibility(View.VISIBLE);
                                    }else{
                                        button_restaurar_bd.setVisibility(View.INVISIBLE);
                                    }
                                    myRef.child("Usuarios").child(user.getUid()).setValue(datos_usuario);
                                }
                            }else{
                                Modelo_usuario datos_usuario= new Modelo_usuario();
                                // datos_usuario.setId(UUID.randomUUID().toString());//crea un id unico
                                datos_usuario.setId(user.getUid());
                                datos_usuario.setNombre(user.getDisplayName());
                                datos_usuario.setCorreo(user.getEmail());
                                datos_usuario.setFecha_registro(fecha_hora);
                                datos_usuario.setFecha_ultima_ingreso(fecha_hora);
                                datos_usuario.setPremium("false");
                                datos_usuario.setBase_datos("");
                                datos_usuario.setFecha_base_datos("");

                                myRef.child("Usuarios").child(user.getUid()).setValue(datos_usuario);
                                button_restaurar_bd.setVisibility(View.INVISIBLE);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else{
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                            .setAvailableProviders(provinder)
                            .setIsSmartLockEnabled(false).build(),REQUEST_CODE);
                }
            }
        };

        button_restaurar_bd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_login.this);
                builder.setTitle(getString(R.string.Atencion));
                builder.setMessage("Esta acción sobreescribe los datos actuales. ¿Esta seguro?");

                builder.setPositiveButton(getString(R.string.Si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ProgressDialog progressDialog;
                        progressDialog = ProgressDialog.show(Activity_login.this, "Recuperando base de datos",
                                "descargando", true);
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        // Create a storage reference from our app
                        StorageReference storageRef = storage.getReference();

                        StorageReference httpsReference = storage.getReferenceFromUrl(datos_usuario.getBase_datos());
                        String pathDB = getDatabasePath("basedatos").toString();
                        File rootPath = new File(pathDB);
                        if(!rootPath.exists()) {
                            rootPath.mkdirs();
                        }

                        final Uri localFile =Uri.fromFile(new File(pathDB));

                        httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(Activity_login.this, "Base de datos recuperada", Toast.LENGTH_LONG).show();
                                progressDialog.cancel();
                                //  updateDb(timestamp,localFile.toString(),position);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(Activity_login.this, "Error de importacion "+pathDB, Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                            }
                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                //calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                //displaying percentage in progress dialog
                                progressDialog.setMessage("Downloading " + ((int) progress) + "%...");



                            }
                        });
                    }
                });
                builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        button_respaldarbd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ProgressDialog progressDialog;
//                progressDialog = ProgressDialog.show(Activity_login.this, "Creando copia de seguridad",
//                        "Subiendo", true);
//                progressDialog.hide();



                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                Date date = new Date();
                String fecha_hora = dateFormat.format(date);

                String pathDB = getDatabasePath("basedatos").toString();


                DatabaseReference myRefencia;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                myRefencia= database.getReference("Usuarios");


                Query dataQuery = myRefencia.orderByChild("id").equalTo(firebaseAutenticacion.getUid());
                dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dataSnapshot_objeto: dataSnapshot.getChildren()) {
                                // si existe

                                Modelo_usuario datos_usuario = dataSnapshot_objeto.getValue(Modelo_usuario.class);
                                datos_usuario.setId(datos_usuario.getId());
                                datos_usuario.setNombre(datos_usuario.getNombre());
                                datos_usuario.setCorreo(datos_usuario.getCorreo());
                                datos_usuario.setFecha_registro(datos_usuario.getFecha_registro());
                                datos_usuario.setFecha_ultima_ingreso(datos_usuario.getFecha_ultima_ingreso());
                                datos_usuario.setPremium(datos_usuario.getPremium());
                                String premium=datos_usuario.getPremium();
                                if (premium.equals("false")){
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_login.this);
        builder.setTitle("Copia de seguridad");
        builder.setMessage("¿Desea ser usuario premium?");

        builder.setPositiveButton(getString(R.string.Si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {









//                progressDialog.show();
//            datos_usuario.setPremium("true");
//                button_restaurar_bd.setVisibility(View.VISIBLE);
//                edittext_label.setVisibility(View.VISIBLE);
//
//                Uri FileUri = Uri.fromFile(new File(pathDB));
//                StorageReference Folder = FirebaseStorage.getInstance().getReference().child(firebaseAutenticacion.getUid());
//                final StorageReference file_name = Folder.child("file"+FileUri.getLastPathSegment());
//                file_name.putFile(FileUri).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {
//
//                    datos_usuario.setBase_datos(String.valueOf(uri));
//                    datos_usuario.setFecha_base_datos(fecha_hora);
//                    myRefencia.child(firebaseAutenticacion.getUid()).setValue(datos_usuario);
//                    progressDialog.cancel();
//                    Toast.makeText(Activity_login.this, "Base de datos respaldada", Toast.LENGTH_SHORT).show();
//                }));



                ProgressDialog progressDialog;
                progressDialog = ProgressDialog.show(Activity_login.this, "Creando copia de seguridad",
                        "descargando", true);



                Uri FileUri = Uri.fromFile(new File(pathDB));
                StorageReference Folder = FirebaseStorage.getInstance().getReference().child(firebaseAutenticacion.getUid());
                final StorageReference file_name = Folder.child("file"+FileUri.getLastPathSegment());


                file_name.putFile(FileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        file_name.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {


                                datos_usuario.setBase_datos(String.valueOf(uri));
                                datos_usuario.setFecha_base_datos(fecha_hora);
                                myRefencia.child(firebaseAutenticacion.getUid()).setValue(datos_usuario);
                                Toast.makeText(Activity_login.this, "Base de datos respaldada", Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Activity_login.this, "error", Toast.LENGTH_SHORT).show();

                    }
                })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100* taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });







            }
        });
        builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
                                }else{



                                    //progressDialog.show();

//                                    Uri FileUri = Uri.fromFile(new File(pathDB));
//                                    StorageReference Folder = FirebaseStorage.getInstance().getReference().child(firebaseAutenticacion.getUid());
//                                    final StorageReference file_name = Folder.child("file"+FileUri.getLastPathSegment());
//                                    file_name.putFile(FileUri).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {
//
//
//
//                                        progressDialog.cancel();
//                                        Toast.makeText(Activity_login.this, "Base de datos respaldada", Toast.LENGTH_SHORT).show();
//                                    }));

                                    ProgressDialog progressDialog;
                                    progressDialog = ProgressDialog.show(Activity_login.this, "Creando copia de seguridad",
                                            "descargando", true);



                                    Uri FileUri = Uri.fromFile(new File(pathDB));
                                    StorageReference Folder = FirebaseStorage.getInstance().getReference().child(firebaseAutenticacion.getUid());
                                    final StorageReference file_name = Folder.child("file"+FileUri.getLastPathSegment());


                                    file_name.putFile(FileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            file_name.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {


                                                    datos_usuario.setBase_datos(String.valueOf(uri));
                                                    datos_usuario.setFecha_base_datos(fecha_hora);
                                                    myRefencia.child(firebaseAutenticacion.getUid()).setValue(datos_usuario);
                                                    Toast.makeText(Activity_login.this, "Base de datos respaldada", Toast.LENGTH_SHORT).show();
                                                    progressDialog.cancel();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(Activity_login.this, "error", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                    double progress = (100* taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                                }
                                            });








                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Activity_login.this, "Problemas de conexion", Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });
        button_cerrar_sesion.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                cerrar_sesion(null);
            }
        });





    }

    public boolean copyFile(String from, String to) {
        boolean result = false;
        try{
            File dir = new File(to.substring(0, to.lastIndexOf('/')));
            dir.mkdirs();
            File tof = new File(dir, to.substring(to.lastIndexOf('/') + 1));
            int byteread;
            File oldfile = new File(from);
            if(oldfile.exists()){
                InputStream inStream = new FileInputStream(from);
                FileOutputStream fs = new FileOutputStream(tof);
                byte[] buffer = new byte[1024];
                while((byteread = inStream.read(buffer)) != -1){
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
                Toast.makeText(Activity_login.this, "Base de datos respaldada", Toast.LENGTH_SHORT).show();
            }
            result = true;
        }catch (Exception e){
            Toast.makeText(Activity_login.this, "Error al respaldar base de datos", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        callbackManager.onActivityResult(requestCode,resultCode,data);
//        super.onActivityResult(requestCode, resultCode, data);
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAutenticacion.addAuthStateListener(authStateListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        firebaseAutenticacion.removeAuthStateListener(authStateListener);
    }

    public void cerrar_sesion(View v){
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Activity_login.this, "Sesion Cerrada", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    @Override
    public void onSuccess(LoginResult loginResult) {

        Toast.makeText(this, "Excelente"+ Profile.getCurrentProfile(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {
        Toast.makeText(this, "cancelado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(FacebookException error) {
        Toast.makeText(this, "error :(", Toast.LENGTH_SHORT).show();
    }
}