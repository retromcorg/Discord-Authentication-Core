package com.johnymuffin.beta.discordauth;

import org.bukkit.entity.Player;

import java.util.HashMap;


public class DiscordAuthCache {
    DiscordAuthentication plugin;

    private HashMap<String, HashMap<String, String>> codeCache = new HashMap<String, HashMap<String, String> >();


    public DiscordAuthCache(DiscordAuthentication plugin) {
        this.plugin = plugin;

    }

    public void addCodeToken(String uuid, String code, String discordID) {
        //Replace possible existing code
        if(codeCache.containsKey(uuid)) {
            codeCache.remove(uuid);
        }
        //Store code and ID
        final HashMap<String, String> tmp = new HashMap<String, String>();
        tmp.put("code", String.valueOf(code));
        tmp.put("id", String.valueOf(discordID));
        codeCache.put(uuid, tmp);

    }

    public boolean isUserPending(String uuid) {
        if(codeCache.containsKey(uuid)) {
            return true;
        }
        return false;
    }

    public boolean verifyCode(String uuid, String codeAttempt){
        if(!codeCache.containsKey(uuid)) {
            return false;
        }
        if(codeCache.get(uuid).get("code").equalsIgnoreCase(codeAttempt)) {
            return true;
        }

        return false;
    }


    public String getUUIDDiscordID(String uuid) {
        if(!codeCache.containsKey(uuid)) {
            return "0";
        }
       return codeCache.get(uuid).get("id");

    }


}
