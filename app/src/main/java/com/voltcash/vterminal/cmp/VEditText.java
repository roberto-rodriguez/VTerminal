package com.voltcash.vterminal.cmp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.voltcash.vterminal.util.listeners.FocusRemoveListener;

public class VEditText  extends android.support.v7.widget.AppCompatEditText{

    private FocusRemoveListener focusRemoveListener;

    public VEditText( Context context )
    {
        super( context );
    }

    public VEditText( Context context, AttributeSet attribute_set )
    {
        super( context, attribute_set );
    }

    public VEditText( Context context, AttributeSet attribute_set, int def_style_attribute )
    {
        super( context, attribute_set, def_style_attribute );
    }

    @Override
    public boolean onKeyPreIme( int key_code, KeyEvent event ) {
        if ( event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){
            this.clearFocus();

            if(focusRemoveListener != null){
                focusRemoveListener.removeTextFieldFocus();
            }
        }
        return super.onKeyPreIme( key_code, event );
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean result =  super.onKeyUp(keyCode, event);

        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER){
            if(focusRemoveListener != null){
                this.clearFocus();
                focusRemoveListener.removeTextFieldFocus();
            }
        }

        return result;
    }

    public void setFocusRemoveListener(FocusRemoveListener focusRemoveListener) {
        this.focusRemoveListener = focusRemoveListener;
    }
}
