package com.example.pexu.proyecto04;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData(String nombre, String telefono, String email, byte[] imagen, String latitud, String longitud){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO Contactos VALUES (NULL, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, nombre);
        statement.bindString(2, telefono);
        statement.bindString(3, email);
        statement.bindBlob(4, imagen);
        statement.bindString(5, latitud);
        statement.bindString(6, longitud);
        statement.executeInsert();
    }

    //para actualizar los datos de la base de datos 2 FASE
    public void updateData(String nombre, String telefono, String email, byte[] imagen, String latitud, String longitud, int id) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE Contactos SET nombre = ?, telefono = ?, email = ?, imagen = ?, latitud = ?, longitud = ? WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, nombre);
        statement.bindString(2, telefono);
        statement.bindString(3, email);
        statement.bindBlob(4, imagen);
        statement.bindString(5, latitud);
        statement.bindString(6, longitud);
        statement.bindDouble(7, (double)id);

        statement.execute();
        database.close();
    }
    //Borrar data en sqlite con el campo id
    public  void deleteData (int id){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM Contactos WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double)id);

        statement.execute();
        database.close();

    }


    public Cursor getData(String sql){ //obtener toda la data de la base de datos
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}