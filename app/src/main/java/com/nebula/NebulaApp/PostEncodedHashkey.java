package com.nebula.NebulaApp;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class PostEncodedHashkey {
    String Authentication_Key;

    public PostEncodedHashkey() {

    }

    public PostEncodedHashkey(String sanitizedEmail, String instituteID, String Authentication_Key) {
        this.Authentication_Key = Authentication_Key;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Authentication Key", Authentication_Key);


        return result;
    }
}
