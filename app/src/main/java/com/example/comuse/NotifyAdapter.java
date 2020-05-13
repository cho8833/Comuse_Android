package com.example.comuse;

import com.example.comuse.DataModel.Member;

public interface NotifyAdapter {
    void notifyInsertToAdapter(int position);
    void notifyChangeToAdapter(int position, Member modified);
    void notifyRemoveToAdapter(int position);
}
