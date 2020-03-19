package com.anysou.flouatw;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallTermux extends AppCompatActivity {

    private EditText editRun;
    private EditText editRunARG;
    private CheckBox checkBox;
    private TextView textView;

    public static final File TASKER_DIR = new File("/data/data/com.termux/files/home/.termux/tasker/");
    public static final String TERMUX_SERVICE = "com.termux.app.TermuxService";
    public static final String ACTION_EXECUTE = "com.termux.service_execute";
    public static final String EXTRA_ARGUMENTS = "com.termux.execute.arguments";  //要传递给脚本的参数
    public static final String ORIGINAL_INTENT = "originalIntent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);

        editRun = (EditText) findViewById(R.id.editTextRun);
        editRunARG = (EditText) findViewById(R.id.editTextTextRunARG);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        textView = (TextView) findViewById(R.id.textView);
    }

    /** 如果用户忘记这样做，请确保文件可读和可执行. */
    static void ensureFileReadableAndExecutable(File file) {
        if (!file.canRead()) file.setReadable(true);
        if (!file.canExecute()) file.setExecutable(true);
    }

    public final boolean isOrderedBroadcast() {
        throw new RuntimeException("Stub!");
    }

    public final void setResultCode(int code) {
        throw new RuntimeException("Stub!");
    }

    public void call(View view) {

        // 获取执行文件名
        File execFile = new File(TASKER_DIR, String.valueOf(editRun.getText())); //获取执行文件
        if (!execFile.isFile()) { //文件不存在
            String message = "没有执行文件:\n" + TASKER_DIR;
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return;
        }
        ensureFileReadableAndExecutable(execFile);  //给文件读执行权限

        //获取参数
        Matcher matcher = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(editRunARG.getText());  //对参数进行处理
        List<String> list = new ArrayList<>();
        while (matcher.find()){
            list.add(matcher.group(1).replace("\"",""));
        }


        //executableFile.getAbsolutePath() 返回抽象路径名的绝对路径名字符串
        Uri scriptUri = new Uri.Builder().scheme("com.termux.file").path(execFile.getAbsolutePath()).build();

        // ACTION_EXECUTE = "com.termux.service_execute"
        Intent executeIntent = new Intent(ACTION_EXECUTE, scriptUri);

        // TERMUX_SERVICE = "com.termux.app.TermuxService"
        executeIntent.setClassName("com.termux", TERMUX_SERVICE);

        boolean inTerminal = checkBox.isChecked(); //是否勾选
        if (!inTerminal) executeIntent.putExtra("com.termux.execute.background", true); //后台运行

        executeIntent.putExtra(EXTRA_ARGUMENTS, list.toArray(new String[list.size()]));  //添加参数
        textView.setText(executeIntent.toString());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // https://developer.android.com/about/versions/oreo/background.html
            this.startForegroundService(executeIntent);
        } else {
            this.startService(executeIntent);
        }

    }
}
