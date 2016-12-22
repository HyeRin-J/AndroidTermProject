

package hyerin.android.chess.bluetooth;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bluetoothchat.R;

import hyerin.android.chess.common.logger.Log;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

// 이 쪼가리들이 블루투스를 통해 다른 기기들과 통신할 수 있도록 제어한다.
public class BluetoothFragment extends Fragment {
    private static final String TAG = "BluetoothFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    /**
     * 기기이름
     */
    private String mConnectedDeviceName = null;

    /**
     * 대화 쓰레드(Conversation Thread)를 위한 어레이 어댑터
     */
    private ArrayAdapter<String> mConversationArrayAdapter;
    /**
     * 발신메시지를 위한 스트링 버퍼
     */
    private StringBuffer mOutStringBuffer;

    /**
     * 로컬 블루투스 어댑터
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * 채팅 서비스를 위한 멤버 객체
     */
    private BluetoothService mChatService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // 겟 로컬 블루투스 어댑터
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 어댑터가 null 이면 블루투스를 지원하는게 아님
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // 블루투스가 안켜져있으면, 켜라는 요청을 보냄
        // 셋업채팅()메소드가 액티비티에서 값이 돌아올떄까지 요청될거임.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // 아니라면, 채팅 세션을 시작
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // onStart()메소드 때부터 블루투스가 실행이 안됬을 경우를 대비해서
        //(so we were paused to enable it, 실행 안되면 오류나니까 중지시키겠다는 이야기)
        // onResume()에 이 체크를 실행함.
        // onResume()는 ACTION_REQUEST_ENABLE 액티비티가 리턴될 때 호출될것임.
        if (mChatService != null) {
            // State가 STATE_NONE일때만 우리는 이게 시작했는지 아닌지 알 수 있음.
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
                // 블루투스 채팅서비스 시작
                mChatService.start();
            }
        }
    }

    /**
     * 채팅(블루투스)을 위해 UI랑 백그라운드 서비스 셋업
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // 대화 쓰레드를 위해 어레이 어댑터 초기화
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        // 블루투스 연결을 실행하기 위해 블루투스 챗서비스 초기화
        mChatService = new BluetoothService(getActivity(), mHandler);

        // 발신 메시지를 위해 버퍼 초기화
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * 기기 은닉하기
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * 메시지 보내기
     *
     * 발신메시지는 한줄짜리 스트링임
     */
    private void sendMessage(String message) {
        // 시작하기 전에 연결됬는지 확인
        if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // 뭐 보낼건 있는것인지 확인
        if (message.length() > 0) {
            // 메시지 바이트를 받고, 블루투스에는 보낼 게 있다고 말해주는 메소드
            byte[] send = message.getBytes();
            mChatService.write(send);

            // 에딧텍스트 초기화 및 발신 스트링 버퍼 초기화
            mOutStringBuffer.setLength(0);
        }
    }

    /**
     * 액션바에 상태 업데이트
     *
     * resID는 스트링형 리소스(자원)ID임
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * 액션바에 상태 업데이트
     *
     * 서브타이틀(자막)은 상태임.(액션 바에 띄울 상태 = 서브타이틀)
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * 블루투스 챗서비스로부터 정보를 (돌려)받아올 핸들러
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // 버퍼로부터 스트링 추출
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // 버퍼의 유효 바이트로부터 스트링 추출
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // 연결했던 장치 (이름) 저장
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE://<<secure, 안전연결
                // 디바이스 리스트 액티비티가 연결할 디바이스를 리턴할 시
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE://<<insecure, 안 안전한거. 즉 위험 감수하고 연결하는 케이스 라는 뜻인듯
                // 디바이스 리스트 액티비티가 연결할 디바이스를 리턴할 시
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // 블루투스 사용 요청이 돌아왔을때(그니까 실행됬을때 이야기인듯)
                if (resultCode == Activity.RESULT_OK) {
                    // 블루투스가 시작됨. 챗 서비스를 실행하셈
                    setupChat();
                } else {
                    // 에러발생했거나 유저시키가 블루투스를 활성화 하지 않음.
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * 다른 기기와 연결 구축
     *
     * data는 디바이스 리스트 액티비티의 인텐트 엑스트라이다.
     * secure는 소켓 보안 타입이다. - 안전(ture) 위험(false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // 기기 MAC 주소
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // 블루투스 장치 오브젝트 받아오기
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // 장치에 연결 시도
        mChatService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // 기기를 보기 위한 디바이스 리스트 액티비티 실행. 그리고 스캔
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // 기기를 보기 위한 디바이스 리스트 액티비티 실행. 그리고 스캔
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                //이 기기가 다른 기기들에게서 감춰져 있는지(discoverable by others) 확인
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }

}