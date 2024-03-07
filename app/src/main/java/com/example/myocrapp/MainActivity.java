package com.example.myocrapp;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.style.ForegroundColorSpan;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ImageButton button_camera, button_library, back_button;
    TextView textview_data, text_explanation;
    private LinearLayout buttonLayout;
    private ScrollView scrollView;
    private static final int REQUEST_CAMERA_CODE = 100;
    private String fileName, fileName2;
    private boolean nut_allergy = false;
    private String foodType, maybeFoodType;
    private String whichFile;

    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            Bitmap cropped = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));
            getTextFromImage(cropped);
        }
    });

    private DatabaseHelper dbHelper;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // inserts data from text file to the sqlite database
        dbHelper.insertDataFromTextFile("vegan_ingredients.txt", CreateTable.VeganIngredients.TABLE_NAME);
        dbHelper.insertDataFromTextFile("maybe_vegan.txt", CreateTable.MaybeVeganIngredients.TABLE_NAME);
        dbHelper.insertDataFromTextFile("vegetarian_ingredients.txt", CreateTable.VegetarianIngredients.TABLE_NAME);
        dbHelper.insertDataFromTextFile("maybe_vegetarian.txt", CreateTable.MaybeVegetarianIngredients.TABLE_NAME);
        dbHelper.insertDataFromTextFile("tree_nut_ingredients.txt", CreateTable.TreeNutIngredients.TABLE_NAME);

        String result = dbHelper.getAllIngredients(CreateTable.VeganIngredients.TABLE_NAME);
        String result2 = dbHelper.getAllIngredients(CreateTable.MaybeVeganIngredients.TABLE_NAME);
        String result3 = dbHelper.getAllIngredients(CreateTable.VegetarianIngredients.TABLE_NAME);
        String result4 = dbHelper.getAllIngredients(CreateTable.MaybeVegetarianIngredients.TABLE_NAME);

        Log.d("RESULTTTTT", "Vegan= " + result);
        Log.d("RESULTTTTT", "maybe Vegan= " + result2);
        Log.d("RESULTTTTT", "Vege= " + result3);
        Log.d("RESULTTTTT", "Maybe Vege= " + result4);

        buttonLayout = findViewById(R.id.button_layout);
        scrollView = findViewById(R.id.scrollView);
        textview_data = findViewById(R.id.text_data);
        button_camera = findViewById(R.id.button_camera);
        button_library = findViewById(R.id.button_library);
        back_button = findViewById(R.id.button_back);
        text_explanation = findViewById(R.id.text_explanation);


        if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    android.Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }

        button_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button_camera) {
                    CropImageOptions cropImageOptions = new CropImageOptions();
                    cropImageOptions.imageSourceIncludeGallery = false;
                    cropImageOptions.imageSourceIncludeCamera = true;
                    CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(null, cropImageOptions);
                    cropImage.launch(cropImageContractOptions);
                }
            }
        });

        button_library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.button_library){
                    CropImageOptions cropImageOptions = new CropImageOptions();
                    cropImageOptions.imageSourceIncludeGallery = true;
                    cropImageOptions.imageSourceIncludeCamera = false;
                    CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(null, cropImageOptions);
                    cropImage.launch(cropImageContractOptions);
                }
            }
        });

        // When back button hit
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonLayout.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                textview_data.setVisibility(View.GONE);
                back_button.setVisibility(View.GONE);
                button_camera.setVisibility(View.GONE);
                button_library.setVisibility(View.GONE);
                text_explanation.setVisibility(View.GONE);
                textview_data.setText("Hello, capture or choose an image from gallery to read text");
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void onPreferenceSelected(View view) {
            if (view.getId() == R.id.button_vegan) {
                // Fetch the vegan table names
                fileName = CreateTable.VeganIngredients.TABLE_NAME;
                fileName2 = CreateTable.MaybeVeganIngredients.TABLE_NAME;

                foodType = "NOT VEGAN FRIENDLY!";
                maybeFoodType = "Maybe not Vegan!";
                nut_allergy = false;

                textview_data.setText(Html.fromHtml("Capture image with camera or upload photo from library <br><br>THIS APP ISN'T PERFECT<br>USE AT OWN RISK <br><br>Definite non-Vegan ingredients will be made <font color='#cc0029'>red</font>, possible non-vegan ingredients will be made <font color='#9370DB'>purple</font>"));

            } else if (view.getId() == R.id.button_vegetarian) {
                // fetch the vegetarian filenames
                fileName = CreateTable.VegetarianIngredients.TABLE_NAME;
                fileName2 = CreateTable.MaybeVegetarianIngredients.TABLE_NAME;

                foodType = "NOT VEGETARIAN FRIENDLY!";
                maybeFoodType = "Maybe not Vegetarian!";
                nut_allergy = false;

                textview_data.setText(Html.fromHtml("Capture image with camera or upload photo from library <br><br>THIS APP ISN'T PERFECT<br>USE AT OWN RISK <br><br>Definite non-Vegetarian ingredients will be made <font color='#cc0029'>red</font>, possible non-vegetarian ingredients will be made <font color='#9370DB'>purple</font>"));

            } else if (view.getId() == R.id.button_nut_allergy) {
                fileName = CreateTable.TreeNutIngredients.TABLE_NAME;

                nut_allergy = true;
                foodType = "CONTAINS NUTS!";
                maybeFoodType = "Maybe contains nuts!";
                textview_data.setText(Html.fromHtml("Capture image with camera or upload photo from library <br><br>THIS APP ISN'T PERFECT<br>USE AT OWN RISK <br><br>Tree nut related ingredients will be made <font color='#cc0029'>red</font>"));
            }

            // Show the ScrollView, TextView, and Capture button when one of the buttons is pressed
            buttonLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            textview_data.setVisibility(View.VISIBLE);
            back_button.setVisibility(View.VISIBLE);
            button_camera.setVisibility(View.VISIBLE);
            button_library.setVisibility(View.VISIBLE);


            text_explanation.setText("Explanation Here");
    }

    @SuppressLint("SetTextI18n")
    private void getTextFromImage(Bitmap bitmap) {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(MainActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            if (textBlockSparseArray.size() == 0) {
                Toast.makeText(MainActivity.this, "Text Unrecognised", Toast.LENGTH_SHORT).show();
                textview_data.setText("Text Unrecognised");
            } else {
                textview_data.setText("");
                Set<String> definiteIngredients = new HashSet<>();
                Set<String> maybeIngredients = new HashSet<>();
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                    Cursor cursorDefinite = db.query(
                            fileName,
                            new String[]{"name"},
                            null,
                            null,
                            null,
                            null,
                            null
                    );
                    while (cursorDefinite.moveToNext()) {
                        definiteIngredients.add(cursorDefinite.getString(0));
                    }
                if(!nut_allergy) {
                    cursorDefinite.close();
                    Cursor cursorMaybe = db.query(
                            fileName2,
                            new String[]{"name"},
                            null,
                            null,
                            null,
                            null,
                            null
                    );
                    while (cursorMaybe.moveToNext()) {
                        maybeIngredients.add(cursorMaybe.getString(0));
                    }
                    cursorMaybe.close();
                }

                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

                for (int i = 0; i < textBlockSparseArray.size(); i++) {
                    TextBlock textBlock = textBlockSparseArray.valueAt(i);
                    String blockValue = textBlock.getValue();

                    String[] lines = blockValue.split("\n");
                    for (String line : lines) {
                        boolean notIngredient = false;
                        boolean notMaybeIngredient = false;

                        SpannableStringBuilder modifiedLine = new SpannableStringBuilder(line);

                        if(!nut_allergy) {
                            for (String maybeIngredient : maybeIngredients) {
                                Log.d("MaybeIngredient", maybeIngredient);
                                // Check if the maybe ingredient is present in the modified line
                                if (modifiedLine.toString().toLowerCase().contains(maybeIngredient.toLowerCase())) {
                                    notMaybeIngredient = true;
                                    // Highlight the matched phrase
                                    int startIndex = modifiedLine.toString().toLowerCase().indexOf(maybeIngredient.toLowerCase());
                                    int endIndex = startIndex + maybeIngredient.length();
                                    modifiedLine.setSpan(new ClickableSpan() {
                                        @Override
                                        public void onClick(View view) {
                                            // when the ingredient is clicked
                                            whichFile = fileName2;
                                            String explanation = getExplanationForIngredient(maybeIngredient);
                                            if (explanation != null) {
                                                // Display the explanation
                                                text_explanation.setText(maybeIngredient + ": " + explanation);
                                                text_explanation.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    // turns text purple
                                    modifiedLine.setSpan(new ForegroundColorSpan(Color.parseColor("#9370DB")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                            }
                        }

                        if (!notMaybeIngredient) {
                            for (String definiteIngredient : definiteIngredients) {
                                Log.d("Ingredient", definiteIngredient);
                                // Checks if the ingredient is present in the modified line
                                if (modifiedLine.toString().toLowerCase().contains(definiteIngredient.toLowerCase())) {
                                    notIngredient = true;
                                    // Highlights the matched phrase
                                    int startIndex = modifiedLine.toString().toLowerCase().indexOf(definiteIngredient.toLowerCase());
                                    int endIndex = startIndex + definiteIngredient.length();
                                    modifiedLine.setSpan(new ClickableSpan() {
                                        @Override
                                        public void onClick(View view) {
                                            if(!nut_allergy) {
                                                whichFile = fileName;
                                                String explanation = getExplanationForIngredient(definiteIngredient);
                                                if (explanation != null) {
                                                    // Display the explanation
                                                    text_explanation.setText(definiteIngredient + ": " + explanation);
                                                    text_explanation.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }
                                    }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    // turns text red
                                    modifiedLine.setSpan(new ForegroundColorSpan(Color.parseColor("#cc0029")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                            }
                        }

                        if (!notIngredient && !notMaybeIngredient) {
                            // no ingredients then just prints the text
                            spannableStringBuilder.append(line).append("\n");
                        } else {
                            // if ingredients then it makes changes to the text
                            spannableStringBuilder.append(modifiedLine).append("\n");
                        }

                        // pop ups
                        if (notIngredient) {
                            Toast.makeText(MainActivity.this, foodType, Toast.LENGTH_SHORT).show();
                        } else if (notMaybeIngredient) {
                            Toast.makeText(MainActivity.this, maybeFoodType, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                // sends the changed or unchanged text to the textview
                textview_data.setText(spannableStringBuilder);
                textview_data.setMovementMethod(LinkMovementMethod.getInstance()); // Make links clickable
            }
        }
    }

    private String getExplanationForIngredient(String ingredient) {
        // queries the database for the explanation using the ingredient name
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {"explanation"};
        String selection = "name = ?";
        String[] selectionArgs = {ingredient};

        Cursor cursor = db.query(whichFile, projection, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String explanation = cursor.getString(cursor.getColumnIndexOrThrow("explanation"));
                cursor.close();
                Log.d("Explanation", "Explanation found for " + ingredient + ": " + explanation);
                return explanation;
            } else {
                Log.d("Explanation", "No explanation found for " + ingredient);
                return "No explanation found";
            }
        } else {
            Log.d("Database", "Cursor is null");
            return "Cursor is null";
        }
    }
}