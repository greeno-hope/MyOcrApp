package com.example.myocrapp;

public class CreateTable {
    private CreateTable() {}

    public static class VeganIngredients {
        public static final String TABLE_NAME = "vegan_ingredients";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EXPLANATION = "explanation";
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_NAME_NAME + " TEXT PRIMARY KEY, " + COLUMN_NAME_EXPLANATION + " TEXT)";
    }

    public static class MaybeVeganIngredients {
        public static final String TABLE_NAME = "maybe_vegan_ingredients";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EXPLANATION = "explanation";
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("  + COLUMN_NAME_NAME + " TEXT PRIMARY KEY, " + COLUMN_NAME_EXPLANATION + " TEXT)";
    }

    public static class VegetarianIngredients {
        public static final String TABLE_NAME = "vegetarian_ingredients";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EXPLANATION = "explanation";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_NAME_NAME + " TEXT PRIMARY KEY, "  + COLUMN_NAME_EXPLANATION + " TEXT)";
    }

    public static class MaybeVegetarianIngredients {
        public static final String TABLE_NAME = "maybe_vegetarian_ingredients";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EXPLANATION = "explanation";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_NAME_NAME + " TEXT PRIMARY KEY , "  + COLUMN_NAME_EXPLANATION + " TEXT)";
    }

    public static class TreeNutIngredients {
        public static final String TABLE_NAME = "tree_nut_ingredients";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_NAME_NAME + " TEXT PRIMARY KEY)";

    }
}
