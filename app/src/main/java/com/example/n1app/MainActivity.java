package com.example.n1app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
        final TextView doorStatus = findViewById(R.id.door_status);
        final TextView edge1_ip = findViewById(R.id.edge1_ip);
        final View edge1 = findViewById(R.id.edge1_constraint);

        //エレメント非表示
        edge1.setVisibility(View.GONE);
        addEdgeButton.setVisibility(View.GONE);

        //端末が見つかり次第デバイス一覧画面を表示するスレッド
        runOnUiThread(new Runnable() {
              public void run() {
                  //端末検索のコード呼び出し
                  boolean isFindedEdge1 = true;   //toDo: 機能呼び出し

                  //端末が見つかったら
                  if (isFindedEdge1) {
                      //エレメント可視化
                      edge1.setVisibility(View.VISIBLE);
                      addEdgeButton.setVisibility(View.VISIBLE);
                      //ipアドレスを表示
                      edge1_ip.setText("ip: " + ip);
                      //「検索中」の文言を非表示
                      search_text.setVisibility(View.GONE);

                      // バックグラウンドスレッドでWifi接続を監視
                      Status.previousWifiStatus = false;
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {

                              // Wifi接続を監視するメソッドの呼び出し
                              boolean isWifiConnect = true;

                              if (isOnSwitch) {
                                  if (isWifiConnect) {
                                      if (isWifiConnect != Status.previousWifiStatus) {    //wifi範囲に入ったら
                                          doorStatus.setText("開錠中");
                                          //** toDo:ここに開錠機能呼び出し **//
                                          Status.previousWifiStatus = true;
                                      }
                                  } else {
                                      if (isWifiConnect != Status.previousWifiStatus) {    //wifi範囲から離れたら
                                          doorStatus.setText("施錠中");
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
                      //端末が見つからなかったら再検索
                      searchTextUpdate();

                      Handler handler = new Handler();
                      handler.postDelayed(this, 200);
                  }
              }
              private void searchTextUpdate(){
                  //ドットを数える
                  int dotCount = 0;
                  for (int i = 0; i < search_text.getText().length(); i++) {
                      if (search_text.getText().charAt(i) == '.') {
                          dotCount++;
                      }
                  }
                  //テキスト更新
                  if(dotCount == 5){
                      search_text.setText("デバイスを検索中");
                  }else{
                      search_text.setText(search_text.getText() + ".");
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

        //端末検索ボタン押下イベント
        addEdgeButton.setOnClickListener( v -> {
            addEdgeButton.setVisibility(View.GONE);
            search_text.setVisibility(View.VISIBLE);

            //** ここにデバイス検索スレッド（今後の展望）**//
            // 見栄えのため5秒待機
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                public void run() {
                    search_text.setVisibility(View.GONE);
                    addEdgeButton.setVisibility(View.VISIBLE);
                }
            }, 5000);
        });
    }
}

