package com.mvstar.edwinwu.mvstar;

/**
 * Created by edwinwu on 2018/2/20.
 */

import android.support.design.widget.TextInputLayout;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadows.ShadowViewGroup;

@Implements(TextInputLayout.class)
public class ShadowTextInputLayout extends ShadowViewGroup {

    @RealObject private TextInputLayout realTextInputLayout;
}