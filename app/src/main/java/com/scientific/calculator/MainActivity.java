package com.scientific.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private TextView expressionView, resultView;
    private GridLayout grid;
    private String expression = "";
    private String lastAnswer = "0";
    private boolean degreeMode = true;

    private SharedPreferences preferences;

    private final String[] buttons = {
            "DEG", "π", "e", "AC", "⌫",
            "sin", "cos", "tan", "√", "÷",
            "asin", "acos", "atan", "ln", "×",
            "log", "(", ")", "x²", "−",
            "7", "8", "9", "xʸ", "+",
            "4", "5", "6", "1/x", "%",
            "1", "2", "3", "n!", "=",
            "0", ".", "Ans", "∛", "Copy"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("calculator", MODE_PRIVATE);
        lastAnswer = preferences.getString("answer", "0");

        createInterface();
    }

    private void createInterface() {

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(16, 16, 16, 16);
        main.setBackgroundColor(Color.rgb(15, 18, 30));

        expressionView = new TextView(this);
        expressionView.setTextColor(Color.LTGRAY);
        expressionView.setTextSize(20);
        expressionView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        expressionView.setPadding(10, 20, 10, 10);

        resultView = new TextView(this);
        resultView.setTextColor(Color.WHITE);
        resultView.setTextSize(36);
        resultView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        resultView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        resultView.setPadding(10, 10, 10, 25);
        resultView.setText("0");

        LinearLayout display = new LinearLayout(this);
        display.setOrientation(LinearLayout.VERTICAL);
        display.setPadding(15, 20, 15, 20);
        display.setBackgroundColor(Color.rgb(27, 32, 48));

        display.addView(expressionView);
        display.addView(resultView);

        main.addView(display, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 180));

        grid = new GridLayout(this);
        grid.setColumnCount(5);
        grid.setRowCount(8);
        grid.setPadding(0, 15, 0, 0);

        for (String text : buttons) {
            Button b = new Button(this);
            b.setText(text);
            b.setTextSize(16);
            b.setTextColor(Color.WHITE);
            b.setAllCaps(false);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(4, 4, 4, 4);

            b.setLayoutParams(params);
            b.setBackgroundColor(getButtonColor(text));

            b.setOnClickListener(v -> handleButton(text));

            grid.addView(b);
        }

        main.addView(grid, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        setContentView(main);
    }

    private int getButtonColor(String text) {

        if (text.equals("="))
            return Color.rgb(46, 204, 113);

        if (text.equals("AC"))
            return Color.rgb(231, 76, 60);

        if (text.equals("⌫") || text.equals("+") ||
                text.equals("−") || text.equals("×") ||
                text.equals("÷"))
            return Color.rgb(255, 159, 67);

        if (text.equals("Copy"))
            return Color.rgb(60, 130, 200);

        if (text.equals("DEG") || text.equals("%"))
            return Color.rgb(60, 130, 200);

        return Color.rgb(42, 49, 69);
    }

    private void handleButton(String value) {

        switch (value) {

            case "AC":
                expression = "";
                expressionView.setText("");
                resultView.setText("0");
                return;

            case "⌫":
                if (!expression.isEmpty()) {
                    expression = expression.substring(0, expression.length() - 1);
                }
                expressionView.setText(expression);
                return;

            case "=":
                calculate();
                return;

            case "DEG":
                degreeMode = !degreeMode;
                Toast.makeText(this,
                        degreeMode ? "DEG Mode" : "RAD Mode",
                        Toast.LENGTH_SHORT).show();
                return;

            case "Copy":
                copyResult();
                return;

            case "x²":
                expression += "^2";
                break;

            case "xʸ":
                expression += "^";
                break;

            case "1/x":
                expression += "inv(";
                break;

            case "n!":
                expression += "!";
                break;

            case "√":
                expression += "sqrt(";
                break;

            case "∛":
                expression += "cbrt(";
                break;

            case "sin":
            case "cos":
            case "tan":
            case "asin":
            case "acos":
            case "atan":
            case "log":
            case "ln":
                expression += value + "(";
                break;

            case "π":
                expression += "pi";
                break;

            case "e":
                expression += "e";
                break;

            case "Ans":
                expression += lastAnswer;
                break;

            case "÷":
                expression += "/";
                break;

            case "×":
                expression += "*";
                break;

            case "−":
                expression += "-";
                break;

            default:
                expression += value;
                break;
        }

        expressionView.setText(expression);
    }

    private void calculate() {

        try {
            MathParser parser = new MathParser(expression, degreeMode);
            double answer = parser.parse();

            if (Double.isNaN(answer) || Double.isInfinite(answer)) {
                throw new ArithmeticException();
            }

            lastAnswer = format(answer);
            resultView.setText(lastAnswer);

            preferences.edit()
                    .putString("answer", lastAnswer)
                    .apply();

        } catch (Exception e) {
            resultView.setText("Math Error");
        }
    }

    private String format(double value) {

        if (value == Math.floor(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }

        return String.format(java.util.Locale.US, "%.10g", value);
    }

    private void copyResult() {

        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        clipboard.setPrimaryClip(
                ClipData.newPlainText("Result", resultView.getText()));

        Toast.makeText(this, "Result copied",
                Toast.LENGTH_SHORT).show();
    }
}
package com.scientific.calculator;

public class MathParser {

    private final String input;
    private final boolean degreeMode;
    private int pos = 0;

    public MathParser(String input, boolean degreeMode) {
        this.input = input.replace(" ", "");
        this.degreeMode = degreeMode;
    }

    public double parse() {
        double result = parseExpression();

        if (pos < input.length()) {
            throw new RuntimeException("Unexpected character");
        }

        return result;
    }

    // الجمع والطرح
    private double parseExpression() {
        double value = parseTerm();

        while (pos < input.length()) {
            char c = input.charAt(pos);

            if (c == '+') {
                pos++;
                value += parseTerm();
            } else if (c == '-') {
                pos++;
                value -= parseTerm();
            } else {
                break;
            }
        }

        return value;
    }

    // الضرب والقسمة
    private double parseTerm() {
        double value = parseUnary();

        while (pos < input.length()) {
            char c = input.charAt(pos);

            if (c == '*') {
                pos++;
                value *= parseUnary();
            } else if (c == '/') {
                pos++;
                value /= parseUnary();
            } else {
                break;
            }
        }

        return value;
    }

    // الإشارة السالبة والموجبة
    private double parseUnary() {

        if (pos < input.length() && input.charAt(pos) == '-') {
            pos++;
            return -parseUnary();
        }

        if (pos < input.length() && input.charAt(pos) == '+') {
            pos++;
            return parseUnary();
        }

        return parsePower();
    }

    // الأس
    private double parsePower() {
        double value = parsePostfix();

        if (pos < input.length() && input.charAt(pos) == '^') {
            pos++;
            double exponent = parseUnary();
            value = Math.pow(value, exponent);
        }

        return value;
    }

    // العمليات اللاحقة مثل ! و%
    private double parsePostfix() {
        double value = parsePrimary();

        while (pos < input.length()) {

            char c = input.charAt(pos);

            if (c == '!') {
                pos++;
                value = factorial(value);
            }
            else if (c == '%') {
                pos++;
                value /= 100.0;
            }
            else {
                break;
            }
        }

        return value;
    }

    // الأرقام والأقواس والدوال
    private double parsePrimary() {

        if (pos >= input.length()) {
            throw new RuntimeException("Unexpected end");
        }

        char c = input.charAt(pos);

        if (c == '(') {
            pos++;
            double value = parseExpression();
            expect(')');
            return value;
        }

        if (Character.isDigit(c) || c == '.') {
            return parseNumber();
        }

        if (Character.isLetter(c)) {
            return parseFunction();
        }

        throw new RuntimeException("Invalid expression");
    }

    // قراءة الرقم
    private double parseNumber() {

        int start = pos;

        while (pos < input.length() &&
                (Character.isDigit(input.charAt(pos))
                        || input.charAt(pos) == '.')) {
            pos++;
        }

        return Double.parseDouble(input.substring(start, pos));
    }

    // قراءة الدوال
    private double parseFunction() {

        int start = pos;

        while (pos < input.length() &&
                Character.isLetter(input.charAt(pos))) {
            pos++;
        }

        String name = input.substring(start, pos);

        if (name.equals("pi")) {
            return Math.PI;
        }

        if (name.equals("e")) {
            return Math.E;
        }

        expect('(');

        double x = parseExpression();

        expect(')');

        double angle = degreeMode ? Math.toRadians(x) : x;

        switch (name) {

            case "sin":
                return Math.sin(angle);

            case "cos":
                return Math.cos(angle);

            case "tan":
                return Math.tan(angle);

            case "asin":
                return convertAngle(Math.asin(x));

            case "acos":
                return convertAngle(Math.acos(x));

            case "atan":
                return convertAngle(Math.atan(x));

            case "sqrt":
                return Math.sqrt(x);

            case "cbrt":
                return Math.cbrt(x);

            case "log":
                return Math.log10(x);

            case "ln":
                return Math.log(x);

            case "inv":
                return 1.0 / x;

            default:
                throw new RuntimeException("Unknown function");
        }
    }

    // تحويل نتيجة الدوال المثلثية العكسية
    private double convertAngle(double value) {
        return degreeMode ? Math.toDegrees(value) : value;
    }

    // العامل المضروب
    private double factorial(double n) {

        if (n < 0 || n != Math.floor(n) || n > 170) {
            throw new RuntimeException("Invalid factorial");
        }

        double result = 1;

        for (int i = 2; i <= (int) n; i++) {
            result *= i;
        }

        return result;
    }

    // التأكد من الأقواس
    private void expect(char expected) {

        if (pos >= input.length() || input.charAt(pos) != expected) {
            throw new RuntimeException("Missing bracket");
        }

        pos++;
    }
}
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="#0F121E"
    android:padding="12dp">

    <!-- شاشة العرض -->

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="180dp"
        android:orientation="vertical"
        android:gravity="center_vertical"
        android:padding="16dp"
        android:background="#1B2030">

        <TextView
            android:id="@+id/expressionView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textColor="#B0B8C8"
            android:textSize="20sp"
            android:gravity="end"
            android:text="" />

        <TextView
            android:id="@+id/resultView"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:text="0"
            android:textColor="#FFFFFF"
            android:textSize="36sp"
            android:textStyle="bold"
            android:gravity="end|center_vertical" />

    </LinearLayout>

    <!-- الأزرار -->

    <GridLayout
        android:id="@+id/calculatorGrid"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:columnCount="5"
        android:rowCount="8"
        android:paddingTop="10dp" />

</LinearLayout>
private void createInterface() {

    setContentView(R.layout.activity_main);

    expressionView = findViewById(R.id.expressionView);
    resultView = findViewById(R.id.resultView);
    grid = findViewById(R.id.calculatorGrid);

    grid.setColumnCount(5);

    for (String text : buttons) {

        Button b = new Button(this);

        b.setText(text);
        b.setTextSize(16);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setBackgroundColor(getButtonColor(text));

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();

        params.width = 0;
        params.height = 0;

        params.columnSpec =
                GridLayout.spec(GridLayout.UNDEFINED, 1f);

        params.rowSpec =
                GridLayout.spec(GridLayout.UNDEFINED, 1f);

        params.setMargins(4, 4, 4, 4);

        b.setLayoutParams(params);

        b.setOnClickListener(v -> handleButton(text));

        grid.addView(b);
    }
}
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:label="Scientific Calculator"
        android:theme="@android:style/Theme.Material.NoActionBar"
        android:usesCleartextTraffic="false">

        <activity
            android:name="com.scientific.calculator.MainActivity"
            android:exported="true">

            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>

        </activity>

    </application>

</manifest>
plugins {
    id 'com.android.application'
}

android {
    namespace 'com.scientific.calculator'
    compileSdk 35

    defaultConfig {
        applicationId 'com.scientific.calculator'
        minSdk 23
        targetSdk 35
        versionCode 1
        versionName "1.0"
    }
}
ScientificCalculator
│
├── app
│   ├── src
│   │   └── main
│   │       ├── java
│   │       │   └── com
│   │       │       └── scientific
│   │       │           └── calculator
│   │       │               ├── MainActivity.java
│   │       │               └── MathParser.java
│   │       │
│   │       ├── res
│   │       │   └── layout
│   │       │       └── activity_main.xml
│   │       │
│   │       └── AndroidManifest.xml
│   │
│   └── build.gradle