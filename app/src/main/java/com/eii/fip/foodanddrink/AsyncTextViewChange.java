package com.eii.fip.foodanddrink;
/**
 * Created by jonathan on 04/11/14.
 * Methode Static de changement d'affichage de text
 */
import android.widget.TextView;



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
