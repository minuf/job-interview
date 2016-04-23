package com.example.jorge.job_interview.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jorge on 22/04/16.
 */
public class MSQLiteHelper extends SQLiteOpenHelper {

    //Sentencia SQL para crear la tabla de runners
    String sqlCreateRunners = "CREATE TABLE IF NOT EXISTS runners (user_id TEXT NOT NULL, "+
            "user_name TEXT NOT NULL, user_photo TEXT, PRIMARY KEY (user_id)) ";

    String sqlCreateRuns = "CREATE TABLE IF NOT EXISTS runs (run_id TEXT NOT NULL, dateTime TEXT NOT NULL, "+
            "distance DOUBLE, pace_hour INTEGER, pace_minute INTEGER, pace_seconds INTEGER, "+
            "duration TEXT, country TEXT, state TEXT, city TEXT, "+
            "runner_photo_thumb TEXT, likes INTEGER, user_id TEXT, "+
            "PRIMARY KEY (run_id), FOREIGN KEY (user_id) REFERENCES runners(user_id))";

    String sqlCreateComments = "CREATE TABLE IF NOT EXISTS comments (comment_id TEXT NOT NULL, user_id TEXT, "+
            "run_id TEXT NOT NULL, user_photo TEXT, user_name TEXT, comment_text TEXT,"+
            " PRIMARY KEY (comment_id), FOREIGN KEY (run_id) REFERENCES runs(run_id)) ";
    String sqlCreate = "CREATE TABLE Usuario (codigo INTEGER, nombre TEXT)";

    public MSQLiteHelper(Context contexto, String nombre,
                                SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de las tablas
        db.execSQL(sqlCreateRunners);
        db.execSQL(sqlCreateRuns);
        db.execSQL(sqlCreateComments);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.

        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Usuarios");

        //Se crea la nueva versión de la tabla
        onCreate(db);
    }
}

//http://developer.android.com/intl/es/training/basics/data-storage/databases.html