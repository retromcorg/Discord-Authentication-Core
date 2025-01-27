package com.johnymuffin.beta.discordauth;

import java.util.HashMap;
import java.util.UUID;


public class DiscordAuthCache {
    DiscordAuthentication plugin;

    private HashMap<String, HashMap<String, String>> codeCache = new HashMap<String, HashMap<String, String>>();


    public DiscordAuthCache(DiscordAuthentication plugin) {
        this.plugin = plugin;

    }

    public void addCodeToken(String uuid, String code, String discordID) {
        //Replace possible existing code
        if (codeCache.containsKey(uuid)) {
            codeCache.remove(uuid);
        }
        //Store code and ID
        final HashMap<String, String> tmp = new HashMap<String, String>();
        tmp.put("code", String.valueOf(code));
        tmp.put("id", String.valueOf(discordID));
        codeCache.put(uuid, tmp);

    }

    @Deprecated
    public boolean isUserPending(String uuid) {
        if (codeCache.containsKey(uuid)) {
            return true;
        }
        return false;
    }

    public boolean isUserPending(UUID uuid) {
        return isUserPending(uuid.toString());
    }

    @Deprecated
    public boolean verifyCode(String uuid, String codeAttempt) {
        if (!codeCache.containsKey(uuid)) {
            return false;
        }
        if (codeCache.get(uuid).get("code").equalsIgnoreCase(codeAttempt)) {
            return true;
        }

        return false;
    }

    public boolean verifyCode(UUID uuid, String codeAttempt) {
        return verifyCode(uuid.toString(), codeAttempt);
    }

    // Remove the code from the cache
    public void removeCode(UUID uuid, String code) {
        String uuidString = uuid.toString();

        if (codeCache.containsKey(uuidString)) {
            if (codeCache.get(uuidString).get("code").equalsIgnoreCase(code)) {
                codeCache.remove(uuidString);
            }
        }
    }

    @Deprecated
    public String getUUIDDiscordID(String uuid) {
        if (!codeCache.containsKey(uuid)) {
            return "0";
        }
        return codeCache.get(uuid).get("id");
    }

    public long getUUIDDiscordID(UUID uuid) {
        return Long.parseLong(getUUIDDiscordID(uuid.toString()));
    }
}
