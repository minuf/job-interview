package com.example.jorge.job_interview.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jorge on 22/04/16.
 */
public class MSQLiteHelper extends SQLiteOpenHelper {

    //Sentencia SQL para crear la tabla de runners
    String sqlCreateRunners = "CREATE TABLE IF NOT EXISTS runners (user_id VARCHAR(45) NOT NULL, "+
            "user_name VARCHAR(45) NOT NULL, user_photo VARCHAR(255), PRIMARY KEY (user_id))"+
            "DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;";

    String sqlCreateRuns = "CREATE TABLE IF NOT EXISTS runs (run_id VARCHAR(45) NOT NULL, dateTime VARCHAR(45) NOT NULL, "+
            "distance DOUBLE, pace_hour INTEGER, pace_minute INTEGER, pace_seconds INTEGER,"+
            "duration VARCHAR(45), country VARCHAR(45), state VARCHAR(45), city VARCHAR(45)"+
            "runner_photo_thumb VARCHAR(255), likes INTEGER, user_id VARCHAR(45)"+
            "PRIMARY KEY (run_id), FOREIGN KEY (user_id) REFERENCES runners(user_id))"+
            "DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;";

    String sqlCreateComments = "CREATE TABLE IF NOT EXISTS comments (comment_id VARCHAR(45) NOT NULL, user_id VARCHAR(45)"+
            "run_id VARCHAR(45) NOT NULL, user_photo VARCHAR(255), user_name VARCHAR(45), comment_text VARCHAR(45),"+
            " PRIMARY KEY (user_id), FOREIGN KEY (user_id) REFERENCES runners(user_id))"+
            "DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;";

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


    }
}
