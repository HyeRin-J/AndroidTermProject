package hyerin.android.chess.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import hyerin.android.chess.common.logger.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 이 클래스는 다른 기기와 블루투스 연결을 세팅하고 관리하는 모든 동작을 다 한다.
 * 수신을 받기 위한 쓰레드가 있으며,
 * 기기와 연결하기 위한 쓰레드
 * 그리고 연결됬을 때 데이터를 발신하는 쓰레드가 있다.
 */
public class BluetoothService {
    // 디버깅
    private static final String TAG = "BluetoothService";

    // 서버 소켓 만들때 SDP 레코드를 위한 이름 넣기
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // 이 앱만의 고유한 UUID
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    // 멤버 필드
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    // 현재 연결 상태를 보여주는 상수
    public static final int STATE_NONE = 0;       // 일안해
    public static final int STATE_LISTEN = 1;     // 수신중이야
    public static final int STATE_CONNECTING = 2; // 발신용 연결중이야
    public static final int STATE_CONNECTED = 3;  // 원격 장비에 연결됬어

    /**
     * 컨스트럭터. 새 블루투스 채팅 세션 준비
     *
     * 컨텍스트는 UI 액티비티 컨텍스트임
     * 핸들러는 UI 액티비티에 메시지를 반송할 핸들러임
     */
    public BluetoothService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    /**
     * 현재 채팅 연결 상태를 세팅
     *
     * state는 현재 연결 상태를 정의하는 정수임
     */
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // UI 액티비티 업데이트를 위해 새 상태 핸들러를 준다.
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * 현재 연결 상태 리턴
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * 챗 서비스 시작, 특별히 Accept 쓰레드를 수신(서버)모드 세션으로 시작하게 한다.
     * 액티비티 onResume()에 의해 호출된다.
     */
    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread(true);
            mSecureAcceptThread.start();
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread(false);
            mInsecureAcceptThread.start();
        }
    }

    /**
     * 원격 장치와의 연결을 실행하기 위해 Connect쓰레드 시작
     *
     * device는 연결할 블루투스 장치이다.(to connect)
     * 안전 소켓 보안 타입 - 안전(true) 위험(false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "connect to: " + device);

        // 연결하려고 하는 모든 쓰레드 취소
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // 현재 연결중인 모든 쓰레드 취소
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // 제공된 장치와의 연결하는 쓰레드 시작
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * 블루투스 연결을 관리하는 것을 시작(begin)하는 Connected쓰레드 시작(start)
     *
     * socket 은 지금 연결된 블루투스 소켓이다.
     * device는 연결된 블루투스 장치이다.(has been connected)
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        Log.d(TAG, "connected, Socket Type:" + socketType);

        // 연결이 완료된 쓰레드 취소
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // 지금 연결중인 쓰레드 취소
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // 한 기기만 연결하길 원하니까 accept 쓰레드도 취소
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        // 연결을 관리하고 통신을 수행하는 쓰레드 시작
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        // UI액티비티에 연결된 기기 이름 보내주기
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /**
     * 모든 쓰레드 중지
     */
    public synchronized void stop() {
        Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        setState(STATE_NONE);
    }

    /**
     * 비동기 방식으로 connected쓰레드 쓰기
     *
     * out은 쓸(write) 바이트이다.
     * ConnectedThread#write(byte[]) 되있는거 잘 봐라.
     */
    public void write(byte[] out) {
        // 임시적 객체 생성
        ConnectedThread r;
        // connected쓰레드 복제품하고 동기화
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // 비동기상태로 쓰기 실행
        r.write(out);
    }

    /**
     * 연결 시도가 실패됬음을 보여주고, UI액티비티에 알림
     */
    private void connectionFailed() {
        // UI액티비티에 실패했다고 전해라
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // 서비스를 수신 모드로 재시작 시키기
        BluetoothService.this.start();
    }

    /**
     * 연결이 아작났다고 보여주고, UI액티비티에도 알림
     */
    private void connectionLost() {
        // UI액티비티에 연결이 아작났다고 전해라
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // 서비스를 수신 모드로 재시작 시키기
        BluetoothService.this.start();
    }

    /**
     * 이 쓰레드는 수신할때만 동작한다.
     * 주국 종국 방식으로 동작하고,
     * 연결이 수락되거나, 취소될때까지 돈다.
     */
    private class AcceptThread extends Thread {
        // 로컬 서버 소켓
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // 새 수신 서버 소켓을 만든다
            try {
                if (secure) {
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
                            MY_UUID_SECURE);
                } else {
                    tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
                            NAME_INSECURE, MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "Socket Type: " + mSocketType +
                    "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

            // 연결이 안되있으면 서버 소켓을 수신(한다는거 같은데 사실 이게 무슨 개소린지 모르겠다)
            while (mState != STATE_CONNECTED) {
                try {
                    // Blocking call(Block : 막다, 차단하다. 알아서 해석해라. 난 모르겠다.) 로써,
                    // 예외나 성공적 연결만을 리턴할 것임
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                    break;
                }

                // 연결이 수락된 경우
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // 상황 일반. connected 쓰레드 시작.
                                connected(socket, socket.getRemoteDevice(),
                                        mSocketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // 이미 연결됬거나, 준비가 안됬거나. 새 소켓 제거.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);

        }

        public void cancel() {
            Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
            }
        }
    }


    /**
     * 장치가 발신 연결을 만드려고 시도할 때 이 쓰레드가 동작함
     * 이거 겁나 단순하게 동작함.
     * 연결이 성공했거나 실패했거나.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // 주어진 블루투스 장치와 연결하기 위해 블루투스 소켓 받기
            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(
                            MY_UUID_SECURE);
                } else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(
                            MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // 느려지게 만드니까 Discovery를 항상 취소.
            mAdapter.cancelDiscovery();

            // 블루투스 소켓과 연결
            try {
                // Blocking call(Block : 막다, 차단하다. 알아서 해석해라. 난 모르겠다.) 로써,
                // 예외나 성공적 연결만을 리턴할 것임
                mmSocket.connect();
            } catch (IOException e) {
                // 소켓 닫기
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // 볼일 다 놨으므로 connected쓰레드를 리셋
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            // connected 쓰레드 실행
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }

    /**
     * 원격 장치와 연결중일때 이 쓰레드가 동작함.
     * 모든 수신/발신을 주관.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // 블루투스 인풋/아웃풋 스트림 받기
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // 연결되 있을 때, 인풋 스트림을 통해 계속 수신
            while (mState == STATE_CONNECTED) {
                try {
                    // 인풋 스트림 읽기
                    bytes = mmInStream.read(buffer);

                    // 확보된 Bytes를 UI액티비티로 보내기
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    // 수신 모드로 장치 재시작
                    BluetoothService.this.start();
                    break;
                }
            }
        }

        /**
         * 연결된 아웃 스트림을 통해 쓰기
         *
         * buffer는 쓸 Bytes
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // UI액티비티에도 발신 메시지 공유
                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}