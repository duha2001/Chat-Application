package com.example.d_five.services;

import org.linphone.core.ChatMessage;
import org.linphone.core.Core;

public interface CallBackData {
    void onDataChange(String key, Core core);
}
