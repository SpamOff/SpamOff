package nldr.spamoff;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.gc.materialdesign.widgets.ProgressDialog;

/**
 * Created by Roee on 14/11/2016.
 */
public class myProgressDialog extends ProgressDialog {

    public myProgressDialog(Context context, String title) {
        super(context, title);
    }

    public myProgressDialog(Context context, String title, int progressColor) {
        super(context, title, progressColor);
    }

    @Override
    public void cancel() {
        //Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
        super.dismiss();
        //super.cancel();
    }

    @Override
    public void dismiss() {
        Toast.makeText(getContext(), "התהליך עבר לרוץ ברקע", Toast.LENGTH_SHORT).show();
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
        this.setTitle("");
        this.getTitleTextView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    @Override
    public View onCreatePanelView(int featureId) {
        return super.onCreatePanelView(featureId);
    }
};