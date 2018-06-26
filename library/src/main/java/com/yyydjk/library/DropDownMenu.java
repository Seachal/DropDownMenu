package com.yyydjk.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


/**
 * Created by dongjunkun on 2015/6/17.
 */
public class DropDownMenu extends LinearLayout {

    //顶部菜单布局
    private LinearLayout tabMenuView;
    //底部容器，包含popupMenuViews，maskView
    private FrameLayout containerView;
    //弹出菜单父布局
    private FrameLayout popupMenuViews;
    //遮罩半透明View，点击可关闭DropDownMenu
    private View maskView;
    //tabMenuView里面选中的tab位置，-1表示未选中
    private int current_tab_position = -1;

    //分割线颜色
    private int dividerColor = 0xffcccccc;
    //tab选中颜色
    private int textSelectedColor = 0xff890c85;
    //tab未选中颜色
    private int textUnselectedColor = 0xff111111;
    //遮罩颜色
    private int maskColor = 0x88888888;
    //tab字体大小
    private int menuTextSize = 14;

    //tab选中图标
    private int menuSelectedIcon;
    //tab未选中图标
    private int menuUnselectedIcon;
    //    tab 文字与图标的距离
    private int ddmenuDrawableRightPadding = 10;

    //menuSelectedIcon  左侧的小图标
    private int menuLeftSmallDefaultIcon;
    //menuSelectedIcon  左侧的小图标
    private int menuLeftSmallSelectedIcon;
    //左侧的小图标的默认的文字颜色
    private int menuLeftSmallDefaultTextColor = 0xff111111;
    //    左侧的小图标选中时的文字颜色
    private int menuLeftSmallSelectedTextColor = 0xff890c85;
    //左侧的小图标的文字大小
    private int menuLeftSmallDefaultTextSize = 10;

    private float menuHeighPercent = 0.5f;


    public DropDownMenu(Context context) {
        super(context, null);
    }

    public DropDownMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);

        //为DropDownMenu添加自定义属性
        int menuBackgroundColor = 0xffffffff;
        int underlineColor = 0xffcccccc;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu);
        underlineColor = a.getColor(R.styleable.DropDownMenu_ddunderlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.DropDownMenu_dddividerColor, dividerColor);
        textSelectedColor = a.getColor(R.styleable.DropDownMenu_ddtextSelectedColor, textSelectedColor);
        textUnselectedColor = a.getColor(R.styleable.DropDownMenu_ddtextUnselectedColor, textUnselectedColor);
        menuBackgroundColor = a.getColor(R.styleable.DropDownMenu_ddmenuBackgroundColor, menuBackgroundColor);
        maskColor = a.getColor(R.styleable.DropDownMenu_ddmaskColor, maskColor);
        menuTextSize = a.getDimensionPixelSize(R.styleable.DropDownMenu_ddmenuTextSize, menuTextSize);
        menuSelectedIcon = a.getResourceId(R.styleable.DropDownMenu_ddmenuSelectedIcon, menuSelectedIcon);
        menuUnselectedIcon = a.getResourceId(R.styleable.DropDownMenu_ddmenuUnselectedIcon, menuUnselectedIcon);
        ddmenuDrawableRightPadding = a.getDimensionPixelSize(R.styleable.DropDownMenu_ddmenuDrawableRightPadding, ddmenuDrawableRightPadding);
        menuLeftSmallDefaultIcon = a.getResourceId(R.styleable.DropDownMenu_ddmenuLeftSmallDefaultIcon, menuLeftSmallDefaultIcon);
        menuLeftSmallSelectedIcon = a.getResourceId(R.styleable.DropDownMenu_ddmenuLeftSmallSelectedIcon, menuLeftSmallSelectedIcon);
        menuLeftSmallDefaultTextColor = a.getColor(R.styleable.DropDownMenu_ddmenuLeftSmallTextDefaultColor, menuLeftSmallDefaultTextColor);
        menuLeftSmallSelectedTextColor = a.getColor(R.styleable.DropDownMenu_ddmenuLeftSmallTextSelectedColor, menuLeftSmallSelectedTextColor);
        menuLeftSmallDefaultTextSize = a.getDimensionPixelSize(R.styleable.DropDownMenu_dddmenuLeftSmallTexttSize, menuLeftSmallDefaultTextSize);


        menuHeighPercent = a.getFloat(R.styleable.DropDownMenu_ddmenuMenuHeightPercent, menuHeighPercent);
        a.recycle();

        //初始化tabMenuView并添加到tabMenuView
        tabMenuView = new LinearLayout(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tabMenuView.setOrientation(HORIZONTAL);
        tabMenuView.setBackgroundColor(menuBackgroundColor);
        tabMenuView.setLayoutParams(params);
        addView(tabMenuView, 0);

        //为tabMenuView添加下划线
        View underLine = new View(getContext());
        underLine.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpTpPx(1.0f)));
        underLine.setBackgroundColor(underlineColor);
        addView(underLine, 1);

        //初始化containerView并将其添加到DropDownMenu，
        containerView = new FrameLayout(context);
        containerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView(containerView, 2);

    }

    /**
     * 初始化DropDownMenu  ， containerView继承自FrameLayout，所以 addview（0），addview（1），addview（2）才有了层层遮挡的效果
     *
     * @param tabTexts
     * @param popupViews
     * @param contentView
     */
    public void setDropDownMenu(@NonNull List<String> tabTexts, @NonNull List<View> popupViews, @NonNull View contentView) {
        if (tabTexts.size() != popupViews.size()) {
            throw new IllegalArgumentException("params not match, tabTexts.size() should be equal popupViews.size()");
        }
        for (int i = 0; i < tabTexts.size(); i++) {
            addTab(tabTexts, i);
        }
        containerView.addView(contentView, 0);

        maskView = new View(getContext());
        maskView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        maskView.setBackgroundColor(maskColor);
        maskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });
        containerView.addView(maskView, 1);
        maskView.setVisibility(GONE);
        if (containerView.getChildAt(2) != null) {
            containerView.removeViewAt(2);
        }

        popupMenuViews = new FrameLayout(getContext());
        popupMenuViews.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (DeviceUtils.getScreenSize(getContext()).y * menuHeighPercent)));
        popupMenuViews.setVisibility(GONE);
        containerView.addView(popupMenuViews, 2);

        for (int i = 0; i < popupViews.size(); i++) {
            popupViews.get(i).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            popupMenuViews.addView(popupViews.get(i), i);
        }

    }

    private void addTab(@NonNull List<String> tabTexts, int i) {
        final LinearLayout tagAndIconContainer = new LinearLayout(getContext());
        tagAndIconContainer.setOrientation(HORIZONTAL);
        tagAndIconContainer.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        tagAndIconContainer.setGravity(Gravity.CENTER);

//        menuText 左侧的小文字
        final TextView menuLeftSmallTabText = new TextView(getContext());
        menuLeftSmallTabText.setSingleLine();
        menuLeftSmallTabText.setEllipsize(TextUtils.TruncateAt.END);
        menuLeftSmallTabText.setGravity(Gravity.CENTER);
        menuLeftSmallTabText.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuLeftSmallDefaultTextSize);
        menuLeftSmallTabText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        menuLeftSmallTabText.setLayoutParams(new LayoutParams(dpTpPx(15), dpTpPx(15)));
        menuLeftSmallTabText.setTextColor(menuLeftSmallDefaultTextColor);
//        menuLeftSmallTabText.setPadding(dpTpPx(5), dpTpPx(12), dpTpPx(5), dpTpPx(12));
        menuLeftSmallTabText.setVisibility(View.GONE);

//        menuTextview
        final TextView menuMainTabText = new TextView(getContext());
        menuMainTabText.setSingleLine();
        menuMainTabText.setEllipsize(TextUtils.TruncateAt.END);
        menuMainTabText.setGravity(Gravity.CENTER);
        menuMainTabText.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize);
        menuMainTabText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        menuMainTabText.setTextColor(textUnselectedColor);
        menuMainTabText.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(menuUnselectedIcon), null);
        menuMainTabText.setCompoundDrawablePadding(ddmenuDrawableRightPadding);
        menuMainTabText.setText(tabTexts.get(i));
        menuMainTabText.setPadding(dpTpPx(5), dpTpPx(12), dpTpPx(5), dpTpPx(12));
        //添加点击事件
        tagAndIconContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMenu(tagAndIconContainer);
            }
        });

        tagAndIconContainer.addView(menuLeftSmallTabText);  //menuTabText左侧的小tag
        tagAndIconContainer.addView(menuMainTabText);
        tabMenuView.addView(tagAndIconContainer);

        //添加分割线
        if (i < tabTexts.size() - 1) {
            View view = new View(getContext());
            view.setLayoutParams(new LayoutParams(dpTpPx(0.5f), ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackgroundColor(dividerColor);
            tabMenuView.addView(view);
        }
    }


    /**
     * 改变tab文字
     *
     * @param text
     */
    public void setLeftSmallTagText(String text) {
        if (current_tab_position != -1) {
            Log.e("sum getChildAt:", tabMenuView.getChildCount() + "");
            Log.e("tab getChildAt:", ((LinearLayout) tabMenuView.getChildAt(current_tab_position)).getChildCount() + "");
            TextView menuTabText = ((TextView) ((LinearLayout) tabMenuView.getChildAt(current_tab_position)).getChildAt(0));
            menuTabText.setBackgroundResource(menuLeftSmallDefaultIcon);
            menuTabText.setVisibility(View.VISIBLE);
            menuTabText.setText(text);
            Log.e("sum getChildAt2:", tabMenuView.getChildCount() + "");
            Log.e("tab getChildAt2:", ((LinearLayout) tabMenuView.getChildAt(current_tab_position)).getChildCount() + "");

        }
    }

    /**
     * 改变tab文字
     *
     * @param text
     */
    public void setTabText(String text) {
        if (current_tab_position != -1) {
//            if (View.VISIBLE == ((LinearLayout) tabMenuView.getChildAt(current_tab_position)).getChildAt(0).getVisibility()) {
//                ((TextView) ((LinearLayout) tabMenuView.getChildAt(current_tab_position)).getChildAt(1)).setText(text);
//            } else {
//                ((TextView) ((LinearLayout) tabMenuView.getChildAt(current_tab_position)).getChildAt(0)).setText(text);
//            }
            ((TextView) ((LinearLayout) tabMenuView.getChildAt(current_tab_position)).getChildAt(1)).setText(text);
        }
    }


    public void setTabClickable(boolean clickable) {
        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
            tabMenuView.getChildAt(i).setClickable(clickable);
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (current_tab_position != -1) {
            ((TextView) ((LinearLayout) tabMenuView.getChildAt(current_tab_position)).getChildAt(1)).setTextColor(textUnselectedColor);
            ((TextView) ((LinearLayout) tabMenuView.getChildAt(current_tab_position)).getChildAt(1)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(menuUnselectedIcon), null);
            popupMenuViews.setVisibility(View.GONE);
            popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_out));
            maskView.setVisibility(GONE);
            maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_out));
            current_tab_position = -1;
        }

    }

    /**
     * DropDownMenu是否处于可见状态
     *
     * @return
     */
    public boolean isShowing() {
        return current_tab_position != -1;
    }

    /**
     * 切换菜单  (seachal 包含点击同一个菜单，非同一个菜单的切换)
     *
     * @param target
     */
    private void switchMenu(View target) {
        System.out.println(current_tab_position);
        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
            if (target == tabMenuView.getChildAt(i)) {
//                seachal 如果点击的是当前的tab，并且是打开状态就关闭菜单。关闭菜单中有条件判断
                if (current_tab_position == i) {
                    closeMenu();
                } else {
                    if (current_tab_position == -1) {
                        popupMenuViews.setVisibility(View.VISIBLE);
                        popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_in));
                        maskView.setVisibility(VISIBLE);
                        maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_in));
                        popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    } else {
                        popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    }
                    current_tab_position = i;
                    ((TextView) ((LinearLayout) tabMenuView.getChildAt(i)).getChildAt(1)).setTextColor(textSelectedColor);
                    ((TextView) ((LinearLayout) tabMenuView.getChildAt(i)).getChildAt(1)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                            getResources().getDrawable(menuSelectedIcon), null);
                }
            } else {
                ((TextView) ((LinearLayout) tabMenuView.getChildAt(i)).getChildAt(1)).setTextColor(textUnselectedColor);
                ((TextView) ((LinearLayout) tabMenuView.getChildAt(i)).getChildAt(1)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(menuUnselectedIcon), null);
                popupMenuViews.getChildAt(i / 2).setVisibility(View.GONE);
            }
        }
    }

    public int dpTpPx(float value) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5);
    }
}
