/*
 * Copyright (C) 2014 skyfish.jy@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.blogspot.thengnet.medic.data;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseCursorRecyclerAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private Context mContext;
    private Cursor mCursor;
    private int mRowIdColumn;
    private boolean mDataValid;
    private DataSetObserver mDataSetObserver;

    public BaseCursorRecyclerAdapter (Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex(BaseColumns._ID) : -1;
        mDataSetObserver = new NotifyDataSetObserver();
        if (mCursor != null) mCursor.registerDataSetObserver(mDataSetObserver);
    }

    public abstract void onBindViewHolder (VH viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder (@NonNull VH holder, int position) {
        if (!mDataValid)
            throw new IllegalStateException("Could not bind ViewHolder; Cursor is in invalid state!");
        if (!mCursor.moveToPosition(position))
            throw new IllegalStateException("Could not move cursor to position " + position);

        onBindViewHolder(holder, mCursor);
    }

    public Cursor getCursor () {
        return mCursor;
    }

    @Override
    public int getItemCount () {
        if (mDataValid && mCursor != null) return mCursor.getCount();
        return 0;
    }

    @Override
    public long getItemId (int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position))
            return mCursor.getLong(mRowIdColumn);
        return 0;
    }

    public Cursor getItem (int position) {
        if (!mDataValid)
            throw new IllegalStateException("Could not fetch item; Cursor in invalid state!");
        if (!mCursor.moveToPosition(position))
            throw new IllegalStateException("Could not move cursor to position " + position);
        return mCursor;
    }


    @Override
    public void setHasStableIds (boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    /**
     * Change the underlying cursor to a new cursor; close the existing cursor if found.
     *
     * @param cursor -- the received old {@link Cursor}
     */
    public void changeCursor (Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) old.close();
    }

    /**
     * Swap new {@link Cursor}, and return an old {@link Cursor}; old {@link Cursor} is not closed.
     *
     * @param newCursor -- the new {@link Cursor}
     * @return the swapped {@link Cursor}
     */
    public Cursor swapCursor (Cursor newCursor) {
        if (newCursor == mCursor) return null;

        final Cursor oldCursor = mCursor;
        // TODO; figure out why app crashes due to
        //  "java.lang.IllegalStateException: Observer not registered" when "mContext == null"
        //  is not checked
        if (oldCursor != null && mDataSetObserver != null && mContext == null)
            oldCursor.unregisterDataSetObserver(mDataSetObserver);

        mCursor = newCursor;
        if (mCursor != null) {
            mRowIdColumn = newCursor.getColumnIndexOrThrow(BaseColumns._ID);
            mDataValid = true;
            notifyDataSetChanged(); // notifyDataSetInvalidated() unavailable in RecyclerView.Adapter
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged(); // notifyDataSetInvalidated() unavailable in RecyclerView.Adapter
        }
        return oldCursor;
    }

    private class NotifyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged () {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated () {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged(); // notifyDataSetInvalidated() unavailable in RecyclerView.Adapter
        }
    }
}
