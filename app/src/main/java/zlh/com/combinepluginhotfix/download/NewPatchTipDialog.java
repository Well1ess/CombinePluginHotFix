package zlh.com.combinepluginhotfix.download;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import zlh.com.combinepluginhotfix.R;

/**
 * Created by shs1330 on 2018/3/27.
 */

public class NewPatchTipDialog extends PopupWindow {
    private static final String TAG = "NewPatchTipDialog";

    public NewPatchTipDialog(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public static class Builder {

        private View tipsView;
        private View progressView;
        private Context mContext;
        private NewPatchTipDialog mPopupWindow;
        private OnPopWindowClickListener positiveButtonClickListener;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder create() {

            final View view = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_new_patch_tip, null);
            mPopupWindow = new NewPatchTipDialog(view, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setContentView(view);
            Button positive = (Button) view.findViewById(R.id.location_ok);
            Button negative = (Button) view.findViewById(R.id.location_cancle);
            this.tipsView = view.findViewById(R.id.llyt_tips);
            this.progressView = view.findViewById(R.id.llyt_progress);
            //window初始化
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);


            WindowManager.LayoutParams layoutParams = ((Activity) mContext).getWindow().getAttributes();
            layoutParams.alpha = 0.7f;
            ((Activity) mContext).getWindow().setAttributes(layoutParams);

            mPopupWindow.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams layoutParams = ((Activity) mContext).getWindow().getAttributes();
                    layoutParams.alpha = 1f;

                    ((Activity) mContext).getWindow().setAttributes(layoutParams);

                }
            });
            negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });
            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tipsView.setVisibility(View.GONE);
                    progressView.setVisibility(View.VISIBLE);
                    positiveButtonClickListener.onClick(view, mPopupWindow);
                }
            });
            return this;
        }


        //设置按钮监听接口
        public Builder setPositiveButton(OnPopWindowClickListener positiveButtonClickListener) {
            this.positiveButtonClickListener = positiveButtonClickListener;
            return this;
        }
        public Builder show(View anchor, int gravity, int x, int y) {
            mPopupWindow.showAtLocation(anchor, gravity, x, y);
            return this;
        }

    }

    public interface OnPopWindowClickListener {
        void onClick(View view, PopupWindow popupWindow);
    }
}
