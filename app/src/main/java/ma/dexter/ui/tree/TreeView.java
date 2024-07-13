/*
 * Copyright 2016 - 2017 ShineM (Xinyuan)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under.
 */

package ma.dexter.ui.tree;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.List;

import ma.dexter.ui.tree.base.BaseNodeViewFactory;
import ma.dexter.ui.tree.base.SelectableTreeAction;
import ma.dexter.ui.tree.helper.TreeHelper;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;

/**
 * Created by xinyuanzhong on 2017/4/20.
 */

public class TreeView<D> implements SelectableTreeAction<D> {
    private final TreeNode<D> root;

    private final Context context;

    private final BaseNodeViewFactory<D> baseNodeViewFactory;

    private RecyclerView rootView;
    private TreeViewAdapter<D> adapter;
    private LinearLayoutManager layoutManager;
    private boolean itemSelectable = true;

    public TreeView(@NonNull TreeNode<D> root, @NonNull Context context, @NonNull BaseNodeViewFactory<D> baseNodeViewFactory) {
        this.root = root;
        this.context = context;
        this.baseNodeViewFactory = baseNodeViewFactory;
    }

    public TreeViewAdapter<D> getAdapter() {
        return adapter;
    }

    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public RecyclerView getView() {
        if (rootView == null) {
            this.rootView = buildRootView();
        }
        return rootView;
    }

    @NonNull
    private RecyclerView buildRootView() {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setMotionEventSplittingEnabled(false); // disable multi touch event to prevent terrible data set error when calculate list.
        ((SimpleItemAnimator) recyclerView.getItemAnimator())
                .setSupportsChangeAnimations(false);

        new FastScrollerBuilder(recyclerView).build();

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TreeViewAdapter<>(context, root, baseNodeViewFactory);
        adapter.setTreeView(this);

        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    @Override
    public void expandAll() {
        TreeHelper.expandAll(root);

        refreshTreeView();
    }


    public void refreshTreeView() {
        if (rootView != null) {
            ((TreeViewAdapter) rootView.getAdapter()).refreshView();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTreeView() {
        if (rootView != null) {
            rootView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void expandNode(TreeNode<D> treeNode) {
        adapter.expandNode(treeNode);
    }

    @Override
    public void expandLevel(int level) {
        TreeHelper.expandLevel(root, level);

        refreshTreeView();
    }

    @Override
    public void collapseAll() {
        TreeHelper.collapseAll(root);

        refreshTreeView();
    }

    @Override
    public void collapseNode(TreeNode<D> treeNode) {
        adapter.collapseNode(treeNode);
    }

    @Override
    public void collapseLevel(int level) {
        TreeHelper.collapseLevel(root, level);

        refreshTreeView();
    }

    @Override
    public void toggleNode(TreeNode<D> treeNode) {
        if (treeNode.isExpanded()) {
            collapseNode(treeNode);
        } else {
            expandNode(treeNode);
        }
    }

    @Override
    public void deleteNode(TreeNode<D> node) {
        adapter.deleteNode(node);
    }

    @Override
    public void addNode(TreeNode<D> parent, TreeNode<D> treeNode) {
        parent.addChild(treeNode);

        refreshTreeView();
    }

    @Override
    public List<TreeNode<D>> getAllNodes() {
        return TreeHelper.getAllNodes(root);
    }

    @Override
    public void selectNode(TreeNode<D> treeNode) {
        if (treeNode != null) {
            adapter.selectNode(true, treeNode);
        }
    }

    @Override
    public void deselectNode(TreeNode<D> treeNode) {
        if (treeNode != null) {
            adapter.selectNode(false, treeNode);
        }
    }

    @Override
    public void selectAll() {
        TreeHelper.selectNodeAndChild(root, true);

        refreshTreeView();
    }

    @Override
    public void deselectAll() {
        TreeHelper.selectNodeAndChild(root, false);

        refreshTreeView();
    }

    @Override
    public List<TreeNode<D>> getSelectedNodes() {
        return TreeHelper.getSelectedNodes(root);
    }

    public boolean isItemSelectable() {
        return itemSelectable;
    }

    public void setItemSelectable(boolean itemSelectable) {
        this.itemSelectable = itemSelectable;
    }

}
