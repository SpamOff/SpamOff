package nldr.spamoff;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by Roee on 22/11/2016.
 */

public class Sharer {

//    public static void showShareMenu(FragmentManager manager) {
//        BottomSheetDialogFragment myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
//        myBottomSheet.show(manager, myBottomSheet.getTag());
//    }

    public static void showSharePage(Context context) {

        Intent intent = new Intent(context, share.class);
        context.startActivity(intent);
    }
}
