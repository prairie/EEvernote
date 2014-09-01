package com.prairie.eevernote.dom.impl;

import org.apache.commons.lang3.StringUtils;

import com.prairie.eevernote.dom.DocumentFragment;
import com.prairie.eevernote.dom.Node;

public class DocumentFragmentImpl extends NodeImpl implements DocumentFragment {

    private String fragment;

    protected DocumentFragmentImpl() {
        fragment = StringUtils.EMPTY;
    }

    protected DocumentFragmentImpl(final String fragment) {
        this.fragment = fragment;
    }

    @Override
    public String getNodeName() {
        return "#document-fragment";
    }

    @Override
    public short getNodeType() {
        return Node.DOCUMENT_FRAGMENT_NODE;
    }

    @Override
    public String getTextContent() {
        return fragment;
    }

    @Override
    public void setTextContent(final String fragment) {
        this.fragment = fragment;
    }

    @Override
    public void insertFragment(final DocumentFragment fragment) {
        this.fragment = fragment.getTextContent() + this.fragment;
    }

    @Override
    public void appendFragment(final DocumentFragment fragment) {
        this.fragment += fragment.getTextContent();
    }

    @Override
    public boolean isEqualNode(final Node other) {
        if (!super.isEqualNode(other)) {
            return false;
        }
        if (!getTextContent().equals(other.getTextContent())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return fragment;
    }
}