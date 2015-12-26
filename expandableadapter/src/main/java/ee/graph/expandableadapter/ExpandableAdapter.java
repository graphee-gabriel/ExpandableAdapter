package ee.graph.expandableadapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabrielmorin on 15/10/15.
 */
public class ExpandableAdapter extends BaseAdapter {
    private static final String TAG = ExpandableAdapter.class.getName();
    private long expandedId = -1;
    private List<Integer> itemViewTypesWithoutExpandList = new ArrayList<>();
    private ListView listView;

    public ExpandableAdapter(ListView listView) {
        this.listView = listView;
    }

    protected void onPreStartActionAnimation(View view, int position, boolean visible) {

    }

    protected int getExpandableViewId() {
        return 0;
    }

    protected boolean usePosition() {
        return false;
    }

    public void setItemViewTypesWithoutExpand(int... itemViewTypesWithoutExpand) {
        itemViewTypesWithoutExpandList.clear();
        for(int j : itemViewTypesWithoutExpand) {
            itemViewTypesWithoutExpandList.add(itemViewTypesWithoutExpand[j]);
        }
    }

    public long getExpandedId() {
        return expandedId;
    }

    public boolean isExpanded(int position) {
        return usePosition() ? getExpandedId() == position : getExpandedId() == getItemId(position);
    }

    public boolean hasItemExpanded() {
        return expandedId != -1;
    }

    public void collapse() {
        expandedId = -1;
        updateActions();
    }

    public void expand(long id) {
        expandedId = id;
        updateActions();
    }

    public boolean toggle(long id) {
        boolean show = getExpandedId() != id;
        expand(show ? id : -1);
        return show;
    }

    // ADAPTER SPECIAL
    public void updateActions() {
        if(listView != null) {
            int first = listView.getFirstVisiblePosition();
            for (int i = 0; i < listView.getChildCount(); i++) {
                final int position = first + i;
                int itemViewType = getItemViewType(position);
                if(!(itemViewTypesWithoutExpandList.contains(itemViewType) || position == 0)) {
                    final View child = listView.getChildAt(i);
                    View actions = child.findViewById(getExpandableViewId());
                    if(actions != null) {
                        final boolean visible = usePosition() ? position == expandedId : getItemId(position) == expandedId;
                        final boolean wasVisible = actions.getVisibility() == View.VISIBLE;
                        final int startHeight = child.getHeight();
                        actions.setVisibility(visible ? View.VISIBLE : View.GONE);
                        onPreStartActionAnimation(child, position, visible);
                        if(visible || wasVisible) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(((View) child.getParent()).getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.AT_MOST));
                            final int finalHeight = child.getMeasuredHeight();
                            child.getLayoutParams().height = startHeight;
                            HeightAnimation animation = new HeightAnimation(child, finalHeight, startHeight);
                            animation.setDuration(300);
                            animation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    if(visible)
                                        listView.smoothScrollToPosition(position);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            child.startAnimation(animation);
                            child.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    if (child.getTranslationX() != 0) {
                                        if (child.getAnimation() != null)
                                            child.getAnimation().cancel();
                                        child.clearAnimation();
                                        child.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
