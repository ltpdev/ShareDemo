package share.imooc.com.sharedemo;

import android.content.DialogInterface;
import android.provider.Settings;
import android.support.annotation.StringDef;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class MainActivity extends AppCompatActivity {
private Button btnStartWX;
    private Button btnSendTxt;
    private CheckBox checkBox;
    private IWXAPI iwxapi;
    private static final String APP_ID="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化api
        iwxapi= WXAPIFactory.createWXAPI(this,APP_ID);
        iwxapi.registerApp(APP_ID);
        btnStartWX= (Button) findViewById(R.id.btn_start_WX);
        btnSendTxt= (Button) findViewById(R.id.btn_send_txt);
        checkBox= (CheckBox) findViewById(R.id.checkbox);
        btnStartWX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动微信
                iwxapi.openWXApp();
            }
        });
        btnSendTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText=new EditText(MainActivity.this);
                editText.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT,LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                editText.setText("默认文本");
                final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(android.R.mipmap.sym_def_app_icon);
                builder.setTitle("共享文本");
                builder.setView(editText);
                builder.setMessage("请输入要分享的文本");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text=editText.getText().toString();
                        if (text==null||text.length()==0){
                            return;
                        }
                        WXTextObject textObject=new WXTextObject();
                        textObject.text=text;

                        WXMediaMessage wxMediaMessage=new WXMediaMessage();
                        wxMediaMessage.mediaObject=textObject;
                        wxMediaMessage.description=text;

                        SendMessageToWX.Req req=new SendMessageToWX.Req();
                        req.transaction=buildTransaction("text");

                        req.scene=checkBox.isChecked()?SendMessageToWX.Req.WXSceneTimeline:SendMessageToWX.Req.WXSceneSession;

                        iwxapi.sendReq(req);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        });
    }

    public String buildTransaction(String text){
        return (text==null)? String.valueOf(System.currentTimeMillis()):text+String.valueOf(System.currentTimeMillis());
    }
}
