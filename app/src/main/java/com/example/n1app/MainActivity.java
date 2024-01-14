package com.example.n1app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //いろいろ定義
        String ip = "192.168.1.2";
        boolean isOnSwitch = true;

        //View定義
        final Button edgeSwitch = findViewById(R.id.edge1_switch);
        final ImageButton addEdgeButton = findViewById(R.id.addEdgeButton);
        final TextView search_text = findViewById(R.id.search_text);
        final TextView edge1_ip = findViewById(R.id.edge1_ip);
        final View edge1 = findViewById(R.id.edge1_constraint);
        final ImageView gifImageView = findViewById(R.id.search_gif);
        final Button doorButton = findViewById(R.id.door_button);

        //エレメント非表示
        edge1.setVisibility(View.GONE);
        addEdgeButton.setVisibility(View.GONE);

        //git再生
        int gifUrl = R.drawable.search;

        // Glideを使用してGIF画像をロード
        Glide.with(this)
                .asGif()
                .load(gifUrl)
                .into(gifImageView);

        Status.isFindedEdge1 = false;   //見た目用
        //端末が見つかり次第デバイス一覧画面を表示するスレッド
        runOnUiThread(new Runnable() {
              public void run() {
                  //端末検索のコード呼び出し

                  //端末が見つかったら
                  if (Status.isFindedEdge1) {
                      //エレメント可視化
                      edge1.setVisibility(View.VISIBLE);
                      addEdgeButton.setVisibility(View.VISIBLE);
                      //ipアドレスを表示
                      edge1_ip.setText("ip: " + ip);
                      //「検索中」の文言を非表示
                      search_text.setVisibility(View.GONE);
                      gifImageView.setVisibility(View.GONE);

                      //現在のドアステータス取得
                      Status.door_status = 0;

                      // Wifi接続を監視するメソッド呼び出し
                      //Status.previousWifiStatus = ここにメソッド;
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {

                              // Wifi接続を監視するメソッドの呼び出し
                              boolean isWifiConnect = true;

                              if (isOnSwitch) {
                                  if (isWifiConnect) {
                                      if (isWifiConnect != Status.previousWifiStatus) {    //wifi範囲に入ったらdoorButton.setText("施錠する");
                                          //** toDo:ここに開錠機能呼び出し **//
                                          Status.previousWifiStatus = true;
                                      }
                                  } else {
                                      if (isWifiConnect != Status.previousWifiStatus) {    //wifi範囲から離れたら
                                          //** toDo:ここに施錠機能呼び出し **//
                                          Status.previousWifiStatus = false;
                                      }

                                      // n秒ごとに繰り返す
                                      Handler handler = new Handler();
                                      handler.postDelayed(this, 2000);
                                  }
                              }
                          }
                      });
                  }else{
                      Handler handler = new Handler();
                      Status.isFindedEdge1 = true;
                      handler.postDelayed(this, 10000);
                  }
              }
        });



        //** 以下イベントハンドラ **//
        //端末ON・OF切り替え時のハンドラ
        edgeSwitch.setOnClickListener( v -> {
                        if (edgeSwitch.getText() == "ON") {
                            edgeSwitch.setText("OFF");
                        } else {
                            edgeSwitch.setText("ON");
                        }
        });

        //開錠/施錠ボタン押下イベント
        doorButton.setOnClickListener(v -> {
            if (Status.door_status == 0) {
                doorButton.setText("開錠中...");

                Runnable myRunnable = () -> {
                    doorButton.setText("施錠する");
                    Status.door_status = 1;

                    // ダイアログを表示
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("開錠しました");
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    new MyTools().waitAndDo(3000, ()-> alertDialog.dismiss());
                };
                new MyTools().waitAndDo(3000, myRunnable);


            } else {
                doorButton.setText("施錠中...");

                Runnable myRunnable = () -> {
                    doorButton.setText("開錠する");
                    // ダイアログを表示
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("施錠しました");
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    new MyTools().waitAndDo(3000, ()-> alertDialog.dismiss());
                };

                new MyTools().waitAndDo(3000, myRunnable);

            }
        });

        //端末検索ボタン押下イベント
        addEdgeButton.setOnClickListener( v -> {
            addEdgeButton.setVisibility(View.GONE);
            search_text.setVisibility(View.VISIBLE);

            // レイアウトからViewを取得
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_input, null);

            // EditTextの取得
            final EditText editText = view.findViewById(R.id.editText);

            // AlertDialogを作成
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setView(view)
                    .setTitle("端末のIPアドレスかURLを入力してください")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 入力された値を取得
                            Status.egde2_ip = editText.getText().toString();
                            //** ここにデバイス検索スレッド（今後の展望）**//
                            // 見栄えのため5秒待機
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    search_text.setVisibility(View.GONE);
                                    addEdgeButton.setVisibility(View.VISIBLE);

                                    // ダイアログを表示
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage("端末が見つかりませんでした");
                                    final AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    new MyTools().waitAndDo(3000, ()-> alertDialog.dismiss());
                                }
                            }, 5000);

                        }
                    })
                    .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // キャンセル時の処理
                            dialog.dismiss();
                        }
                    });

            // AlertDialogを表示
            builder.create().show();
        });
    }


}

