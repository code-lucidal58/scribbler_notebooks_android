package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.R;

/**
 * Created by Jibin_ism on 12-Mar-15.
 */
public class JazzySwitchView extends RelativeLayout{

    public static final boolean STATE_ON=true;
    public static final boolean STATE_OFF=false;

    private Context mContext;
    private SwitchToggledListener mListener;
    private int selectedColor=Color.parseColor("#ff2196f3");
//    private int selectedColor=Color.parseColor("#ff8885ff");
    private TextView positiveTextView, negativeTextView;
    private int BACKGROUND_COLOR_ON_DEFAULT,BACKGROUND_COLOR_ON_SELECTED,BACKGROUND_COLOR_OFF_SELECTED,BACKGROUND_COLOR_OFF_DEFAULT;
    private boolean currentState=STATE_ON;
    private String positiveTextResource,negativeTextResource,defaultCurrentState="";
    private int COLOR_ON_TEXT_DEFAULT,COLOR_OFF_TEXT_DEFAULT, COLOR_ON_TEXT_SELECTED, COLOR_OFF_TEXT_SELECTED;
    private float DISTANCE_BETWEEN, DEFAULT_TEXT_SIZE, SELECTED_TEXT_SIZE;
    private Drawable ON_DEFAULT_DRAWABLE=null,ON_SELECTED_DRAWABLE=null, OFF_DEFAULT_DRAWABLE=null, OFF_SELECTED_DRAWABLE=null;

    public JazzySwitchView(Context context) {
        this(context,null);
        mContext=context;
    }

    public JazzySwitchView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public JazzySwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.JazzySwitchView,0,0);
        try{
            positiveTextResource=ta.getString(R.styleable.JazzySwitchView_textOn);
            negativeTextResource=ta.getString(R.styleable.JazzySwitchView_textOff);

            /*Should be 'STATE_ON' or 'STATE_OFF' */
            defaultCurrentState=ta.getString(R.styleable.JazzySwitchView_currentState);

            COLOR_ON_TEXT_DEFAULT=ta.getColor(R.styleable.JazzySwitchView_textOnDefaultColor, Color.GRAY);
            COLOR_ON_TEXT_SELECTED=ta.getColor(R.styleable.JazzySwitchView_textOnSelectedColor, Color.BLACK);
            COLOR_OFF_TEXT_DEFAULT=ta.getColor(R.styleable.JazzySwitchView_textOffDefaultColor, Color.GRAY);
            COLOR_OFF_TEXT_SELECTED=ta.getColor(R.styleable.JazzySwitchView_textOffSelectedColor, Color.BLACK);

            BACKGROUND_COLOR_ON_DEFAULT=ta.getColor(R.styleable.JazzySwitchView_onColorDefault,Color.WHITE);
            BACKGROUND_COLOR_ON_SELECTED=ta.getColor(R.styleable.JazzySwitchView_onColorSelected, selectedColor);
            BACKGROUND_COLOR_OFF_DEFAULT=ta.getColor(R.styleable.JazzySwitchView_offColorDefault, Color.WHITE);
            BACKGROUND_COLOR_OFF_SELECTED=ta.getColor(R.styleable.JazzySwitchView_offColorSelected, selectedColor);

            ON_DEFAULT_DRAWABLE=ta.getDrawable(R.styleable.JazzySwitchView_onDefaultDrawable);
            ON_SELECTED_DRAWABLE=ta.getDrawable(R.styleable.JazzySwitchView_onSelectedDrawable);
            OFF_DEFAULT_DRAWABLE=ta.getDrawable(R.styleable.JazzySwitchView_offDefaultDrawable);
            OFF_SELECTED_DRAWABLE=ta.getDrawable(R.styleable.JazzySwitchView_offSelectedDrawable);

            DEFAULT_TEXT_SIZE=ta.getDimension(R.styleable.JazzySwitchView_defaultTextSize, 12);
            SELECTED_TEXT_SIZE=ta.getDimension(R.styleable.JazzySwitchView_selectedTextSize, 15);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            ta.recycle();
        }
        try {
            initialize(attrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize(AttributeSet attrs) throws Exception{
        inflate(mContext, R.layout.jazzy_switch_layout, this);
        positiveTextView=(TextView)findViewById(R.id.customSwitchPositiveButton);
        negativeTextView=(TextView)findViewById(R.id.customSwitchNegativeButton);

        if(!positiveTextResource.isEmpty())
            positiveTextView.setText(positiveTextResource);

        if (!negativeTextResource.isEmpty())
            negativeTextView.setText(negativeTextResource);

        if(defaultCurrentState==null || defaultCurrentState.equals("STATE_ON") || defaultCurrentState.isEmpty())
            onPositiveButtonClick();
        else
            onNegativeButtonClick();

    }


    private void setBackgroundColors()
    {
        GradientDrawable OffBackground,OnBackground;
        OnBackground = (GradientDrawable) positiveTextView.getBackground();
        OffBackground = (GradientDrawable) negativeTextView.getBackground();

        /* Switch in On position*/
        if(currentState)
        {
            if(ON_SELECTED_DRAWABLE!=null)
                positiveTextView.setBackgroundDrawable(ON_SELECTED_DRAWABLE);
            else
                OnBackground.setColor(BACKGROUND_COLOR_ON_SELECTED);

            if(OFF_DEFAULT_DRAWABLE!=null)
                negativeTextView.setBackgroundDrawable(OFF_DEFAULT_DRAWABLE);
            else
                OffBackground.setColor(BACKGROUND_COLOR_OFF_DEFAULT);
        }
        else
        {
            if(ON_DEFAULT_DRAWABLE!=null)
                positiveTextView.setBackgroundDrawable(ON_DEFAULT_DRAWABLE);
            else
                OnBackground.setColor(BACKGROUND_COLOR_ON_DEFAULT);

            if(OFF_SELECTED_DRAWABLE!=null)
                negativeTextView.setBackgroundDrawable(OFF_SELECTED_DRAWABLE);
            else
                OffBackground.setColor(BACKGROUND_COLOR_OFF_SELECTED);
        }



    }

    private void onPositiveButtonClick(){
        Log.e("Position", "On");
        buttonClick(positiveTextView, R.drawable.jazzy_switch_left_selected, COLOR_ON_TEXT_SELECTED, SELECTED_TEXT_SIZE, Typeface.DEFAULT_BOLD, negativeTextView, R.drawable.jazzy_switch_right_default, COLOR_OFF_TEXT_DEFAULT, DEFAULT_TEXT_SIZE, Typeface.DEFAULT, STATE_ON);
    }

    private void onNegativeButtonClick(){
        Log.e("Position","Off");
        buttonClick(positiveTextView,R.drawable.jazzy_switch_left_default,COLOR_ON_TEXT_DEFAULT, DEFAULT_TEXT_SIZE,Typeface.DEFAULT, negativeTextView, R.drawable.jazzy_switch_right_selected, COLOR_OFF_TEXT_SELECTED, SELECTED_TEXT_SIZE, Typeface.DEFAULT_BOLD, STATE_OFF);
    }


    private void buttonClick(TextView positive, int positiveDrawable, int positiveTextColor, float positiveTextSize,Typeface positiveTypeface, TextView negative, int negativeDrawable, int negativeTextColor, float negativeTextSize,Typeface negativeTypeface, boolean targetState){
        positive.setBackgroundResource(positiveDrawable);
        positive.setTextColor(positiveTextColor);
        positive.setTypeface(positiveTypeface);
        positive.setTextSize(positiveTextSize);

        negative.setBackgroundResource(negativeDrawable);
        negative.setTextColor(negativeTextColor);
        negative.setTypeface(negativeTypeface);
        negative.setTextSize(negativeTextSize);

        this.currentState=targetState;
        setBackgroundColors();
        if(mListener!=null)
            mListener.onSwitchToggle(targetState);
    }


    public interface SwitchToggledListener{
        public void onSwitchToggle(boolean checked);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("SwitchView","Entered");

        if(currentState && event.getX()>negativeTextView.getLeft() && event.getX()<negativeTextView.getRight())
            onNegativeButtonClick();
        else if(event.getX()>positiveTextView.getLeft() && event.getX()<positiveTextView.getRight())
            onPositiveButtonClick();
        return true;
    }

    private void refreshView()
    {
        if(currentState)
            onPositiveButtonClick();
        else
            onNegativeButtonClick();
    }



    /*User Customization public functions */
    public void setSwitchToggleListener(SwitchToggledListener listener)
    {
        mListener=listener;
    }


    public Boolean getState(){
        return currentState;
    }


    public void setCurrentState(Boolean state)
    {
        this.currentState=state;
        if(state)
        {
            onPositiveButtonClick();
        }
        else
        {
            onNegativeButtonClick();
        }
    }

    public void setPositiveText(String string)
    {
        positiveTextView.setText(string);
    }

    public void setNegativeText(String string)
    {
        negativeTextView.setText(string);
    }

    public void setBackgroundOnDefaultColor(int color)
    {
        this.BACKGROUND_COLOR_ON_DEFAULT=color;
        refreshView();
    }

    public void setBackgroundOnSelectedColor(int color)
    {
        this.BACKGROUND_COLOR_ON_SELECTED=color;
        refreshView();
    }

    public void setBackgroundOffDefaultColor(int color)
    {
        this.BACKGROUND_COLOR_OFF_DEFAULT=color;
        refreshView();
    }

    public void setBackgroundOffSelectedColor(int color)
    {
        this.BACKGROUND_COLOR_OFF_SELECTED=color;
        refreshView();
    }

    public void setOnTextDefaultColor(int color)
    {
        this.COLOR_ON_TEXT_DEFAULT=color;
        refreshView();
    }

    public void setOffTextDefaultColor(int color)
    {
        this.COLOR_OFF_TEXT_DEFAULT=color;
        refreshView();
    }

    public void setOnTextSelectedColor(int color)
    {
        this.COLOR_ON_TEXT_SELECTED=color;
        refreshView();
    }

    public void setOffTextSelectedColor(int color)
    {
        this.COLOR_OFF_TEXT_SELECTED=color;
        refreshView();
    }

    public void setOnDefaultDrawable(int id)
    {
        this.ON_DEFAULT_DRAWABLE=getResources().getDrawable(id);
        refreshView();
    }

    public void setOnSelectedDrawable(int id)
    {
        this.ON_SELECTED_DRAWABLE=getResources().getDrawable(id);
        refreshView();
    }

    public void setOffDefaultDrawable(int id)
    {
        this.OFF_DEFAULT_DRAWABLE=getResources().getDrawable(id);
        refreshView();
    }

    public void setOffSelectedDrawable(int id)
    {
        this.OFF_SELECTED_DRAWABLE=getResources().getDrawable(id);
        refreshView();
    }

    public void setDefaultTextSize(float size)
    {
        this.DEFAULT_TEXT_SIZE=size;
        refreshView();
    }

    public void setSelectedTextSize(float size)
    {
        this.SELECTED_TEXT_SIZE=size;
        refreshView();
    }

    public void toggleSwitch(){
        if(currentState)
            onNegativeButtonClick();
        else
            onPositiveButtonClick();
    }





}
