package com.example.d_five.services;

import static com.example.d_five.PagerAdapter.callFragment;
import static com.example.d_five.PagerAdapter.chatFragment;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.d_five.R;
import com.example.d_five.activities.LoginActivity;
import com.example.d_five.callsession.DetailCallActivity;
import com.example.d_five.callsession.InComingCallActivity;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.DAOFactory;
import com.example.d_five.model.Conversation;
import com.example.d_five.model.Default;
import com.example.d_five.model.LocationMessage;
import com.example.d_five.model.Message;
import com.example.d_five.model.User;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.ImageHandler;
import com.example.d_five.utilities.NotificationHandler;
import com.example.d_five.utilities.PreferenceManager;

import org.linphone.core.Account;
import org.linphone.core.AccountParams;
import org.linphone.core.Address;
import org.linphone.core.AudioDevice;
import org.linphone.core.AuthInfo;
import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatMessageListener;
import org.linphone.core.ChatMessageListenerStub;
import org.linphone.core.ChatRoom;
import org.linphone.core.ChatRoomBackend;
import org.linphone.core.ChatRoomListener;
import org.linphone.core.ChatRoomListenerStub;
import org.linphone.core.ChatRoomParams;
import org.linphone.core.Content;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Event;
import org.linphone.core.Factory;
import org.linphone.core.MediaEncryption;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;
import org.linphone.core.TransportType;
import org.linphone.mediastream.video.capture.CaptureTextureView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class LinphoneService extends Service {
    public static LinphoneService instance;
    private Core core;
    private CoreListenerStub coreListenerStub;
    private CoreListenerStub subscribeListener;
    private boolean initialRegister = true;
    private final IBinder iBinder = new LocalBinder();
    private CallBackMessage chatBoxActivity, chatRoomBoxActivity, mainActivityMsg;
    AudioManager audioManager;
    Event event;
    private CallBackData loginActivity, mainActivity, detailCallActivity, incommingActivity, videoCallActivity;
    private CameraManager cameraManager;
    private String cameraId;
    private Thread blinkFlashCamera;
    private PreferenceManager preferenceManager;
    private DAOFactory daoFactory;
    private boolean isInCall, isRinging, isCaller, isVideoCall, isUpdateCall;
    private LocalDateTime createdAtCallTime, endTimeCall, startTimeCall;


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LinphoneService getService() {
            // Return this instance of LocalService so clients can call public methods.
            return LinphoneService.this;
        }
    }

    public void setMainActivity(CallBackData mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setLoginActivity(CallBackData loginActivity) {
        this.loginActivity = loginActivity;
    }

    public void setIncommingActivity(InComingCallActivity incommingActivity) {
        this.incommingActivity = incommingActivity;
    }

    public void setDetailCallActivity(DetailCallActivity detailCallActivity) {
        this.detailCallActivity = detailCallActivity;
    }

    public void setChatBoxActivity(CallBackMessage chatBoxActivity) {
        this.chatBoxActivity = chatBoxActivity;
    }

    public void setMainActivityMsg(CallBackMessage mainActivityMsg) {
        this.mainActivityMsg = mainActivityMsg;
    }

    public void setChatRoomBoxActivity(CallBackMessage chatRoomBoxActivity) {
        this.chatRoomBoxActivity = chatRoomBoxActivity;
    }

    public LinphoneService() {
        isInCall = false;
        isRinging = false;
        isUpdateCall = false;
        isVideoCall = false;
        isCaller = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ConnectionDB connectionDB = new ConnectionDB();
        daoFactory = new DAOFactory(ConnectionDB.connection);
        preferenceManager = new PreferenceManager(getApplicationContext());

        Factory factory = Factory.instance();
        core = factory.createCore(null, null, getApplicationContext());
        core.setDnsServersApp(Default.dnsList);
        core.start();

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notify);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Service", "BIND");
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("Service", "UNBIND");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Service", "DESTROY");
    }

    public Core getCore() {
        return core;
    }

    public static LinphoneService getInstance() {
        if (instance == null) {
            synchronized (LinphoneService.class) {
                if (null == instance) {
                    instance = new LinphoneService();
                }
            }
        }
        return instance;
    }

    private void createSubscribe(Address serverAddress, Address identityAddress) {
        Account account = core.getDefaultAccount();
        AccountParams accountParams1 = account.getParams();
        Address[] addresses = {serverAddress, identityAddress};
        AccountParams cloneParams = accountParams1.clone();
        cloneParams.setRoutesAddresses(addresses);

        account.setParams(cloneParams);
        core.setDefaultAccount(account);

        event = core.createSubscribe(identityAddress, "reg", 3600);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setValidating(false);

        subscribeListener = new CoreListenerStub() {
            @Override
            public void onNotifyReceived(@NonNull Core core, @NonNull Event linphoneEvent, @NonNull String notifiedEvent, @NonNull Content body) {
                super.onNotifyReceived(core, linphoneEvent, notifiedEvent, body);
                if (notifiedEvent.equals("reg")) {
                    Log.i("Subscribe", body.getUtf8Text());

                    try {
                        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                        Document document = documentBuilder.parse(new InputSource(new StringReader(body.getUtf8Text())));
                        NodeList nodeList = document.getElementsByTagName("contact");
                        System.out.println(nodeList.getLength());
                        int count = 0;
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            Node node = nodeList.item(i);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element element = (Element) node;
                                String event = element.getAttribute("event");
                                String expire = element.getAttribute("expires");

                                if (event.equals("registered") && !expire.equals("0")) {
                                    count++;
                                }
                            }
                        }

                        if (count >= 2 && mainActivity != null && isSubscribe) {
                            mainActivity.onDataChange("NOTIFICATION", core);
                        }
                    } catch (ParserConfigurationException | IOException | SAXException e) {
                        Toast.makeText(getApplicationContext(), "Has an error when received notify", Toast.LENGTH_SHORT).show();
//                        throw new RuntimeException(e);
                    }
                }

            }
        };
        core.addListener(subscribeListener);
    }

    private boolean isSubscribe = false;

    public boolean isSubscribe() {
        return isSubscribe;
    }

    public void subscribe() {
        if (isSubscribe) {
            isSubscribe = false;
            Address address = core.getDefaultAccount().getParams().getIdentityAddress();
            event = core.createSubscribe(address, "reg", 0);
            event.sendSubscribe(null);
            Toast.makeText(getApplicationContext(), "De-Subscribe reg event success", Toast.LENGTH_SHORT).show();
        } else {
            isSubscribe = true;
            Address address = core.getDefaultAccount().getParams().getIdentityAddress();
            event = core.createSubscribe(address, "reg", 3600);
            event.sendSubscribe(null);
            Toast.makeText(getApplicationContext(), "Subscribe reg event success", Toast.LENGTH_SHORT).show();
        }
    }


    public void initialRegister(String domain, String username, String password) {
        AuthInfo authInfo = Factory.instance().createAuthInfo(username, null, password, null, null, domain, null);
        AccountParams accountParams = core.createAccountParams();
        Address identityAddress = Factory.instance().createAddress(Default.identity(domain, username));

        if (identityAddress == null) {
            Toast.makeText(getApplicationContext(), "Wrong username! Please enter again!", Toast.LENGTH_SHORT).show();
            if (loginActivity != null) {
                loginActivity.onDataChange("REGISTER", core);
            } else {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        } else {
            accountParams.setIdentityAddress(identityAddress);
            Address serverAddress = Factory.instance().createAddress(Default.serverAddress(domain));
            assert serverAddress != null;
            serverAddress.setTransport(TransportType.Tcp);

            // Default = 3600
            accountParams.setExpires(3600);
            accountParams.setServerAddress(serverAddress);
            accountParams.setRegisterEnabled(true);
            Account account = core.createAccount(accountParams);
            account.setCustomHeader("Allow", "OPTIONS, SUBSCRIBE, NOTIFY, INVITE, ACK, CANCEL, BYE, REFER, INFO, MESSAGE");

            core.addAuthInfo(authInfo);
            core.addAccount(account);
            core.setDefaultAccount(account);

            coreListenerStub = new CoreListenerStub() {
                @Override
                public void onRegistrationStateChanged(@NonNull Core core,
                                                       @NonNull ProxyConfig proxyConfig,
                                                       RegistrationState state,
                                                       @NonNull String message) {
                    super.onRegistrationStateChanged(core, proxyConfig, state, message);
//                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    if (state == RegistrationState.Failed) {
                        switch (message) {
                            case "Service Unavailable":
                            case "io error":
                            case "Authentication Failed":
                            case "Forbidden - HSS User Unknown":
                                core.clearAllAuthInfo();
                                core.clearAccounts();
                                break;

                        }
                        if (loginActivity != null) {
                            loginActivity.onDataChange("REGISTER", core);
                        }
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else if (state == RegistrationState.Cleared) {
                        if (mainActivity != null) {
                            mainActivity.onDataChange("LOGOUT", core);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                        Toast.makeText(getApplicationContext(), "Logout success", Toast.LENGTH_SHORT).show();
                    } else if (state == RegistrationState.Ok) {
                        if (initialRegister) {
                            Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
                            initialRegister = false;

                            if (loginActivity != null) {
                                loginActivity.onDataChange("REGISTER", core);
                            }

                            // luu data
                            // static connection
                            preferenceManager = new PreferenceManager(getApplicationContext());
                            preferenceManager.putString(Constants.USER_NAME, username);
                            preferenceManager.putString(Constants.DOMAIN, domain);
                            saveNewUserDB(username, domain);

                        } else {
                            Toast.makeText(getApplicationContext(), "Refresh success", Toast.LENGTH_SHORT).show();
                        }

                        createSubscribe(serverAddress, identityAddress);
                    }
                }

                // =================================================== onCallStateChanged
                public void onCallStateChanged(@NonNull Core core, @NonNull org.linphone.core.Call call, org.linphone.core.Call.State state, @NonNull String message) {
                    super.onCallStateChanged(core, call, state, message);
                    final String TAG = "CALL STATE";

                    switch (state) {
                        case IncomingReceived:
                            Log.i(TAG, Constants.CALL_INCOMING_RECEIVED + ": " + message);
                            Toast.makeText(getApplicationContext(), "Incoming Call", Toast.LENGTH_SHORT).show();

                            createdAtCallTime = LocalDateTime.now();
                            isRinging = true;
                            isCaller = false;

                            if (!isVideoCall && call.getRemoteParams().isVideoEnabled()) {
                                isVideoCall = true;
                            }

//                            audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
//                            audioManager.setMode(AudioManager.MODE_IN_CALL);
//                            core.setMicEnabled(true);

                            if (mainActivity != null) {
                                mainActivity.onDataChange(Constants.CALL_INCOMING_RECEIVED, core);
                            }
                            break;
                        case OutgoingInit:
                            Log.i(TAG, Constants.CALL_OUTGOING_INIT + ": " + message);

                            createdAtCallTime = LocalDateTime.now();
                            isCaller = true;

                            if (!isVideoCall && call.getParams().isVideoEnabled()) {
                                isVideoCall = true;
                            }

                            if (detailCallActivity != null) {
                                detailCallActivity.onDataChange(Constants.CALL_OUTGOING_INIT, core);
                            }
                            break;
                        case OutgoingProgress:
                            Log.i(TAG, Constants.CALL_OUTGOING_PROGRESS + ": " + message);
                            break;
                        case OutgoingRinging:
                            Log.i(TAG, Constants.CALL_OUTGOING_RINGING + ": " + message);
                            isRinging = true;

                            if (detailCallActivity != null) {
                                detailCallActivity.onDataChange(Constants.CALL_OUTGOING_RINGING, core);
                            }
                            break;
                        case Connected:
                            Log.i(TAG, Constants.CALL_CONNECTED + ": " + message);

                            isInCall = true;
                            startTimeCall = LocalDateTime.now();

                            audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
                            audioManager.setMode(AudioManager.MODE_IN_CALL);
                            core.setMicEnabled(true);
                            core.setMaxCalls(1); // all incoming calls after the first one will be denied with Busy reason automatically.

                            if (detailCallActivity != null) {
                                detailCallActivity.onDataChange(Constants.CALL_CONNECTED, core);
                            }
                            break;
                        case Updating:
                            Log.i(TAG, Constants.CALL_UPDATING + ": " + message);
                            break;
                        case UpdatedByRemote:
                            Log.i(TAG, Constants.CALL_UPDATE_BY_REMOTE + ": " + message);

                            if (call.getRemoteParams().isVideoEnabled() && !call.getParams().isVideoEnabled()) {
                                call.deferUpdate();

                                if (detailCallActivity != null) {
                                    detailCallActivity.onDataChange(Constants.CALL_UPDATE_BY_REMOTE, core);
                                }
                            } else if (!call.getRemoteParams().isVideoEnabled() && call.getParams().isVideoEnabled()) {
                                updateVideoCall(false);
                            }
                            break;
                        case StreamsRunning:
                            Log.i(TAG, Constants.CALL_STREAMS_RUNNING + ": " + message);

                            if (!isVideoCall) {
                                if (call.getParams().isVideoEnabled() || call.getRemoteParams().isVideoEnabled()) {
                                    isVideoCall = true;
                                }
                            }

                            if (detailCallActivity != null) {
                                detailCallActivity.onDataChange(Constants.CALL_STREAMS_RUNNING, core);
                            }
                            break;
                        case End:
                            Log.i(TAG, Constants.CALL_END + ": " + message);
                            call.terminate();
                            break;
                        case Released:
                            Log.i(TAG, Constants.CALL_RELEASED + ": " + message);

                            saveToDatabase(call);

                            if (detailCallActivity != null) {
                                if (isRinging == true) {
                                    if (isInCall == true) {
                                        detailCallActivity.onDataChange(Constants.CALL_RELEASED, core);
                                    } else {
                                        detailCallActivity.onDataChange(Constants.CALL_RELEASED_DECLINE, core);
                                    }
                                } else {
                                    detailCallActivity.onDataChange(Constants.CALL_RELEASED_UNAVAILABLE, core);
                                }
                            }

                            if (incommingActivity != null) {
                                incommingActivity.onDataChange(Constants.CALL_RELEASED, core);
                            }

                            isInCall = false;
                            isRinging = false;
                            isVideoCall = false;
                            if (callFragment != null) {
                                callFragment.onActivityChange();
                            }
                            //callFragment.onActivityChange();
                            break;
                        default:
                            Log.i(TAG, state.toString());
                            break;
                    }
                }
            };

            core.addListener(coreListenerStub);
            core.addListener(coreListenerMsg);
        }
    }

    private void saveNewUserDB(String username, String domain) {
        ImageHandler imageHandler = new ImageHandler(getApplicationContext());
        try {
            if (!daoFactory.getUserDAO().checkUserExist(username)) {
                User newUser = new User(username, imageHandler.encodeImage(imageHandler.decodeResource(R.drawable.ic_account)), domain);
                daoFactory.getUserDAO().insertInfoUser(newUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "ERROR when login", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void reRegister() {
        if (mainActivity != null) {
            core.refreshRegisters();
        } else {
            Context context = getApplicationContext();
            if (context != null) {
            }
            //Toast.makeText(getApplicationContext(), "Has some error when re-register", Toast.LENGTH_SHORT).show();
            else {
                Log.e("reregister", "context is null");
            }
        }
    }

    public void deRegister() {
        initialRegister = true;
        Account account = core.getDefaultAccount();

        if (account != null) {
            AccountParams params = account.getParams();
            AccountParams cloneParams = params.clone();

            cloneParams.setRegisterEnabled(false);
            cloneParams.setExpires(0);

            account.setParams(cloneParams);
            core.setDefaultAccount(account);
        } else {
            Toast.makeText(getApplicationContext(), "Has some error when de-register", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            delete();
        }
    }

    public void delete() {
        Account account = core.getDefaultAccount();
        core.removeListener(coreListenerStub);
        core.removeListener(subscribeListener);
        core.removeListener(coreListenerMsg);
        if (account != null) {
            core.removeAccount(account);
            core.clearAccounts();
            core.clearAllAuthInfo();
        } else {
            Log.e("Registration", "Account is null when delete");
        }
    }

    // =================================================== CALL SERVICE
    public void makeCall(String sipAddress, CallBackData detailCallActivity, boolean isVideoCall) {
        this.detailCallActivity = detailCallActivity;
        Address address = core.interpretUrl(sipAddress);

        if (address == null) {
            Toast.makeText(getApplicationContext(), "Address is NULL", Toast.LENGTH_SHORT).show();
            return;
        }

        CallParams params = core.createCallParams(null);
        if (params == null) {
            return;
        }

        if (isVideoCall) {
            params.setVideoEnabled(true);
        } else {
            params.setAudioEnabled(true);
        }

        params.setMediaEncryption(MediaEncryption.None);
        core.inviteAddressWithParams(address, params);
    }

    public void acceptCall(CallBackData detailCallActivity, boolean isVideoCall) {
        if (detailCallActivity != null) {
            this.detailCallActivity = detailCallActivity;
        }
        if (core.getCallsNb() == 0) return;

        org.linphone.core.Call call = core.getCurrentCall() != null ? core.getCurrentCall() : core.getCalls()[0];
        if (call == null) {
            return;
        }

        CallParams params = core.createCallParams(call);
        if (params == null) {
            return;
        }

        if (isVideoCall) {
            params.setVideoEnabled(true);
        } else {
            params.setAudioEnabled(true);
        }
        call.acceptWithParams(params);
    }

    public void rejectCall() {
        if (core.getCallsNb() == 0) return;

        for (org.linphone.core.Call call : core.getCalls()) {
            if (call == null) {
                continue;
            }
            call.terminate();
        }
    }

    public void videoDisplay(CaptureTextureView localPreviewVideoSurface, TextureView remoteVideoSurface) {
        core.setNativePreviewWindowId(localPreviewVideoSurface);
        core.setNativeVideoWindowId(remoteVideoSurface);
    }

    public void saveToDatabase(Call call) {
        User caller, callee;
        Address remoteAddress = call.getRemoteAddress(); // address of callee
        String type;

        if (isVideoCall) {
            type = "video";
        } else {
            type = "audio";
        }


        try {
            // Get 2 users
            caller = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            callee = daoFactory.getUserDAO().getInfoUser(remoteAddress.getUsername());

            // Make new conversation
            Long conversationID = daoFactory.getConversationDAO().checkExistConversation2P(caller.getId(), callee.getId());

            if (conversationID == null) {
                Log.e("Call", "Error when saving call");
                return;
            }

            long duration = 0;
            if (isInCall) {
                endTimeCall = LocalDateTime.now();
                Duration durationD = Duration.between(startTimeCall, endTimeCall);
                duration = durationD.toMillis();
            }

            if (isCaller) {
                daoFactory.getUserDAO().insertCall(caller.getId(), conversationID, createdAtCallTime, LocalDateTime.now(), Constants.CALL_OUT_GOING, type, duration);
                // insert message
                String typeMsg;
                Message msg = daoFactory.getUserDAO().insertMessage(caller.getId(), conversationID, type + ": " + duration / 1000 + " seconds", "CALL");
                // update for conversation
                Conversation conversation = daoFactory.getConversationDAO().getInfoConversation(conversationID);
                conversation.setLast_message(msg.getContent());
                conversation.setLast_message_id(msg.getId());
                daoFactory.getConversationDAO().updateConversation(conversation);

                if (chatBoxActivity != null && Objects.equals(chatBoxActivity.getConversationId(), conversationID)) {
                    chatBoxActivity.onMessageChange(null, LocationMessage.RIGHT);
                }
                sendMessage(callee.getUsername(), conversation.getId());
            } else {
                if (isInCall) {
                    daoFactory.getUserDAO().insertCall(caller.getId(), conversationID, createdAtCallTime, LocalDateTime.now(), Constants.CALL_IN_COMING, type, duration);
                } else {
                    daoFactory.getUserDAO().insertCall(caller.getId(), conversationID, createdAtCallTime, LocalDateTime.now(), Constants.CALL_MISSED, type, duration);
                }
            }
            chatFragment.onActivityChange(conversationID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void externalSpeaker() {     // use External Speaker in the Call
        if (core.getCurrentCall() == null)
            return;

        // get current output audio device in the Call
        AudioDevice currentAudioDevice = core.getCurrentCall().getOutputAudioDevice();
        boolean isSpeaker = currentAudioDevice.getType() == AudioDevice.Type.Speaker;

        // if current output audio device isn't Speaker -> set current output audio device to Speaker
        for (AudioDevice audioDevice : core.getAudioDevices()) {
            if (!isSpeaker && audioDevice.getType() == AudioDevice.Type.Speaker) {
                core.getCurrentCall().setOutputAudioDevice(audioDevice);
            }
        }
    }

    public void internalSpeaker() {    // use Internal Speaker in the Call
        if (core.getCurrentCall() == null)
            return;

        // get current output audio device in the Call
        AudioDevice currentAudioDevice = core.getCurrentCall().getOutputAudioDevice();
        boolean isSpeaker = currentAudioDevice.getType() == AudioDevice.Type.Speaker;

        // if current output audio device is Speaker -> set current output audio device to Earpiece
        for (AudioDevice audioDevice : core.getAudioDevices()) {
            if (isSpeaker && audioDevice.getType() == AudioDevice.Type.Earpiece) {
                core.getCurrentCall().setOutputAudioDevice(audioDevice);
            }
        }
    }

    public void toggleSpeaker() {
        if (core.getCurrentCall() == null)
            return;

        AudioDevice currentAudioDevice = core.getCurrentCall().getOutputAudioDevice();
        boolean speakerEnabled = currentAudioDevice.getType() == AudioDevice.Type.Speaker;

        for (AudioDevice audioDevice : core.getAudioDevices()) {
            if (speakerEnabled && audioDevice.getType() == AudioDevice.Type.Earpiece) {
                core.getCurrentCall().setOutputAudioDevice(audioDevice);
            } else if (!speakerEnabled && audioDevice.getType() == AudioDevice.Type.Speaker) {
                core.getCurrentCall().setOutputAudioDevice(audioDevice);
            }
            // If we want to route the audio to a bluetooth headset
            else if (audioDevice.getType() == AudioDevice.Type.Bluetooth) {
                core.getCurrentCall().setOutputAudioDevice(audioDevice);
            }
        }
    }

    public void setMic(boolean state) {
        core.setMicEnabled(state);
    }

    public boolean isMicEnable() {
        return core.isMicEnabled();
    }

    public void toggleVideo() {
        if (core.getCallsNb() == 0) {
            return;
        }

        Call call = core.getCurrentCall() != null ? core.getCurrentCall() : core.getCalls()[0];
        if (call == null) {
            return;
        }

        boolean isVideo = call.getParams().isVideoEnabled();
        CallParams params = core.createCallParams(call);
        if (params != null) {
            params.setVideoEnabled(!isVideo);
        }

        call.update(params);
    }

    public void updateVideoCall(boolean isAcceptVideoCall) {
        if (core.getCallsNb() == 0) {
            return;
        }

        Call call = core.getCurrentCall() != null ? core.getCurrentCall() : core.getCalls()[0];
        if (call == null) {
            return;
        }

        CallParams params = core.createCallParams(call);
        if (params != null) {
            params.setVideoEnabled(isAcceptVideoCall);
        }

        call.acceptUpdate(params);
    }

    public void switchCamera() {
        String currentDevice = core.getVideoDevice();

        // Let's iterate over all camera available and choose another one
        for (String camera : core.getVideoDevicesList()) {
            if (!camera.equals(currentDevice) && !camera.equals("StaticImage: Static picture")) {
                core.setVideoDevice(camera);
                break;
            }
        }
    }

    public void videoSetUp() {
        core.setVideoCaptureEnabled(true);
        core.setVideoPreviewEnabled(true);
        core.setVideoDisplayEnabled(true);
    }

    public boolean getIsVideoCall() {
        return isVideoCall;
    }

    // =================================================== CHAT SERVICE
    ChatMessageListener chatMessageListener = new ChatMessageListenerStub() {
        @Override
        public void onMsgStateChanged(@NonNull ChatMessage message, ChatMessage.State state) {
            super.onMsgStateChanged(message, state);
//            System.out.println(message.getChatRoom().getSubject());
            if (chatRoomBoxActivity != null) {
                chatRoomBoxActivity.onMessageChange(message, LocationMessage.RIGHT);
            } else if (chatBoxActivity != null) {
                chatBoxActivity.onMessageChange(message, LocationMessage.RIGHT);
            }
        }
    };

    ChatRoomListener chatRoomListener = new ChatRoomListenerStub() {
        @Override
        public void onStateChanged(@NonNull ChatRoom chatRoom, ChatRoom.State newState) {
            super.onStateChanged(chatRoom, newState);
            System.out.println(newState);
            if (newState == ChatRoom.State.Created) {

            }
        }
    };

    MediaPlayer mediaPlayer;


    //receive message .
    CoreListenerStub coreListenerMsg = new CoreListenerStub() {
        @Override
        public void onMessageReceived(@NonNull Core core, @NonNull ChatRoom chatRoom, @NonNull ChatMessage message) {
            super.onMessageReceived(core, chatRoom, message);
            chatRoom.markAsRead();
            Log.i("MESSAGE", message.getUtf8Text());

            Long conversationId = Long.valueOf(Objects.requireNonNull(message.getUtf8Text()));

            if (chatRoomBoxActivity != null && chatRoomBoxActivity.getConversationId().equals(conversationId)) {
                chatRoomBoxActivity.onMessageChange(message, LocationMessage.LEFT);
            } else if (chatBoxActivity != null && chatBoxActivity.getConversationId().equals(conversationId)) {
                chatBoxActivity.onMessageChange(message, LocationMessage.LEFT);
            }

            if (mainActivityMsg != null) {
                mainActivityMsg.onMessageChange(message, LocationMessage.LEFT);
            }

            if(mainActivity != null) {
                mainActivity.onDataChange(Constants.MESSAGE_NEW, core);
            }

        }
    };

    ChatRoom chatRoom = null;

    public void createBasicChatRoom(String peerAddress) {
        ChatRoomParams params = core.createDefaultChatRoomParams();
        params.setBackend(ChatRoomBackend.Basic);
        params.setEncryptionEnabled(false);
        params.setGroupEnabled(false);

        if (params.isValid()) {
            // We also need the SIP address of the person we will chat with
            Address remoteAddress = Factory.instance().createAddress(peerAddress);

            if (remoteAddress != null) {
                // And finally we will need our local SIP address
                Address localAddress = core.getDefaultAccount().getParams().getIdentityAddress();
                ChatRoom room = core.createChatRoom(params, localAddress, new Address[]{remoteAddress});

                if (room != null) {
                    room.addListener(chatRoomListener);
                    chatRoom = room;
                }
            }
        }
    }

    public ChatMessage sendMessageToPerson(Long id, String peerAddress) {
        createBasicChatRoom(peerAddress);

        ChatMessage chatMessage = null;
        if (chatRoom != null) {
            chatMessage = chatRoom.createMessageFromUtf8(String.valueOf(id));
            chatMessage.addListener(chatMessageListener);
            chatMessage.send();
            if (mainActivityMsg != null) {
                mainActivityMsg.onMessageChange(chatMessage, LocationMessage.RIGHT);
            } else {
                Log.e("Message", "Main activity is null");
            }
        } else {
            Log.e("Chat room", "Error when create chat room");
        }

        return chatMessage;
    }


    public void createChatRoom(String peerName) {
        ChatRoomParams params = core.createDefaultChatRoomParams();
        params.setBackend(ChatRoomBackend.Basic);
        params.setGroupEnabled(false);
        params.setEncryptionEnabled(false);

        if (params.isValid()) {
            Address remoteAddress = Factory.instance().createAddress(Default.createAddress(peerName));
            if (remoteAddress != null) {
                Address localAddress = core.getDefaultAccount().getParams().getIdentityAddress();
                ChatRoom room = core.createChatRoom(params, localAddress, new Address[]{remoteAddress});

                if (room != null) {
                    room.addListener(chatRoomListener);
                    chatRoom = room;
                } else {
                    Log.e("Chat room", "Chat room is null");
                }
            } else {
                Log.e("Chat room", "RemoteAddress is null");
            }
        } else {
            Log.e("Chat room", "Chat room params is null");
        }
    }

    public ChatMessage sendMessage(String peerName, Long id) {
        createChatRoom(peerName);
        ChatMessage chatMessage = null;
        if (chatRoom == null) {
            Log.e("Chat room", "Chat room is null");
        } else {
            chatMessage = chatRoom.createMessageFromUtf8(String.valueOf(id));
            chatMessage.send();
            if (mainActivityMsg != null) {
                mainActivityMsg.onMessageChange(chatMessage, LocationMessage.RIGHT);
            } else {
                Log.e("Message", "Main activity is null");
            }
        }
        return chatMessage;
    }


    public void callGroup(String sipAddress) {
        Address address = Factory.instance().createAddress(sipAddress);
        System.out.println(sipAddress);

        if (address == null) {
            Toast.makeText(getApplicationContext(), "Address is NULL", Toast.LENGTH_SHORT).show();
            return;
        }

        CallParams params = core.createCallParams(null);
        if (params == null) {
            Log.e("CALL PARAMS", " Call Params is Null");
            return;
        }

        params.setAudioEnabled(true);
        params.setMediaEncryption(MediaEncryption.None);

        System.out.println(core.getMaxCalls());
        core.inviteAddressWithParams(address, params);
    }


}
