//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jubi.ai.chatbot.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {
    private static final String TAG = PreferenceHelper.class.getSimpleName();
    private SharedPreferences mPreferences;

    public PreferenceHelper(Context context) {
        this.mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private SharedPreferences getPreference() {
        return this.mPreferences;
    }

    public void addPreference(String key, Object value) {
        if(value instanceof String) {
            this.addPreference(key, (String)value);
        } else if(value instanceof Integer) {
            this.addPreference(key, ((Integer)value).intValue());
        } else if(value instanceof Long) {
            this.addPreference(key, ((Long)value).longValue());
        } else if(value instanceof Boolean) {
            this.addPreference(key, ((Boolean)value).booleanValue());
        } else if(value instanceof Float) {
            this.addPreference(key, ((Float)value).floatValue());
        }

    }

    public void removePreference(String key) {
        this.getPreference().edit().remove(key).apply();
    }

    private void addPreference(String key, int value) {
        this.getPreference().edit().putInt(key, value).apply();
    }

    private void addPreference(String key, boolean value) {
        this.getPreference().edit().putBoolean(key, value).apply();
    }

    private void addPreference(String key, long value) {
        this.getPreference().edit().putLong(key, value).apply();
    }

    private void addPreference(String key, float value) {
        this.getPreference().edit().putFloat(key, value).apply();
    }

    private void addPreference(String key, String value) {
        this.getPreference().edit().putString(key, value).apply();
    }

    public Object get(String key, Object defaultValue) {
        return defaultValue instanceof String ?this.getString(key, (String)defaultValue):(defaultValue instanceof Integer ? Integer.valueOf(this.getInt(key, ((Integer)defaultValue).intValue())):(defaultValue instanceof Long ? Long.valueOf(this.getLong(key, ((Long)defaultValue).longValue())):(defaultValue instanceof Boolean ? Boolean.valueOf(this.getBoolean(key, ((Boolean)defaultValue).booleanValue())):(defaultValue instanceof Float ? Float.valueOf(this.getFloat(key, ((Float)defaultValue).floatValue())):null))));
    }

    protected void clearSession() {
        this.getPreference().edit().clear().apply();
    }

    public int getInt(String key, int def) {
        return this.getPreference().getInt(key, def);
    }

    public long getLong(String key, long def) {
        return this.getPreference().getLong(key, def);
    }

    public String getString(String key, String def) {
        return this.getPreference().getString(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return this.getPreference().getBoolean(key, def);
    }

    public float getFloat(String key, float def) {
        return this.getPreference().getFloat(key, def);
    }
}
