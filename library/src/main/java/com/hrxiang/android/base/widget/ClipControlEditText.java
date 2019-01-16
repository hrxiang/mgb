package com.hrxiang.android.base.widget;

import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by xianghairui on 2018/10/10.
 */
public class ClipControlEditText extends android.support.v7.widget.AppCompatEditText {
  //事件监听器
  private ArrayList<OnEditTextClipListener> listeners;
  private ClipboardManager clipboard;

  public ClipControlEditText(Context context) {
    super(context);
    listeners = new ArrayList<>();
  }

  public ClipControlEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    listeners = new ArrayList<>();
    clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
  }

  public ClipControlEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    listeners = new ArrayList<>();
  }

  public void addClipListener(OnEditTextClipListener listener) {
    try {
      listeners.add(listener);
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }


  @Override
  public boolean onTextContextMenuItem(int id) {
    switch (id) {
      case android.R.id.cut: //剪切
        onTextCut();
        break;
      case android.R.id.paste: //复制
        onTextPaste();
        break;
      case android.R.id.copy: //粘贴
        onTextCopy();
        break;
    }
    return super.onTextContextMenuItem(id);
  }

  public void onTextCut() {
    if (listeners != null) {
      for (OnEditTextClipListener listener : listeners) {
        listener.onCut(this);
      }
    }
  }

  public void onTextCopy() {
    if (listeners != null) {
      for (OnEditTextClipListener listener : listeners) {
        listener.onCopy(this);
      }
    }
  }

  public void onTextPaste() {
    if (listeners != null) {
      for (OnEditTextClipListener listener : listeners) {
        listener.onPaste(this, getClipContent());
      }
    }
  }

  private CharSequence getClipContent() {
    if (clipboard.getPrimaryClip().getItemCount() > 0) {
      return clipboard.getPrimaryClip().getItemAt(0).getText();
    }
    return "";
  }

  public interface OnEditTextClipListener {
    default void onCut(View view) {
    }

    default void onCopy(View view) {
    }

    void onPaste(View view, CharSequence clipContent);
  }

}
