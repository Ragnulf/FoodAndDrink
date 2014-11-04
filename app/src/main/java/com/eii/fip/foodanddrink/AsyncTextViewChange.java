package com.eii.fip.foodanddrink;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;


/**
 * Created by jonathan on 04/11/14.
 */
public class AsyncTextViewChange
{
    public static void setTextInView(final TextView _view, final String _text)
    {
        _view.post(new Runnable()
        {
            public void run()
            {
                _view.setText(_text);
            }
        });
    }


}
