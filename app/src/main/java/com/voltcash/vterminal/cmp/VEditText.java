package com.voltcash.vterminal.cmp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.voltcash.vterminal.util.listeners.KeyImeChangeListener;

public class VEditText  extends android.support.v7.widget.AppCompatEditText{

    private KeyImeChangeListener keyImeChangeListener;

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
        if ( event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP )
            this.clearFocus();

        if(keyImeChangeListener != null){
            keyImeChangeListener.onKeyPreIme( key_code, event);
        }

        return super.onKeyPreIme( key_code, event );
    }

    public void setKeyImeChangeListener(KeyImeChangeListener keyImeChangeListener) {
        this.keyImeChangeListener = keyImeChangeListener;
    }
}
