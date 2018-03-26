package com.mercadopago.util;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * Created by vaserber on 7/21/17.
 */

public class MercadoPagoESCImpl implements MercadoPagoESC {

    private static final String ESC_BUILDER_CLASS_NAME = "com.mercadopago.ml_esc_manager.ESCManagerBuilder";
    private static final String METHOD_SET_CONTEXT = "setApplicationContext";
    private static final String METHOD_BUILD = "build";
    private static final String METHOD_SAVE_ESC = "saveESC";
    private static final String METHOD_GET_ESC = "getESC";
    private static final String METHOD_DELETE_ESC = "deleteESC";
    private static final String METHOD_DELETE_ALL_ESC = "deleteAllESC";
    private static final String METHOD_GET_SAVED_CARD_IDS = "getSavedCardIds";

    private final Context context;
    private Object actualClass;
    private boolean escEnabled;

    public MercadoPagoESCImpl(Context context, boolean escEnabled) {
        this.context = context;
        this.escEnabled = escEnabled;
        if (escEnabled && isESCAvailable()) {
            actualClass = getESCClass();
        } else {
            this.escEnabled = false;
        }
    }

    @Override
    public boolean isESCEnabled() {
        return escEnabled;
    }

    private Object getESCClass() {
        try {
            java.lang.reflect.Method appContextMethod;
            java.lang.reflect.Method buildMethod;

            Class builderclass = Class.forName(ESC_BUILDER_CLASS_NAME);
            Object builder = builderclass.newInstance();

            appContextMethod = builder.getClass().getMethod(METHOD_SET_CONTEXT, Context.class);
            Object builder2 = appContextMethod.invoke(builder, context);

            buildMethod = builder2.getClass().getMethod(METHOD_BUILD);
            Object actualClass = buildMethod.invoke(builder2);

            return actualClass;
        } catch (InvocationTargetException e1) {
            return null;
        } catch (NoSuchMethodException e2) {
            return null;
        } catch (InstantiationException e3) {
            return null;
        } catch (IllegalAccessException e4) {
            return null;
        } catch (ClassNotFoundException e5) {
            return null;
        }
    }

    @Override
    public String getESC(String cardId) {
        if (escEnabled) {
            try {
                java.lang.reflect.Method getMethod;

                if (actualClass != null) {

                    getMethod = actualClass.getClass().getMethod(METHOD_GET_ESC, String.class);
                    Object esc = getMethod.invoke(actualClass, cardId);
                    return (String) esc;
                }
            } catch (IllegalAccessException e1) {
                return null;
            } catch (InvocationTargetException e2) {
                return null;
            } catch (NoSuchMethodException e3) {
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean saveESC(String cardId, String value) {
        if (escEnabled) {
            try {

                java.lang.reflect.Method saveMethod;

                if (actualClass != null) {
                    saveMethod = actualClass.getClass().getMethod(METHOD_SAVE_ESC, String.class, String.class);
                    Object wasSaved = saveMethod.invoke(actualClass, cardId, value);
                    return (Boolean) wasSaved;
                }
            } catch (IllegalAccessException e1) {
                return false;
            } catch (InvocationTargetException e2) {
                return false;
            } catch (NoSuchMethodException e3) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void deleteESC(String cardId) {
        if (escEnabled) {
            try {

                java.lang.reflect.Method deleteMethod;

                if (actualClass != null) {
                    deleteMethod = actualClass.getClass().getMethod(METHOD_DELETE_ESC, String.class);
                    deleteMethod.invoke(actualClass, cardId);
                }
            } catch (IllegalAccessException e1) {

            } catch (InvocationTargetException e2) {

            } catch (NoSuchMethodException e3) {

            }
        }
    }

    @Override
    public void deleteAllESC() {
        if (escEnabled) {
            try {

                java.lang.reflect.Method deleteAllMethod;

                if (actualClass != null) {
                    deleteAllMethod = actualClass.getClass().getMethod(METHOD_DELETE_ALL_ESC);
                    deleteAllMethod.invoke(actualClass);
                }
            } catch (IllegalAccessException e1) {

            } catch (InvocationTargetException e2) {

            } catch (NoSuchMethodException e3) {

            }
        }
    }

    @Override
    public Set<String> getESCCardIds() {
        if (escEnabled) {
            try {

                java.lang.reflect.Method getAllMethod;

                if (actualClass != null) {
                    getAllMethod = actualClass.getClass().getMethod(METHOD_GET_SAVED_CARD_IDS);
                    Object objects = getAllMethod.invoke(actualClass);
                    Set<String> cardIds = (Set<String>) objects;
                    return cardIds;
                }
            } catch (IllegalAccessException e) {
                return null;
            } catch (InvocationTargetException e) {
                return null;
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
        return null;
    }

    private boolean isESCAvailable() {
        boolean answer;
        try {
            Class cls = Class.forName(ESC_BUILDER_CLASS_NAME);
            answer = true;
        } catch (ClassNotFoundException e) {
            answer = false;
        }
        return answer;
    }
}
