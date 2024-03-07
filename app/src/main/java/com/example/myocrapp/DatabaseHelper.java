package com.example.myocrapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ingredients_database.db";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EXPLANATION = "explanation";
    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables for both vegan and maybe vegan ingredients
        db.execSQL(CreateTable.VeganIngredients.SQL_CREATE_TABLE);
        db.execSQL(CreateTable.MaybeVeganIngredients.SQL_CREATE_TABLE);
        db.execSQL(CreateTable.VegetarianIngredients.SQL_CREATE_TABLE);
        db.execSQL(CreateTable.MaybeVegetarianIngredients.SQL_CREATE_TABLE);
        db.execSQL(CreateTable.TreeNutIngredients.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables if they exist and recreate them
        db.execSQL("DROP TABLE IF EXISTS " + CreateTable.VeganIngredients.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CreateTable.MaybeVeganIngredients.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CreateTable.VegetarianIngredients.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CreateTable.MaybeVegetarianIngredients.TABLE_NAME);
        onCreate(db);
    }

    public String getAllIngredients(String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder result = new StringBuilder();

        String[] projection = {COLUMN_NAME, COLUMN_EXPLANATION};

        Cursor cursor = db.query(
                tableName,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String explanation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPLANATION));
            result.append("Name: ").append(name).append(", Explanation: ").append(explanation).append("\n");
        }
        cursor.close();
        return result.toString();
    }

    public void insertDataFromTextFile(String fileName, String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();
        BufferedReader reader = null;

        try {
            InputStream inputStream = context.getAssets().open(fileName);
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!fileName.equals("tree_nut_ingredients.txt")) {
                    String[] parts = line.split(":");
                    String ingredientName = parts[0].trim();
                    String explanation = parts[1].trim();

                    ContentValues values = new ContentValues();
                        //                values.put("_id", (String) null);
                    values.put("name", ingredientName);
                    values.put("explanation", explanation);

                    db.insert(tableName, null, values);

                    Log.d("Database Insert", "Inserted ingredient " + ingredientName + " into table " + tableName);
                } else {
                    ContentValues values = new ContentValues();

                    values.put("name", line);

                    db.insert(tableName, null, values);

                    Log.d("Database Insert", "Inserted ingredient " + line + " into table " + tableName);
                }
            }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

    }
}
