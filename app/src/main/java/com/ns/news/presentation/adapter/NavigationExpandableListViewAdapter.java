package com.ns.news.presentation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.ns.news.R;
import com.ns.news.data.api.model.SectionItem;

import java.util.List;

public class NavigationExpandableListViewAdapter extends BaseExpandableListAdapter {
    private final Context mContext;
    private final List<SectionItem> mSectionList;
//    private OnExpandableListViewItemClickListener onExpandableListViewItemClickListener;

    public NavigationExpandableListViewAdapter(Context mContext, List<SectionItem> mSectionList) {
        this.mContext = mContext;
        this.mSectionList = mSectionList;
    }

    @Override
    public int getGroupCount() {
        return mSectionList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mSectionList.get(groupPosition).getSubSections().size();
    }

    @Override
    public SectionItem getGroup(int groupPosition) {
        return mSectionList.get(groupPosition);
    }

    @Override
    public SectionItem getChild(int groupPosition, int childPosition) {
        return mSectionList.get(groupPosition).getSubSections().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder mGroupViewHolder = null;
        if (convertView == null) {
            mGroupViewHolder = new GroupViewHolder();
            convertView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                    inflate(R.layout.navigation_group_row, parent, false);
            mGroupViewHolder.mSectionNameLayout = convertView.findViewById(R.id.parentSectionNameLayout);
            mGroupViewHolder.textView = convertView.findViewById(R.id.title);
            mGroupViewHolder.mExpandButton = convertView.findViewById(R.id.expandButton);
            mGroupViewHolder.mNewTagImageView = convertView.findViewById(R.id.newTagImage);
            mGroupViewHolder.mBadgeImageView = convertView.findViewById(R.id.badgeImageView);
            convertView.setTag(mGroupViewHolder);
        } else {
            mGroupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        if(getGroup(groupPosition) != null) {

            String mSectionName = getGroup(groupPosition).getName();
            SectionItem sectionBean = getGroup(groupPosition);
//            boolean isNewSection = sectionBean != null && sectionBean.isNew();
//            String customeScreen = getGroup(groupPosition).getCustomScreen();
            if(mSectionName == null) {
                mSectionName = "";
            }
//            mGroupViewHolder.textView.setText(groupPosition+" :: "+mSectionName +" - "+customeScreen);
            mGroupViewHolder.textView.setText(mSectionName);



            List<SectionItem> mChildList = getGroup(groupPosition).getSubSections();
            if (mChildList != null && mChildList.size() > 0) {
                mGroupViewHolder.mExpandButton.setVisibility(View.VISIBLE);
            } else {
                mGroupViewHolder.mExpandButton.setVisibility(View.INVISIBLE);
            }

            if (isExpanded) {
                mGroupViewHolder.mExpandButton.setBackgroundResource(R.drawable.minus_new);
            } else {
                mGroupViewHolder.mExpandButton.setBackgroundResource(R.drawable.ic_plus);
            }

            final GroupViewHolder finalMGroupViewHolder = mGroupViewHolder;
            String finalMSectionName = mSectionName;
            mGroupViewHolder.mExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*onExpandableListViewItemClickListener.onExpandButtonClick(groupPosition, isExpanded);
                    if (isExpanded) {
                        finalMGroupViewHolder.mExpandButton.setBackgroundResource(R.drawable.ic_plus);
                    } else {
                        finalMGroupViewHolder.mExpandButton.setBackgroundResource(R.drawable.minus_new);

                    }*/
                }
            });

            mGroupViewHolder.mSectionNameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //onExpandableListViewItemClickListener.onGroupClick(groupPosition, getGroup(groupPosition), isExpanded);
                }
            });
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder = null;
        if (convertView == null) {
            childViewHolder = new ChildViewHolder();
            convertView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.navigation_list_row, parent, false);
            childViewHolder.textView = convertView.findViewById(R.id.title);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.textView.setText(getChild(groupPosition, childPosition).getName());
        childViewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onExpandableListViewItemClickListener.onChildClick(groupPosition, childPosition, getGroup(groupPosition), getChild(groupPosition, childPosition));
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public class ChildViewHolder {
        public TextView textView;
    }

    public class GroupViewHolder {
        public LinearLayout mSectionNameLayout;
        public TextView textView;
        public Button mExpandButton;
        public ImageView mNewTagImageView;
        public AppCompatImageView mBadgeImageView;
    }


    /*public void addStaticItemGroup(List<SectionItem> staticItemList) {
        mSectionList.addAll(staticItemList);
        notifyDataSetChanged();

    }*/
}
