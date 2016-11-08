package ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;


/**
 * 这也是 自定义View 的一种方式,  4.继承派生特殊的Layout
 * Created by benjious on 2016/11/4.
 */

public class HorizontalScrollViewEx extends ViewGroup {
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    //分别记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;

    private int mLastXIntercept = 0;
    private int mLastYIntercept = 0;


    //他们的定义看onLayout()方法
    private int mChildrenSize;  //子元素的数量
    private int mChildrenWidth; //子元素的宽度
    private int mChildrenIndex; //子元素的指引


    public HorizontalScrollViewEx(Context context) {
        super(context);
        init();
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }



    private void init() {
        if (mScroller==null) {
            mScroller = new Scroller(getContext());
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    //-----------------------------
    //Scorller
    //缓慢滚动到指定位置 desX,desY是指定地点
    private void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(getScrollX(), 0, dx, 0, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        //computeScrollOffset()方法:
        //Call this when you want to know the new location.
        //If it returns true, the animation is not yet finished.
        //当我按着拖动的时候,这个方法可以
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    //----------------------------------------

    /**
     * 我的计划是可以滑动的,那么我的宽度肯定是所有控件宽度之和了.而我的高度我只是显示一个
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = 0;
        int measuredHeight = 0;
        final int childCount = getChildCount();
        //measureChildren()方法来自ViewGroup
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int widthSpaceSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpaceSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSpaceMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpaceMode = MeasureSpec.getMode(heightMeasureSpec);
        if (childCount == 0) {
            setMeasuredDimension(0, 0);

        } else if (widthSpaceMode == MeasureSpec.AT_MOST && heightSpaceMode == MeasureSpec.AT_MOST) {
            //如果宽高都采用wrap_content的话,HSVE的高宽都是所有元素的高宽之和
            final View childView = getChildAt(0);
            measuredWidth = childView.getMeasuredWidth() * childCount;
            measuredHeight = childView.getMeasuredHeight() * childCount;
            setMeasuredDimension(measuredWidth, measuredHeight);
        } else if (widthSpaceMode == MeasureSpec.AT_MOST) {
            //如果宽采用了wrap_content,那么HSVE的宽度就是所有元素的宽度
            //DemoActivity1中width是wrap_content,
            final View childView = getChildAt(0);
            measuredWidth = childView.getMeasuredWidth() * childCount;
            setMeasuredDimension(measuredWidth, heightSpaceSize);
        } else if (heightSpaceMode == MeasureSpec.AT_MOST) {
            //如果高采用了wrap_content,那么HSVE的高度就是第一个元素的高度,这里的第一个元素指的是例子中的ListView
            final View childView = getChildAt(0);
            measuredHeight = childView.getMeasuredHeight();
            setMeasuredDimension(widthSpaceSize, measuredHeight);
        }
    }

    //为什么mChildrenSize要多次赋值,
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        final int childCount = getChildCount();
        mChildrenSize = childCount;

        for (int i = 0; i < childCount; i++) {
            final View childAt = getChildAt(i);
            if (childAt.getVisibility() != View.GONE) {
                final int childWidth = childAt.getMeasuredWidth();
                mChildrenWidth = childWidth;
                childAt.layout(childLeft, 0, childLeft + childWidth, childAt.getMeasuredHeight());
                childLeft += childWidth;
            }
        }

    }

    //-----------------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                //假设我View内容从由右向左滑动,x - mLastX为负值,而我们滑动
                // scrollTo(mScorllerX,mScorlleY)的mScorllerX应该为正值;
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                scrollBy(-deltaX, 0);
                break;
            }
            case MotionEvent.ACTION_UP: {
                int scrollX = getScrollX();
                int scrollToChildIndex=scrollX/mChildrenWidth;
                //计算滑动速度
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVeloctity = mVelocityTracker.getXVelocity();
                if (Math.abs(xVeloctity) >= 50) {
                    //当滑动的速度过大时,根据正负判断
                    mChildrenIndex = xVeloctity > 0 ? mChildrenIndex - 1 : mChildrenIndex + 1;
                } else {
                    //书P130,判断是否过了显示界面的中线,过了那么就显示下个界面
                    mChildrenIndex = (scrollX + mChildrenWidth / 2) / mChildrenWidth;
                }
                //这里当滑动了最后一个界面时,回到第一个界面
                mChildrenIndex = Math.max(0, Math.min(mChildrenIndex, mChildrenSize - 1));
                int dx = mChildrenIndex * mChildrenWidth - scrollX;
                smoothScrollBy(dx, 0);
                mVelocityTracker.clear();
                break;
            }
            default:
                break;
        }


        mLastX = x;
        mLastY = y;
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }


    /**
     * 子类的事件是ListView,那么父类要的是,左右滑动,为了判断在哪个界面,那么抬起的动作也要
     *
     * @param ev 触摸事件
     * @return 是否阻拦
     */


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                //不阻拦,传给子类
                intercepted = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastXIntercept;
                int deltaY = y - mLastYIntercept;

                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    intercepted = true;
                } else {
                    intercepted = false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                intercepted = false;
                break;
            }
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        mLastXIntercept = x;
        mLastYIntercept = y;
        return intercepted;

    }
}
