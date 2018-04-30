package com.jubi.ai.chatbot.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.jubi.ai.chatbot.R;


public class CustomPopoverView {

    private Context mContext;

    private View mView;

    private String mTitle;
    private String mMessage;

    private AlertDialog mAlertDialog;

    private DialogButtonClickListener dialogButtonClickListener;

    private CustomPopoverView(Context context) {
        mContext = context;
        mAlertDialog = new AlertDialog.Builder(mContext).create();
    }


    private void setPositiveButton(String title) {
        setButton(DialogInterface.BUTTON_POSITIVE, title);
    }

    private void setButton(int which, String title) {
        mAlertDialog.setButton(which, title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialogButtonClickListener != null) {

                    switch (which) {
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialogButtonClickListener.negativeButtonClicked(mView, mAlertDialog);
                            break;
                        case DialogInterface.BUTTON_POSITIVE:
                            dialogButtonClickListener.positiveButtonClicked(mView, mAlertDialog);
                            break;
                        case DialogInterface.BUTTON_NEUTRAL:
                            dialogButtonClickListener.neutralButtonClicked(mView, mAlertDialog);
                            break;
                    }
                }

            }
        });
    }

    void createDialog() {
        if (mMessage != null)
            mAlertDialog.setMessage(mMessage);

        if(mTitle !=null){
            mAlertDialog.setTitle(mTitle);
        }
        if(mView!=null){
            mAlertDialog.setView(mView);
        }
        mAlertDialog.setIcon(R.mipmap.ic_launcher);
    }

    private void setNegativeButton(String title) {
        setButton(DialogInterface.BUTTON_NEGATIVE, title);
    }

    private void setNeutralButton(String title) {
        setButton(DialogInterface.BUTTON_NEUTRAL, title);
    }

    private void setCancelable(boolean cancel) {
        mAlertDialog.setCancelable(cancel);
    }

    public void show() {
        mAlertDialog.show();
    }

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    public final static class Builder {
        CustomPopoverView mCustomPopoverView;

        public Builder(Context context) {
            mCustomPopoverView = new CustomPopoverView(context);
        }

        public Builder withTitle(String title) {
            mCustomPopoverView.mTitle = title;
            return this;
        }

        public Builder withMessage(String message) {
            mCustomPopoverView.mMessage = message;
            return this;
        }

        public Builder withView(View view) {
            mCustomPopoverView.mView = view;
            return this;
        }

        public CustomPopoverView build() {
            mCustomPopoverView.createDialog();
            return mCustomPopoverView;
        }

        public Builder withPositiveTitle(String positiveTitle) {
            mCustomPopoverView.setPositiveButton(positiveTitle);
            return this;
        }

        public Builder withNegativeTitle(String negativeTitle) {
            mCustomPopoverView.setNegativeButton(negativeTitle);
            return this;
        }

        public Builder setDialogButtonClickListener(DialogButtonClickListener listener) {
            mCustomPopoverView.dialogButtonClickListener = listener;
            return this;
        }

        public Builder withNeutralTitle(String neutralTitle) {
            mCustomPopoverView.setNeutralButton(neutralTitle);
            return this;
        }

        public Builder setCancelable(boolean cancel) {
            mCustomPopoverView.setCancelable(cancel);
            return this;
        }
    }

    public interface DialogButtonClickListener {

        void positiveButtonClicked(View view, AlertDialog alertDialog);

        void negativeButtonClicked(View view, AlertDialog alertDialog);

        void neutralButtonClicked(View view, AlertDialog alertDialog);
    }
}